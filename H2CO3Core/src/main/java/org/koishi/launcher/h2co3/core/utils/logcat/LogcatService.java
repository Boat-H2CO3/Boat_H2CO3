package org.koishi.launcher.h2co3.core.utils.logcat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.io.IOException;

/**
 * Service for out-of-process crash handling daemon. Should be run from a separate process.
 * 用于处理崩溃的守护进程的服务。应该在一个单独的进程中运行。
 */
public class LogcatService extends Service {

    /**
     * Key for report file in arguments.
     * 参数中报告文件的键。
     */
    public static final String EXTRA_REPORT_FILE = "report_file";
    /**
     * A name for shared preferences.
     * 共享首选项的名称。
     */
    private static final String PREFS_NAME = "LogcatService";
    /**
     * Indicates if a daemon was started.
     * 指示守护进程是否已启动。
     */
    private static boolean mDaemonStarted = false;
    private static Process mLogcatProcess;

    /**
     * Starts NDCrash out-of-process unwinding daemon. This is necessary for out of process crash
     * handling. This method is run from a service that works in separate process.
     * 启动NDCrash的守护进程。这对于处理进程崩溃是必要的。此方法在运行在单独进程中的服务中运行。
     *
     * @param context         Context instance. Used to determine a socket name.
     * @param crashReportPath Path where to save a crash report.
     * @param unwinder        Unwinder to use.
     * @param callback        Callback to execute when a crash has occurred.
     * @return Error status.
     */
    static int startOutOfProcessDaemon(
            /*@NonNull */Context context,
            /*@Nullable */String crashReportPath) {
        if (LogcatUtils.isMainProcess(context)) {
            return 1;
        }
        try {
            mLogcatProcess = new ProcessBuilder("logcat", "-v", "long", "-f", crashReportPath).start();
        } catch (IOException ignored) {
        }
        return 0;
    }

    /**
     * Stops NDCrash out-of-process unwinding daemon.
     * 停止NDCrash的守护进程。
     *
     * @return Flag whether daemon stopping was successful.
     */
    static boolean stopOutOfProcessDaemon() {
        mLogcatProcess.destroy();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String reportPath = null;
        if (intent != null) {
            reportPath = intent.getStringExtra(EXTRA_REPORT_FILE);
            // Using the same keys as extras.
            final SharedPreferences.Editor editor = preferences.edit();
            if (reportPath != null) {
                editor.putString(EXTRA_REPORT_FILE, reportPath);
            } else {
                editor.remove(EXTRA_REPORT_FILE);
            }
            editor.apply();
        } else {
            reportPath = preferences.getString(EXTRA_REPORT_FILE, null);
        }
        if (!mDaemonStarted) {
            mDaemonStarted = true;
            final int initResult = startOutOfProcessDaemon(this, reportPath);
            if (initResult != 0) {
                //Log.e(TAG, "Couldn't start NDCrash out-of-process daemon with unwinder: " + unwinder + ", error: " + initResult);
            } else {
                //Log.i(TAG, "Out-of-process unwinding daemon is started with unwinder: " + unwinder + " report path: " +
                //	  (reportPath != null ? reportPath : "null"));

            }
        } else {
            //Log.i(TAG, "NDCrash out-of-process daemon is already started.");
        }
        // START_REDELIVER_INTENT may seem better but found by experimental way that when we return
        // this value a service is restarted significantly slower (with a longer delay) after its
        // process is killed. So a workaround is used: Saving initialization parameters to shared
        // preferences and reading them when intent is null.
        return Service.START_STICKY;
    }

    @Override //@CallSuper
    public void onDestroy() {

        if (mDaemonStarted) {
            mDaemonStarted = false;
            final boolean stoppedSuccessfully = stopOutOfProcessDaemon();
            //Log.i(TAG, "Out-of-process daemon " + (stoppedSuccessfully ? "is successfully stopped." : "failed to stop."));
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service doesn't support to be bound.
        return null;
    }

}