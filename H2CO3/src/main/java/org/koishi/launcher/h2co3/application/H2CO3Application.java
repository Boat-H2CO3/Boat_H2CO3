package org.koishi.launcher.h2co3.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.BuildConfig;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.ui.CrashActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import timber.log.Timber;

public class H2CO3Application extends Application implements Application.ActivityLifecycleCallbacks {
    public static final H2CO3Application mInstance = new H2CO3Application();
    public static Activity mCurrentActivity;
    public SharedPreferences mPref;
    private final boolean is_OTG = false;

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public static H2CO3Application getInstance() {
        return mInstance;
    }

    public static final ExecutorService sExecutorService = new ThreadPoolExecutor(4, 4, 500, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<>());

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)
                .showErrorDetails(false)
                .showRestartButton(false)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                .errorDrawable(R.drawable.ic_boat)
                .errorActivity(CrashActivity.class)
                .eventListener(new CustomEventListener())
                .apply();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 处理配置更改逻辑，例如更新UI布局
        // 不重载Activity
    }

    private static class CustomEventListener implements CustomActivityOnCrash.EventListener {
        private static final String TAG = "Boat_H2CO3";

        @Override
        public void onLaunchErrorActivity() {
            Logger.e(TAG, "onLaunchErrorActivity()");
        }

        @Override
        public void onRestartAppFromErrorActivity() {
            Logger.e(TAG, "onRestartAppFromErrorActivity()");
        }

        @Override
        public void onCloseAppFromErrorActivity() {
            Logger.e(TAG, "onCloseAppFromErrorActivity()");
        }
    }
}