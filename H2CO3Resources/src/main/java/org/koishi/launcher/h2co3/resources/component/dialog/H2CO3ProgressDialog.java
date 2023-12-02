package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class H2CO3ProgressDialog {
    private final MaterialAlertDialogBuilder builder;
    private final AlertDialog builderAlert;
    private final ProgressBar progressBar;

    public H2CO3ProgressDialog(Context context) {
        // 创建一个LinearLayout作为根布局
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(36, 36, 36, 36);

        // 创建一个ProgressBar并设置布局参数
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT );
        progressBar.setLayoutParams(params);

        // 将ProgressBar和TextView添加到LinearLayout中
        layout.addView(progressBar);

        // 创建MaterialAlertDialogBuilder并设置自定义视图
        builder = new MaterialAlertDialogBuilder(context);
        builderAlert = builder.create();
        builderAlert.setView(layout);
    }

    public void showWithProgress() {
        builderAlert.show();
    }

    public void dismiss() {
        builderAlert.dismiss();
    }

    public void setCancelable(boolean cancelable) {
        builderAlert.setCancelable(cancelable);
    }
}