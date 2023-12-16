package org.koishi.launcher.h2co3.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.boat.H2CO3BoatActivity;
import org.koishi.launcher.h2co3.boat.H2CO3BoatLib;
import org.koishi.launcher.h2co3.boat.function.H2CO3BoatCallback;
import org.koishi.launcher.h2co3.core.H2CO3Game;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.MinecraftVersion;
import org.koishi.launcher.h2co3.utils.launch.MCOptionUtils;
import org.koishi.launcher.h2co3.utils.launch.boat.VirGLService;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class H2CO3BoatClientActivity extends H2CO3BoatActivity {

    private H2CO3Game gameLaunchSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.koishi.launcher.h2co3.boat.R.layout.overlay);
        mainTextureView = findViewById(org.koishi.launcher.h2co3.boat.R.id.main_game_render_view);
        initUI();

    }

    private void initUI() {
        handleCallback();
        init();
    }

    private void handleCallback() {
        setH2CO3BoatCallback(new H2CO3BoatCallback() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surface.setDefaultBufferSize((int) (width * scaleFactor), (int) (height * scaleFactor));
                MCOptionUtils.load(H2CO3Game.getGameDirectory());
                MCOptionUtils.set("overrideWidth", String.valueOf((int) (width * scaleFactor)));
                MCOptionUtils.set("overrideHeight", String.valueOf((int) (height * scaleFactor)));
                MCOptionUtils.set("fullscreen", "true");
                MCOptionUtils.save(H2CO3Game.getGameDirectory());
                new Thread(() -> {
                    Vector<String> args = H2CO3Game.getMcArgs(gameLaunchSetting, H2CO3BoatClientActivity.this, (int) (width * scaleFactor), (int) (height * scaleFactor));
                    runOnUiThread(() -> {
                        H2CO3BoatLib.setH2CO3BoatNativeWindow(new Surface(surface));
                        H2CO3BoatLib.setEventPipe();

                        String javaPath = H2CO3Game.getJavaPath();
                        String boatRenderer = H2CO3Tools.GL_GL114;

                        System.out.println(args);
                        MinecraftVersion mcVersion = MinecraftVersion.fromDirectory(new File(H2CO3Game.getGameCurrentVersion()));
                        boolean isHighVersion = mcVersion.minimumLauncherVersion >= 21;
                        startGame(javaPath,
                                H2CO3Tools.PUBLIC_FILE_PATH,
                                isHighVersion,
                                args,
                                boatRenderer,
                                H2CO3Game.getGameDirectory());
                    });
                }).start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onCursorModeChange(int mode) {
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
                Intent virGLService = new Intent(H2CO3BoatClientActivity.this, VirGLService.class);
                stopService(virGLService);
            }
        });
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
}