package org.koishi.launcher.h2co3.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.FileUtils;
import org.koishi.launcher.h2co3.core.utils.LocaleUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.core.utils.cainiaohh.CHTools;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends H2CO3Activity {

    private boolean boat = false;
    private boolean h2co3_app = false;
    private boolean java8 = false;
    private boolean java17 = false;

    private H2CO3MessageDialog permissionDialog;
    private ProgressBar boatProgress;
    private ProgressBar h2co3AppProgress;
    private ProgressBar java8Progress;
    private ProgressBar java17Progress;
    private ImageView boatState;
    private ImageView h2co3AppState;
    private ImageView java8State;
    private ImageView java17State;
    private boolean installing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkPermission();
    }

    private void init() {
        CHTools.loadPaths(this);
        installRuntime();
    }

    private void installRuntime() {
        H2CO3CustomViewDialog installDialog = new H2CO3CustomViewDialog(this);
        installDialog.setCustomView(R.layout.custom_dialog_install_runtime);
        installDialog.setCancelable(false);
        installDialog.setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_install_runtime));
        installDialog.show();
        boatProgress = installDialog.findViewById(R.id.boat_progress);
        h2co3AppProgress = installDialog.findViewById(R.id.h2co3_app_progress);
        java8Progress = installDialog.findViewById(R.id.java8_progress);
        java17Progress = installDialog.findViewById(R.id.java17_progress);

        boatState = installDialog.findViewById(R.id.boat_state);
        h2co3AppState = installDialog.findViewById(R.id.h2co3_app_state);
        java8State = installDialog.findViewById(R.id.java8_state);
        java17State = installDialog.findViewById(R.id.java17_state);

        initState();

        refreshDrawables();

        check();
        install();
    }

    public void enterLauncher() {
        Intent intent = new Intent(this, H2CO3MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void checkPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        init();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            XXPermissions.startPermissionActivity(WelcomeActivity.this, permissions);
                        } else {
                            showDialog(permissions);
                        }
                    }
                });
    }

    private void showDialog(List<String> permissions) {
        permissionDialog = new H2CO3MessageDialog(WelcomeActivity.this);
        permissionDialog.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_warn));
        permissionDialog.setMessage(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.welcome_permission_hint));
        permissionDialog.setNeutralButton(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialog, which) -> {
            XXPermissions.startPermissionActivity(WelcomeActivity.this, permissions, XXPermissions.REQUEST_CODE);
            dismissDialog();
        });
        permissionDialog.setNegativeButton(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), (dialog, which) -> {
            finish();
        });
        permissionDialog.setCancelable(false);
        permissionDialog.show();
    }

    private void dismissDialog() {
        permissionDialog.dismiss();
    }

    private void initState() {
        try {
            boat = RuntimeUtils.isLatest(CHTools.BOAT_LIBRARY_DIR, "/assets/app_runtime/boat");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java8 = RuntimeUtils.isLatest(CHTools.JAVA_8_PATH, "/assets/app_runtime/jre_8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            java17 = RuntimeUtils.isLatest(CHTools.JAVA_17_PATH, "/assets/app_runtime/jre_17");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            h2co3_app = RuntimeUtils.isLatest(CHTools.H2CO3_LIBRARY_DIR, "/assets/h2co3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLatest(String dir, String path) {
        File file = new File(dir, path);
        return file.exists();
    }

    private void refreshDrawables() {
        Drawable stateUpdate = ContextCompat.getDrawable(this, org.koishi.launcher.h2co3.resources.R.drawable.ic_update);
        Drawable stateDone = ContextCompat.getDrawable(this, org.koishi.launcher.h2co3.resources.R.drawable.ic_done);

        boatState.setBackground(boat ? stateDone : stateUpdate);
        h2co3AppState.setBackground(h2co3_app ? stateDone : stateUpdate);
        java8State.setBackground(java8 ? stateDone : stateUpdate);
        java17State.setBackground(java17 ? stateDone : stateUpdate);
    }

    private boolean isLatest() {
        return boat && java8 && java17 && h2co3_app;
    }

    private void check() {
        if (isLatest()) {
            enterLauncher();
        }
    }

    private void install() {
        if (installing)
            return;

        installing = true;
        if (!boat) {
            boatState.setVisibility(View.GONE);
            boatProgress.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    RuntimeUtils.install(WelcomeActivity.this, CHTools.BOAT_LIBRARY_DIR, "app_runtime/boat");
                    boat = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(() -> {
                    boatState.setVisibility(View.VISIBLE);
                    boatProgress.setVisibility(View.GONE);
                    refreshDrawables();
                    check();
                });
            }).start();
        }
        if (!h2co3_app) {
            h2co3AppState.setVisibility(View.GONE);
            h2co3AppProgress.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    RuntimeUtils.install(WelcomeActivity.this, CHTools.H2CO3_LIBRARY_DIR, "h2co3");
                    h2co3_app = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(() -> {
                    h2co3AppState.setVisibility(View.VISIBLE);
                    h2co3AppProgress.setVisibility(View.GONE);
                    refreshDrawables();
                    check();
                });
            }).start();
        }
        if (!java8) {
            java8State.setVisibility(View.GONE);
            java8Progress.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(WelcomeActivity.this, CHTools.JAVA_8_PATH, "app_runtime/jre_8");
                    java8 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(() -> {
                    java8State.setVisibility(View.VISIBLE);
                    java8Progress.setVisibility(View.GONE);
                    refreshDrawables();
                    check();
                });
            }).start();
        }
        if (!java17) {
            java17State.setVisibility(View.GONE);
            java17Progress.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    RuntimeUtils.installJava(WelcomeActivity.this, CHTools.JAVA_17_PATH, "app_runtime/jre_17");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileUtils.writeText(new File(CHTools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileUtils.writeText(new File(CHTools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java17 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(() -> {
                    java17State.setVisibility(View.VISIBLE);
                    java17Progress.setVisibility(View.GONE);
                    refreshDrawables();
                    check();
                });
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