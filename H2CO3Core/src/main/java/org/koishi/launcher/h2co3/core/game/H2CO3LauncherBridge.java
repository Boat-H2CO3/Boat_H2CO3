package org.koishi.launcher.h2co3.core.game;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.io.File;
import java.io.Serializable;

public class H2CO3LauncherBridge implements Serializable {

    static {
        System.loadLibrary("h2co3launcher");
    }

    public H2CO3LauncherBridge() {
    }

    public native int chdir(String path);
    public native int redirectStdio(String path);
    public native void setenv(String name, String value);
    public native int dlopen(String name);
    public native void setEventPipe();
    public native void nativeMoveWindow(int x, int y);
    public native int[] renderAWTScreenFrame();
    public static native void pushEvent(long time, int type, int p1, int p2);
    public static native int[] getPointer();
    public native void setLdLibraryPath(String path);
    public native void h2co3LauncherSetNativeWindow(Surface surface);
    public native int setupExitTrap(H2CO3LauncherBridge bridge);
    public native void setupJLI();
    public native int jliLaunch(String[] args);
    public native void setH2CO3LauncherBridge(H2CO3LauncherBridge h2co3LauncherBridgeBridge);
    public native void nativeSendData(int type, int i1, int i2, int i3, int i4);

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

    private boolean surfaceDestroyed;
    private String logPath;
    private H2CO3LauncherBridgeCallBack callback;
    private Surface surface;
    private Handler handler;
    private Thread thread;

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isSurfaceDestroyed() {
        return surfaceDestroyed;
    }

    public void setSurfaceDestroyed(boolean surfaceDestroyed) {
        this.surfaceDestroyed = surfaceDestroyed;
    }

    public H2CO3LauncherBridgeCallBack getCallback() {
        return callback;
    }

    @NonNull
    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void receiveLog(String log) {
        if (callback != null) {
            callback.onLog(log);
        }
    }

    public void execute(Surface surface, H2CO3LauncherBridgeCallBack callback) {
        this.handler = new Handler();
        this.callback = callback;
        this.surface = surface;
        setH2CO3LauncherBridge(this);
        receiveLog("invoke redirectStdio");
        int errorCode = redirectStdio(H2CO3Tools.LOG_DIR);
        if (errorCode != 0) {
            receiveLog("Can't exec redirectStdio! Error code: " + errorCode);
        }
        receiveLog("invoke setLogPipeReady");
        // set graphic output and event pipe
        if (surface != null) {
            handleWindow();
        }
        receiveLog("invoke setEventPipe");

        setEventPipe();

        // start
        if (thread != null) {
            thread.start();
        }
    }

    public void onExit(int code) {
        if (callback != null) {
            callback.onLog("OpenJDK exited with code : " + code);
            callback.onExit(code);
        }
    }

    public void setHitResultType(int type) {
        if (callback != null) {
            callback.onHitResultTypeChange(type);
        }
    }

    public void setCursorMode(int mode) {
        if (callback != null) {
            callback.onCursorModeChange(mode);
        }
    }

    public static void setMouseButton(int button, boolean press) {
        pushEventMouseButton(button, press);
    }

    public static void setPointer(int x, int y) {
        pushEventPointer(x, y);
    }

    public static void setKey(int keyCode, int keyChar, boolean press) {
        pushEventKey(keyCode, keyChar, press);
    }

    public static void pushEventMouseButton(int button, boolean press) {
        pushEvent(System.nanoTime(), press ? ButtonPress : ButtonRelease, button, 0);
    }

    public static void pushEventPointer(int x, int y) {
        pushEvent(System.nanoTime(), MotionNotify, x, y);
    }

    public static void pushEventKey(int keyCode, int keyChar, boolean press) {
        pushEvent(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
    }

    public void pushEventWindow(int width, int height) {
        pushEvent(System.nanoTime(), ConfigureNotify, width, height);
    }

    public void handleWindow() {
        if (H2CO3GameHelper.getGameDirectory() != null) {
            receiveLog("invoke setH2CO3LauncherNativeWindow\n");
            h2co3LauncherSetNativeWindow(this.surface);
        } else {
            receiveLog("start Android AWT Renderer thread\n");
            Thread canvasThread = new Thread(() -> {
                Canvas canvas;
                Bitmap rgbArrayBitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint();
                try {
                    while (!surfaceDestroyed && this.surface.isValid()) {
                        canvas = this.surface.lockCanvas(null);
                        canvas.drawRGB(0, 0, 0);
                        int[] rgbArray = renderAWTScreenFrame();
                        if (rgbArray != null) {
                            canvas.save();
                            rgbArrayBitmap.setPixels(rgbArray, 0, DEFAULT_WIDTH, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                            canvas.drawBitmap(rgbArrayBitmap, 0, 0, paint);
                            canvas.restore();
                        }
                        this.surface.unlockCanvasAndPost(canvas);
                    }
                } catch (Throwable throwable) {
                    Handler handler = new Handler();
                    handler.post(() -> receiveLog(throwable.toString()));
                }
                rgbArrayBitmap.recycle();
                this.surface.release();
            }, "AndroidAWTRenderer");
            canvasThread.start();
        }
    }

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

    public void setPrimaryClipString(String string) {
        ClipboardManager clipboard = (ClipboardManager) H2CO3Tools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("H2CO3Launcher Clipboard", string);
        clipboard.setPrimaryClip(clip);
    }

    public String getPrimaryClipString() {
        ClipboardManager clipboard = (ClipboardManager) H2CO3Tools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboard.hasPrimaryClip()) {
            return null;
        }
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        return item.getText().toString();
    }
}