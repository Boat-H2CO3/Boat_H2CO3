package org.koishi.launcher.h2co3.launcher;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridgeCallBack;
import org.koishi.launcher.h2co3.resources.component.ControlLayout;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class H2CO3LauncherActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    private static final String TAG = "H2CO3LauncherActivity";
    private static final int SYSTEM_UI_HIDE_DELAY_MS = 3000;
    private static final int THREAD_POOL_SIZE = 1;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public ControlLayout mainTextureView;
    public RelativeLayout baseLayout;
    public H2CO3LauncherBridgeCallBack h2co3LauncherCallback;
    private Timer timer;
    private int output = 0;
    private TimerTask systemUiTimerTask;
    public static IH2CO3Launcher h2co3LauncherInterface;

    private final View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                if (systemUiTimerTask != null) {
                    systemUiTimerTask.cancel();
                }
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

    static {
        System.loadLibrary("h2co3Launcher");
    }

    public void init() {
        timer = new Timer();
        mainTextureView.setSurfaceTextureListener(this);
    }

    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SurfaceTexture is available!");
        h2co3LauncherCallback.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        h2co3LauncherCallback.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

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

    public void setH2CO3LauncherCallback(H2CO3LauncherBridgeCallBack callback) {
        this.h2co3LauncherCallback = callback;
    }

    public abstract void onClick(View view);

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            h2co3LauncherCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight());
        }
    }

    public void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> h2co3LauncherInterface.setGrabCursor(isGrabbed));
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (params instanceof RelativeLayout.LayoutParams) {
            baseLayout.addView(view, params);
        } else {
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(params.width, params.height);
            baseLayout.addView(view, newParams);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (h2co3LauncherInterface.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (h2co3LauncherInterface.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    public int[] getPointer() {
        return H2CO3LauncherBridge.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        H2CO3LauncherBridge.setKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        H2CO3LauncherBridge.setMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        H2CO3LauncherBridge.setPointer(x, y);
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
            if (systemUiTimerTask != null) {
                systemUiTimerTask.cancel();
            }
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

    private void hideSystemUI(View decorView) {
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

    public void exit(Context context, int code) {
    }

    public static void onExit(Context ctx, int code) {
        ((H2CO3LauncherActivity) ctx).h2co3LauncherCallback.onExit(code);
    }

    public interface IH2CO3Launcher {

        void onActivityCreate(H2CO3LauncherActivity H2CO3LauncherActivity);

        void setGrabCursor(boolean isGrabbed);

        void onStop();

        void onResume();

        void onPause();

        boolean dispatchKeyEvent(KeyEvent event);

        boolean dispatchGenericMotionEvent(MotionEvent event);
    }
}