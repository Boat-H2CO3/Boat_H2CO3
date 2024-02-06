package org.koishi.launcher.h2co3.core.login.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtils {

    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";

    public static int getPxFromDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static float getDpFromPx(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale);
    }

    public static int getPxFromSp(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                display.getRealMetrics(realDisplayMetrics);
                int realHeight = realDisplayMetrics.heightPixels;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                int displayHeight = displayMetrics.heightPixels;
                return realHeight - displayHeight > 0;
            }
        } else {
            Resources rs = context.getResources();
            int id = rs.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
            if (id > 0) {
                return rs.getBoolean(id);
            }
        }
        return false;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(NAV_BAR_HEIGHT_RES_NAME, "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    public static int[] getApplicationWindowSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public static int[] getDisplayWindowSize(Context context) {
        int[] applicationWindowSize = getApplicationWindowSize(context);
        int screenWidth = applicationWindowSize[0];
        int screenHeight = applicationWindowSize[1];
        int navigationBarHeight = checkDeviceHasNavigationBar(context) ? getNavigationBarHeight(context) : 0;
        return new int[]{screenWidth, screenHeight + navigationBarHeight};
    }
}