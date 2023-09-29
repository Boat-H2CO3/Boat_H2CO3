package org.koishi.launcher.h2co3.dialog;

public interface ListItemValues<T> {
    /**
     * Get String value of an Object for use in the first value of a list item
     *
     * @param obj Current Object for getting first String value of it
     * @return String value
     */
    String getValue1(T obj);

    /**
     * Get String value of an Object for use in the second value of a list item
     *
     * @param obj Current Object for getting second String value of it
     * @return String value
     */
    String getValue2(T obj);
}
