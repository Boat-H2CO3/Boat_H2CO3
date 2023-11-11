package org.koishi.launcher.h2co3.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public static String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/org.koishi.launcher/h2co3/.minecraft";

    public static String AUTHLIB_INJECTOR_PATH;
    public static String MULTIPLAYER_FIX_PATH;
    public static String APP_DATA_PATH;
    public static String PUBLIC_FILE_PATH;


    public static String H2CO3_CONFIG_NAME;
    public static String H2CO3_BOAT_CONFIG_NAME;


    public static String LOGIN_USERS;
    public static String LOGIN_AUTH_PLAYER_NAME;
    public static String LOGIN_USER_EMAIL;
    public static String LOGIN_USER_PASSWORD;
    public static String LOGIN_USER_TYPE;
    public static String LOGIN_API_URL;
    public static String LOGIN_AUTH_SESSION;
    public static String LOGIN_UUID;
    public static String LOGIN_USER_SKINTEXTURE;
    public static String LOGIN_TOKEN;
    public static String LOGIN_REFRESH_TOKEN;
    public static String LOGIN_CLIENT_TOKEN;
    public static String LOGIN_IS_OFFLINE;
    public static String LOGIN_IS_SELECTED;
    public static String LOGIN_ERROR;
    public static String LOGIN_INFO;


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

        H2CO3_CONFIG_NAME = "H2CO3Config.json";
        H2CO3_BOAT_CONFIG_NAME = "H2CO3BoatConfig.json";

        LOGIN_USERS = "h2co3_users";
        LOGIN_AUTH_PLAYER_NAME = "h2co3_users_auth_player_name";
        LOGIN_USER_EMAIL = "h2co3_users_email";
        LOGIN_USER_PASSWORD = "h2co3_users_auth_player_password";
        LOGIN_USER_TYPE = "h2co3_users_type";
        LOGIN_API_URL = "h2co3_users_api_url";
        LOGIN_AUTH_SESSION = "h2co3_users_auth_session";
        LOGIN_UUID = "h2co3_users_uuid";
        LOGIN_USER_SKINTEXTURE = "h2co3_skin_texture";
        LOGIN_TOKEN = "h2co3_users_token";
        LOGIN_REFRESH_TOKEN = "h2co3_users_refresh_token";
        LOGIN_CLIENT_TOKEN = "h2co3_users_client_token";
        LOGIN_IS_OFFLINE = "h2co3_users_isOffline";
        LOGIN_IS_SELECTED = "h2co3_users_isSelected";
        LOGIN_ERROR = "Login Filed";
        LOGIN_INFO = "h2co3_users_info";

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
        init(H2CO3_CONFIG_NAME);
        init(H2CO3_BOAT_CONFIG_NAME);
    }

    private static void init(String path) {
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
    }

    public static boolean getBoatValue(String key, boolean defaultValue) {
        if (getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) +"/"+ H2CO3_BOAT_CONFIG_NAME, "usesGlobal", false, Boolean.class)) {
            return getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) +"/"+H2CO3_BOAT_CONFIG_NAME, key, defaultValue, Boolean.class);
        } else {
            return getValue(H2CO3_SETTING_DIR + "/"+H2CO3_BOAT_CONFIG_NAME, key, defaultValue, Boolean.class);
        }
    }

    public static String getBoatValueString(String key, String defaultValue) {
        if (getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/"+H2CO3_BOAT_CONFIG_NAME, "usesGlobal", false, Boolean.class)) {
            return getValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/"+H2CO3_BOAT_CONFIG_NAME, key, defaultValue, String.class);
        } else {
            return getValue(H2CO3_SETTING_DIR + "/"+H2CO3_BOAT_CONFIG_NAME, key, defaultValue, String.class);
        }
    }

    public static void setBoatValue(String key, java.io.Serializable value) {
        if (getBoatValue("usesGlobal", false)) {
            setValue(getH2CO3ValueString("currentVersion", H2CO3_SETTING_DIR) + "/"+H2CO3_BOAT_CONFIG_NAME, key, value);
        } else {
            setValue(H2CO3_SETTING_DIR + "/"+H2CO3_BOAT_CONFIG_NAME, key, value);
        }
    }

    public static String getH2CO3ValueString(String key, String defaultValue) {
        return getValue(H2CO3_SETTING_DIR + "/"+H2CO3_CONFIG_NAME, key, defaultValue, String.class);
    }

    public static boolean getH2CO3Value(String key, boolean defaultValue) {
        return getValue(H2CO3_SETTING_DIR + "/"+H2CO3_CONFIG_NAME, key, defaultValue, Boolean.class);
    }

    public static void setH2CO3Value(String key, String value) {
        setValue(H2CO3_SETTING_DIR + "/"+H2CO3_CONFIG_NAME, key, value);
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
                } else if (type == Boolean.class) {
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

    public static void setValue(String configFile, String key, java.io.Serializable value) {
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

    public static String read(InputStream is) throws IOException {
        String readResult = IOUtils.toString(is, StandardCharsets.UTF_8);
        is.close();
        return readResult;
    }

    public static String read(String path) throws IOException {
        return read(new FileInputStream(path));
    }

    public static void write(String path, String content) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if(parent != null && !parent.exists()) {
            if(!parent.mkdirs()) throw new IOException("Failed to create parent directory");
        }
        try(FileOutputStream outStream = new FileOutputStream(file)) {
            IOUtils.write(content, outStream);
        }
    }
}