package org.koishi.launcher.h2co3.core.color;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

public class ThemeColorUtils {
    public static int getColorFromTheme(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(attr, typedValue, true);
        if (resolved) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return typedValue.data;
            } else if (typedValue.type == TypedValue.TYPE_STRING) {
                return Color.parseColor(typedValue.string.toString());
            }
        }
        return Color.RED; // 默认使用红色
    }


}
