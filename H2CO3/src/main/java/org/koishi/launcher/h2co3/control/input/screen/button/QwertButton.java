package org.koishi.launcher.h2co3.control.input.screen.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class QwertButton extends BaseButton {

    private String char_none;
    private String char_shift;
    private String char_capslock;

    public QwertButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3.resources.R.styleable.QwertButton);
        setCharNone(array.getString(org.koishi.launcher.h2co3.resources.R.styleable.QwertButton_char_none));
        setCharShift(array.getString(org.koishi.launcher.h2co3.resources.R.styleable.QwertButton_char_shift));
        setCharCaps(array.getString(org.koishi.launcher.h2co3.resources.R.styleable.QwertButton_char_capslock));
        array.recycle();
    }

    public String getCharNone() {
        return char_none;
    }

    public void setCharNone(String charName) {
        char_none = charName;
    }

    public String getCharShift() {
        return char_shift;
    }

    public void setCharShift(String charName) {
        char_shift = charName;
    }

    public String getCharCaps() {
        return char_capslock;
    }

    public void setCharCaps(String charName) {
        char_capslock = charName;
    }
}
