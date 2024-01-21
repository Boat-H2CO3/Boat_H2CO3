package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import org.jetbrains.annotations.NotNull;

public class H2CO3Spinner extends AppCompatSpinner {
    public H2CO3Spinner(@NonNull @NotNull Context context) {
        super(context);
    }

    public H2CO3Spinner(@NonNull @NotNull Context context, int mode) {
        super(context, mode);
    }

    public H2CO3Spinner(@NonNull @NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public H2CO3Spinner(@NonNull @NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public H2CO3Spinner(@NonNull @NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public H2CO3Spinner(@NonNull @NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }
}