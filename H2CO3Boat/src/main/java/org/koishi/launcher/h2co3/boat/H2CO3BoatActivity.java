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
import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Activity;

import java.util.Timer;
import java.util.Vector;

public class H2CO3BoatActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    public static IH2CO3 h2co3Interface;

    static {
        System.loadLibrary("h2co3_boat");
    }

    public final float scaleFactor = 1.0F;
    public TextureView mainTextureView;
    public H2CO3Callback h2co3Callback;
    public Timer timer;
    public RelativeLayout base;
    public int initialX;
    public int initialY;
    public int baseX;
    public int baseY;
    int output = 0;

    public static native void setH2CO3NativeWindow(Surface surface);

    public static void onExit(Context ctx, int code) {
        ((H2CO3BoatActivity) ctx).h2co3Callback.onExit(code);
    }

    public static void sendKeyPress(int keyCode, boolean status) {
        //sendKeyPress(keyCode, 0, modifiers, status);
        H2CO3BoatLib.setKey(keyCode, 0, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode, int keyChar, boolean status) {
        //sendKeyPress(keyCode, 0, modifiers, status);
        H2CO3BoatLib.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode, char keyChar, int scancode, int modifiers, boolean status) {
        H2CO3BoatLib.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode) {
        H2CO3BoatLib.setKey(keyCode, 0, true);
        H2CO3BoatLib.setKey(keyCode, 0, false);
        System.out.print(keyCode);
    }

    public static void sendMouseButton(int button, boolean status) {
        H2CO3BoatLib.setMouseButton(button, status);
    }

    public void init() {
        nOnCreate();
        timer = new Timer();
        mainTextureView = findViewById(R.id.main_surface);
        mainTextureView.setSurfaceTextureListener(this);
    }

    public native void nOnCreate();

    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        System.out.println("SurfaceTexture is available!");
        h2co3Callback.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        h2co3Callback.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

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

    public void startGame(final String javaPath, final String home, final boolean highVersion, final Vector<String> args, String renderer, String gameDir) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> LoadMe.launchMinecraft(handler, H2CO3BoatActivity.this, javaPath, home, highVersion, args, renderer, gameDir, new H2CO3Callback() {
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
        })).start();
    }

    public void setCursorMode(int mode) {
        h2co3Callback.onCursorModeChange(mode);
    }

    public void setH2CO3Callback(H2CO3Callback callback) {
        this.h2co3Callback = callback;
    }

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> h2co3Callback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
    }

    public void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> h2co3Interface.setGrabCursor(isGrabbed));
    }

    // Override addContentView method to dynamically add views
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (params instanceof RelativeLayout.LayoutParams) {
            base.addView(view, params);
        } else {
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(params.width, params.height);
            base.addView(view, newParams);
        }
    }

    // Override the dispatchGenericMotionEvent method to redirect events to H2CO3
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (h2co3Interface.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    public int[] getPointer() {
        return H2CO3BoatLib.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        H2CO3BoatLib.setKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        H2CO3BoatLib.setMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        H2CO3BoatLib.setPointer(x, y);
    }

    public interface IH2CO3 {

        void onActivityCreate(H2CO3BoatActivity h2co3Activity);

        void setGrabCursor(boolean isGrabbed);

        void onStop();

        void onResume();

        void onPause();

        boolean dispatchGenericMotionEvent(MotionEvent event);
    }
}


