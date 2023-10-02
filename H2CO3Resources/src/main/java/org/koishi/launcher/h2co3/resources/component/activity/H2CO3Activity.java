package org.koishi.launcher.h2co3.resources.component.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.utils.cainiaohh.CHTools;
import org.koishi.launcher.h2co3.resources.R;

import rikka.material.app.MaterialActivity;

public class H2CO3Activity extends MaterialActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CHTools.loadPaths(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //这里做你想做的事
            boolean spIsAuth = CHTools.getH2CO3Value("enable_monet", true);
            if (spIsAuth) {
                setTheme(R.style.Theme_H2CO3_DynamicColors);
            } else {
                setTheme(R.style.Theme_H2CO3);
            }
        } else {
            setTheme(R.style.Theme_H2CO3);
        }
    }

    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        if (getWindow() != null) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
