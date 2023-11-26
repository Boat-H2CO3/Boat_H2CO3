package org.koishi.launcher.h2co3.core.utils.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action) || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                // 处理ACTION_MEDIA_MOUNTED和ACTION_MEDIA_UNMOUNTED动作
            } else {
                // 处理其他动作
            }
        }
    }
}