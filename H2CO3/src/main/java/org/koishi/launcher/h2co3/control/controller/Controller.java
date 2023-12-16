package org.koishi.launcher.h2co3.control.controller;

import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.control.client.Client;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.Input;

public interface Controller {

    void sendKey(BaseKeyEvent event);

    int getInputCounts();

    boolean addInput(Input input);

    boolean removeInput(Input input);

    boolean removeAllInputs();

    boolean containsInput(Input input);

    void setGrabCursor(boolean mode);

    void addContentView(View view, ViewGroup.LayoutParams params);

    void addView(View v);

    void typeWords(String str);

    void onStop();

    boolean isGrabbed();

    int[] getGrabbedPointer();

    int[] getLossenPointer();

    void saveConfig();

    Client getClient();

    void onPaused();

    void onResumed();

    Config getConfig();

    class Config {
        private final int screenWidth;
        private final int screenHeight;

        public Config(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }
    }
}



