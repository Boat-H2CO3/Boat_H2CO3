package org.koishi.launcher.h2co3.boat.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.boat.LoadMe;
import org.koishi.launcher.h2co3.boat.function.ApiInstallerCallback;

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

    public void startApiInstaller(String javaPath, ArrayList<String> commands, String debugDir, ApiInstallerCallback callback) {
        this.callback = callback;
        new Thread(() -> {
            int exitCode = LoadMe.launchJVM(javaPath, commands, debugDir);
            onExit(H2CO3ApiService.this, exitCode);
        }).start();
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
