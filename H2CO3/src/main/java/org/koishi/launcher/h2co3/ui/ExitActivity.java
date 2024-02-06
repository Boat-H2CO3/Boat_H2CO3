package org.koishi.launcher.h2co3.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

@Keep
public class ExitActivity extends H2CO3Activity {

    public static void showExitMessage(Context ctx, int code) {
        Intent i = new Intent(ctx, H2CO3MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

    @SuppressLint("StringFormatInvalid")
    //invalid on some translations but valid on most, cant fix that atm
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int code = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            code = extras.getInt("code", -1);
        }

        MaterialAlertDialogBuilder exitDialog = new H2CO3MessageDialog(this)
                .setMessage("Minecraft exited with code:" + code)
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setOnDismissListener(dialog -> {
                    ExitActivity.this.finish();
                    startActivity(new Intent(this, H2CO3MainActivity.class));
                });
        exitDialog.show();
    }

}
