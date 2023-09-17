package org.koishi.launcher.h2co3.resources.application;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.resources.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class H2CO3Application extends Application implements Application.ActivityLifecycleCallbacks {
    public static final H2CO3Application mInstance = null;
    public static Activity mCurrentActivity;
    public SharedPreferences mPref;
    private boolean is_OTG = false;

    public static Activity getCurrentActivity() {
        return H2CO3Application.mCurrentActivity;
    }

    public static H2CO3Application getInstance() {
        return mInstance;
    }

    public boolean is_OTG() {
        return is_OTG;
    }

    public void setIs_OTG(boolean is_OTG) {
        this.is_OTG = is_OTG;
    }

    @Override
    public void onActivityCreated(@NonNull Activity p1, Bundle p2) {
        // TODO: Implement this method

    }

    @Override
    public void onActivityStarted(@NonNull Activity p1) {
        // TODO: Implement this method
        H2CO3Application.mCurrentActivity = p1;
        System.out.println(H2CO3Application.mCurrentActivity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity p1) {
        // TODO: Implement this method

    }

    @Override
    public void onActivityPaused(@NonNull Activity p1) {
        // TODO: Implement this method

    }

    @Override
    public void onActivityStopped(@NonNull Activity p1) {
        // TODO: Implement this method
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity p1, @NonNull Bundle p2) {
        // TODO: Implement this method
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity p1) {
        // TODO: Implement this method
    }


    @Override
    public void onCreate() {
        // TODO: Implement this method
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        CaocConfig.Builder.create()
                //程序在后台时，发生崩溃的三种处理方式
                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
                //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
                //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！这种模式我感觉最好
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)     //这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                .showErrorDetails(false) //这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪。
                .showRestartButton(false)    //是否可以重启页面
                .trackActivities(true)     //错误页面中显示错误详细信息
                .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以Ok我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                .errorDrawable(R.drawable.ic_boat)     //崩溃页面显示的图标
                .errorActivity(CrashActivity.class) //程序崩溃后显示的页面
                .eventListener(new CustomEventListener())//设置监听
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置

    }

    /**
     * 监听程序崩溃/重启
     */
    private static class CustomEventListener implements CustomActivityOnCrash.EventListener {
        //程序崩溃回调
        @Override
        public void onLaunchErrorActivity() {
            Log.e(TAG, "onLaunchErrorActivity()");
        }

        //重启程序时回调
        @Override
        public void onRestartAppFromErrorActivity() {
            Log.e(TAG, "onRestartAppFromErrorActivity()");
        }

        //在崩溃提示页面关闭程序时回调
        @Override
        public void onCloseAppFromErrorActivity() {
            Log.e(TAG, "onCloseAppFromErrorActivity()");
        }

    }

}
