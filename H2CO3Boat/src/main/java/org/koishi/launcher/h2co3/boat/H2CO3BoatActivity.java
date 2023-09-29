package org.koishi.launcher.h2co3.boat;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.boat.function.H2CO3Callback;
import org.koishi.launcher.h2co3.resources.component.H2CO3Activity;

import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class H2CO3BoatActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    // 加载本地库
    static {
        System.loadLibrary("h2co3_boat");
    }

    // 缩放因子
    public final float scaleFactor = 1.0F;
    // 主纹理视图
    public TextureView mainTextureView;
    // H2CO3回调
    public H2CO3Callback h2co3Callback;
    // 定时器
    public Timer timer;
    // 基础布局
    public RelativeLayout base;
    // 初始X坐标
    public int initialX;
    // 初始Y坐标
    public int initialY;
    // 基础X坐标
    public int baseX;
    // 基础Y坐标
    public int baseY;
    // 输出
    int output = 0;

    // 设置H2CO3本地窗口
    public static native void setH2CO3NativeWindow(Surface surface);

    // 退出
    public static void onExit(Context ctx, int code) {
        ((H2CO3BoatActivity) ctx).h2co3Callback.onExit(code);
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
        h2co3Callback.onSurfaceTextureAvailable(surface, width, height);
    }

    // 当SurfaceTexture尺寸改变时调用
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        h2co3Callback.onSurfaceTextureSizeChanged(surface, width, height);
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
            h2co3Callback.onPicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    // 启动游戏
    // 启动游戏
    public void startGame(final String javaPath, final String home, final boolean highVersion, final Vector<String> args, String renderer, String gameDir) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            LoadMe.launchMinecraft(handler, H2CO3BoatActivity.this, javaPath, home, highVersion, args, renderer, gameDir, new H2CO3Callback() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public void onCursorModeChange(int mode) {

                }

                @Override
                public void onStart() {
                    h2co3Callback.onStart();
                }

                @Override
                public void onPicOutput() {

                }

                @Override
                public void onError(Exception e) {
                    h2co3Callback.onError(e);
                }

                @Override
                public void onExit(int code) {

                }
            });
        });
        executor.shutdown();
    }

    // 设置光标模式
    public void setCursorMode(int mode) {
        h2co3Callback.onCursorModeChange(mode);
    }

    // 设置H2CO3回调
    public void setH2CO3Callback(H2CO3Callback callback) {
        this.h2co3Callback = callback;
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

    // 在恢复后调用
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> h2co3Callback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
    }

    // 设置光标抓取
    public void setGrabCursor(boolean isGrabbed) {
    }

    // 获取指针坐标
    public int[] getPointer() {
        return H2CO3BoatLib.getPointer();
    }

    // 设置按键
    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        H2CO3BoatLib.setKey(keyCode, keyChar, isPressed);
    }

    // 设置鼠标按键
    public void setMouseButton(int button, boolean isPressed) {
        H2CO3BoatLib.setMouseButton(button, isPressed);
    }

    // 设置指针坐标
    public void setPointer(int x, int y) {
        H2CO3BoatLib.setPointer(x, y);
    }
}


