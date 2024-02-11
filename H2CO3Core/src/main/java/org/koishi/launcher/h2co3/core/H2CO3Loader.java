package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.showError;

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

import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;

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
            showError(context,"Error");
            return null;
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            if (headBitmap != null) {
                return new BitmapDrawable(context.getResources(), headBitmap);
            } else {
                showError(context,"Error");
                return null;
            }
        } catch (Exception | OutOfMemoryError e) {
            showError(context,String.valueOf(e));
            return null;
        }
    }

    public static void getHead(Context context, String texture, ImageView imageView) {
        if (context == null || texture == null || imageView == null) {
            showError(context,"Error");
            return;
        }

        try {
            Bitmap headBitmap = decodeAndCropHeadBitmap(texture);
            if (headBitmap != null) {
                Glide.with(context)
                        .load(headBitmap)
                        .apply(requestOptions)
                        .into(imageView);
            } else {
                showError(context,"Error");
            }
        } catch (Exception | OutOfMemoryError e) {
            showError(context,String.valueOf(e));
        }
    }

    public static Bitmap parseHeadTexture(String texture) {
        try {
            return decodeAndCropHeadBitmap(texture);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap decodeAndCropHeadBitmap(String texture) {
        byte[] decodedBytes = Base64.decode(texture, Base64.DEFAULT);
        Bitmap skinBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        if (skinBitmap != null) {
            return cropHeadFromSkin(skinBitmap);
        } else {
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
}