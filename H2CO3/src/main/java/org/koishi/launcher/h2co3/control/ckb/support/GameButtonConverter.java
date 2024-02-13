package org.koishi.launcher.h2co3.control.ckb.support;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.koishi.launcher.h2co3.control.ckb.achieve.CkbManager;
import org.koishi.launcher.h2co3.control.ckb.button.GameButton;
import org.koishi.launcher.h2co3.control.ckb.button.GameButtonOld;
import org.koishi.launcher.h2co3.control.definitions.map.MouseMap;
import org.koishi.launcher.h2co3.core.utils.ColorUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

public class GameButtonConverter {

    private final static String TAG = "GameButtonConverter";
    private final Context mContext;

    public GameButtonConverter(Context context) {
        this.mContext = context;
    }

    /**
     * 将旧版游戏按钮转换为新版游戏按钮并输出到文件
     *
     * @param file 旧版游戏按钮的文件
     * @return 是否成功输出
     */
    public boolean convertAndOutput(File file) {
        try {
            GameButtonOld[] oldButtons = getOldButtonsFromJson(file);
            if (oldButtons == null) {
                return false;
            }
            KeyboardRecorder keyboardRecorder = getNewKeyboardFromOldKeyboard(oldButtons);
            String newFileName = getNewFileName(file);
            CkbManager.outputFile(keyboardRecorder, newFileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从Json文件中获取旧版游戏按钮
     *
     * @param file Json文件
     * @return 旧版游戏按钮数组
     */
    private GameButtonOld[] getOldButtonsFromJson(File file) {
        InputStream inputStream;
        Gson gson = new Gson();
        if (!file.exists()) {
            return null;
        }
        try {
            inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            return gson.fromJson(reader, GameButtonOld[].class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将旧版游戏按钮转换为新版游戏按钮
     *
     * @param oldButtons 旧版游戏按钮数组
     * @return 新版游戏按钮
     */
    private KeyboardRecorder getNewKeyboardFromOldKeyboard(GameButtonOld[] oldButtons) {
        KeyboardRecorder keyboardRecorder = new KeyboardRecorder();
        GameButtonRecorder[] gameButtonRecorders = new GameButtonRecorder[oldButtons.length];
        for (int i = 0; i < gameButtonRecorders.length; i++) {
            gameButtonRecorders[i] = getGameButtonRecorderFromOldButtonModel(oldButtons[i]);
        }
        keyboardRecorder.setRecorderDatas(gameButtonRecorders);
        keyboardRecorder.setVersionCode(KeyboardRecorder.VERSION_THIS);
        keyboardRecorder.setScreenArgs(mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        return keyboardRecorder;
    }

    /**
     * 从旧版游戏按钮模型中获取新版游戏按钮模型
     *
     * @param oldButton 旧版游戏按钮模型
     * @return 新版游戏按钮模型
     */
    private GameButtonRecorder getGameButtonRecorderFromOldButtonModel(GameButtonOld oldButton) {
        final String OLD_MOUSE_PRI = "MOUSE_Pri";
        final String OLD_MOUSE_SEC = "MOUSE_Sec";

        GameButtonRecorder gameButtonRecorder = new GameButtonRecorder();
        gameButtonRecorder.keyPos = new float[]{oldButton.getKeyLX(), oldButton.getKeyLY()};
        gameButtonRecorder.keyChars = "";
        gameButtonRecorder.keySize = new float[]{oldButton.getKeySizeW(), oldButton.getKeySizeH()};
        gameButtonRecorder.isChars = false;
        Log.e(TAG, "透明度" + ColorUtils.int2rgba(ColorUtils.hex2Int(oldButton.getColorhex()))[3]);
        gameButtonRecorder.alphaSize = (int) ((ColorUtils.int2rgba(ColorUtils.hex2Int(oldButton.getColorhex()))[3]) / 255f * 100);
        gameButtonRecorder.cornerRadius = (oldButton.getCornerRadius() / 180) * 100;
        gameButtonRecorder.designIndex = GameButton.DEFAULT_DESIGN_INDEX;
        gameButtonRecorder.isHide = oldButton.isHide();
        gameButtonRecorder.isKeep = oldButton.isAutoKeep();
        gameButtonRecorder.isViewerFollow = false;

        String[] keyMap = new String[GameButton.MAX_KEYMAP_SIZE];
        Arrays.fill(keyMap, "");

        int[] keyTypes = new int[GameButton.MAX_KEYMAP_SIZE];
        if (oldButton.getKeyMain().equals(OLD_MOUSE_PRI)) {
            keyMap[0] = MouseMap.MOUSEMAP_BUTTON_LEFT;
            keyTypes[0] = GameButton.MOUSE_TYPE;
        } else if (oldButton.getKeyMain().equals(OLD_MOUSE_SEC)) {
            keyMap[0] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
            keyTypes[0] = GameButton.MOUSE_TYPE;
        } else if (!oldButton.getKeyMain().equals("空")) {
            keyMap[0] = oldButton.getKeyMain();
            keyTypes[0] = GameButton.KEY_TYPE;
        }
        if (oldButton.isMult()) {
            if (oldButton.getSpecialOne().equals(OLD_MOUSE_PRI)) {
                keyMap[1] = MouseMap.MOUSEMAP_BUTTON_LEFT;
                keyTypes[1] = GameButton.MOUSE_TYPE;
            } else if (oldButton.getSpecialOne().equals(OLD_MOUSE_SEC)) {
                keyMap[1] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
                keyTypes[1] = GameButton.MOUSE_TYPE;
            } else if (!oldButton.getSpecialOne().equals("空")) {
                keyMap[1] = oldButton.getKeyMain();
                keyTypes[1] = GameButton.KEY_TYPE;
            }

            if (oldButton.getSpecialTwo().equals(OLD_MOUSE_PRI)) {
                keyMap[2] = MouseMap.MOUSEMAP_BUTTON_LEFT;
                keyTypes[2] = GameButton.MOUSE_TYPE;
            } else if (oldButton.getSpecialOne().equals(OLD_MOUSE_SEC)) {
                keyMap[2] = MouseMap.MOUSEMAP_BUTTON_RIGHT;
                keyTypes[2] = GameButton.MOUSE_TYPE;
            } else if (!oldButton.getSpecialTwo().equals("空")) {
                keyMap[2] = oldButton.getKeyMain();
                keyTypes[2] = GameButton.KEY_TYPE;
            }
        }
        gameButtonRecorder.keyTypes = keyTypes;
        gameButtonRecorder.keyMaps = keyMap;

        gameButtonRecorder.keyName = oldButton.getKeyName();
        gameButtonRecorder.show = GameButton.SHOW_ALL;
        gameButtonRecorder.textColor = oldButton.getTextColorHex();
        gameButtonRecorder.themeColors = new String[CkbThemeRecorder.COLOR_INDEX_LENGTH];
        gameButtonRecorder.themeColors[0] = new StringBuilder(oldButton.getColorhex().substring(3)).insert(0, '#').toString();
        gameButtonRecorder.textSize = GameButton.DEFAULT_TEXT_SIZE_SP;

        return gameButtonRecorder;
    }

    /**
     * 获取新文件名
     *
     * @param file 旧文件
     * @return 新文件名
     */
    private String getNewFileName(File file) {
        StringBuilder fileName = new StringBuilder();
        for (int i = 0; i < file.getName().length() - 5; i++) {
            fileName.append(file.getName().charAt(i));
        }
        return fileName + "-new";
    }
}