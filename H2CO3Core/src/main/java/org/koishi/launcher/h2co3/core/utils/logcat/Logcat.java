package org.koishi.launcher.h2co3.core.utils.logcat;

import android.content.Context;
import android.content.Intent;

/**
 * NDCrash功能的主要绑定类。
 */
public class Logcat {

    /**
     * 用于独立进程模式的后台服务类。
     */
    private static Class<? extends LogcatService> mServiceClass = null;

    /**
     * 使用独立进程模式初始化NDCrash库信号处理程序。应该在Application的子类的onCreate()方法中调用。
     *
     * @param context         Context实例。用于确定套接字名称并启动服务。
     * @param crashReportPath 崩溃报告保存的路径。
     * @param serviceClass    后台服务的类。当我们需要使用自定义的NDCrashUnwinder子类作为后台服务时使用。
     *                        如果没有子类化NDCrashUnwinder，请传递NDCrashUnwinder.class。
     * @return 错误状态。
     */
    public static int initializeOutOfProcess(
            Context context,
            String crashReportPath,
            Class<? extends LogcatService> serviceClass) {
        if (LogcatUtils.isLogcatServiceProcess(context, serviceClass)) {
            // 如果是后台崩溃服务进程，我们不需要初始化任何内容，
            // 我们将此情况视为无错误，因为此方法设计为从Application.onCreate()调用。
            return 1;
        }
        // 保存服务类，我们应该能够在取消初始化时停止它。
        mServiceClass = serviceClass;
        // 启动崩溃报告服务。仅在主进程中。
        if (LogcatUtils.isMainProcess(context)) {
            final Intent serviceIntent = new Intent(context, serviceClass);
            serviceIntent.putExtra(LogcatService.EXTRA_REPORT_FILE, crashReportPath);
            try {
                context.startService(serviceIntent);
            } catch (RuntimeException e) {
                return 2;
            }
        }
        // 初始化信号处理程序。
        return 0;
    }

    /**
     * 使用独立进程模式取消初始化NDCrash库信号处理程序。
     *
     * @param context Context实例。用于停止服务。
     * @return 取消初始化是否成功的标志。
     */
    public static boolean deInitializeOutOfProcess(Context context) {
        if (mServiceClass != null) {
            context.stopService(new Intent(context, mServiceClass));
            mServiceClass = null;
        }
        return true;
    }


}