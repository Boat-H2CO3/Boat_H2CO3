package org.koishi.launcher.h2co3.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class H2CO3Loader {

    private static final int HEAD_SIZE = 5000;
    private static final int HEAD_LEFT = 7;
    private static final int HEAD_TOP = 8;
    private static final int HEAD_RIGHT = 17;
    private static final int HEAD_BOTTOM = 16;

    private static final RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public static Drawable getHeadDrawable(Context context, String texture) {
        if (context == null || texture == null) {
            showErrorToast("Error");
            return null;
        }

        try {
            byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
            Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (skinBitmap != null) {
                Bitmap headBitmap = cropHeadFromSkin(skinBitmap);
                return new BitmapDrawable(context.getResources(), headBitmap);
            } else {
                showErrorToast("Error");
                return null;
            }
        } catch (Exception | OutOfMemoryError e) {
            showErrorToast(String.valueOf(e));
            return null;
        }
    }

    public static void getHead(Context context, String texture, ImageView imageView) {
        if (context == null || texture == null || imageView == null) {
            showErrorToast("Error");
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
            Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (skinBitmap != null) {
                Bitmap headBitmap = cropHeadFromSkin(skinBitmap);
                Glide.with(context)
                        .load(headBitmap)
                        .apply(requestOptions)
                        .into(imageView);
            } else {
                showErrorToast("Error");
            }
        } catch (Exception | OutOfMemoryError e) {
            showErrorToast(String.valueOf(e));
        }
    }

    public static Bitmap parseHeadTexture(String texture) {
        try {
            byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
            Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            return cropHeadFromSkin(skinBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap cropHeadFromSkin(Bitmap skinBitmap) {
        Bitmap headBitmap = Bitmap.createBitmap(HEAD_SIZE, HEAD_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(headBitmap);
        Rect srcRect = new Rect(HEAD_LEFT, HEAD_TOP, HEAD_RIGHT, HEAD_BOTTOM);
        Rect dstRect = new Rect(0, 0, HEAD_SIZE, HEAD_SIZE);
        canvas.drawBitmap(skinBitmap, srcRect, dstRect, null);
        return headBitmap;
    }

    private static void showErrorToast(String errorMessage) {
        Log.d("Error", errorMessage);
    }
    public static void setDir(String dir) {
        H2CO3Game.setGameDirectory(dir);
        H2CO3Game.setGameAssets(dir + "/assets/virtual/legacy");
        H2CO3Game.setGameAssetsRoot(dir + "/assets");
        H2CO3Game.setGameCurrentVersion(dir + "/versions");
    }
}