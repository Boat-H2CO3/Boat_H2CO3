package org.koishi.launcher.h2co3.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.FileUtils;
import org.koishi.launcher.h2co3.core.utils.LocaleUtils;
import org.koishi.launcher.h2co3.core.utils.RuntimeUtils;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends H2CO3Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        H2CO3ToolBar toolBar = findViewById(org.koishi.launcher.h2co3.resources.R.id.toolbar);
        toolBar.setTitle(org.koishi.launcher.h2co3.resources.R.string.title_activity_login);
        checkPermission();
        initUI();
    }

    /***************登录部分*****************/

    private ImageButton mDropDown;
    private CircularRevealFrameLayout login_name_layout;
    private TextInputEditText login_name,login_password,login_api;
    private TextInputLayout login_password_layout,login_api_layout;
    private void initUI() {
        login_name = findViewById(R.id.login_name);
        login_password = findViewById(R.id.login_password);
        login_api = findViewById(R.id.login_api);
        login_name_layout = findViewById(R.id.login_name_layout);
        login_password_layout = findViewById(R.id.login_password_layout);
        login_api_layout = findViewById(R.id.login_api_layout);
        TabLayout tab = findViewById(R.id.login_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // 第一个选项卡被选中时的逻辑
                    login_name_layout.setVisibility(View.VISIBLE);
                    login_password_layout.setVisibility(View.GONE);
                    login_api_layout.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 1) {
                    // 第二个选项卡被选中时的逻辑
                    login_name_layout.setVisibility(View.GONE);
                    login_password_layout.setVisibility(View.GONE);
                    login_api_layout.setVisibility(View.GONE);
                }
                if (tab.getPosition() == 2) {
                    // 第三个选项卡被选中时的逻辑
                    login_name_layout.setVisibility(View.VISIBLE);
                    login_password_layout.setVisibility(View.VISIBLE);
                    login_api_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 选项卡被取消选中时的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 选项卡被重新选中时的逻辑
            }
        });
    }

    /***************运行库部分*****************/

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

    private void init() {
        H2CO3Tools.loadPaths(this);
        installRuntime();
    }

    H2CO3CustomViewDialog installDialog;
    AlertDialog dialog;
    private void installRuntime() {
        installDialog = new H2CO3CustomViewDialog(this);
        installDialog.setCustomView(R.layout.custom_dialog_install_runtime);
        installDialog.setCancelable(false);
        installDialog.setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_install_runtime));

        dialog = installDialog.create();
        dialog.show();

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
        dialog.cancel();
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
                            XXPermissions.startPermissionActivity(LoginActivity.this, permissions);
                        } else {
                            showDialog(permissions);
                        }
                    }
                });
    }

    private void showDialog(List<String> permissions) {
        permissionDialog = new H2CO3MessageDialog(LoginActivity.this);
        permissionDialog.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_warn));
        permissionDialog.setMessage(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.welcome_permission_hint));
        permissionDialog.setNeutralButton(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialog, which) -> {
            XXPermissions.startPermissionActivity(LoginActivity.this, permissions, XXPermissions.REQUEST_CODE);
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
                    RuntimeUtils.install(LoginActivity.this, H2CO3Tools.BOAT_LIBRARY_DIR, "app_runtime/boat");
                    boat = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoginActivity.this.runOnUiThread(() -> {
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
                    RuntimeUtils.install(LoginActivity.this, H2CO3Tools.H2CO3_LIBRARY_DIR, "h2co3");
                    h2co3_app = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoginActivity.this.runOnUiThread(() -> {
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
                    RuntimeUtils.installJava(LoginActivity.this, H2CO3Tools.JAVA_8_PATH, "app_runtime/jre_8");
                    java8 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoginActivity.this.runOnUiThread(() -> {
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
                    RuntimeUtils.installJava(LoginActivity.this, H2CO3Tools.JAVA_17_PATH, "app_runtime/jre_17");
                    if (!LocaleUtils.getSystemLocale().getDisplayName().equals(Locale.CHINA.getDisplayName())) {
                        FileUtils.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 1.1.1.1\n" + "nameserver 1.0.0.1");
                    } else {
                        FileUtils.writeText(new File(H2CO3Tools.JAVA_17_PATH + "/resolv.conf"), "nameserver 8.8.8.8\n" + "nameserver 8.8.4.4");
                    }
                    java17 = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LoginActivity.this.runOnUiThread(() -> {
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