package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import timber.log.Timber;

public class LogActivity extends H2CO3Activity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logTextView = findViewById(R.id.logTextView);

        // 设置Timber的DebugTree，以便将日志输出到Logcat和LogActivity
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                super.log(priority, tag, message, t);
                // 将日志添加到LogActivity的TextView中
                appendLogToTextView(message);
            }
        });
    }

    private void appendLogToTextView(final String logMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextView.append(logMessage + "\n");
            }
        });
    }
}