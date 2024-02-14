package org.koishi.launcher.h2co3.adapter;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.Version;
import org.koishi.launcher.h2co3.dialog.DownloadDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder> {

    private static List<Version> versionList;

    public VersionAdapter(List<Version> versionList) {
        VersionAdapter.versionList = versionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Version version = versionList.get(position);
        holder.versionNameTextView.setText(version.getVersionName());
        holder.versionTypeTextView.setText(version.getVersionType());
    }

    @Override
    public int getItemCount() {
        return versionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String baseUrl = "https://piston-meta.mojang.com/v1/packages/";
        public TextView versionNameTextView;
        public TextView versionTypeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            versionNameTextView = itemView.findViewById(R.id.id);
            versionTypeTextView = itemView.findViewById(R.id.type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Version version = versionList.get(position);
                String url = baseUrl + version.getVersionSha1() + "/" + version.getVersionName() + ".json";
                new FetchVersionDetailsTask().execute(url);
            }
        }

        private void showDialog(String name, String type, String details) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(itemView.getContext());
            builder.setTitle("Version Details");
            builder.setMessage("Name: " + name + "\nType: " + type + "\nDetails: " + details);
            builder.setPositiveButton("OK", null);
            builder.show();

            DownloadDialog dialog = new DownloadDialog(itemView.getContext());
            dialog.setJsonString(details);
            dialog.show();
        }

        private class FetchVersionDetailsTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                String url = urls[0];
                String details = "";

                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (InputStream inputStream = connection.getInputStream();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            details = response.toString();
                        }
                    } else {
                        details = "HTTP error: " + responseCode;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    details = "Error: " + e.getMessage();
                }

                return details;
            }

            @Override
            protected void onPostExecute(String details) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Version version = versionList.get(position);
                    showDialog(version.getVersionName(), version.getVersionType(), details);

                }
            }
        }
    }
}