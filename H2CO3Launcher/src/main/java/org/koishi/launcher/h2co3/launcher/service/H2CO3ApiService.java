package org.koishi.launcher.h2co3.launcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.launcher.function.ApiInstallerCallback;

import java.util.ArrayList;

public class H2CO3ApiService extends Service {

    public ApiInstallerCallback callback;

    public static void onExit(Context context, int exitCode) {
        ((H2CO3ApiService) context).callback.onExit(exitCode);
        ((H2CO3ApiService) context).stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
