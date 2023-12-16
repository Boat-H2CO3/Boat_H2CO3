package org.koishi.launcher.h2co3.control.input;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface HwInput extends Input {
    boolean onKey(KeyEvent event);

    boolean onMotionKey(MotionEvent event);

    int getSource();
}
