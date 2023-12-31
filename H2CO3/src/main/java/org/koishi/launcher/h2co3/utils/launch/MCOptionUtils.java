package org.koishi.launcher.h2co3.utils.launch;

import android.os.Build;
import android.os.FileObserver;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MCOptionUtils {
    private static final HashMap<String, String> parameterMap = new HashMap<>();
    private static final ArrayList<WeakReference<MCOptionListener>> optionListeners = new ArrayList<>();
    private static FileObserver fileObserver;

    /**
     * 加载选项
     * @param gameDir 游戏目录
     */
    public static void loadOptions(String gameDir) {
        if (fileObserver == null) {
            setupFileObserver(gameDir);
        }

        parameterMap.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(gameDir + "/options.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                int firstColonIndex = line.indexOf(':');
                if (firstColonIndex < 0) {
                    continue;
                }
                parameterMap.put(line.substring(0, firstColonIndex), line.substring(firstColonIndex + 1));
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置选项值
     * @param key 选项键
     * @param value 选项值
     */
    public static void setOption(String key, String value) {
        parameterMap.put(key, value);
    }

    /**
     * 设置选项值
     * @param key 选项键
     * @param values 选项值列表
     */
    public static void setOption(String key, List<String> values) {
        parameterMap.put(key, values.toString());
    }

    /**
     * 获取选项值
     * @param key 选项键
     * @return 选项值
     */
    public static String getOption(String key) {
        return parameterMap.get(key);
    }

    /**
     * 获取选项值列表
     * @param key 选项键
     * @return 选项值列表
     */
    public static List<String> getOptionAsList(String key) {
        String value = getOption(key);

        // Fallback if the value doesn't exist
        if (value == null) return new ArrayList<>();

        // Remove the edges
        value = value.replace("[", "").replace("]", "");
        if (value.isEmpty()) return new ArrayList<>();

        return Arrays.asList(value.split(","));
    }

    /**
     * 保存选项
     * @param gameDir 游戏目录
     */
    public static void saveOptions(String gameDir) {
        StringBuilder result = new StringBuilder();
        for (String key : parameterMap.keySet())
            result.append(key)
                    .append(':')
                    .append(parameterMap.get(key))
                    .append('\n');

        try {
            H2CO3Tools.write(gameDir + "/options.txt", result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Minecraft GUI缩放比例
     * @param gameDir 游戏目录
     * @return GUI缩放比例
     */
    public static int getMcScale(String gameDir) {
        loadOptions(gameDir);
        String str = getOption("guiScale");
        int guiScale = (str == null ? 0 : Integer.parseInt(str));

        int scale = Math.min(1920 / 320, 1080 / 240);
        if (scale < guiScale || guiScale == 0) {
            guiScale = scale;
        }

        return guiScale;
    }

    /**
     * 设置文件观察者，当文件发生变化时重新加载选项
     * @param gameDir 游戏目录
     */
    private static void setupFileObserver(String gameDir) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileObserver = new FileObserver(new File(gameDir + "/options.txt"), FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    loadOptions(gameDir);
                    notifyListeners();
                }
            };
        } else {
            fileObserver = new FileObserver(gameDir + "/options.txt", FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    loadOptions(gameDir);
                    notifyListeners();
                }
            };
        }

        fileObserver.startWatching();
    }

    /**
     * 通知选项监听器
     */
    public static void notifyListeners() {
        for (WeakReference<MCOptionListener> weakReference : optionListeners) {
            MCOptionListener optionListener = weakReference.get();
            if (optionListener == null) continue;

            optionListener.onOptionChanged();
        }
    }

    /**
     * 添加选项监听器
     * @param listener 选项监听器
     */
    public static void addOptionListener(MCOptionListener listener) {
        optionListeners.add(new WeakReference<>(listener));
    }

    /**
     * 移除选项监听器
     * @param listener 选项监听器
     */
    public static void removeOptionListener(MCOptionListener listener) {
        for (WeakReference<MCOptionListener> weakReference : optionListeners) {
            MCOptionListener optionListener = weakReference.get();
            if (optionListener == null) continue;
            if (optionListener == listener) {
                optionListeners.remove(weakReference);
                return;
            }
        }
    }

    public interface MCOptionListener {
        /**
         * 当选项发生变化时调用
         */
        void onOptionChanged();
    }
}