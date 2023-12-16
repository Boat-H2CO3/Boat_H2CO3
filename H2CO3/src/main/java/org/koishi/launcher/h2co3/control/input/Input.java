package org.koishi.launcher.h2co3.control.input;

import android.content.Context;

import org.koishi.launcher.h2co3.control.controller.Controller;

public interface Input {
    boolean load(Context context, Controller controller);

    boolean unload();

    void setGrabCursor(boolean isGrabbed); // 赋值 MARK_INPUT_MODE

    void runConfigure();

    void saveConfig();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void onPaused();

    void onResumed();

    Controller getController();
}
