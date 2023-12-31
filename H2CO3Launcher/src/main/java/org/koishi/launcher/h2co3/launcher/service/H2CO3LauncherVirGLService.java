package org.koishi.launcher.h2co3.launcher.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import org.koishi.launcher.h2co3.launcher.H2CO3LauncherLoader;

import java.util.Objects;

public class H2CO3LauncherVirGLService extends Service {

    private static final int FOREGROUND_ID = 1000;
    private static final String CHANNEL_ID = "H2CO3_CHANNEL";

    /**
     * onStartCommand调用
     */
    public static void startForeground(Service service, String channelName, String channelDesc, String contentTitle, String contentText) {
        NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        notification = getNotification(service, manager, channelName, channelDesc, contentTitle, contentText);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        service.startForeground(FOREGROUND_ID, notification);
    }

    private static Notification getNotification(Context context, NotificationManager manager, String name, String desc, String contentTitle, String contentText) {
        Notification.Builder builder;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(desc);

        manager.createNotificationChannel(channel);
        builder = new Notification.Builder(context, CHANNEL_ID);
        builder.setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(this, "VirGLService", "VirGL service is running", "H2CO3", "VirGL service is running");
        new Thread(() -> H2CO3LauncherLoader.startVirGLService(this, Objects.requireNonNull(getExternalFilesDir("debug")).getAbsolutePath(), getCacheDir().getAbsolutePath())).start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service doesn't support to be bound.
        return null;
    }
}