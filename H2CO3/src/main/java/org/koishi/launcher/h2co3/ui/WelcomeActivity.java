package org.koishi.launcher.h2co3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainerView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.cainiaohh.CHTools;
import org.koishi.launcher.h2co3.dialog.BasicDialog;
import org.koishi.launcher.h2co3.resources.component.H2CO3Activity;
import org.koishi.launcher.h2co3.ui.fragment.welcome.RuntimeFragment;
import org.koishi.launcher.h2co3.ui.fragment.welcome.WelcomeFragment;

import java.io.File;
import java.util.List;

public class WelcomeActivity extends H2CO3Activity {

    FragmentContainerView fragment;
    private WelcomeFragment welcomeFragment;
    private RuntimeFragment runtimeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        fragment = findViewById(R.id.fragment);
        checkPermission();
    }

    private void init() {
        CHTools.initPaths(this);
        Logging.start(new File(CHTools.LOG_DIR, "logs").toPath());
        initFragment();
        isFirstLaunch();
    }

    public void isFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences("launcher", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isFirstLaunch", true)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, welcomeFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, runtimeFragment).commit();
        }
    }

    private void initFragment() {
        welcomeFragment = new WelcomeFragment();
        runtimeFragment = new RuntimeFragment();
    }

    public void enterLauncher() {
        Intent intent = new Intent(this, H2CO3MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void checkPermission() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        init();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(WelcomeActivity.this, permissions);
                        } else {
                            BasicDialog dialog = new BasicDialog();
                            dialog.Builder(WelcomeActivity.this)
                                    .setTitle("Title")
                                    .setLeftButtonText("button1")
                                    .setRightButtonText("button2")
                                    .onButtonClick(() -> {
                                        // Do something
                                    })
                                    .show();
                        }
                    }
                });
    }
}
