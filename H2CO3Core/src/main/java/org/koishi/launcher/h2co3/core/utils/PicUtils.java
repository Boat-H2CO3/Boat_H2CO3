package org.koishi.launcher.h2co3.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class PicUtils {

    // 高斯模糊
    public static Bitmap blur(Context context, int radius, final Bitmap bitmap) {
        RenderScript rs = RenderScript.create(context);
        Bitmap blurredBitmap = bitmap.copy(bitmap.getConfig(), true);

        Allocation input = Allocation.createFromBitmap(rs, blurredBitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setInput(input);
        blur.setRadius(radius);
        blur.forEach(output);

        output.copyTo(blurredBitmap);
        rs.destroy();

        return blurredBitmap;
    }
}