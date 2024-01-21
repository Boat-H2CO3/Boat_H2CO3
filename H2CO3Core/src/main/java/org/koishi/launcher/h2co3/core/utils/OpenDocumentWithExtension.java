package org.koishi.launcher.h2co3.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OpenDocumentWithExtension extends ActivityResultContract<Object, Uri> {
    private final String extension;

    public OpenDocumentWithExtension(String extension) {
        this.extension = extension;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, @NonNull Object input) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(getMimeType());
        return intent;
    }

    @Nullable
    @Override
    public final SynchronousResult<Uri> getSynchronousResult(@NonNull Context context,
                                                             @NonNull Object input) {
        return null;
    }

    @Nullable
    @Override
    public final Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            return null;
        }
        return intent.getData();
    }

    private String getMimeType() {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType != null ? mimeType : "*/*";
    }
}