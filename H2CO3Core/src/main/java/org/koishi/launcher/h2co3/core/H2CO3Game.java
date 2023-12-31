package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.getBoatValueString;
import static org.koishi.launcher.h2co3.core.H2CO3Tools.setBoatValue;

import android.content.Context;
import android.os.Build;

import org.koishi.launcher.h2co3.core.game.MinecraftVersion;
import org.koishi.launcher.h2co3.core.game.TouchInjector;
import org.koishi.launcher.h2co3.core.utils.Architecture;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;
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
        return getBoatValueString("h2co3_launcher_render", H2CO3Tools.GL_GL114);
    }

    public static void setRender(String path) {
        setBoatValue("h2co3_launcher_render", path);
    }

    public static String getJavaPath() {
        return getBoatValueString("h2co3_launcher_java", H2CO3Tools.JAVA_8_PATH);
    }

    public static void setJavaPath(String path) {
        setBoatValue("h2co3_launcher_java", path);
    }

    public static String getRuntimePath() {
        return getBoatValueString("runtime_path", H2CO3Tools.RUNTIME_DIR);
    }

    public static void setRuntimePath(String path) {
        setBoatValue("runtime_path", path);
    }

    public static String getH2CO3Home() {
        return getBoatValueString("h2co3_home", H2CO3Tools.PUBLIC_FILE_PATH);
    }

    public static void setH2CO3Home(String home) {
        setBoatValue("h2co3_home", home);
    }

    public static String getBackground() {
        return getBoatValueString("background", "");
    }

    public static void setBackground(String background) {
        setBoatValue("background", background);
    }

    public static String getGameDirectory() {
        return getBoatValueString("game_directory", H2CO3Tools.MINECRAFT_DIR);
    }

    public static void setGameDirectory(String directory) {
        setBoatValue("game_directory", directory);
    }

    public static String getGameAssets() {
        return getBoatValueString("game_assets", H2CO3Tools.MINECRAFT_DIR + "/assets/virtual/legacy/");
    }

    public static void setGameAssets(String assets) {
        setBoatValue("game_assets", assets);
    }

    public static String getGameAssetsRoot() {
        return getBoatValueString("game_assets_root", H2CO3Tools.MINECRAFT_DIR + "/assets/");
    }

    public static void setGameAssetsRoot(String assetsRoot) {
        setBoatValue("game_assets_root", assetsRoot);
    }

    public static String[] getExtraJavaFlags() {
        return new String[]{getBoatValueString("extra_java_flags", "")};
    }

    public static void setExtraJavaFlags(String[] javaFlags) {
        setBoatValue("extra_java_flags", javaFlags);
    }

    public static String[] getExtraMinecraftFlags() {
        return new String[]{getBoatValueString("extra_minecraft_flags", "")};
    }

    public static void setExtraMinecraftFlags(String[] minecraftFlags) {
        setBoatValue("extra_minecraft_flags", minecraftFlags);
    }

    public static String getGameCurrentVersion() {
        return getBoatValueString("current_version", "null");
    }

    public static void setGameCurrentVersion(String version) {
        setBoatValue("current_version", version);
    }

    public static Vector<String> getMcArgs(H2CO3Game config, Context context, int width, int height) {
        try {
            MinecraftVersion version = MinecraftVersion.fromDirectory(new File(getGameCurrentVersion()));
            String lwjglPath = H2CO3Tools.RUNTIME_DIR + "/boat/lwjgl";
            String javaPath = getJavaPath();
            boolean highVersion = version.minimumLauncherVersion >= 21;
            String libraryPath;
            String classPath;
            boolean isJava17 = javaPath.equals(H2CO3Tools.JAVA_17_PATH);
            if (!highVersion) {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(false, isJava17);
            } else {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(true, isJava17);
            }

            String nativeDir = context.getApplicationInfo().nativeLibraryDir;
            String libDirName = Architecture.is64BitsDevice() ? "lib64" : "lib";
            String jliLibDir = "/jli";
            String split = ":";

            libraryPath = javaPath +
                    "/lib/" + (isJava17 ? "" : getArchitectureString(Architecture.getDeviceArchitecture())) +
                    split +

                    javaPath +
                    "/lib/" + (isJava17 ? jliLibDir : getArchitectureString(Architecture.getDeviceArchitecture()) + "/" + jliLibDir) +
                    split +

                    javaPath +
                    "/lib/" + (isJava17 ? "server" : getArchitectureString(Architecture.getDeviceArchitecture()) + "/server") +
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

            Vector<String> args = new Vector<>();
            args.add(javaPath + "/bin/java");
            addCacioOptions(args, isJava17, width, height);
            args.add("-cp");
            args.add(classPath);
            args.add("-Djava.library.path=" + libraryPath);
            args.add("-Djna.boot.library.path=" + H2CO3Tools.NATIVE_LIB_DIR);
            args.add("-Dfml.earlyprogresswindow=false");
            args.add("-Dorg.lwjgl.util.DebugLoader=true");
            args.add("-Dorg.lwjgl.util.Debug=true");
            args.add("-Dos.name=Linux");
            args.add("-Dos.version=Android-" + Build.VERSION.RELEASE);
            args.add("-Dlwjgl.platform=H2CO3");
            args.add("-Duser.language=" + System.getProperty("user.language"));
            args.add("-Dwindow.width=" + width);
            args.add("-Dwindow.height=" + height);

            args.add("-Djava.rmi.server.useCodebaseOnly=true");
            args.add("-Dcom.sun.jndi.rmi.object.trustURLCodebase=false");
            args.add("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false");

            args.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            args.add("-Dfml.ignorePatchDiscrepancies=true");
            args.add("-Dh2co3.injector=3:bib:z:s:a");

            args.add("-Duser.timezone=" + TimeZone.getDefault().getID());
            args.add("-Duser.home=" + H2CO3Game.getGameDirectory());

            if (getRender().equals(H2CO3Tools.GL_VIRGL)) {
                args.add("-Dorg.lwjgl.opengl.libname=libGL.so.1");
            } else {
                args.add("-Dorg.lwjgl.opengl.libname=libgl4es.so");
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
                if (!JVMArg.startsWith("-DFabricMcEmu") && !JVMArg.startsWith("net.minecraft.client.main.Main")) {
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

    private static String getArchitectureString(int architecture) {
        return switch (architecture) {
            case Architecture.ARCH_ARM -> "aarch32";
            case Architecture.ARCH_X86 -> "i386";
            case Architecture.ARCH_X86_64 -> "amd64";
            default -> "aarch64";
        };
    }

    private static void addCacioOptions(Vector<String> args, boolean isJava17, int width, int height) {
        args.add("-Djava.awt.headless=false");
        args.add("-Dcacio.managed.screensize=" + width + "x" + height);
        args.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        args.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        args.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");

        if (!isJava17) {
            args.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
            args.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
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

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(!isJava17 ? "p" : "a");
        File cacioDir = new File(!isJava17 ? H2CO3Tools.CACIOCAVALLO_8_DIR : H2CO3Tools.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        args.add(cacioClasspath.toString());
    }
}