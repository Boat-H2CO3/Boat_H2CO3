package org.koishi.launcher.h2co3.launcher;

import static org.koishi.launcher.h2co3.core.game.H2CO3GameHelper.getJavaPath;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.game.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.game.MinecraftVersion;
import org.koishi.launcher.h2co3.core.game.TouchInjector;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.OperatingSystem;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.stream.Stream;

public class H2CO3LauncherHelper {

    private static final String TAG = H2CO3LauncherHelper.class.getSimpleName();
    private static final String TASK_TITLE_FORMAT = "------------------- %s -------------------";
    private static final String ARCHITECTURE_FORMAT = "Architecture: %s";
    private static final String CPU_FORMAT = "CPU: %s";
    private static final String UNSUPPORTED_ARCHITECTURE_ERROR = "Unsupported architecture!";
    private static final String ENV_FORMAT = "Env: %s=%s";
    private static final String WORKING_DIRECTORY_FORMAT = "Working directory: %s";
    private static final String JVM_EXITED_WITH_CODE_FORMAT = "Jvm Exited With Code: %d";

    private static final String JAVA_HOME = "JAVA_HOME";
    private static final String H2CO3Launcher_NATIVEDIR = "H2CO3Launcher_NATIVEDIR";
    private static final String TMPDIR = "TMPDIR";
    private static final String HOME = "HOME";
    private static final String LIBGL_STRING = "LIBGL_STRING";
    private static final String LIBGL_ES = "LIBGL_ES";
    private static final String LIBGL_MIPMAP = "LIBGL_MIPMAP";
    private static final String LIBGL_NORMALIZE = "LIBGL_NORMALIZE";
    private static final String LIBGL_VSYNC = "LIBGL_VSYNC";
    private static final String LIBGL_NOINTOVLHACK = "LIBGL_NOINTOVLHACK";
    private static final String LIBGL_NOERROR = "LIBGL_NOERROR";
    private static final String LIBGL_NAME = "LIBGL_NAME";
    private static final String LIBEGL_NAME = "LIBEGL_NAME";

    public static void printTaskTitle(H2CO3LauncherBridge bridge, String task) {
        bridge.getCallback().onLog(String.format(TASK_TITLE_FORMAT, task));
    }

    public static void logStartInfo(H2CO3LauncherBridge bridge, String task) {
        printTaskTitle(bridge, "Start " + task);
        bridge.getCallback().onLog(String.format(ARCHITECTURE_FORMAT, Architecture.archAsString(Architecture.getDeviceArchitecture())));
        bridge.getCallback().onLog(String.format(CPU_FORMAT, Build.HARDWARE));
    }

    public static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new HashMap<>();
        try (BufferedReader jreReleaseReader = new BufferedReader(new FileReader(javaPath + "/release"))) {
            String currLine;
            while ((currLine = jreReleaseReader.readLine()) != null) {
                if (currLine.contains("=")) {
                    String[] keyValue = currLine.split("=");
                    jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
                }
            }
        }
        return jreReleaseMap;
    }

    public static String getJreLibDir(String javaPath) throws IOException {
        String jreArchitecture = readJREReleaseProperties(javaPath).get("OS_ARCH");
        if (Architecture.archAsInt(jreArchitecture) == Architecture.ARCH_X86) {
            jreArchitecture = "i386/i486/i586";
        }
        String jreLibDir = File.separator + "lib";
        if (jreArchitecture == null) {
            throw new IOException(UNSUPPORTED_ARCHITECTURE_ERROR);
        }
        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, "lib" + File.separator + arch);
            if (file.exists() && file.isDirectory()) {
                jreLibDir = File.separator + "lib" + File.separator + arch;
            }
        }
        return jreLibDir;
    }

    public static String getJvmLibDir(String javaPath) throws IOException {
        String jreLibDir = getJreLibDir(javaPath);
        File jvmFile = new File(javaPath + jreLibDir + "/server/libjvm.so");
        return jvmFile.exists() ? "/server" : "/client";
    }

    public static String getLibraryPath(Context context, String javaPath) throws IOException {
        String jreLibDir = getJreLibDir(javaPath);
        String jvmLibDir = getJvmLibDir(javaPath);
        return javaPath + jreLibDir + ":" +
                javaPath + jreLibDir + File.separator + "jli:" +
                javaPath + jreLibDir + jvmLibDir + ":" +
                "/system/" + (Architecture.is64BitsDevice() ? "lib64" : "lib") + ":" +
                "/vendor/" + (Architecture.is64BitsDevice() ? "lib64" : "lib") + ":" +
                "/vendor/" + (Architecture.is64BitsDevice() ? "lib64" : "lib") + "/hw" + ":" +
                context.getApplicationInfo().nativeLibraryDir;
    }


    public static String[] rebaseArgs(Context context, int width, int height) throws IOException {
        final CommandBuilder command = getMcArgs(context, width, height);
        List<String> rawCommandLine = command.asList();
        if (rawCommandLine.stream().anyMatch(StringUtils::isBlank)) {
            throw new IllegalStateException("Illegal command line " + rawCommandLine);
        }
        List<String> argList = new ArrayList<>(rawCommandLine);
        argList.add(0, getJavaPath() + "/bin/java");
        return argList.toArray(new String[0]);
    }

    public static void addCommonEnv(Context context, HashMap<String, String> envMap) {
        envMap.put(HOME, H2CO3Tools.LOG_DIR);
        envMap.put(JAVA_HOME, getJavaPath());
        envMap.put(H2CO3Launcher_NATIVEDIR, context.getApplicationInfo().nativeLibraryDir);
        envMap.put(TMPDIR, context.getCacheDir().getAbsolutePath());
    }

    public static void addRendererEnv(Context context, HashMap<String, String> envMap, String render) {
        envMap.put(LIBGL_STRING, render);
        if (render.equals(H2CO3Tools.GL_GL114)) {
            envMap.put(LIBGL_NAME, "libgl4es.so");
            envMap.put(LIBEGL_NAME, "libEGL.so");
            setGLValues(envMap, "2", "3", "1", "1", "1", "1");
        } else if (render.equals(H2CO3Tools.GL_VGPU)) {
            envMap.put(LIBGL_NAME, "libvgpu.so");
            envMap.put(LIBEGL_NAME, "libEGL.so");
            setGLValues(envMap, "2", "3", "1", "1", "1", "1");
        } else if (render.equals(H2CO3Tools.GL_VIRGL)) {
            envMap.put(LIBGL_NAME, "libOSMesa_81.so");
            envMap.put(LIBEGL_NAME, "libEGL.so");
            setGLValues(envMap, "2", "3", "1", "1", "1", "1");
            envMap.put("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
            envMap.put("MESA_GL_VERSION_OVERRIDE", "4.3");
            envMap.put("MESA_GLSL_VERSION_OVERRIDE", "430");
            envMap.put("force_glsl_extensions_warn", "true");
            envMap.put("allow_higher_compat_version", "true");
            envMap.put("allow_glsl_extension_directive_midshader", "true");
            envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
            envMap.put("VTEST_SOCKET_NAME", new File(context.getCacheDir().getAbsolutePath(), ".virgl_test").getAbsolutePath());
            envMap.put("GALLIUM_DRIVER", "virpipe");
            envMap.put("OSMESA_NO_FLUSH_FRONTBUFFER", "1");
        } else if (render.equals(H2CO3Tools.GL_ZINK)) {
            envMap.put(LIBGL_NAME, "libOSMesa_8.so");
            envMap.put(LIBEGL_NAME, "libEGL.so");
            setGLValues(envMap, "2", "3", "1", "1", "1", "1");
            envMap.put("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
            envMap.put("MESA_GL_VERSION_OVERRIDE", "4.6");
            envMap.put("MESA_GLSL_VERSION_OVERRIDE", "460");
            envMap.put("force_glsl_extensions_warn", "true");
            envMap.put("allow_higher_compat_version", "true");
            envMap.put("allow_glsl_extension_directive_midshader", "true");
            envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
            envMap.put("VTEST_SOCKET_NAME", new File(context.getCacheDir().getAbsolutePath(), ".virgl_test").getAbsolutePath());
            envMap.put("GALLIUM_DRIVER", "zink");
        } else if (render.equals(H2CO3Tools.GL_ANGLE)) {
            envMap.put(LIBGL_NAME, "libtinywrapper.so");
            envMap.put(LIBEGL_NAME, "libEGL_angle.so");
            envMap.put(LIBGL_ES, "3");
        }
    }

    public static void setGLValues(HashMap<String, String> envMap, String libglEs, String libglMipmap, String libglNormalize, String libglVsync, String libglNointovlhack, String libglNoerror) {
        envMap.put(LIBGL_ES, libglEs);
        envMap.put(LIBGL_MIPMAP, libglMipmap);
        envMap.put(LIBGL_NORMALIZE, libglNormalize);
        envMap.put(LIBGL_VSYNC, libglVsync);
        envMap.put(LIBGL_NOINTOVLHACK, libglNointovlhack);
        envMap.put(LIBGL_NOERROR, libglNoerror);
    }

    public static void setEnv(Context context, H2CO3LauncherBridge bridge) {
        HashMap<String, String> envMap = new HashMap<>(8);
        addCommonEnv(context, envMap);
        addRendererEnv(context, envMap, H2CO3GameHelper.getRender());
        printTaskTitle(bridge, "Env Map");
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            bridge.getCallback().onLog(String.format(ENV_FORMAT, entry.getKey(), entry.getValue()));
            bridge.setenv(entry.getKey(), entry.getValue());
        }
        printTaskTitle(bridge, "Env Map");
    }

    public static void setUpJavaRuntime(Context context, H2CO3LauncherBridge bridge) throws IOException {
        String jreLibDir = getJavaPath() + getJreLibDir(getJavaPath());
        String jliLibDir = new File(jreLibDir + "/jli/libjli.so").exists() ? jreLibDir + "/jli" : jreLibDir;
        String jvmLibDir = jreLibDir + getJvmLibDir(getJavaPath());
        // dlopen jre
        bridge.dlopen(jliLibDir + "/libjli.so");
        bridge.dlopen(jvmLibDir + "/libjvm.so");
        bridge.dlopen(jreLibDir + "/libfreetype.so");
        bridge.dlopen(jreLibDir + "/libverify.so");
        bridge.dlopen(jreLibDir + "/libjava.so");
        bridge.dlopen(jreLibDir + "/libnet.so");
        bridge.dlopen(jreLibDir + "/libnio.so");
        bridge.dlopen(jreLibDir + "/libawt.so");
        bridge.dlopen(jreLibDir + "/libawt_headless.so");
        bridge.dlopen(jreLibDir + "/libfontmanager.so");
        bridge.dlopen(jreLibDir + "/libtinyiconv.so");
        bridge.dlopen(jreLibDir + "/libinstrument.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/libglfw.so");
        bridge.dlopen(context.getApplicationInfo().nativeLibraryDir + "/liblwjgl.so");
        File javaPath = new File(getJavaPath());
        for (File file : locateLibs(javaPath)) {
            bridge.dlopen(file.getAbsolutePath());
        }
    }

    public static ArrayList<File> locateLibs(File path) throws IOException {
        ArrayList<File> returnValue = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(path.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".so"))
                    .forEach(p -> returnValue.add(p.toFile()));
        }
        return returnValue;
    }

    public static void setupGraphicAndSoundEngine(Context context, H2CO3LauncherBridge bridge) {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        bridge.dlopen(nativeDir + "/libopenal.so");
    }

    public static void launch(Context context, H2CO3LauncherBridge bridge, int width, int height, String task) throws IOException {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseArgs(context, width, height);
        for (String arg : args) {
            bridge.getCallback().onLog(task + " argument: " + arg);
        }
        bridge.setupJLI();
        bridge.setLdLibraryPath(getLibraryPath(context, getJavaPath()));
        bridge.getCallback().onLog("Hook exit " + (bridge.setupExitTrap(bridge) == 0 ? "success" : "failed"));
        int exitCode = bridge.jliLaunch(args);
        Log.e(TAG, String.format(JVM_EXITED_WITH_CODE_FORMAT, exitCode));
        bridge.onExit(exitCode);
    }

    public static H2CO3LauncherBridge launchMinecraft(Context context, int width, int height) {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_DIR + "/latest_game.txt");
        Logging.LOG.log(Level.INFO, "surface ready, start jvm now!");
        Thread gameThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "Minecraft");

                // env
                setEnv(context, bridge);

                // setup java runtime
                setUpJavaRuntime(context, bridge);

                // setup graphic and sound engine
                setupGraphicAndSoundEngine(context, bridge);

                // set working directory
                bridge.getCallback().onLog(String.format(WORKING_DIRECTORY_FORMAT, H2CO3GameHelper.getGameDirectory()));
                bridge.chdir(H2CO3GameHelper.getGameDirectory());

                // launch game
                launch(context, bridge, width, height, "Minecraft");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gameThread.setPriority(Thread.MAX_PRIORITY);
        bridge.setThread(gameThread);

        return bridge;
    }

    public static H2CO3LauncherBridge launchJarExecutor(Context context, int width, int height) {

        // initialize FCLBridge
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_FILE_PATH + "/latest_jar_executor.log");
        Thread javaGUIThread = new Thread(() -> {
            try {

                logStartInfo(bridge, "Jar Executor");

                // env
                setEnv(context, bridge);

                // setup java runtime
                setUpJavaRuntime(context, bridge);

                // setup graphic and sound engine
                setupGraphicAndSoundEngine(context, bridge);

                // set working directory
                bridge.getCallback().onLog(String.format(WORKING_DIRECTORY_FORMAT, H2CO3GameHelper.getGameDirectory()));
                bridge.chdir(H2CO3GameHelper.getGameDirectory());

                // launch jar executor
                launch(context, bridge, width, height, "Jar Executor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bridge.setThread(javaGUIThread);

        return bridge;
    }

    public static H2CO3LauncherBridge launchAPIInstaller(Context context, int width, int height) {

        // initialize FCLBridge
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(H2CO3Tools.LOG_DIR + "/latest_api_installer.log");
        Thread apiInstallerThread = new Thread(() -> {
            try {

                logStartInfo(bridge, "API Installer");

                // env
                setEnv(context, bridge);

                // setup java runtime
                setUpJavaRuntime(context, bridge);

                // set working directory
                bridge.getCallback().onLog(String.format(WORKING_DIRECTORY_FORMAT, H2CO3GameHelper.getGameDirectory()));
                bridge.chdir(H2CO3GameHelper.getGameDirectory());

                // launch api installer
                launch(context, bridge, width, height, "API Installer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bridge.setThread(apiInstallerThread);

        return bridge;
    }

    public static CommandBuilder getMcArgs(Context context, int width, int height) throws IOException {
        H2CO3Tools.loadPaths(context);
        H2CO3GameHelper.setRender(H2CO3Tools.GL_GL114);
        MinecraftVersion version = MinecraftVersion.fromDirectory(new File(H2CO3GameHelper.getGameCurrentVersion()));
        String lwjglPath = H2CO3Tools.RUNTIME_DIR + "/h2co3Launcher/lwjgl";
        String javaPath = getJavaPath();
        boolean highVersion = version.minimumLauncherVersion >= 21;
        String classPath;
        boolean isJava8 = javaPath.equals(H2CO3Tools.JAVA_8_PATH);
        if (!highVersion) {
            classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(false, isJava8);
        } else {
            classPath = lwjglPath + "/lwjgl.jar:" + version.getClassPath(true, isJava8);
        }
        CommandBuilder args = new CommandBuilder();
        addCacioOptions(args, height, width, javaPath);
        args.add("-cp");
        args.add(classPath);
        args.addDefault("-Djava.library.path=", getLibraryPath(context, javaPath));
        args.addDefault("-Djna.boot.library.path=", H2CO3Tools.NATIVE_LIB_DIR);
        args.addDefault("-Dfml.earlyprogresswindow=", "false");
        args.addDefault("-Dorg.lwjgl.util.DebugLoader=", "true");
        args.addDefault("-Dorg.lwjgl.util.Debug=", "true");
        args.addDefault("-Dos.name=", "Linux");
        args.addDefault("-Dos.version=Android-" , Build.VERSION.RELEASE);
        args.addDefault("-Dlwjgl.platform=", "H2CO3Launcher");
        args.addDefault("-Duser.language=", System.getProperty("user.language"));
        args.addDefault("-Dwindow.width=", String.valueOf(width));
        args.addDefault("-Dwindow.height=", String.valueOf(height));

        args.addDefault("-Djava.rmi.server.useCodebaseOnly=", "true");
        args.addDefault("-Dcom.sun.jndi.rmi.object.trustURLCodebase=", "false");
        args.addDefault("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=", "false");

        Charset encoding = OperatingSystem.NATIVE_CHARSET;
        String fileEncoding = args.addDefault("-Dfile.encoding=", encoding.name());
        if (fileEncoding != null && !"-Dfile.encoding=COMPAT".equals(fileEncoding)) {
            try {
                encoding = Charset.forName(fileEncoding.substring("-Dfile.encoding=".length()));
            } catch (Throwable ex) {
                Logging.LOG.log(Level.WARNING, "Bad file encoding", ex);
            }
        }

        args.addDefault("-Dsun.stdout.encoding=", encoding.name());
        args.addDefault("-Dsun.stderr.encoding=", encoding.name());

        args.addDefault("-Dfml.ignoreInvalidMinecraftCertificates=", "true");
        args.addDefault("-Dfml.ignorePatchDiscrepancies=", "true");
        args.addDefault("-Duser.timezone=", TimeZone.getDefault().getID());
        args.addDefault("-Duser.home=", H2CO3GameHelper.getGameDirectory());
        args.addDefault("-Dorg.lwjgl.vulkan.libname=", "libvulkan.so");

        if (H2CO3GameHelper.getRender().equals(H2CO3Tools.GL_VIRGL)) {
            args.addDefault("-Dorg.lwjgl.opengl.libname=", "libGL.so.1");
        } else {
            args.addDefault("-Dorg.lwjgl.opengl.libname=", "libgl4es.so");
        }
        args.addDefault("-Djava.io.tmpdir=", H2CO3Tools.CACHE_DIR);

        String[] accountArgs = new String[0];
        Collections.addAll(args.asList(), accountArgs);
        String[] JVMArgs = version.getJVMArguments();
        for (String JVMArg : JVMArgs) {
            if (JVMArg.startsWith("-DignoreList") && !JVMArg.endsWith("," + new File(H2CO3GameHelper.getGameCurrentVersion()).getName() + ".jar")) {
                JVMArg = JVMArg + "," + new File(H2CO3GameHelper.getGameCurrentVersion()).getName() + ".jar";
            }
            if (!JVMArg.startsWith("-DFabricMcEmu") && !JVMArg.startsWith("net.minecraft.client.main.Main")) {
                args.add(JVMArg);
            }
        }
        args.add("-Xms" + "1024" + "M");
        args.add("-Xmx" + "6000" + "M");
        args.add(version.mainClass);
        String[] minecraftArgs = version.getMinecraftArguments(highVersion);
        args.add(minecraftArgs);
        args.add("--width");
        args.add(String.valueOf(width));
        args.add("--height");
        args.add(String.valueOf(height));
        return TouchInjector.rebaseArguments(args);
    }

    public static void addCacioOptions(CommandBuilder args, int height, int width, String javaPath) {
        boolean isJava8 = javaPath.equals(H2CO3Tools.JAVA_8_PATH);
        boolean isJava11 = javaPath.equals(H2CO3Tools.JAVA_11_PATH);
        args.addDefault("-Djava.awt.headless=", "false");
        args.addDefault("-Dcacio.managed.screensize=", width + "x" + height);
        args.addDefault("-Dcacio.font.fontmanager=", "sun.awt.X11FontManager");
        args.addDefault("-Dcacio.font.fontscaler=", "sun.font.FreetypeFontScaler");
        args.addDefault("-Dswing.defaultlaf=", "javax.swing.plaf.metal.MetalLookAndFeel");

        if (isJava8) {
            args.addDefault("-Dawt.toolkit=", "net.java.openjdk.cacio.ctc.CTCToolkit");
            args.addDefault("-Djava.awt.graphicsenv=", "net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
            args.addDefault("-Dawt.toolkit=", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
            args.addDefault("-Djava.awt.graphicsenv=", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
            args.addDefault("-Djava.system.class.loader=", "com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");

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
        cacioClasspath.append("-Xbootclasspath/").append(isJava8 ? "p" : "a");
        File cacioDir = new File(isJava8 ? H2CO3Tools.CACIOCAVALLO_8_DIR : isJava11 ? H2CO3Tools.CACIOCAVALLO_11_DIR : H2CO3Tools.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        args.add(cacioClasspath.toString());
    }

    public interface LogReceiver {
        void pushLog(String log);
        String getLogs();
    }

    public static class DefaultLogReceiver implements LogReceiver {
        private final List<String> logs = new ArrayList<>();

        @Override
        public synchronized void pushLog(String log) {
            logs.add(log);
        }

        @Override
        public synchronized String getLogs() {
            List<String> logsCopy = new ArrayList<>(logs);
            return String.join("\n", logsCopy);
        }
    }

}