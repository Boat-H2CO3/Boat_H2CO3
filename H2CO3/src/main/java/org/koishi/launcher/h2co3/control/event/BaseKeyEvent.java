package org.koishi.launcher.h2co3.control.event;

import org.jetbrains.annotations.NotNull;

public class BaseKeyEvent {
    private final String tag;
    private final boolean pressed;
    private final int type;
    private final int[] mPointer;
    private final String keyName;
    private String chars;

    public BaseKeyEvent(String tag, String keyName, boolean pressed, int type, int[] mPointer) {
        this.tag = tag;
        this.pressed = pressed;
        this.keyName = keyName;
        this.type = type;
        this.mPointer = mPointer;
    }

    public String getTag() {
        return tag;
    }


    public boolean isPressed() {
        return pressed;
    }

    public int getType() {
        return type;
    }

    public String getKeyName() {
        return keyName;
    }

    public int[] getPointer() {
        return mPointer;
    }

    public String getChars() {
        return chars;
    }

    public BaseKeyEvent setChars(String str) {
        this.chars = str;
        return this;
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("BaseKeyEvent { tag = \"%s\", pressed = %s, type = %s, pointer = %s }", this.tag, this.pressed, this.type, "[0]: " + this.mPointer[0] + "[1]: " + this.mPointer[1]);
    }
}
