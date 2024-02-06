package org.koishi.launcher.h2co3.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.VersionAdapter;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.utils.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VanillaActivity extends H2CO3Activity {

    private static final String API_URL_BMCLAPI = "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json";
    private static final String API_URL_MCBBS = "https://download.mcbbs.net/mc/game/version_manifest_v2.json";
    private static final String API_URL_MOJANG = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private RecyclerView recyclerView;
    private VersionAdapter versionAdapter;
    private RadioGroup typeRadioGroup;
    private RadioButton rbRelease;
    private RadioButton rbSnapshot;
    private RadioButton rbOldbeta;

    private List<Version> versionList;
    private List<Version> filteredList;

    private Spinner spDownloadSourceMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        initView();
        initListeners();

        versionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        versionAdapter = new VersionAdapter(filteredList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(versionAdapter);

        // 自动获取版本列表
        fetchVersionsFromApi(2);
    }

    private void initView() {
        recyclerView = findViewById(R.id.loadingversionFileListView1);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        rbRelease = findViewById(R.id.rb_release);
        rbSnapshot = findViewById(R.id.rb_snapshot);
        rbOldbeta = findViewById(R.id.rb_old_beta);

        spDownloadSourceMode = findViewById(R.id.sp_download_source_mode);
        String[] mItems = getResources().getStringArray(R.array.download_source);
        ArrayAdapter<String> adapter_source = new ArrayAdapter<>(VanillaActivity.this,
                android.R.layout.simple_spinner_dropdown_item, mItems);
        spDownloadSourceMode.setAdapter(adapter_source);
        spDownloadSourceMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchVersionsFromApi(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterVersions(checkedId);
        });
    }

    private void fetchVersionsFromApi(int position) {
        recyclerView.setAdapter(null);
        String apiUrl = getApiUrl(position);
        if (apiUrl != null) {
            new FetchVersionsTask().execute(apiUrl);
        } else {
            Toast.makeText(this, "Invalid source", Toast.LENGTH_SHORT).show();
        }
    }

    private String getApiUrl(int position) {
        return switch (position) {
            case 0 -> API_URL_BMCLAPI;
            case 1 -> API_URL_MCBBS;
            case 2 -> API_URL_MOJANG;
            default -> null;
        };
    }

    private void filterVersions(int checkedId) {
        filteredList.clear();
        for (Version version : versionList) {
            if (checkedId == R.id.rb_release && version.getVersionType().equals("release")) {
                filteredList.add(version);
            } else if (checkedId == R.id.rb_snapshot && version.getVersionType().equals("snapshot")) {
                filteredList.add(version);
            } else if (checkedId == R.id.rb_old_beta && (version.getVersionType().equals("old_alpha") || version.getVersionType().equals("old_beta"))) {
                filteredList.add(version);
            }
        }
        versionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(versionAdapter);
    }

    private class FetchVersionsTask extends AsyncTask<String, Void, List<Version>> {

        @Override
        protected List<Version> doInBackground(String... urls) {
            String apiUrl = urls[0];
            List<Version> versionList = new ArrayList<>();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line).append("\n");
                        }

                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray versionsArray = jsonObject.getJSONArray("versions");
                        for (int i = 0; i < versionsArray.length(); i++) {
                            JSONObject versionObject = versionsArray.getJSONObject(i);
                            String versionName = versionObject.getString("id");
                            String versionType = versionObject.getString("type");
                            String versionUrl = versionObject.getString("url");
                            String versionSha1 = versionObject.getString("sha1");
                            Version version = new Version(versionName, versionType, versionUrl, versionSha1);
                            versionList.add(version);
                        }
                    }
                } else {
                    Toast.makeText(VanillaActivity.this, "HTTP error: " + responseCode, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return versionList;
        }

        @Override
        protected void onPostExecute(List<Version> versionListFromApi) {
            versionList.clear();
            versionList.addAll(versionListFromApi);
            filterVersions(typeRadioGroup.getCheckedRadioButtonId());
        }
    }
}