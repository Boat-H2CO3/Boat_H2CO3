package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.utils.Architecture.is64BitsDevice;

import android.content.Context;

import org.koishi.launcher.h2co3.core.game.MinecraftVersion;
import org.koishi.launcher.h2co3.core.game.TouchInjector;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class H2CO3Game extends HashMap<String, String> {

    public boolean touchInjector = false;
    public final String extraJavaFlags;
    public final String extraMinecraftFlags;

    public H2CO3Game() {
        this.extraJavaFlags = "";
        this.extraMinecraftFlags = "";
    }

    public static String getRender() {
        return H2CO3Tools.getBoatValueString("h2co3_boat_render", H2CO3Tools.GL_GL114);
    }

    public static void setRender(String path) {
        H2CO3Tools.setBoatValue("h2co3_boat_render", path);
    }

    public static String getJavaPath() {
        return H2CO3Tools.getBoatValueString("h2co3_boat_java", H2CO3Tools.JAVA_8_PATH);
    }

    public static void setJavaPath(String path) {
        H2CO3Tools.setBoatValue("h2co3_boat_java", path);
    }


    public static String getRuntimePath() {
        return H2CO3Tools.getBoatValueString("runtime_path", H2CO3Tools.RUNTIME_DIR);
    }

    public static void setRuntimePath(String path) {
        H2CO3Tools.setBoatValue("runtime_path", path);
    }

    public static String getH2CO3Home() {
        return H2CO3Tools.getBoatValueString("h2co3_home", H2CO3Tools.PUBLIC_FILE_PATH);
    }

    public static void setH2CO3Home(String home) {
        H2CO3Tools.setBoatValue("h2co3_home", home);
    }

    public static String getBackground() {
        return H2CO3Tools.getBoatValueString("background", "");
    }

    public static void setBackground(String background) {
        H2CO3Tools.setBoatValue("background", background);
    }

    public static String getGameDirectory() {
        return H2CO3Tools.getBoatValueString("game_directory", H2CO3Tools.MINECRAFT_DIR);
    }

    public static void setGameDirectory(String directory) {
        H2CO3Tools.setBoatValue("game_directory", directory);
    }

    public static String getGameAssets() {
        return H2CO3Tools.getBoatValueString("game_assets", H2CO3Tools.MINECRAFT_DIR + "/assets/virtual/legacy/");
    }

    public static void setGameAssets(String assets) {
        H2CO3Tools.setBoatValue("game_assets", assets);
    }

    public static String getGameAssetsRoot() {
        return H2CO3Tools.getBoatValueString("game_assets_root", H2CO3Tools.MINECRAFT_DIR + "/assets/");
    }

    public static void setGameAssetsRoot(String assetsRoot) {
        H2CO3Tools.setBoatValue("game_assets_root", assetsRoot);
    }

    public static String[] getExtraJavaFlags() {
        return new String[]{H2CO3Tools.getBoatValueString("extra_java_flags", "")};
    }

    public static void setExtraJavaFlags(String[] javaFlags) {
        H2CO3Tools.setBoatValue("extra_java_flags", javaFlags);
    }

    public static String[] getExtraMinecraftFlags() {
        return new String[]{H2CO3Tools.getBoatValueString("extra_minecraft_flags", "")};
    }

    public static void setExtraMinecraftFlags(String[] minecraftFlags) {
        H2CO3Tools.setBoatValue("extra_minecraft_flags", minecraftFlags);
    }

    public static String getGameCurrentVersion() {
        return H2CO3Tools.getBoatValueString("current_version", "null");
    }

    public static void setGameCurrentVersion(String version) {
        H2CO3Tools.setBoatValue("current_version", version);
    }


    public static Vector<String> getMcArgs(H2CO3Game config, Context context, int width, int height) {
        try {
            MinecraftVersion version = MinecraftVersion.fromDirectory(new File(getGameCurrentVersion()));
            String lwjglPath = H2CO3Tools.RUNTIME_DIR + "/boat/lwjgl";
            String javaPath = getJavaPath();
            boolean highVersion = version.minimumLauncherVersion >= 21;
            String libraryPath;
            String classPath;
            String r = getRender().equals(H2CO3Tools.GL_VIRGL) ? H2CO3Tools.GL_VIRGL : H2CO3Tools.GL_GL114;
            boolean isJava17 = javaPath.equals(H2CO3Tools.JAVA_17_PATH);
            if (!highVersion) {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(false, isJava17);
            } else {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(true, isJava17);
            }

            String nativeDir = context.getApplicationInfo().nativeLibraryDir;
            String libDirName = is64BitsDevice() ? "lib64" : "lib";
            String jliLibDir = "/jli";
            String split = ":";

            if (isJava17) {
                libraryPath = javaPath +
                        "/lib/" +
                        split +

                        javaPath +
                        "/lib/" +
                        jliLibDir +
                        split +

                        javaPath +
                        "/lib/server" +
                        split +

                        "/system/" +
                        libDirName +
                        split +

                        "/vendor/" +
                        libDirName +
                        split +

                        "/vendor/" +
                        libDirName +
                        "/hw" +
                        split +

                        nativeDir +
                        split +

                        lwjglPath;
            } else {
                libraryPath = javaPath +
                        "/lib/aarch64" +
                        split +

                        javaPath +
                        "/lib/aarch64" +
                        jliLibDir +
                        split +

                        javaPath +
                        "/lib/aarch64/server" +
                        split +

                        "/system/" +
                        libDirName +
                        split +

                        "/vendor/" +
                        libDirName +
                        split +

                        "/vendor/" +
                        libDirName +
                        "/hw" +
                        split +

                        nativeDir +
                        split +

                        lwjglPath;
            }

            Vector<String> args = new Vector<>();
            args.add(javaPath + "/bin/java");
            if (!isJava17) {
                args.add("-Djava.awt.headless=false");
                args.add("-Dcacio.managed.screensize=" + width + "x" + height);
                args.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
                args.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
                if (!isJava17) {
                    args.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
                    args.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
                    args.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
                } else {
                    args.add("-Dcacio.font.fontmanager=com.github.caciocavallosilano.cacio.ctc.CTCFontManager");
                    args.add("-Dawt.toolkit=com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
                    args.add("-Djava.awt.graphicsenv=com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
                    args.add("-Djava.system.class.loader=com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");
                    args.add("--add-exports=java.desktop/java.awt=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED");
                    args.add("--add-exports=java.desktop/sun.font=ALL-UNNAMED");
                    args.add("--add-exports=java.base/sun.security.action=ALL-UNNAMED");
                    args.add("--add-opens=java.base/java.util=ALL-UNNAMED");
                    args.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
                    args.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
                    args.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
                    args.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
                    args.add("--add-opens=java.base/java.net=ALL-UNNAMED");
                }
                String boatPath = H2CO3Tools.RUNTIME_DIR + "/boat";
                StringBuilder cacioClasspath = new StringBuilder();
                cacioClasspath.append("-Xbootclasspath/" + (!isJava17 ? "p" : "a"));
                File cacioDir = new File(boatPath + "/plugin" + "/caciocavallo" + (!isJava17 ? "" : "17"));
                if (cacioDir.exists() && cacioDir.isDirectory()) {
                    for (File file : cacioDir.listFiles()) {
                        if (file.getName().endsWith(".jar")) {
                            cacioClasspath.append(":" + file.getAbsolutePath());
                        }
                    }
                }
                args.add(cacioClasspath.toString());
            }
            args.add("-cp");
            args.add(classPath);
            args.add("-Djava.library.path=" + libraryPath);
            args.add("-Dfml.earlyprogresswindow=false");
            args.add("-Dorg.lwjgl.util.DebugLoader=true");
            args.add("-Dorg.lwjgl.util.Debug=true");
            args.add("-Dos.name=Linux");
            args.add("-Dlwjgl.platform=H2CO3");
            args.add("-Duser.language=" + System.getProperty("user.language"));
            args.add("-Dwindow.width="+ width);
            args.add("-Dwindow.height=" + height);
            if (getRender().equals(H2CO3Tools.GL_VIRGL)) {
                args.add("-Dorg.lwjgl.opengl.libname=libGL.so.1");
            } else {
                args.add("-Dorg.lwjgl.opengl.libname=libgl4es_114.so");
            }
            args.add("-Djava.io.tmpdir=" + H2CO3Tools.CACHE_DIR);
            H2CO3Tools.loadPaths(context);
            String[] accountArgs = new String[0];
            Collections.addAll(args, accountArgs);
            String[] JVMArgs = version.getJVMArguments();
            for (String JVMArg : JVMArgs) {
                if (JVMArg.startsWith("-DignoreList") && !JVMArg.endsWith("," + new File(getGameCurrentVersion()).getName() + ".jar")) {
                    JVMArg = JVMArg + "," + new File(getGameCurrentVersion()).getName() + ".jar";
                }
                if (!JVMArg.startsWith("-DFabricMcEmu") && !JVMArg.startsWith("net.minecraft.boat.main.Main")) {
                    args.add(JVMArg);
                }
            }
            args.add("-Xms" + "1024" + "M");
            args.add("-Xmx" + "6000" + "M");
            args.add(version.mainClass);
            String[] minecraftArgs = version.getMinecraftArguments(config, highVersion);
            Collections.addAll(args, minecraftArgs);
            String[] extraMinecraftArgs = getExtraMinecraftFlags();
            Collections.addAll(args, extraMinecraftArgs);
            return TouchInjector.rebaseArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}