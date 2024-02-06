package org.koishi.launcher.h2co3.resources.component.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.R;

import java.io.File;
import java.io.IOException;

import rikka.material.app.MaterialActivity;

public class H2CO3Activity extends MaterialActivity {
    public static void clearCacheFiles(Context context) {

    }

    public static void clearWebViewCache(Context context) throws IOException {
        String WEB_VIEW_CACHE_DIR = context.getDir("webview", 0).getAbsolutePath();
        FileTools.deleteDirectory(new File(WEB_VIEW_CACHE_DIR));
        CookieManager.getInstance().removeAllCookies(null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        H2CO3Tools.loadPaths(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //这里做你想做的事
            boolean spIsAuth = H2CO3Tools.getH2CO3Value("enable_monet", true, Boolean.class);
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
