package org.koishi.launcher.h2co3.launcher;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.launcher.function.H2CO3LauncherCallback;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class H2CO3LauncherActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    static {
        System.loadLibrary("h2co3launcher");
    }

    // 主纹理视图
    public TextureView mainTextureView;
    public RelativeLayout baseLayout;
    // H2CO3回调
    public H2CO3LauncherCallback h2co3LauncherCallback;
    // 定时器
    public Timer timer;
    private TimerTask systemUiTimerTask;
    // 输出
    int output = 0;
    public final float scaleFactor = 1.0F;

    public static IBoat boatInterface;

    private static final int SYSTEM_UI_HIDE_DELAY_MS = 3000;

    // 初始化
    public void init() {
        nOnCreate();
        timer = new Timer();
        mainTextureView.setSurfaceTextureListener(this);
    }

    // 调用本地方法
    public native void nOnCreate();

    public static native void setupExitTrap(Context context);

    // 当SurfaceTexture可用时调用
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d("H2CO3LauncherActivity", "SurfaceTexture is available!");
        h2co3LauncherCallback.onSurfaceTextureAvailable(surface, width, height);
    }

    // 当SurfaceTexture尺寸改变时调用
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        h2co3LauncherCallback.onSurfaceTextureSizeChanged(surface, width, height);
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
            h2co3LauncherCallback.onPicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    public static final ExecutorService sExecutorService = new ThreadPoolExecutor(4, 4, 500, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<>());

    // 启动游戏
    public void startGame(Context context,final String javaPath, final String home, final boolean highVersion, final Vector<String> args, String renderer, String gameDir) {
        setupExitTrap(this);
        sExecutorService.execute(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            H2CO3LauncherLoader.launchMinecraft(handler, context, javaPath, home, highVersion, args, renderer, gameDir, new H2CO3LauncherCallback() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    H2CO3LauncherLib.h2co3launcherSetNativeWindow(new Surface(surface));
                    H2CO3LauncherLib.setEventPipe();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    H2CO3LauncherLib.pushEventWindow(width, height);
                }

                @Override
                public void onCursorModeChange(int mode) {

                }

                @Override
                public void onStart() {
                    h2co3LauncherCallback.onStart();
                }

                @Override
                public void onPicOutput() {
                }

                @Override
                public void onError(Exception e) {
                    h2co3LauncherCallback.onError(e);
                }

                /**
                 * @param code
                 */
                @Override
                public void onExit(int code) {

                }
            });
        });
    }

    // 设置H2CO3回调
    public void setH2CO3LauncherCallback(H2CO3LauncherCallback callback) {
        this.h2co3LauncherCallback = callback;
    }

    public abstract void onClick(View p1);

    // 在恢复后调用
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> h2co3LauncherCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
    }

    public void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> boatInterface.setGrabCursor(isGrabbed));
    }

    // Override addContentView method to dynamically add views
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (params instanceof RelativeLayout.LayoutParams) {
            baseLayout.addView(view, params);
        } else {
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(params.width, params.height);
            baseLayout.addView(view, newParams);
        }
    }

    // Override the dispatchKeyEvent method to redirect events to Boat
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (boatInterface.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    // Override the dispatchGenericMotionEvent method to redirect events to Boat
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (boatInterface.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    // 获取指针坐标
    public int[] getPointer() {
        return H2CO3LauncherLib.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        H2CO3LauncherLib.setKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        H2CO3LauncherLib.setMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        H2CO3LauncherLib.setPointer(x, y);
    }

    public interface IBoat {

        void onActivityCreate(H2CO3LauncherActivity boatActivity);

        void setGrabCursor(boolean isGrabbed);

        void onStop();

        void onResume();

        void onPause();

        boolean dispatchKeyEvent(KeyEvent event);

        boolean dispatchGenericMotionEvent(MotionEvent event);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setOnSystemUiVisibilityChangeListener(onSystemUiVisibilityChangeListener);
            hideSystemUI(decorView);
        } else {
            decorView.setOnSystemUiVisibilityChangeListener(null);
            if (systemUiTimerTask != null) systemUiTimerTask.cancel();
        }
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

    private final View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener
            = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                if (systemUiTimerTask != null) systemUiTimerTask.cancel();
                systemUiTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> hideSystemUI(getWindow().getDecorView()));
                    }
                };
                timer.schedule(systemUiTimerTask, SYSTEM_UI_HIDE_DELAY_MS);
            }
        }
    };

    private void hideSystemUI (View decorView){
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void setCursorMode(int mode) {
        h2co3LauncherCallback.onCursorModeChange(mode);
    }

    public static void onExit(Context ctx, int code) {
        ((H2CO3LauncherActivity) ctx).h2co3LauncherCallback.onExit(code);
    }

}