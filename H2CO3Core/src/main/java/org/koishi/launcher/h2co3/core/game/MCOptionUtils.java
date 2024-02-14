package org.koishi.launcher.h2co3.core.game;

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
    private static final String OPTIONS_FILE_NAME = "options.txt";
    private static final String OPTION_SEPARATOR = ":";
    private static final String LINE_SEPARATOR = "\n";
    private static final String VALUE_SEPARATOR = ",";
    private static final int DEFAULT_GUI_SCALE = 0;

    private static final HashMap<String, String> parameterMap = new HashMap<>();
    private static final ArrayList<WeakReference<MCOptionListener>> optionListeners = new ArrayList<>();
    private static FileObserver fileObserver;

    private static String getOptionsFilePath(String gameDir) {
        return gameDir + File.separator + OPTIONS_FILE_NAME;
    }

    private static void loadOptionsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int firstColonIndex = line.indexOf(OPTION_SEPARATOR);
                if (firstColonIndex < 0) {
                    continue;
                }
                parameterMap.put(line.substring(0, firstColonIndex), line.substring(firstColonIndex + 1));
            }
        }
    }

    public static void loadOptions(String gameDir) {
        if (fileObserver == null) {
            setupFileObserver(gameDir);
        }

        parameterMap.clear();

        try {
            loadOptionsFromFile(getOptionsFilePath(gameDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setOption(String key, String value) {
        parameterMap.put(key, value);
    }

    public static void setOption(String key, List<String> values) {
        parameterMap.put(key, values.toString());
    }

    public static String getOption(String key) {
        return parameterMap.get(key);
    }

    public static List<String> getOptionAsList(String key) {
        String value = getOption(key);

        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.asList(value.replace("[", "").replace("]", "").split(VALUE_SEPARATOR));
    }

    public static void saveOptions(String gameDir) {
        StringBuilder result = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            result.append(key)
                    .append(OPTION_SEPARATOR)
                    .append(parameterMap.get(key))
                    .append(LINE_SEPARATOR);
        }

        try {
            H2CO3Tools.write(getOptionsFilePath(gameDir), result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getMcScale(String gameDir) {
        loadOptions(gameDir);
        String str = getOption("guiScale");
        int guiScale = (str == null ? DEFAULT_GUI_SCALE : Integer.parseInt(str));

        int scale = Math.min(1920 / 320, 1080 / 240);
        if (scale < guiScale || guiScale == DEFAULT_GUI_SCALE) {
            guiScale = scale;
        }

        return guiScale;
    }

    private static void setupFileObserver(String gameDir) {
        fileObserver = new FileObserver(getOptionsFilePath(gameDir), FileObserver.MODIFY) {
            @Override
            public void onEvent(int i, @Nullable String s) {
                loadOptions(gameDir);
                notifyListeners();
            }
        };

        fileObserver.startWatching();
    }

    public static void notifyListeners() {
        for (WeakReference<MCOptionListener> weakReference : optionListeners) {
            MCOptionListener optionListener = weakReference.get();
            if (optionListener != null) {
                optionListener.onOptionChanged();
            }
        }
    }

    public static void addOptionListener(MCOptionListener listener) {
        optionListeners.add(new WeakReference<>(listener));
    }

    public static void removeOptionListener(MCOptionListener listener) {
        for (WeakReference<MCOptionListener> weakReference : optionListeners) {
            MCOptionListener optionListener = weakReference.get();
            if (optionListener != null && optionListener == listener) {
                optionListeners.remove(weakReference);
                return;
            }
        }
    }

    public interface MCOptionListener {
        void onOptionChanged();
    }
}