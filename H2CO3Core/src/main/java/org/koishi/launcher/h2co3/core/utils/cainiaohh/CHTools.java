package org.koishi.launcher.h2co3.core.utils.cainiaohh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class CHTools {

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
    public static String CONTROLLER_DIR;

    public static String MINECRAFT_DIR;
    public static String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/org.koishi.launcher/h2co3/.minecraft";

    public static String AUTHLIB_INJECTOR_PATH;
    public static String MULTIPLAYER_FIX_PATH;
    public static String APP_DATA_PATH;
    public static String PUBLIC_FILE_PATH;
    public static String BOATCFG;
    final CHTools.ZipListener zipListener;
    @SuppressLint("HandlerLeak")
    private final Handler zipHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                zipListener.onStart();
            } else if (msg.what == 1) {
                zipListener.onProgress((int) msg.obj);
            } else if (msg.what == 2) {
                zipListener.onFinish();
            } else if (msg.what == 3) {
                zipListener.onError((String) msg.obj);
            }
        }
    };
    int lastProgress = 0;

    public CHTools(CHTools.ZipListener zipListener) {
        this.zipListener = zipListener;
    }

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
        CONTROLLER_DIR = APP_DATA_PATH + "/keyboards";

        FILES_DIR = context.getFilesDir().getAbsolutePath();

        MINECRAFT_DIR = PUBLIC_FILE_PATH + "/.minecraft";

        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        MULTIPLAYER_FIX_PATH = PLUGIN_DIR + "/MultiplayerFix.jar";

        BOATCFG = PUBLIC_FILE_PATH + "/config.txt";

        init(LOG_DIR);
        init(CACHE_DIR);
        init(RUNTIME_DIR);
        init(JAVA_8_PATH);
        init(JAVA_17_PATH);
        init(BOAT_LIBRARY_DIR);
        init(FILES_DIR);
        init(PLUGIN_DIR);
        init(CONTROLLER_DIR);
        init(MINECRAFT_DIR);
        init(APP_DATA_PATH);
        init(SHARED_COMMON_DIR);
        init(PUBLIC_FILE_PATH);
        init(BOATCFG);
    }

    private static boolean init(String path) {
        if (!new File(path).exists()) {
            return new File(path).mkdirs();
        }
        return true;
    }

    /**************版本隔离核心代码************/
    public static String H2CO3CfgPath() {
        String h2co3Cfg = getBoatCfg("currentVersion", PUBLIC_FILE_PATH) + "/h2co3Cfg.json";
        boolean exH2co3cfg = FileUtils.isFileExists(getBoatCfg("currentVersion", PUBLIC_FILE_PATH) + "/h2co3Cfg.json" );
        if (exH2co3cfg){
        }else {
        }
        String pdir = getExtraCfg("allVerLoad", "false", h2co3Cfg);
        getBoatCfg("currentVersion", PUBLIC_FILE_PATH);
        String H2CO3CfgPath;
        if (pdir.equals("false")) {
            H2CO3CfgPath = PUBLIC_FILE_PATH + "/h2co3Cfg.json";
        } else {
            H2CO3CfgPath = getBoatCfg("currentVersion", PUBLIC_FILE_PATH) + "/h2co3Cfg.json";
        }
        return H2CO3CfgPath;
    }

    //---------------------获取json的值---------------------

    private static Boolean getCfgBoolean(String value, Boolean defaultValue, String dir) {
        try {
            FileInputStream in = new FileInputStream(dir);
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            return json.getString(value).equals("true");
        } catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public static void setBoatJson(String name, String value) {
        try {
            FileInputStream in = new FileInputStream(BOATCFG);
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            json.remove(name);
            json.put(name, value);
            FileWriter fr = new FileWriter(BOATCFG);
            fr.write(json.toString());
            fr.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void setAppJson(String name, String value) {
        try {
            FileInputStream in = new FileInputStream(CHTools.H2CO3CfgPath());
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            json.remove(name);
            json.put(name, value);
            FileWriter fr = new FileWriter(CHTools.H2CO3CfgPath());
            fr.write(json.toString());
            fr.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void setExtraJson(String name, String value, String dir) {
        try {
            FileInputStream in = new FileInputStream(dir);
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            json.remove(name);
            json.put(name, value);
            FileWriter fr = new FileWriter(dir);
            fr.write(json.toString());
            fr.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //h2co3cfg
    public static String getBoatCfg(String name, String defaultValue) {
        try {
            FileInputStream in = new FileInputStream(BOATCFG);
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            return json.getString(name);
        } catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public static String getAppCfg(String name, String defaultValue) {
        try {
            FileInputStream in = new FileInputStream(CHTools.H2CO3CfgPath());
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            return json.getString(name);
        } catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public static String getExtraCfg(String value, String defaultValue, String dir) {
        try {
            FileInputStream in = new FileInputStream(dir);
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
            String str = new String(b);
            JSONObject json = new JSONObject(str);
            return json.getString(value);
        } catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public interface ZipListener {
        void onStart();

        void onProgress(int progress);

        void onFinish();

        void onError(String err);
    }
}