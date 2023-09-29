package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Activity;

public class WelcomeActivity extends H2CO3Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
