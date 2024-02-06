package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
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
import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.download.DownloadItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadDialog extends MaterialAlertDialogBuilder {
    private static final int BUFFER_SIZE = 1024;
    private static final String DOWNLOAD_PATH = H2CO3Game.getGameDirectory() + "/libraries";
    private static final String LIBRARY_URL_PREFIX = "https://libraries.minecraft.net/";
    private static final String LIBRARY_URL_REPLACE = "https://bmclapi2.bangbang93.com/maven/";

    private final Context context;
    private String jsonString;

    private final RecyclerView recyclerView;

    private AlertDialog dialog;

    private DownloadTask downloadTask;

    private final List<DownloadItem> downloadItems;

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
            JSONObject json = new JSONObject(jsonString);
            JSONArray libraries = json.getJSONArray("libraries");

            for (int i = 0; i < libraries.length(); i++) {
                JSONObject library = libraries.getJSONObject(i);
                JSONObject downloads = library.getJSONObject("downloads");
                JSONObject artifact = downloads.getJSONObject("artifact");

                String name = library.getString("name");
                String path = artifact.getString("path");
                String url = artifact.getString("url");

                int size = artifact.getInt("size");

                // 过滤掉name中包含"windows"或"macos"的项
                if (name.contains("windows") || name.contains("macos")) {
                    continue;
                }

                DownloadItem item = new DownloadItem(name, path, url, size);
                downloadItems.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private class DownloadTask extends AsyncTask<Void, Integer, Void> {
        private final ExecutorService executorService;
        private final DownloadAdapter adapter;

        public DownloadTask(ExecutorService executorService, DownloadAdapter adapter) {
            this.executorService = executorService;
            this.adapter = adapter;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            int totalSize = 0;

            for (DownloadItem item : downloadItems) {
                totalSize += item.getSize();
            }

            List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < downloadItems.size(); i++) {
                DownloadItem item = downloadItems.get(i);

                File file = new File(DOWNLOAD_PATH + "/" + item.getPath());
                if (file.exists() && file.length() == item.getSize()) {
                    item.setProgress(100);
                    publishProgress(i);
                    continue;
                }

                int finalI = i;
                Future<Void> future = executorService.submit(() -> {
                    int retryCount = 0;
                    while (retryCount < 3) { // 最多重试3次
                        try {
                            if (isCancelled()) {
                                return null;
                            }

                            URL url = new URL(item.getUrl());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.connect();

                            connection.getContentLength();

                            int lastIndex = item.getPath().lastIndexOf("/");
                            String folderPath = DOWNLOAD_PATH + "/" + item.getPath().substring(0, lastIndex + 1);
                            File folder = new File(folderPath);
                            if (!folder.exists()) {
                                if (!folder.mkdirs()) {
                                    throw new IOException("Failed to create folder: " + folderPath);
                                }
                            }

                            try (InputStream input = new BufferedInputStream(url.openStream());
                                 FileOutputStream output = new FileOutputStream(DOWNLOAD_PATH + "/" + item.getPath())) {

                                byte[] data = new byte[BUFFER_SIZE];
                                int count;
                                int downloadedSizeForItem = 0;
                                while ((count = input.read(data)) != -1) {
                                    if (isCancelled()) {
                                        return null;
                                    }

                                    downloadedSizeForItem += count;
                                    output.write(data, 0, count);
                                    int progress = (downloadedSizeForItem * 100) / item.getSize();
                                    synchronized (downloadItems) {
                                        item.setProgress(progress);
                                    }
                                    if (progress % 5 == 0) {
                                        publishProgress(finalI);
                                    }
                                }
                            }

                            break;

                        } catch (IOException e) {
                            e.printStackTrace();
                            retryCount++;
                            showErrorDialog(e.getMessage());
                            publishProgress(finalI);
                        }
                    }

                    return null;
                });
                futures.add(future);
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel(true); // 取消所有下载任务
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            synchronized (downloadItems) {
                adapter.notifyItemChanged(values[0]);
                adapter.removeCompletedItems();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            synchronized (downloadItems) {
                adapter.removeCompletedItems();

                if (downloadItems.isEmpty()) {
                    dialog.dismiss();
                }

                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showErrorDialog("下载失败");
        }

        private void showErrorDialog(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("错误")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    private static class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
        private final Context context;
        private final List<DownloadItem> downloadItems;

        public DownloadAdapter(Context context, List<DownloadItem> downloadItems) {
            this.context = context;
            this.downloadItems = downloadItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_download, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            synchronized (downloadItems) {
                if (position >= 0 && position < downloadItems.size()) {
                    DownloadItem item = downloadItems.get(position);
                    holder.bind(item);
                }
            }
        }

        @Override
        public int getItemCount() {
            synchronized (downloadItems) {
                return downloadItems.size();
            }
        }

        public void removeCompletedItems() {
            synchronized (downloadItems) {
                for (int i = downloadItems.size() - 1; i >= 0; i--) {
                    DownloadItem item = downloadItems.get(i);
                    File file = new File(DOWNLOAD_PATH + "/" + item.getPath());
                    if (item.getProgress() == 100 && file.exists() && file.length() == item.getSize()) {
                        downloadItems.remove(i);
                        notifyItemRemoved(i);
                    }
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
}