package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class H2CO3MaterialDialog extends MaterialAlertDialogBuilder {
    AlertDialog dialog;
    MaterialAlertDialogBuilder builder;

    public H2CO3MaterialDialog(@NonNull Context context) {
        super(context);
        this.builder = new MaterialAlertDialogBuilder((context));
    }

    public void createDialog() {
        builder.create().show();
    }

    public void dismissDialog() {
        builder.create().dismiss();
    }
}