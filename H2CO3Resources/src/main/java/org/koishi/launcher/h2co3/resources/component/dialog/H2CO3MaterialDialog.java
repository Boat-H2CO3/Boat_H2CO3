package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.resources.R;

public class H2CO3MaterialDialog extends MaterialAlertDialogBuilder {

    AlertDialog dialog;
    public H2CO3MaterialDialog(Context context) {
        super(context, R.style.AppTheme_MaterialAlertDialog);
        dialog = create();
    }
    public AlertDialog show() {
        dialog.show();
        return dialog;
    }
    public AlertDialog cancel() {
        dialog.cancel();
        return dialog;
    }
    public void dismiss() {
        this.create().dismiss();
    }
}