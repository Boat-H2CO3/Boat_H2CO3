package org.koishi.launcher.h2co3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3MessageDialog;

@Keep
public class ExitActivity extends H2CO3Activity {

    private static final String EXTRA_CODE = "code";

    public static void showExitMessage(Context ctx, int code) {
        Intent i = new Intent(ctx, ExitActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(EXTRA_CODE, code);
        ctx.startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int code = getIntent().getIntExtra(EXTRA_CODE, -1);

        H2CO3MessageDialog exitDialog = (H2CO3MessageDialog) new H2CO3MessageDialog(this)
                .setMessage("Minecraft exited with code:" + code)
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setOnDismissListener(dialog -> {
                    finish();
                    startActivity(new Intent(this, H2CO3MainActivity.class));
                });
        exitDialog.show();
    }
}