package org.koishi.launcher.h2co3.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.H2CO3Auth;
import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.utils.LocaleUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends H2CO3Activity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    public LinearLayout splash;
    public TextView splashCheck;
    private boolean h2co3Launcher = false;
    private boolean h2co3_app = false;
    private boolean java8 = false;
    private boolean java11 = false;
    private boolean java17 = false;
    private boolean java21 = false;
    private boolean installing = false;
    private AlertDialog permissionDialogAlert;
    private boolean hasJumped = false;
    private boolean hasEnteredLauncher = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash = findViewById(R.id.splash_view);
        splashCheck = findViewById(R.id.splash_check);
        boolean isFirstLaunch = H2CO3Tools.getH2CO3Value("isFirstLaunch", true, Boolean.class);

        if (isFirstLaunch) {
            H2CO3Auth.resetUserState();
            H2CO3GameHelper.setDir(H2CO3Tools.MINECRAFT_DIR);
            H2CO3Tools.setH2CO3Value("isFirstLaunch", false);
            checkPermission();
        } else {
            checkPermission();
        }
    }

    public void start() {
        handler.postDelayed(this::startApp, 1000);
    }

    public void startApp() {
        initState();
        check();
        install();
    }

    private void enterLauncher() {

        if (!hasEnteredLauncher) {
            hasEnteredLauncher = true;
            Intent intent = new Intent(SplashActivity.this, H2CO3MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        start();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            XXPermissions.startPermissionActivity(SplashActivity.this, permissions);
                        } else {
                            showDialog(permissions);
                        }
                    }
                });
    }

    private void showDialog(List<String> permissions) {
        H2CO3MessageDialog permissionDialog = new H2CO3MessageDialog(SplashActivity.this);
        permissionDialog.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_warn));
        permissionDialog.setMessage(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.welcome_permission_hint));
        permissionDialog.setNeutralButton(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialog, which) -> {
            XXPermissions.startPermissionActivity(SplashActivity.this, permissions, XXPermissions.REQUEST_CODE);
            dismissDialog();
        });
        permissionDialog.setNegativeButton(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), (dialog, which) -> finish());
        permissionDialogAlert = permissionDialog.create();
        permissionDialogAlert.setCancelable(false);
        permissionDialogAlert.show();
    }

    private void dismissDialog() {
        permissionDialogAlert.dismiss();
    }

    private boolean isLatest() {
        return h2co3Launcher && h2co3_app && java8 && java11 && java17 && java21;
    }

    private void check() {
        if (isLatest()) {
            enterLauncher();
        }
    }

    private void initState() {
        try {
            h2co3Launcher = RuntimeUtils.isLatest(H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "/assets/app_runtime/h2co3Launcher");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            h2co3_app = RuntimeUtils.isLatest(H2CO3Tools.H2CO3_LIBRARY_DIR, "/assets/h2co3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/java/jre8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java11 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_11_PATH, "/assets/app_runtime/java/jre11");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/java/jre17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java21 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_21_PATH, "/assets/app_runtime/java/jre21");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void install() {
        if (installing) {
            return;
        }

        installing = true;
        if (!h2co3Launcher) {
            new Thread(() -> {
                try {
                    RuntimeUtils.install(SplashActivity.this, H2CO3Tools.H2CO3LAUNCHER_LIBRARY_DIR, "app_runtime/h2co3Launcher");
                    h2co3Launcher = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!h2co3_app) {
            new Thread(() -> {
                try {
                    RuntimeUtils.install(SplashActivity.this, H2CO3Tools.H2CO3_LIBRARY_DIR, "h2co3");
                    h2co3_app = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java8) {
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_8_PATH, "app_runtime/java/jre8");
                    java8 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java11) {
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_11_PATH, "app_runtime/java/jre11");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_11_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_11_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java11 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java17) {
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_17_PATH, "app_runtime/java/jre17");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java17 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java21) {
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_21_PATH, "app_runtime/java/jre21");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_21_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileTools.writeText(new File(H2CO3Tools.JAVA_21_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java21 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            checkPermission();
        }
        if (!hasJumped) {
            hasJumped = true;
            Intent intent = new Intent(SplashActivity.this, H2CO3MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}