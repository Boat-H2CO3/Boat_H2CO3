package org.koishi.launcher.h2co3.dialog;

import android.graphics.drawable.Drawable;

public interface ListItemImageValue<T> {
    /**
     * Get String value of an Object for using in a list
     *
     * @param obj Current Object for getting first String value of it
     * @return String value
     */
    String getTitle(T obj);

    /**
     * Get Drawable value of an Object for use in a list item
     *
     * @param obj Current Object for getting second String value of it
     * @return Drawable
     */
    Drawable getImage(T obj);
}
