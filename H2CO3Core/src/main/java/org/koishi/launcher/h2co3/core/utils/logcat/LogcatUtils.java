package org.koishi.launcher.h2co3.core.utils.logcat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Process;

/**
 * Contains some utility code.
 * 包含一些实用代码。
 */
public class LogcatUtils {

    /**
     * Checks if a current process is a main process of application.
     * 检查当前进程是否是应用程序的主进程。
     *
     * @param context Current context.
     *                当前上下文。
     * @return Flag whether it's a main process.
     *         标志是否是主进程。
     */
    public static boolean isMainProcess(/*@NonNull*/ Context context) {
        final int pid = Process.myPid();
        final String packageName = context.getPackageName();
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (final ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
                if (info.pid == pid) {
                    return packageName.equals(info.processName);
                }
            }
        }
        return true;
    }

    /**
     * Checks if a current process is a background crash service process.
     * 检查当前进程是否是后台崩溃服务进程。
     *
     * @param context      Current context.
     *                     当前上下文。
     * @param serviceClass Class of background crash reporting service.
     *                     后台崩溃报告服务的类。
     * @return Flag whether a current process is a background crash service process.
     *         标志当前进程是否是后台崩溃服务进程。
     */
    public static boolean isLogcatServiceProcess(/*@NonNull */Context context,/* @NonNull */Class<? extends LogcatService> serviceClass) {
        final ServiceInfo serviceInfo;
        try {
            serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(context, serviceClass), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
        final int pid = Process.myPid();
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (final ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
                if (info.pid == pid) {
                    return serviceInfo.processName.equals(info.processName);
                }
            }
        }
        return false;
    }
}