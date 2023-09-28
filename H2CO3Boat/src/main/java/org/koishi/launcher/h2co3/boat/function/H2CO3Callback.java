package org.koishi.launcher.h2co3.boat.function;

import android.graphics.SurfaceTexture;

public interface H2CO3Callback {
    void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

    void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

    void onCursorModeChange(int mode);

    void onStart();

    void onPicOutput();

    void onError(Exception e);

    void onExit(int code);
}