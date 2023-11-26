package org.koishi.launcher.h2co3.application;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.Tree {
    private static final String TAG = "FileLoggingTree";
    private static final String LOG_FILE_NAME = "app_log.txt";
    private static final SimpleDateFormat LOG_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String logMessage = LOG_DATE_FORMAT.format(new Date()) + " " + tag + ": " + message + "\n";
        writeLogToFile(logMessage);
    }

    private void writeLogToFile(String logMessage) {
        try {
            File logFile = getLogFile();
            FileOutputStream fos = new FileOutputStream(logFile, true);
            fos.write(logMessage.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing log to file: " + e.getMessage());
        }
    }

    private File getLogFile() {
        File logDir = new File(Environment.getExternalStorageDirectory(), "AppLogs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        return new File(logDir, LOG_FILE_NAME);
    }
}