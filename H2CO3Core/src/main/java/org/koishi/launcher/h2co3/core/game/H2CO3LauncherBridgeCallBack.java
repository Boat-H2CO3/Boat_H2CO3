package org.koishi.launcher.h2co3.core.game;

import android.graphics.SurfaceTexture;

public interface H2CO3LauncherBridgeCallBack {
    void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

    void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

    void onCursorModeChange(int mode);
    void onLog(String log);
    void onStart();

    void onPicOutput();

    void onError(Exception e);

    void onExit(int code);

    void onHitResultTypeChange(int type);
}
