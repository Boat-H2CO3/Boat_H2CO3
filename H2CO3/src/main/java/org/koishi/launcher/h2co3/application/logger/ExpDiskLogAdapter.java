package org.koishi.launcher.h2co3.application.logger;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;

public class ExpDiskLogAdapter implements LogAdapter {
    @NonNull private final FormatStrategy formatStrategy;

    @Override public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    @Override public void log(int priority, @Nullable String tag, @NonNull String message) {
        formatStrategy.log(priority, tag, message);
    }

    public ExpDiskLogAdapter() {
        formatStrategy = ExpCsvFormatStrategy.newBuilder().build();
    }
}