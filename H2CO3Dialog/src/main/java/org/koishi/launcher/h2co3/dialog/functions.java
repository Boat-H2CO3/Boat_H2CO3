package org.koishi.launcher.h2co3.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class functions {
    public static void SetDialogSize(@NonNull Context context, Dialog dialog, int maxWidth) {
        Configuration configuration = context.getResources().getConfiguration();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (configuration.screenWidthDp > maxWidth)
            width = dpToPixels(context, maxWidth);

        dialog.getWindow().setLayout(width, height);

    }

    public static int dpToPixels(@NonNull Context context, float dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
