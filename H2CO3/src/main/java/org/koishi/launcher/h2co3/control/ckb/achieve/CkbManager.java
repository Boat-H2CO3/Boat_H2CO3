package org.koishi.launcher.h2co3.control.ckb.achieve;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.koishi.launcher.h2co3.control.ckb.button.GameButton;
import org.koishi.launcher.h2co3.control.ckb.support.CallCustomizeKeyboard;
import org.koishi.launcher.h2co3.control.ckb.support.GameButtonArray;
import org.koishi.launcher.h2co3.control.ckb.support.GameButtonConverter;
import org.koishi.launcher.h2co3.control.ckb.support.GameButtonRecorder;
import org.koishi.launcher.h2co3.control.ckb.support.KeyboardRecorder;
import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;

public class CkbManager {

    public final static int MAX_KEYBOARD_SIZE = 160;
    public final static String LAST_KEYBOARD_LAYOUT_NAME = "default";
    public final static int SHOW_BUTTON = 1;
    public final static int HIDE_BUTTON = 2;
    private final Context mContext;
    private final CallCustomizeKeyboard mCall;
    private final Controller mController;
    private boolean hasHide = false;

    private GameButtonArray<GameButton> buttonList;

    private int buttonMode = GameButton.MODE_MOVEABLE_EDITABLE;
    private int displayWidth;
    private int displayHeight;

    public CkbManager(@NonNull Context context, @NonNull CallCustomizeKeyboard call, Controller controller) {
        super();
        H2CO3Tools.loadPaths(context);
        this.mContext = context;
        this.mCall = call;
        this.mController = controller;
        init();
    }

    public static void outputFile(KeyboardRecorder kr, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(kr);
        try {
            File dir = new File(H2CO3Tools.H2CO3_CONTROL_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName + ".json");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter jsonWriter = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(jsonWriter);
            out.write(jsonString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getDisplaySize() {
        return new int[]{displayWidth, displayHeight};
    }

    private void init() {
        buttonList = new GameButtonArray<>();
        displayWidth = DisplayUtils.checkDeviceHasNavigationBar(mContext) ? DisplayUtils.getApplicationWindowSize(mContext)[0] + DisplayUtils.getNavigationBarHeight(mContext) : DisplayUtils.getApplicationWindowSize(mContext)[0];
        displayHeight = DisplayUtils.getApplicationWindowSize(mContext)[1];
        autoLoadKeyboard();
    }

    public Controller getController() {
        return mController;
    }

    public void addGameButton(GameButton button) {
        if (containGameButton(button) || buttonList.size() >= MAX_KEYBOARD_SIZE) {
            button.unsetFirstAdded();
        } else {
            if (button == null) {
                button = new GameButton(mContext, mCall, mController, this).setButtonMode(buttonMode).setFirstAdded();
                (new GameButtonDialog(mContext, button, this)).show();
            }
            buttonList.add(button);
            addView(button);
        }
    }

    public boolean containGameButton(GameButton button) {
        return buttonList.contains(button);
    }

    public void removeGameButton(GameButton button) {
        if (containGameButton(button)) {
            buttonList.remove(button);
            removeView(button);
        }
    }

    private void addView(GameButton button) {
        if (button != null && button.getParent() == null) {
            mCall.addView(button);
        }
    }

    private void removeView(GameButton button) {
        if (button != null && button.getParent() != null) {
            ViewGroup vg = (ViewGroup) button.getParent();
            vg.removeView(button);
        }
    }

    public int getButtonCount() {
        return buttonList.size();
    }

    public int getButtonMode() {
        return buttonMode;
    }

    public void setButtonMode(int mode) {
        if (mode == GameButton.MODE_GAME || mode == GameButton.MODE_MOVEABLE_EDITABLE || mode == GameButton.MODE_PREVIEW) {
            for (GameButton button : buttonList) {
                button.setButtonMode(mode);
            }
            buttonMode = mode;
        }
    }

    public GameButton[] getGameButtons() {
        return buttonList.toArray(new GameButton[0]);
    }

    public GameButton getGameButton(int index) {
        if (index >= 0 && index < buttonList.size()) {
            return buttonList.get(index);
        } else {
            return null;
        }
    }

    public void setInputMode(boolean mode) {
        for (GameButton button : buttonList) {
            button.setGrabbed(mode);
        }
    }

    public void exportKeyboard(String fileName) {
        GameButtonRecorder[] gbrs = new GameButtonRecorder[buttonList.size()];
        for (int a = 0; a < buttonList.size(); a++) {
            GameButtonRecorder gbr = new GameButtonRecorder();
            gbr.recordData(buttonList.get(a));
            gbrs[a] = gbr;
        }
        KeyboardRecorder kr = new KeyboardRecorder();
        kr.setScreenArgs(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        kr.setRecorderDatas(gbrs);
        kr.setVersionCode(KeyboardRecorder.VERSION_THIS);

        outputFile(kr, fileName);
    }

    public void autoSaveKeyboard() {
        exportKeyboard(LAST_KEYBOARD_LAYOUT_NAME);
    }

    public void autoLoadKeyboard() {
        loadKeyboard(LAST_KEYBOARD_LAYOUT_NAME + ".json");
    }

    public boolean loadKeyboard(File file) {
        if (!file.exists()) {
            return false;
        }
        KeyboardRecorder kr;
        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            kr = gson.fromJson(reader, KeyboardRecorder.class);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_note), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_try_to_convert_keyboard_layout), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    super.runWhenPositive();
                    if (new GameButtonConverter(mContext).convertAndOutput(file)) {
                        DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_note), String.format(mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_successed_to_convert_keyboard_file), file.getName() + "-new.json"), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), null);
                    } else {
                        DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_note), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_failed_to_convert_keyboard_file), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), null);
                    }
                }
            });
            return false;
        }
        loadKeyboard(kr);
        return true;
    }

    public boolean loadKeyboard(String fileName) {
        File file = new File(H2CO3Tools.H2CO3_CONTROL_DIR + "/" + fileName);
        return loadKeyboard(file);
    }

    public void loadKeyboard(KeyboardRecorder kr) {
        GameButtonRecorder[] gbr;
        if (kr != null) {
            gbr = kr.getRecorderDatas();
        } else {
            return;
        }

        if (kr.getVersionCode() == KeyboardRecorder.VERSION_UNKNOWN) {
            for (GameButtonRecorder tgbr : gbr) {
                tgbr.keyPos[0] = DisplayUtils.getDpFromPx(mContext, tgbr.keyPos[0]);
                tgbr.keyPos[1] = DisplayUtils.getDpFromPx(mContext, tgbr.keyPos[1]);
            }
        }
        clearKeyboard();
        for (GameButtonRecorder tgbr : gbr) {
            addGameButton(tgbr.recoverData(mContext, mCall, mController, this));
        }
    }

    public void clearKeyboard() {
        for (GameButton button : buttonList) {
            removeView(button);
        }
        buttonList.clear();
    }

    public void showOrHideGameButtons(int i) {
        switch (i) {
            case SHOW_BUTTON:
                if (hasHide) {
                    for (GameButton button : buttonList) {
                        addView(button);
                    }
                    hasHide = false;
                }
                break;
            case HIDE_BUTTON:
                if (!hasHide) {
                    for (GameButton button : buttonList) {
                        removeView(button);
                    }
                    hasHide = true;
                }
                break;
        }
    }
}