package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.download.DownloadItem;
import org.koishi.launcher.h2co3.launcher.utils.H2CO3GameHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadDialog extends MaterialAlertDialogBuilder {
    private static final int BUFFER_SIZE = 1024;
    private static final String DOWNLOAD_PATH = H2CO3GameHelper.getGameDirectory() + "/libraries";
    private static final String LIBRARY_URL_PREFIX = "https://libraries.minecraft.net/";
    private static final String LIBRARY_URL_REPLACE = "https://bmclapi2.bangbang93.com/maven/";

    private final Context context;
    private final RecyclerView recyclerView;
    private final List<DownloadItem> downloadItems;
    private String jsonString;
    private AlertDialog dialog;
    private DownloadTask downloadTask;

    public DownloadDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        this.downloadItems = new ArrayList<>();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setView(view);
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString.replace(LIBRARY_URL_PREFIX, LIBRARY_URL_REPLACE);
    }

    @Override
    public AlertDialog create() {
        parseJsonString();

        DownloadAdapter adapter = new DownloadAdapter(context, downloadItems);
        recyclerView.setAdapter(adapter);

        int threadCount = getThreadCount();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        downloadTask = new DownloadTask(executorService, adapter);
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dialog = super.create();
        dialog.setOnDismissListener(dialog -> {
            if (downloadTask != null) {
                downloadTask.cancel(true);
                executorService.shutdownNow();
            }
        });

        return dialog;
    }

    private void parseJsonString() {
        try {
            JSONObject rootJsonObject = new JSONObject(jsonString);
            JSONArray libraries = rootJsonObject.getJSONArray("libraries");

            for (int i = 0; i < libraries.length(); i++) {
                JSONObject library = libraries.getJSONObject(i);
                if (shouldFilterLibrary(library)) {
                    continue;
                }

                JSONObject downloads = library.getJSONObject("downloads");
                JSONObject artifact = downloads.getJSONObject("artifact");

                String name = library.getString("name");
                String path = artifact.getString("path");
                String url = artifact.getString("url");
                int size = artifact.getInt("size");

                DownloadItem item = new DownloadItem(name, path, url, size);
                downloadItems.add(item);
            }
        } catch (JSONException e) {
            // Consider logging the error with more context or rethrowing a custom exception
            e.printStackTrace();
        }
    }

    private boolean shouldFilterLibrary(JSONObject library) throws JSONException {
        String name = library.getString("name");
        return name.contains("windows") || name.contains("macos");
    }

    private int getThreadCount() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return 5;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return 2;
                }
            }
        }
        return 1;
    }

    private static class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
        private final Context context;
        private final CopyOnWriteArrayList<DownloadItem> downloadItems;

        public DownloadAdapter(Context context, List<DownloadItem> downloadItems) {
            this.context = context;
            this.downloadItems = new CopyOnWriteArrayList<>(downloadItems);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_download, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position >= 0 && position < downloadItems.size()) {
                DownloadItem item = downloadItems.get(position);
                holder.bind(item);
            }
        }

        @Override
        public int getItemCount() {
            return downloadItems.size();
        }

        public void removeCompletedItems() {
            List<DownloadItem> itemsToRemove = new ArrayList<>();
            for (DownloadItem item : downloadItems) {
                File file = new File(DOWNLOAD_PATH + File.separator + item.getPath());
                if (item.getProgress() == 100 && file.exists() && file.length() == item.getSize()) {
                    itemsToRemove.add(item);
                }
            }
            for (DownloadItem itemToRemove : itemsToRemove) {
                int index = downloadItems.indexOf(itemToRemove);
                if (index != -1) {
                    downloadItems.remove(itemToRemove);
                    notifyItemRemoved(index);
                }
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView fileNameText;
            LinearProgressIndicator progress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameText = itemView.findViewById(R.id.fileNameText);
                progress = itemView.findViewById(R.id.fileProgress);
            }

            public void bind(DownloadItem item) {
                fileNameText.setText(item.getName());
                progress.setProgress(item.getProgress());
            }
        }
    }

    private class DownloadTask extends AsyncTask<Void, Integer, Void> {
        private final ExecutorService executorService;
        private final DownloadAdapter adapter;

        public DownloadTask(ExecutorService executorService, DownloadAdapter adapter) {
            this.executorService = executorService;
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < downloadItems.size(); i++) {
                DownloadItem item = downloadItems.get(i);

                if (isFileValid(item)) {
                    item.setProgress(100);
                    publishProgress(i);
                    continue;
                }

                int finalI = i;
                Future<Void> future = executorService.submit(() -> {
                    try {
                        URL url = new URL(item.getUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        createDirectoryForItem(item);

                        try (InputStream input = new BufferedInputStream(connection.getInputStream());
                             OutputStream output = new BufferedOutputStream(new FileOutputStream(DOWNLOAD_PATH + "/" + item.getPath()))) {

                            byte[] data = new byte[BUFFER_SIZE];
                            int count;
                            int downloadedSizeForItem = 0;
                            while ((count = input.read(data)) != -1) {
                                if (isCancelled()) {
                                    return null;
                                }

                                downloadedSizeForItem += count;
                                output.write(data, 0, count);
                                if (item.getSize() > 0) { // 避免除以零的错误
                                    int progress = (downloadedSizeForItem * 100) / item.getSize();
                                    item.setProgress(progress);
                                    publishProgress(finalI);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DownloadTask", "Error downloading file: " + e.getMessage());
                        showErrorDialogOnUIThread(e.getMessage());
                        cancel(true);
                        downloadItems.remove(item);
                        publishProgress(finalI);
                        throw new RuntimeException("Error downloading file: " + item.getUrl(), e);
                    }
                    return null;
                });
                futures.add(future);
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    Log.e("DownloadTask", "Error executing download task: " + e.getMessage());
                    cancel(true);
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adapter.notifyItemChanged(values[0]);
            adapter.removeCompletedItems();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.removeCompletedItems();
            if (downloadItems.isEmpty()) {
                dialog.dismiss();
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showErrorDialogOnUIThread("下载失败");
        }

        private boolean isFileValid(DownloadItem item) {
            File file = new File(DOWNLOAD_PATH + "/" + item.getPath());
            return file.exists() && file.length() == item.getSize();
        }

        private void createDirectoryForItem(DownloadItem item) throws IOException {
            int lastIndex = item.getPath().lastIndexOf("/");
            Path folderPath = Paths.get(DOWNLOAD_PATH, item.getPath().substring(0, lastIndex + 1));
            Files.createDirectories(folderPath);
        }

        private AlertDialog dialog;

        private void showErrorDialogOnUIThread(String message) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (dialog == null || !dialog.isShowing()) {
                    dialog = new AlertDialog.Builder(context)
                            .setTitle("错误")
                            .setMessage(message)
                            .setPositiveButton("确定", null)
                            .create();
                }
                dialog.show();
            });
        }
    }
}