package org.koishi.launcher.h2co3.core.game;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.getBoatValue;
import static org.koishi.launcher.h2co3.core.H2CO3Tools.getH2CO3Value;
import static org.koishi.launcher.h2co3.core.H2CO3Tools.setBoatValue;
import static org.koishi.launcher.h2co3.core.H2CO3Tools.setH2CO3Value;

import org.koishi.launcher.h2co3.core.H2CO3Tools;

import java.util.HashMap;

public class H2CO3GameHelper extends HashMap<String, String> {

    public final String extraJavaFlags;
    public final String extraMinecraftFlags;
    public boolean touchInjector = false;

    public H2CO3GameHelper() {
        this.extraJavaFlags = "";
        this.extraMinecraftFlags = "";
    }

    public static String getRender() {
        return getH2CO3Value("h2co3_launcher_render", H2CO3Tools.GL_GL114, String.class);
    }

    public static void setRender(String path) {
        setH2CO3Value("h2co3_launcher_render", path);
    }

    public static String getJavaPath() {
        return getBoatValue("h2co3_launcher_java", H2CO3Tools.JAVA_8_PATH, String.class);
    }

    public static void setJavaPath(String path) {
        setBoatValue("h2co3_launcher_java", path);
    }

    public static String getGameDirectory() {
        return getH2CO3Value("game_directory", H2CO3Tools.MINECRAFT_DIR, String.class);
    }

    public static void setGameDirectory(String directory) {
        setBoatValue("game_directory", directory);
    }

    public static String getGameAssetsRoot() {
        return getH2CO3Value("game_assets_root", H2CO3Tools.MINECRAFT_DIR + "/assets/", String.class);
    }

    public static void setGameAssetsRoot(String assetsRoot) {
        setBoatValue("game_assets_root", assetsRoot);
    }

    public static String[] getExtraMinecraftFlags() {
        return new String[]{getBoatValue("extra_minecraft_flags", "", String.class)};
    }

    public void setExtraMinecraftFlags(String[] minecraftFlags) {
        setBoatValue("extra_minecraft_flags", minecraftFlags);
    }

    public static String getGameCurrentVersion() {
        return getBoatValue("current_version", "null", String.class);
    }

    public static void setGameCurrentVersion(String version) {
        setBoatValue("current_version", version);
    }

    public String getRuntimePath() {
        return getH2CO3Value("runtime_path", H2CO3Tools.RUNTIME_DIR, String.class);
    }

    public void setRuntimePath(String path) {
        setH2CO3Value("runtime_path", path);
    }

    public String getH2CO3Home() {
        return getH2CO3Value("h2co3_home", H2CO3Tools.PUBLIC_FILE_PATH, String.class);
    }

    public void setH2CO3Home(String home) {
        setBoatValue("h2co3_home", home);
    }

    public String getBackground() {
        return getH2CO3Value("background", "", String.class);
    }

    public void setBackground(String background) {
        setBoatValue("background", background);
    }

    public String getGameAssets() {
        return getH2CO3Value("game_assets", H2CO3Tools.MINECRAFT_DIR + "/assets/virtual/legacy/", String.class);
    }

    public static void setGameAssets(String assets) {
        setBoatValue("game_assets", assets);
    }

    public String[] getExtraJavaFlags() {
        return new String[]{getBoatValue("extra_java_flags", "", String.class)};
    }

    public void setExtraJavaFlags(String[] javaFlags) {
        setBoatValue("extra_java_flags", javaFlags);
    }

    public static void setDir(String dir) {
        H2CO3GameHelper.setGameDirectory(dir);
        H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
        H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
        H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
    }
}