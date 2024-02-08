package org.koishi.launcher.h2co3.core;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.*;

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

    public final String extraJavaFlags;
    public final String extraMinecraftFlags;
    public boolean touchInjector = false;

    public H2CO3Game() {
        this.extraJavaFlags = "";
        this.extraMinecraftFlags = "";
    }

    public static String getRender() {
        return getH2CO3Value("h2co3_launcher_render", GL_GL114, String.class);
    }

    public void setRender(String path) {
        setH2CO3Value("h2co3_launcher_render", path);
    }

    public static String getJavaPath() {
        return getBoatValue("h2co3_launcher_java", JAVA_8_PATH, String.class);
    }

    public static void setJavaPath(String path) {
        setBoatValue("h2co3_launcher_java", path);
    }

    public static String getGameDirectory() {
        return getH2CO3Value("game_directory", MINECRAFT_DIR, String.class);
    }

    public static void setGameDirectory(String directory) {
        setBoatValue("game_directory", directory);
    }

    public static String getGameAssetsRoot() {
        return getH2CO3Value("game_assets_root", MINECRAFT_DIR + "/assets/", String.class);
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

    public static Vector<String> getMcArgs(H2CO3Game config, Context context, int width, int height) {
        try {
            MinecraftVersion version = MinecraftVersion.fromDirectory(new File(getGameCurrentVersion()));
            String lwjglPath = RUNTIME_DIR + "/boat/lwjgl";
            String javaPath = getJavaPath();
            boolean highVersion = version.minimumLauncherVersion >= 21;
            String libraryPath;
            String classPath;
            boolean isJava8 = javaPath.equals(JAVA_8_PATH);
            if (!highVersion) {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(false, isJava8);
            } else {
                classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(true, isJava8);
            }

            String nativeDir = context.getApplicationInfo().nativeLibraryDir;
            String libDirName = Architecture.is64BitsDevice() ? "lib64" : "lib";
            String jliLibDir = "/jli";
            String split = ":";

            libraryPath = javaPath +
                    "/lib/" + (isJava8 ? getArchitectureString(Architecture.getDeviceArchitecture()) : "") +
                    split +

                    javaPath +
                    "/lib/" + (isJava8 ? getArchitectureString(Architecture.getDeviceArchitecture()) + "/" + jliLibDir : jliLibDir) +
                    split +

                    javaPath +
                    "/lib/" + (isJava8 ? getArchitectureString(Architecture.getDeviceArchitecture()) + "/server" : "server") +
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
            //addCacioOptions(args, javaPath, width, height);
            args.add("-cp");
            args.add(classPath);
            args.add("-Djava.library.path=" + libraryPath);
            args.add("-Djna.boot.library.path=" + NATIVE_LIB_DIR);
            args.add("-Dfml.earlyprogresswindow=false");
            args.add("-Dorg.lwjgl.util.DebugLoader=true");
            args.add("-Dorg.lwjgl.util.Debug=true");
            args.add("-Dos.name=Linux");
            args.add("-Dos.version=Android-" + Build.VERSION.RELEASE);
            args.add("-Dlwjgl.platform=H2CO3Launcher");
            args.add("-Duser.language=" + System.getProperty("user.language"));
            args.add("-Dwindow.width=" + width);
            args.add("-Dwindow.height=" + height);

            args.add("-Djava.rmi.server.useCodebaseOnly=true");
            args.add("-Dcom.sun.jndi.rmi.object.trustURLCodebase=false");
            args.add("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false");

            args.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
            args.add("-Dfml.ignorePatchDiscrepancies=true");

            args.add("-Duser.timezone=" + TimeZone.getDefault().getID());
            args.add("-Duser.home=" + getGameDirectory());

            if (getRender().equals(GL_VIRGL)) {
                args.add("-Dorg.lwjgl.opengl.libname=libGL.so.1");
            } else {
                args.add("-Dorg.lwjgl.opengl.libname=libgl4es.so");
            }
            args.add("-Djava.io.tmpdir=" + CACHE_DIR);
            loadPaths(context);
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

    public static String getArchitectureString(int architecture) {
        return switch (architecture) {
            case Architecture.ARCH_ARM -> "aarch32";
            case Architecture.ARCH_X86 -> "i386";
            case Architecture.ARCH_X86_64 -> "amd64";
            default -> "aarch64";
        };
    }

    private static void addCacioOptions(Vector<String> args, String javaPath, int width, int height) {
        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(javaPath.equals(JAVA_11_PATH) || javaPath.equals(JAVA_17_PATH) || javaPath.equals(JAVA_21_PATH) ? "a" : "p");
        File cacioDir = new File(CACIOCAVALLO_8_DIR);
        if (javaPath.equals(JAVA_8_PATH)){
            cacioDir = new File(CACIOCAVALLO_8_DIR);
        } else if (javaPath.equals(JAVA_11_PATH)) {
            cacioDir = new File(CACIOCAVALLO_11_DIR);
        }else if (javaPath.equals(JAVA_17_PATH) || javaPath.equals(JAVA_21_PATH)) {
            cacioDir = new File(CACIOCAVALLO_17_DIR);
        }
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        args.add(cacioClasspath.toString());
        args.add("-Djava.awt.headless=false");
        args.add("-Dcacio.managed.screensize=" + width + "x" + height);
        args.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        args.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        args.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
        if (javaPath.equals(JAVA_8_PATH) || javaPath.equals(JAVA_11_PATH)) {
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
            args.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
            args.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
            args.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
            args.add("--add-opens=java.base/java.net=ALL-UNNAMED");

        }
    }

    public String getRuntimePath() {
        return getH2CO3Value("runtime_path", RUNTIME_DIR, String.class);
    }

    public void setRuntimePath(String path) {
        setH2CO3Value("runtime_path", path);
    }

    public String getH2CO3Home() {
        return getH2CO3Value("h2co3_home", PUBLIC_FILE_PATH, String.class);
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
        return getH2CO3Value("game_assets", MINECRAFT_DIR + "/assets/virtual/legacy/", String.class);
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
}