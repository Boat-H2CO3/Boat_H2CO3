package org.koishi.launcher.h2co3.resources.component.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import rikka.material.app.MaterialActivity;

public class H2CO3Activity extends MaterialActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        if (getWindow() != null) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
