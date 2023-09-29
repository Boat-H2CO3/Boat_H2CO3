package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.appbar.MaterialToolbar;

public class H2CO3ToolBar extends MaterialToolbar {

    public H2CO3ToolBar(Context context) {
        super(context);
    }

    public H2CO3ToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public H2CO3ToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        setTitleCentered(true);
    }
}