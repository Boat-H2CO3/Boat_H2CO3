package org.koishi.launcher.h2co3.boat;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.boat.function.H2CO3BoatCallback;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.util.List;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class H2CO3BoatActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    static {
        System.loadLibrary("h2co3_boat");
    }

    // 主纹理视图
    public TextureView mainTextureView;
    // H2CO3回调
    public H2CO3BoatCallback h2co3BoatCallback;
    // 定时器
    public Timer timer;
    // 输出
    int output = 0;
    public final float scaleFactor = 1.0F;

    // 退出
    public static void onExit(Context ctx, int code) {
        ((H2CO3BoatActivity) ctx).h2co3BoatCallback.onExit(code);
    }

    // 发送按键事件
    public static void sendKeyPress(int keyCode, boolean status) {
        H2CO3BoatLib.setKey(keyCode, 0, status);
        System.out.print(keyCode);
    }

    // 发送按键事件
    public static void sendKeyPress(int keyCode, int keyChar, boolean status) {
        H2CO3BoatLib.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    // 发送按键事件
    public static void sendKeyPress(int keyCode, char keyChar, int scancode, int modifiers, boolean status) {
        H2CO3BoatLib.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    // 发送按键事件
    public static void sendKeyPress(int keyCode) {
        H2CO3BoatLib.setKey(keyCode, 0, true);
        H2CO3BoatLib.setKey(keyCode, 0, false);
        System.out.print(keyCode);
    }

    // 发送鼠标按键事件
    public static void sendMouseButton(int button, boolean status) {
        H2CO3BoatLib.setMouseButton(button, status);
    }

    // 初始化
    public void init() {
        nOnCreate();
        timer = new Timer();
        mainTextureView.setSurfaceTextureListener(this);
    }

    // 调用本地方法
    public native void nOnCreate();

    // 当SurfaceTexture可用时调用
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        System.out.println("SurfaceTexture is available!");
        h2co3BoatCallback.onSurfaceTextureAvailable(surface, width, height);
    }

    // 当SurfaceTexture尺寸改变时调用
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        h2co3BoatCallback.onSurfaceTextureSizeChanged(surface, width, height);
    }

    // 当SurfaceTexture销毁时调用
    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    // 当SurfaceTexture更新时调用
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (output == 1) {
            h2co3BoatCallback.onPicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    public static final ExecutorService sExecutorService = new ThreadPoolExecutor(4, 4, 500, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<>());

    // 启动游戏
    // 启动游戏
    public void startGame(final String javaPath, final String home, final boolean highVersion, final Vector<String> args, String renderer, String gameDir) {
        sExecutorService.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            H2CO3LoadMe.launchMinecraft(handler, H2CO3BoatActivity.this, javaPath, home, highVersion, args, renderer, gameDir, new H2CO3BoatCallback() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    H2CO3BoatLib.setH2CO3BoatNativeWindow(new Surface(surface));
                    H2CO3BoatLib.setEventPipe();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    H2CO3BoatLib.pushEventWindow(width, height);
                }

                @Override
                public void onCursorModeChange(int mode) {

                }

                @Override
                public void onStart() {
                    h2co3BoatCallback.onStart();
                }

                @Override
                public void onPicOutput() {

                }

                @Override
                public void onError(Exception e) {
                    h2co3BoatCallback.onError(e);
                    MaterialAlertDialogBuilder exitDialog = new H2CO3MessageDialog(H2CO3BoatActivity.this)
                            .setMessage("error" + e)
                            .setPositiveButton("Exit", (dialog, which) -> finish())
                            .setOnDismissListener(dialog -> H2CO3BoatActivity.this.finish());
                    exitDialog.show();
                }

                @Override
                public void onExit(int code) {
                    MaterialAlertDialogBuilder exitDialog = new H2CO3MessageDialog(H2CO3BoatActivity.this)
                            .setMessage("error" + code)
                            .setPositiveButton("Exit", (dialog, which) -> finish())
                            .setOnDismissListener(dialog -> H2CO3BoatActivity.this.finish());
                    exitDialog.show();
                }
            });
        });
    }

    // 设置光标模式
    public void setCursorMode(int mode) {
        h2co3BoatCallback.onCursorModeChange(mode);
    }

    // 设置H2CO3回调
    public void setH2CO3BoatCallback(H2CO3BoatCallback callback) {
        this.h2co3BoatCallback = callback;
    }

    // 当窗口焦点改变时调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public abstract void onClick(View p1);

    // 在恢复后调用
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> h2co3BoatCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
    }

    // 获取指针坐标
    public int[] getPointer() {
        return H2CO3BoatLib.getPointer();
    }
}


