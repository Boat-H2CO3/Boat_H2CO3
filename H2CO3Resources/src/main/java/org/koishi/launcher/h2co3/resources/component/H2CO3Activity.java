package org.koishi.launcher.h2co3.resources.component;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import rikka.material.app.MaterialActivity;

public class H2CO3Activity extends MaterialActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UltimateBarX.statusBar(this)
                .fitWindow(true)
                .apply();
    }
}
