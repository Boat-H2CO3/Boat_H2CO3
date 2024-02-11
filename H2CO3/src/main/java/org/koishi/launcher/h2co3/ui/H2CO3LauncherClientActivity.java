package org.koishi.launcher.h2co3.ui;

import static org.koishi.launcher.h2co3.launcher.H2CO3LauncherHelper.launchMinecraft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.BuildConfig;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.controller.H2CO3VirtualController;
import org.koishi.launcher.h2co3.control.controller.HardwareController;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridgeCallBack;
import org.koishi.launcher.h2co3.core.game.MinecraftVersion;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.launcher.H2CO3LauncherActivity;
import org.koishi.launcher.h2co3.launcher.H2CO3LauncherHelper;
import org.koishi.launcher.h2co3.launcher.R;
import org.koishi.launcher.h2co3.resources.component.LogView;
import org.koishi.launcher.h2co3.utils.launch.MCOptionUtils;
import org.koishi.launcher.h2co3.utils.launch.boat.VirGLService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.logging.Level;

public class H2CO3LauncherClientActivity extends H2CO3LauncherActivity implements H2CO3ControlClient, TextureView.SurfaceTextureListener {

    private final static int CURSOR_SIZE = 16; //dp
    private final int[] grabbedPointer = new int[]{999, 89999};
    private MaterialAlertDialogBuilder dialog;
    private H2CO3LauncherBridge launcherLib;
    private boolean grabbed = false;
    private ImageView cursorIcon;
    private LogView mLogView;
    private int screenWidth;
    private int screenHeight;
    int scaleFactor = 1;
    public static H2CO3LauncherHelper.LogReceiver logReceiver;

    public static void attachControllerInterface() {
        H2CO3LauncherClientActivity.boatInterface = new H2CO3LauncherClientActivity.IBoat() {
            private H2CO3VirtualController virtualController;
            private HardwareController hardwareController;
            private Timer timer;

            @Override
            public void onActivityCreate(H2CO3LauncherActivity boatActivity) {
                virtualController = new H2CO3VirtualController((H2CO3ControlClient) boatActivity, KEYMAP_TO_X);
                hardwareController = new HardwareController((H2CO3ControlClient) boatActivity, KEYMAP_TO_X);
            }

            @Override
            public void setGrabCursor(boolean isGrabbed) {
                virtualController.setGrabCursor(isGrabbed);
                hardwareController.setGrabCursor(isGrabbed);
            }

            @Override
            public void onStop() {
                virtualController.onStop();
                hardwareController.onStop();
            }

            @Override
            public void onResume() {
                virtualController.onResumed();
                hardwareController.onResumed();
            }

            @Override
            public void onPause() {
                virtualController.onPaused();
                hardwareController.onPaused();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return hardwareController.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return hardwareController.dispatchMotionKeyEvent(event);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay);
        mainTextureView = findViewById(R.id.main_game_render_view);
        baseLayout = findViewById(R.id.main_base);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        cursorIcon = new ImageView(this);
        cursorIcon.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(this, CURSOR_SIZE), DisplayUtils.getPxFromDp(this, CURSOR_SIZE)));
        cursorIcon.setImageResource(org.koishi.launcher.h2co3.resources.R.drawable.cursor5);
        mLogView = new LogView(this);
        mLogView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        baseLayout.addView(mLogView);
        this.addView(cursorIcon);
        initUI();
        launcherLib = launchMinecraft(this,screenWidth,screenHeight);
        h2co3LauncherCallback = launcherLib.getCallback();
        h2co3LauncherCallback = new H2CO3LauncherBridgeCallBack() {
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
            public void onLog(String log) {
                if (log.contains("OR:") || log.contains("ERROR:") || log.contains("INTERNAL ERROR:")) {
                    return;
                }
                if (logReceiver == null) {
                    logReceiver = new H2CO3LauncherHelper.DefaultLogReceiver();
                }
                logReceiver.pushLog(log);


                if (BuildConfig.DEBUG) {
                    Log.d("H2CO3 Debug", log);
                }
                try {
                    if (firstLog) {
                        FileTools.writeText(new File(launcherLib.getLogPath()), log + "\n");
                        firstLog = false;
                    } else {
                        runOnUiThread(() -> mLogView.mTextView.setText(logReceiver.getLogs()));

                        FileTools.writeTextWithAppendMode(new File(launcherLib.getLogPath()), log + "\n");
                    }
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Can't log game log to target file", e.getMessage());
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onPicOutput() {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onExit(int code) {

            }

            @Override
            public void onHitResultTypeChange(int type) {

            }
        };
    }

    private void initUI() {
        init();
        boatInterface.onActivityCreate(this);
        dialog = new MaterialAlertDialogBuilder(H2CO3LauncherClientActivity.this);
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        Logging.LOG.log(Level.INFO, "surface ready, start jvm now!");
        launcherLib.setSurfaceDestroyed(false);
        int width = screenWidth;
        int height = screenHeight;
        configureSurfaceTexture(surfaceTexture,width,height);
        surfaceTexture.setDefaultBufferSize(width, height);
        launcherLib.execute(new Surface(surfaceTexture), h2co3LauncherCallback);
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        int width = screenWidth;
        int height = screenHeight;
        surfaceTexture.setDefaultBufferSize(width, height);
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        launcherLib.setSurfaceDestroyed(true);
        return false;
    }

    private int output = 0;

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
        if (output < 1) {
            output++;
        }
    }

    private boolean firstLog = true;

    private void configureSurfaceTexture(SurfaceTexture surface, int width, int height) {
        surface.setDefaultBufferSize(width * scaleFactor, height * scaleFactor);
        MCOptionUtils.saveOptions(H2CO3GameHelper.getGameDirectory());
        MCOptionUtils.setOption("overrideWidth", String.valueOf(width * scaleFactor));
        MCOptionUtils.setOption("overrideHeight", String.valueOf(height * scaleFactor));
        MCOptionUtils.setOption("fullscreen", "true");
        MCOptionUtils.saveOptions(H2CO3GameHelper.getGameDirectory());
    }

    private void startGame(CommandBuilder args) {
        String javaPath = H2CO3GameHelper.getJavaPath();
        String boatRenderer = H2CO3Tools.GL_GL114;

        MinecraftVersion mcVersion = MinecraftVersion.fromDirectory(new File(H2CO3GameHelper.getGameCurrentVersion()));
        boolean isHighVersion = mcVersion.minimumLauncherVersion >= 21;
    }

    private void stopVirGLService() {
        Intent virGLService = new Intent(H2CO3LauncherClientActivity.this, VirGLService.class);
        stopService(virGLService);
    }

    @Override
    public void onClick(View p1) {
        // TODO: Implement this method
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void exit(Context context, int code) {
        super.exit(context, code);
        ExitActivity.showExitMessage(context, code);
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode, 0, pressed);
    }

    @Override
    public void setPointerInc(int xInc, int yInc) {
        if (!grabbed) {
            int x, y;
            x = grabbedPointer[0] + xInc;
            y = grabbedPointer[1] + yInc;
            if (x >= 0 && x <= screenWidth)
                grabbedPointer[0] += xInc;
            if (y >= 0 && y <= screenHeight)
                grabbedPointer[1] += yInc;
            setPointer(grabbedPointer[0], grabbedPointer[1]);
            cursorIcon.setX(grabbedPointer[0]);
            cursorIcon.setY(grabbedPointer[1]);
        } else {
            setPointer(getPointer()[0] + xInc, getPointer()[1] + yInc);
        }
    }

    @Override
    public void setPointer(int x, int y) {
        super.setPointer(x, y);
        if (!grabbed) {
            cursorIcon.setX(x);
            cursorIcon.setY(y);
            grabbedPointer[0] = x;
            grabbedPointer[1] = y;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void addView(View v) {
        this.addContentView(v, v.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        if (str == null) return;
        for (int i = 0; i < str.length(); i++) {
            setKey(0, str.charAt(i), true);
            setKey(0, str.charAt(i), false);
        }
    }

    @Override
    public int[] getGrabbedPointer() {
        return this.grabbedPointer;
    }

    @Override
    public int[] getLoosenPointer() {
        return this.getPointer();
    }

    @Override
    public ViewGroup getViewsParent() {
        return (ViewGroup) findViewById(android.R.id.content).getRootView();
    }

    @Override
    public View getSurfaceLayerView() {
        return mainTextureView;
    }

    @Override
    public boolean isGrabbed() {
        return this.grabbed;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        super.setGrabCursor(isGrabbed);
        this.grabbed = isGrabbed;
        if (!isGrabbed) {
            setPointer(grabbedPointer[0], grabbedPointer[1]);
            cursorIcon.setVisibility(View.VISIBLE);
        } else if (cursorIcon.getVisibility() == View.VISIBLE) {
            cursorIcon.setVisibility(View.INVISIBLE);
        }
    }
}