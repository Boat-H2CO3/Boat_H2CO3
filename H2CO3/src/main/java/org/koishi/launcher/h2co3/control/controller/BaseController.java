package org.koishi.launcher.h2co3.control.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.input.Input;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseController implements Controller {
    private final static String TAG = "BaseController";
    private final static int DEFAULT_INTERVAL_TIME = 5000;
    public final H2CO3ControlClient h2CO3ControlClient;
    public final Context context;
    private final int internalTime;
    private final Config mConfig;
    private final boolean isTimerEnable;
    public ArrayList<Input> inputs;
    private Timer mTimer;

    public BaseController(H2CO3ControlClient h2CO3ControlClient, int intervalTime, boolean enableTimer) {
        this.h2CO3ControlClient = h2CO3ControlClient;
        this.context = h2CO3ControlClient.getActivity();
        inputs = new ArrayList<>();
        this.internalTime = intervalTime;
        this.mConfig = new Config(DisplayUtils.getDisplayWindowSize(context)[0], DisplayUtils.getDisplayWindowSize(context)[1]);
        this.isTimerEnable = enableTimer;
        if (enableTimer) {
            createAutoSaveTimer();
        }
    }

    public BaseController(H2CO3ControlClient h2CO3ControlClient, boolean enableTimer) {
        this(h2CO3ControlClient, BaseController.DEFAULT_INTERVAL_TIME, enableTimer);
    }


    @Override
    public boolean containsInput(Input input) {
        for (Input i : inputs) {
            if (i == input) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addInput(Input input) {
        if (containsInput(input) || input == null) {
            return false;
        } else {
            if (input.load(context, this)) {
                inputs.add(input);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean removeInput(Input input) {
        if (!containsInput(input) || input == null || !input.unload()) {
            return false;
        } else {
            ArrayList<Input> tmp = new ArrayList<>();
            for (Input i : inputs) {
                if (input != i) {
                    tmp.add(i);
                }
            }
            inputs = tmp;
            return true;
        }
    }

    @Override
    public int getInputCounts() {
        return inputs.size();
    }

    @Override
    public boolean removeAllInputs() {
        boolean success = true;
        for (Input i : inputs) {
            if (!removeInput(i)) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        for (Input i : inputs) {
            i.setGrabCursor(isGrabbed);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        h2CO3ControlClient.addContentView(view, params);
    }

    @Override
    public void addView(View view) {
        h2CO3ControlClient.addContentView(view, view.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        h2CO3ControlClient.typeWords(str);
    }

    @Override
    public void onStop() {
        this.saveConfig();
    }

    @Override
    public boolean isGrabbed() {
        return h2CO3ControlClient.isGrabbed();
    }

    @Override
    public int[] getGrabbedPointer() {
        return h2CO3ControlClient.getGrabbedPointer();
    }

    @Override
    public void saveConfig() {
        for (Input i : inputs) {
            i.saveConfig();
        }
    }

    @Override
    public H2CO3ControlClient getClient() {
        return h2CO3ControlClient;
    }

    @Override
    public void onPaused() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        for (Input i : inputs) {
            i.onPaused();
        }
    }

    @Override
    public void onResumed() {
        if (isTimerEnable) {
            createAutoSaveTimer();
        }
        for (Input i : inputs) {
            i.onResumed();
        }
    }

    private void createAutoSaveTimer() {
        if (mTimer != null) return;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                BaseController.this.saveConfig();
            }
        }, internalTime, internalTime);
    }

    @Override
    public int[] getLossenPointer() {
        return h2CO3ControlClient.getLoosenPointer();
    }

    @Override
    public Config getConfig() {
        return this.mConfig;
    }

    public boolean isTimerEnabled() {
        return this.isTimerEnable;
    }
}


