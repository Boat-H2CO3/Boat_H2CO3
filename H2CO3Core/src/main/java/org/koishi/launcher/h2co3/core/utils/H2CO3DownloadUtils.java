package org.koishi.launcher.h2co3.core.utils;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class H2CO3DownloadUtils {

    public static void download(String url, OutputStream os) throws IOException {
        download(new URL(url), os);
    }

    public static void download(URL url, OutputStream os) throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned HTTP " + conn.getResponseCode()
                        + ": " + conn.getResponseMessage());
            }
            is = conn.getInputStream();
            IOUtils.copy(is, os);
        } catch (IOException e) {
            throw new IOException("Unable to download from " + url, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String downloadString(String url) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        download(url, bos);
        bos.close();
        return bos.toString(StandardCharsets.UTF_8);
    }

    public static void downloadFile(String url, File out) throws IOException {
        out.getParentFile().mkdirs();
        File tempOut = File.createTempFile(out.getName(), ".part", out.getParentFile());
        BufferedOutputStream bos = null;
        try {
            OutputStream bos2 = new BufferedOutputStream(new FileOutputStream(tempOut));
            try {
                download(url, bos2);
                tempOut.renameTo(out);
                if (bos2 != null) {
                    bos2.close();
                }
                if (tempOut.exists()) {
                    tempOut.delete();
                }
            } catch (IOException th2) {
                if (bos != null) {
                    bos.close();
                }
                if (tempOut.exists()) {
                    tempOut.delete();
                }
                throw th2;
            }
        } catch (IOException th3) {

            if (bos != null) {
                bos.close();
            }
            if (tempOut.exists()) {
                tempOut.delete();
            }
            throw th3;
        }
    }

    public static void downloadFileMonitored(String urlInput, File outputFile, byte[] buffer,
                                             H2CO3DownloaderFeedback monitor) throws IOException {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(urlInput).openConnection();
        InputStream readStr = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(outputFile);
        int cur;
        int oval = 0;
        int len = conn.getContentLength();

        if (buffer == null) buffer = new byte[65535];

        while ((cur = readStr.read(buffer)) != -1) {
            oval += cur;
            fos.write(buffer, 0, cur);
            monitor.updateProgress(oval, len);
        }
        fos.close();
        conn.disconnect();
    }

    public interface H2CO3DownloaderFeedback {
        void updateProgress(int current, int total);
    }
}