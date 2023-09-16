package cosine.boat;

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

import java.util.Timer;
import java.util.Vector;

import cosine.boat.function.BoatCallback;
import cosine.boat.function.BoatLaunchCallback;
import rikka.material.app.MaterialActivity;

public class BoatActivity extends MaterialActivity implements TextureView.SurfaceTextureListener {

    public static IBoat boatInterface;

    static {
        System.loadLibrary("boat");
    }

    public final float scaleFactor = 1.0F;
    public TextureView mainTextureView;
    public BoatCallback boatCallback;
    public Timer timer;
    public RelativeLayout base;
    public int initialX;
    public int initialY;
    public int baseX;
    public int baseY;
    int output = 0;

    public static native void setBoatNativeWindow(Surface surface);

    public static void onExit(Context ctx, int code) {
        ((BoatActivity) ctx).boatCallback.onExit(code);
    }

    public static void sendKeyPress(int keyCode, boolean status) {
        //sendKeyPress(keyCode, 0, modifiers, status);
        BoatInput.setKey(keyCode, 0, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode, int keyChar, boolean status) {
        //sendKeyPress(keyCode, 0, modifiers, status);
        BoatInput.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode, char keyChar, int scancode, int modifiers, boolean status) {
        BoatInput.setKey(keyCode, keyChar, status);
        System.out.print(keyCode);
    }

    public static void sendKeyPress(int keyCode) {
        BoatInput.setKey(keyCode, 0, true);
        BoatInput.setKey(keyCode, 0, false);
        System.out.print(keyCode);
    }

    public static void sendMouseButton(int button, boolean status) {
        BoatInput.setMouseButton(button, status);
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
        boatCallback.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        boatCallback.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (output == 1) {
            boatCallback.onPicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    public void startGame(final String javaPath, final String home, final boolean highVersion, final Vector<String> args, String renderer, String gameDir) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> LoadMe.launchMinecraft(handler, BoatActivity.this, javaPath, home, highVersion, args, renderer, gameDir, new BoatLaunchCallback() {
            @Override
            public void onStart() {
                boatCallback.onStart();
            }

            @Override
            public void onError(Exception e) {
                boatCallback.onError(e);
            }
        })).start();
    }

    public void setCursorMode(int mode) {
        boatCallback.onCursorModeChange(mode);
    }

    public void setBoatCallback(BoatCallback callback) {
        this.boatCallback = callback;
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
            mainTextureView.post(() -> boatCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
    }

    public void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> boatInterface.setGrabCursor(isGrabbed));
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

    // Override the dispatchGenericMotionEvent method to redirect events to Boat
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (boatInterface.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    public int[] getPointer() {
        return BoatInput.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        BoatInput.setKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        BoatInput.setMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        BoatInput.setPointer(x, y);
    }

    public interface IBoat {

        void onActivityCreate(BoatActivity boatActivity);

        void setGrabCursor(boolean isGrabbed);

        void onStop();

        void onResume();

        void onPause();

        boolean dispatchGenericMotionEvent(MotionEvent event);
    }
}


