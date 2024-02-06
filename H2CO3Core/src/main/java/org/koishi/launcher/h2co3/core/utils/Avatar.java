package org.koishi.launcher.h2co3.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class Avatar {

    static final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public static void setAvatar(String texture, ImageView face) {
        if (texture == null || face == null) {
            return;
        }
        face.post(() -> {
            Bitmap skin = stringToBitmap(texture);
            if (skin == null) {
                return;
            }
            Bitmap faceBitmap = Bitmap.createBitmap(skin, 8, 8, 8, 8);
            Matrix matrix = new Matrix();
            float scale = face.getWidth() / 8f;
            matrix.postScale(scale, scale);
            Bitmap newBitmap = Bitmap.createBitmap(faceBitmap, 0, 0, 8, 8, matrix, false);
            handler.post(() -> {
                face.setImageBitmap(newBitmap);
            });
        });
    }

    public static Bitmap stringToBitmap(String string) {
        if (string == null) {
            return null;
        }
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap getBitmapFromRes(Context context, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        options.inTargetDensity = value.density;
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), id, options);
    }
}