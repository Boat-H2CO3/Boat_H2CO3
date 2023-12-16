package org.koishi.launcher.h2co3.control.input.otg;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.ANDROID_TO_KEYMAP;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.koishi.launcher.h2co3.control.codes.Translation;
import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.control.definitions.map.KeyMap;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.HwInput;

public class Phone implements HwInput {

    private final static String TAG = "Phone";
    private Controller mController;
    private Context mContext;
    private boolean isEnabled = false;

    @Override
    public boolean load(Context context, Controller controller) {
        Translation mTrans = new Translation(ANDROID_TO_KEYMAP);
        this.mContext = context;
        this.mController = controller;
        return true;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public boolean onKey(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            sendKeyEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        return false;
    }

    @Override
    public int getSource() {
        return InputDevice.SOURCE_KEYBOARD;
    }

    private void sendKeyEvent(KeyEvent event) {
        boolean pressed;
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                pressed = false;
                break;
            case KeyEvent.ACTION_DOWN:
                pressed = true;
                break;
            default:
                return;

        }
        mController.sendKey(new BaseKeyEvent(TAG, KeyMap.KEYMAP_KEY_ESC, pressed, KEYBOARD_BUTTON, null));
    }

    private void adjustAudio(int direction) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Controller getController() {
        return this.mController;
    }
}
