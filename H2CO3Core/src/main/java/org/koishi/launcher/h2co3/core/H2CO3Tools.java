package org.koishi.launcher.h2co3.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class H2CO3Tools {

    @SuppressLint("StaticFieldLeak")
    public static Context CONTEXT;

    public static String NATIVE_LIB_DIR;

    public static String LOG_DIR;
    public static String CACHE_DIR;

    public static String RUNTIME_DIR;
    public static String JAVA_8_PATH;
    public static String JAVA_17_PATH;
    public static String BOAT_LIBRARY_DIR;

    public static String FILES_DIR;
    public static String PLUGIN_DIR;
    public static String H2CO3_LIBRARY_DIR;
    public static String H2CO3_SETTING_DIR;

    public static String MINECRAFT_DIR;
    public static String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FCL/.minecraft";

    public static String AUTHLIB_INJECTOR_PATH;
    public static String MULTIPLAYER_FIX_PATH;
    public static String APP_DATA_PATH;
    public static String PUBLIC_FILE_PATH;

    @SuppressLint("SdCardPath")
    public static void loadPaths(Context context) {
        CONTEXT = context;

        NATIVE_LIB_DIR = context.getApplicationInfo().nativeLibraryDir;

        PUBLIC_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/org.koishi.launcher/h2co3";

        LOG_DIR = PUBLIC_FILE_PATH + "/log";
        CACHE_DIR = context.getCacheDir() + "/boat_h2co3";

        APP_DATA_PATH = "/data/data/org.koishi.launcher.h2co3";
        RUNTIME_DIR = APP_DATA_PATH + "/app_runtime";
        JAVA_8_PATH = RUNTIME_DIR + "/jre_8";
        JAVA_17_PATH = RUNTIME_DIR + "/jre_17";
        BOAT_LIBRARY_DIR = RUNTIME_DIR + "/boat";
        PLUGIN_DIR = RUNTIME_DIR + "/boat/plugin";
        H2CO3_LIBRARY_DIR = APP_DATA_PATH + "/h2co3";
        H2CO3_SETTING_DIR = APP_DATA_PATH + "/h2co3_setting";

        FILES_DIR = context.getFilesDir().getAbsolutePath();

        MINECRAFT_DIR = PUBLIC_FILE_PATH + "/.minecraft";

        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        MULTIPLAYER_FIX_PATH = PLUGIN_DIR + "/MultiplayerFix.jar";


        init(LOG_DIR);
        init(CACHE_DIR);
        init(RUNTIME_DIR);
        init(JAVA_8_PATH);
        init(JAVA_17_PATH);
        init(BOAT_LIBRARY_DIR);
        init(FILES_DIR);
        init(PLUGIN_DIR);
        init(H2CO3_LIBRARY_DIR);
        init(H2CO3_SETTING_DIR);
        init(MINECRAFT_DIR);
        init(APP_DATA_PATH);
        init(SHARED_COMMON_DIR);
        init(PUBLIC_FILE_PATH);
    }

    private static void init(String path) {
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
    }

    public static boolean getBoatValue(String key, boolean defaultValue) {
        if (getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/BoatConfig.json", "usesGlobal", false, boolean.class)) {
            return getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/BoatConfig.json", key, defaultValue, boolean.class);
        } else {
            return getValue(H2CO3_SETTING_DIR + "/BoatConfig.json", key, defaultValue, boolean.class);
        }
    }

    public static String getBoatValueString(String key, String defaultValue) {
        if (getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/BoatConfig.json", "usesGlobal", false, boolean.class)) {
            return getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/BoatConfig.json", key, defaultValue, String.class);
        } else {
            return getValue(H2CO3_SETTING_DIR + "/BoatConfig.json", key, defaultValue, String.class);
        }
    }

    public static void setBoatValue(String key, Object value) {
        if (getBoatValue("usesGlobal", false)) {
            setValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/BoatConfig.json", key, value);
        } else {
            setValue(H2CO3_SETTING_DIR + "/BoatConfig.json", key, value);
        }
    }

    public static String getH2CO3ValueString(String key, String defaultValue) {
        return getValue(H2CO3_SETTING_DIR + "/H2CO3Config.json", key, defaultValue, String.class);
    }

    public static boolean getH2CO3Value(String key, boolean defaultValue) {
        return getValue(H2CO3_SETTING_DIR + "/H2CO3Config.json", key, defaultValue, boolean.class);
    }

    public static void setH2CO3Value(String key, Object value) {
        setValue(H2CO3_SETTING_DIR + "/H2CO3Config.json", key, value);
    }

    private static <T> T getValue(String configFile, String key, T defaultValue, Class<T> type) {
        try {
            Path configPath = Paths.get(configFile);
            if (!Files.exists(configPath)) {
                createConfigFile(configPath, key, defaultValue);
                return defaultValue;
            }

            try (Stream<String> lines = Files.lines(configPath)) {
                Optional<String> configContent = lines.reduce((a, b) -> a + b);
                if (!configContent.isPresent()) {
                    throw new IOException("Failed to read config file: " + configFile);
                }

                JSONObject configJson = new JSONObject(configContent.get());

                if (!configJson.has(key)) {
                    configJson.put(key, defaultValue);
                    Files.write(configPath, configJson.toString().getBytes());
                    return defaultValue;
                }

                if (type == String.class) {
                    return (T) configJson.optString(key);
                } else if (type == boolean.class) {
                    return (T) Boolean.valueOf(configJson.optBoolean(key));
                } else {
                    return (T) configJson.opt(key);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or create config file: " + configFile, e);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse config file: " + configFile, e);
        }
    }

    public static void setValue(String configFile, String key, Object value) {
        try {
            Path configPath = Paths.get(configFile);
            if (!Files.exists(configPath)) {
                createConfigFile(configPath, key, value);
                return;
            }

            String configContent = new String(Files.readAllBytes(configPath));
            JSONObject configJson = new JSONObject(configContent);

            configJson.put(key, value);
            Files.write(configPath, configJson.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or create config file: " + configFile, e);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse config file: " + configFile, e);
        }
    }

    private static void createConfigFile(Path configPath, String key, Object value) throws IOException {
        Files.createDirectories(configPath.getParent());
        Files.createFile(configPath);
        JSONObject defaultConfigJson = new JSONObject();
        try {
            defaultConfigJson.put(key, value);
            Files.write(configPath, defaultConfigJson.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}