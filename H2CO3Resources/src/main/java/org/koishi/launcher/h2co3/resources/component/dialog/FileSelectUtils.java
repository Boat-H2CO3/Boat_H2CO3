package org.koishi.launcher.h2co3.resources.component.dialog;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * 文件选择器
 */
public class FileSelectUtils {

    private static Callback callback = (requestCode, resultCode, data) -> {

    };

    public static Callback getCallback() {
        return callback;
    }

    public static void setCallback(Callback callback) {
        FileSelectUtils.callback = callback;
    }

    public static void startActivityForResult(Activity activity, int requestCode, Callback callback) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/x-xz");
        activity.startActivityForResult(intent, requestCode);
        FileSelectUtils.callback = callback;
    }

    public interface Callback {
        void onResult(int requestCode, int resultCode, @Nullable Intent data);
    }
}
