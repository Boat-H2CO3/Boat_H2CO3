package org.koishi.launcher.h2co3.launcher;

import static org.koishi.launcher.h2co3.launcher.H2CO3LauncherLoader.receiveLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.io.File;

public class H2CO3LauncherLib {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    public static final int KeyPress = 2;
    public static final int KeyRelease = 3;
    public static final int ButtonPress = 4;
    public static final int ButtonRelease = 5;
    public static final int MotionNotify = 6;
    public static final int ConfigureNotify = 22;

    public static final int Button1 = 1;
    public static final int Button2 = 2;
    public static final int Button3 = 3;
    public static final int Button4 = 4;
    public static final int Button5 = 5;
    public static final int Button6 = 6;
    public static final int Button7 = 7;

    public static final int CursorEnabled = 1;
    public static final int CursorDisabled = 0;

    static {
        System.loadLibrary("h2co3launcher");
        System.loadLibrary("h2co3_exec_awt");
    }

    private boolean surfaceDestroyed;

    public static void setMouseButton(int button, boolean press) {
        pushEventMouseButton(button, press);
    }

    public static void setPointer(int x, int y) {
        pushEventPointer(x, y);
    }

    public static void setKey(int keyCode, int keyChar, boolean press) {
        pushEventKey(keyCode, keyChar, press);
    }

    public static native void setEventPipe();

    public static native void pushEvent(long time, int type, int p1, int p2);

    public static void pushEventMouseButton(int button, boolean press) {
        H2CO3LauncherLib.pushEvent(System.nanoTime(), press ? ButtonPress : ButtonRelease, button, 0);
    }

    public static void pushEventPointer(int x, int y) {
        H2CO3LauncherLib.pushEvent(System.nanoTime(), MotionNotify, x, y);
    }

    public static void pushEventKey(int keyCode, int keyChar, boolean press) {
        H2CO3LauncherLib.pushEvent(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
    }

    public static void pushEventWindow(int width, int height) {
        H2CO3LauncherLib.pushEvent(System.nanoTime(), ConfigureNotify, width, height);
    }

    public static String getPrimaryClipString() {
        return null;
    }

    public static void setPrimaryClipString(String string) {
    }

    public static native int[] getPointer();

    // 设置H2CO3本地窗口
    public static native void h2co3launcherSetNativeWindow(Surface surface);

    public static void openLink(final String link) {
        Context context = H2CO3Tools.CONTEXT;
        ((Activity) context).runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String targetLink = link;
                if (targetLink.startsWith("file://")) {
                    targetLink = targetLink.replace("file://", "");
                } else if (targetLink.startsWith("file:")) {
                    targetLink = targetLink.replace("file:", "");
                }
                Uri uri;
                if (targetLink.startsWith("http")) {
                    uri = Uri.parse(targetLink);
                } else {
                    //can`t get authority by R.string.file_browser_provider
                    uri = FileProvider.getUriForFile(context, "org.koishi.launcher.h2co3.provider", new File(targetLink));
                }
                intent.setDataAndType(uri, "*/*");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("openLink error", e.toString());
            }
        });
    }

    public native int[] renderAWTScreenFrame();

    public boolean isSurfaceDestroyed() {
        return surfaceDestroyed;
    }

    public void setSurfaceDestroyed(boolean surfaceDestroyed) {
        this.surfaceDestroyed = surfaceDestroyed;
    }

    public void handleWindow(Surface surface) {
        if (H2CO3Game.getGameDirectory() != null) {
            receiveLog("invoke setH2CO3LauncherNativeWindow");
            h2co3launcherSetNativeWindow(surface);
        } else {
            receiveLog("start Android AWT Renderer thread");
            Thread canvasThread = new Thread(() -> {
                Canvas canvas;
                Bitmap rgbArrayBitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint();
                try {
                    while (!surfaceDestroyed && surface.isValid()) {
                        canvas = surface.lockCanvas(null);
                        canvas.drawRGB(0, 0, 0);
                        int[] rgbArray = renderAWTScreenFrame();
                        if (rgbArray != null) {
                            canvas.save();
                            rgbArrayBitmap.setPixels(rgbArray, 0, DEFAULT_WIDTH, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                            canvas.drawBitmap(rgbArrayBitmap, 0, 0, paint);
                            canvas.restore();
                        }
                        surface.unlockCanvasAndPost(canvas);
                    }
                } catch (Throwable throwable) {
                    Handler handler = new Handler();
                    handler.post(() -> receiveLog(throwable.toString()));
                }
                rgbArrayBitmap.recycle();
                surface.release();
            }, "AndroidAWTRenderer");
            canvasThread.start();
        }
    }
}
