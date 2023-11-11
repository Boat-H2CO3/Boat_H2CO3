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
import org.koishi.launcher.h2co3.core.utils.FileUtils;
import org.koishi.launcher.h2co3.core.utils.LocaleUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends H2CO3Activity {

    public LinearLayout splash;
    public TextView splashCheck;

    private boolean boat = false;
    private boolean h2co3_app = false;
    private boolean java8 = false;
    private boolean java17 = false;
    private boolean installing = false;
    private H2CO3MessageDialog permissionDialog;
    private AlertDialog permissionDialogAlert;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.Theme_Boat_H2O2_Custom_GREEN);
        setContentView(R.layout.activity_splash);
        splash = findViewById(R.id.splash_view);
        splashCheck = findViewById(R.id.splash_check);
        checkPermission();
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
        startActivity(new Intent(this, H2CO3MainActivity.class));
        finish();
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
        permissionDialog = new H2CO3MessageDialog(SplashActivity.this);
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
        return boat && java8 && java17 && h2co3_app;
    }

    private void check() {
        if (isLatest()) {
            enterLauncher();
        }
    }

    private void initState() {
        try {
            boat = RuntimeUtils.isLatest(H2CO3Tools.BOAT_LIBRARY_DIR, "/assets/app_runtime/boat");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_8_PATH, "/assets/app_runtime/jre_8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(H2CO3Tools.JAVA_17_PATH, "/assets/app_runtime/jre_17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            h2co3_app = RuntimeUtils.isLatest(H2CO3Tools.H2CO3_LIBRARY_DIR, "/assets/h2co3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void install() {
        if (installing) {
            return;
        }

        installing = true;
        if (!boat) {
            new Thread(() -> {
                try {
                    RuntimeUtils.install(SplashActivity.this, H2CO3Tools.BOAT_LIBRARY_DIR, "app_runtime/boat");
                    boat = true;
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
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_8_PATH, "app_runtime/jre_8");
                    java8 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(this::check);
            }).start();
        }
        if (!java17) {
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(SplashActivity.this, H2CO3Tools.JAVA_17_PATH, "app_runtime/jre_17");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileUtils.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileUtils.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java17 = true;
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
    }
}