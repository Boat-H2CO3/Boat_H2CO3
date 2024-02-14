package org.koishi.launcher.h2co3.core.utils;

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

public class H2CO3DownloadUtils {

    public static void download(String url, OutputStream os) throws IOException {
        download(new URL(url), os);
    }

    public static void download(URL url, OutputStream os) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned HTTP " + conn.getResponseCode()
                        + ": " + conn.getResponseMessage());
            }
            is = conn.getInputStream();
            IOUtils.copy(is, os);
        } finally {
            if (is != null) {
                is.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String downloadString(String url) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            download(url, bos);
            return bos.toString("UTF-8");
        }
    }

    public static void downloadFile(String url, File out) throws IOException {
        out.getParentFile().mkdirs();
        File tempOut = File.createTempFile(out.getName(), ".part", out.getParentFile());
        try (OutputStream bos = new BufferedOutputStream(new FileOutputStream(tempOut))) {
            download(url, bos);
            tempOut.renameTo(out);
        } finally {
            if (tempOut.exists()) {
                tempOut.delete();
            }
        }
    }

    public static void downloadFileMonitored(String urlInput, File outputFile, byte[] buffer,
                                             H2CO3DownloaderFeedback monitor) throws IOException {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }

        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            conn = (HttpURLConnection) new URL(urlInput).openConnection();
            is = conn.getInputStream();
            fos = new FileOutputStream(outputFile);
            int cur;
            int oval = 0;
            int len = conn.getContentLength();

            if (buffer == null) buffer = new byte[65535];

            while ((cur = is.read(buffer)) != -1) {
                oval += cur;
                fos.write(buffer, 0, cur);
                monitor.updateProgress(oval, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (is != null) {
                is.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public interface H2CO3DownloaderFeedback {
        void updateProgress(int current, int total);
    }
}