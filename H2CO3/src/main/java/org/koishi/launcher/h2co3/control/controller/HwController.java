package org.koishi.launcher.h2co3.control.controller;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface HwController extends Controller {

    boolean dispatchKeyEvent(KeyEvent event);

    boolean dispatchMotionKeyEvent(MotionEvent event);
}
