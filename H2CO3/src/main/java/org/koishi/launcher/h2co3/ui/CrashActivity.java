package org.koishi.launcher.h2co3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class CrashActivity extends H2CO3Activity {

    public TextView crash;
    public Button restart;
    public CaocConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        H2CO3ToolBar toolbar = findViewById(org.koishi.launcher.h2co3.resources.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.crash_title));
        crash = findViewById(R.id.crash);
        restart = findViewById(R.id.restart);
        restart.setOnClickListener(rv -> restart());
        initData();

    }

    private void initData() {
        String allString = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());// 获取所有的信息
        config = CustomActivityOnCrash.getConfigFromIntent(getIntent());//获得配置信息,比如设置的程序崩溃显示的页面和重新启动显示的页面等等信息
        crash.setText(allString);

        Thread.setDefaultUncaughtExceptionHandler((p1, p2) -> {


            Writer i = new StringWriter();
            p2.printStackTrace(new PrintWriter(i));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            p2.printStackTrace(new PrintStream(baos));
            byte[] bug = baos.toByteArray();

            try {
                FileOutputStream f = new FileOutputStream(H2CO3Tools.PUBLIC_FILE_PATH + "/log.txt");

                f.write(i.toString().getBytes());


                f.write(bug);


                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void oc(View v) {
        if (config != null)
            CustomActivityOnCrash.closeApplication(CrashActivity.this, config);
    }

    public void restart() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
        this.finish();
    }
}