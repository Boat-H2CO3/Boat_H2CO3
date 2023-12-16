package org.koishi.launcher.h2co3.control.input.otg;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;

import android.content.Context;
import android.os.Build;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.control.definitions.map.MouseMap;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.HwInput;

import java.util.Timer;
import java.util.TimerTask;

public class Mouse implements HwInput {

    private final static String TAG = "Mouse";
    private final static int type2 = MOUSE_BUTTON;
    private final static int type1 = MOUSE_POINTER_INC;
    private final static int CURSOR_EXTRA_RELEASE = 3; //times
    private final static int CURSOR_EXTRA_GRABBED = 2; //times

    private Controller mController;
    private boolean isEnabled = false;
    private int screenWidth;
    private int screenHeight;
    private Timer mTimer;

    @Override
    public int getSource() {
        return InputDevice.SOURCE_MOUSE;
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mController = controller;
        //设定鼠标监听器（SDK >= 26）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View.OnCapturedPointerListener mCapturedPointerListener = (view, event) -> {
                Mouse.this.onMotionKey(event);
                return true;
            };
            mController.getClient().getViewsParent().setOnCapturedPointerListener(mCapturedPointerListener);
        }
        //创建定时器
        createTimer();
        return true;
    }

    @Override
    public boolean unload() {
        cancelTimer();
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    //私有事件封装
    private void sendPointerInc(int x, int y) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type1, new int[]{x, y}));
    }

    private void sendKeyEvent(String keyName, boolean pressed) {
        if (keyName == null) return;
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, Mouse.type2, null));
    }

    //配置信息处理
    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    //事件分发处理
    @Override
    public boolean onKey(KeyEvent event) {
        return false;
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            doMotion(event);
        }
        return true;
    }

    //主要的控制逻辑处理
    private void doMotion(MotionEvent event) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_BUTTON_PRESS:
                sendKeyEvent(mapCovert(event.getActionButton()), true);
                break;
            case MotionEvent.ACTION_BUTTON_RELEASE:
                sendKeyEvent(mapCovert(event.getActionButton()), false);
                break;
            case MotionEvent.ACTION_SCROLL:
                String keyName;
                if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) > 0) {
                    keyName = MouseMap.MOUSEMAP_WHEEL_UP;
                } else {
                    keyName = MouseMap.MOUSEMAP_WHEEL_DOWN;
                }
                sendKeyEvent(keyName, true);
                sendKeyEvent(keyName, false);
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
            case MotionEvent.ACTION_HOVER_MOVE:
            case MotionEvent.ACTION_HOVER_EXIT:
            case MotionEvent.ACTION_MOVE:
                if (mController.isGrabbed()) {
                    sendPointerInc((int) event.getAxisValue(MotionEvent.AXIS_X) * CURSOR_EXTRA_GRABBED, (int) event.getAxisValue(MotionEvent.AXIS_Y) * CURSOR_EXTRA_GRABBED);
                } else {
                    sendPointerInc((int) event.getAxisValue(MotionEvent.AXIS_X) * CURSOR_EXTRA_RELEASE, (int) event.getAxisValue(MotionEvent.AXIS_Y) * CURSOR_EXTRA_RELEASE);
                }
                break;
        }

    }

    private String mapCovert(int actionButton) {
        switch (actionButton) {
            case MotionEvent.BUTTON_PRIMARY:
                return MouseMap.MOUSEMAP_BUTTON_LEFT;
            case MotionEvent.BUTTON_TERTIARY:
                return MouseMap.MOUSEMAP_BUTTON_MIDDLE;
            case MotionEvent.BUTTON_SECONDARY:
                return MouseMap.MOUSEMAP_BUTTON_RIGHT;
            default:
                return null;
        }
    }

    @Override
    public void onPaused() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cancelTimer();
        }
    }

    @Override
    public void onResumed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createTimer();
        }
    }

    @Override
    public Controller getController() {
        return this.mController;
    }

    private void createTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!mController.getClient().getViewsParent().isFocusable())
                        mController.getClient().getViewsParent().setFocusable(true);
                    if (!mController.getClient().getViewsParent().hasPointerCapture()) {
                        mController.getClient().getViewsParent().requestPointerCapture();
                    }
                }
            }, 0, 500);
        }
    }

    private void cancelTimer() {
        try {
            mTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //鼠标加速分段算法
    private float mouseAcceleration(float d) {
        float MAX_D = 0.05f;
        float MAX_S = 1.2f;
        float tmp = Math.abs(d);
        int times = 1;
        while (tmp >= MAX_D) {
            tmp %= MAX_D;
            times++;
        }
        return (float) (d * Math.pow(MAX_S, times));

    }
}
