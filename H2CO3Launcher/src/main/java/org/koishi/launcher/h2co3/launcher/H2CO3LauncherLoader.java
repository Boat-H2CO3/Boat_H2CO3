package org.koishi.launcher.h2co3.launcher;

import static org.koishi.launcher.h2co3.core.H2CO3Game.getArchitectureString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.launcher.function.H2CO3LauncherCallback;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class H2CO3LauncherLoader {
    private static final String H2CO3_LIB_DIR = H2CO3Tools.RUNTIME_DIR + "/h2co3_launcher";
    public static final String LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/minecraft_log.log";
    private static final String ARGS_FILE_PATH = H2CO3Tools.LOG_DIR + "/boat_args.txt";
    private static final String SERVICE_LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/h2co3_service_log.txt";
    private static final String API_INSTALLER_LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/h2co3_api_installer_log.txt";
    private static final String VIRGL_TEST_SOCKET_NAME = ".virgl_test";
    private static final String VIRGL_TEST_SOCKET_PATH = H2CO3Tools.CACHE_DIR + "/" + VIRGL_TEST_SOCKET_NAME;

    public static LogReceiver logReceiver;

    static {
        System.loadLibrary("h2co3launcher");
    }

    public static native int chdir(String path);

    public static native void saveLogToPath(String path);

    public static native void redirectStdio();

    public static native void setenv(String name, String value);

    public static native int dlopen(String name);

    public static native void patchLinker();

    public static native int dlexec(String[] args);

    public static native int setupExitTrap(Context context);

    @SuppressLint("SuspiciousIndentation")
    public static void launchMinecraft(Handler handler, Context context, String javaPath, String home, boolean highVersion, List<String> args, String renderer, String gameDir, H2CO3LauncherCallback callback) {
        handler.post(callback::onStart);
        int architecture = Architecture.getDeviceArchitecture();
        Boolean isJava8 = javaPath.equals(H2CO3Tools.JAVA_8_PATH);
        String arch = getArchitectureString(architecture);
        String[] finalArgs = filterEmptyArgs(args);
        int exitCode = dlexec(finalArgs);
        patchLinker();
        try {
            setEnvironmentVariables(home, javaPath, renderer, context, highVersion);
            loadNativeLibraries(javaPath, arch, isJava8);
            saveLogToPath(LOG_FILE_PATH);
            receiveLog("114514");
            chdir(gameDir);
            Log.e("H2OC3", String.valueOf(exitCode));
        } catch (Exception e) {
            handler.post(() -> callback.onError(e));
        } finally {
            if (exitCode == -1 || exitCode == -2) {
                ((H2CO3LauncherActivity) context).exit(context,exitCode);
            }
        }
    }

    private static String[] filterEmptyArgs(List<String> args) {
        return args.stream()
                .filter(arg -> !arg.trim().isEmpty())
                .toArray(String[]::new);
    }

    private static void loadNativeLibraries(String javaPath, String arch, Boolean isJava8) {
        String[] libraries;
        if (isJava8) {
            libraries = new String[]{
                    "jli/libjli.so",
                    "server/libjvm.so",
                    "libfreetype.so",
                    "libverify.so",
                    "libjava.so",
                    "libnet.so",
                    "libnio.so",
                    "libawt.so",
                    "libawt_headless.so",
                    "libfontmanager.so",
                    "libtinyiconv.so",
                    "libinstrument.so"
            };
        } else {
            libraries = new String[]{
                    "libjli.so",
                    "server/libjvm.so",
                    "libfreetype.so",
                    "libverify.so",
                    "libjava.so",
                    "libnet.so",
                    "libnio.so",
                    "libawt.so",
                    "libawt_headless.so",
                    "libfontmanager.so",
                    "libtinyiconv.so",
                    "libinstrument.so"
            };
        }

        ArrayList<File> locatedLibs = locateLibs(new File(javaPath));
        for (String library : libraries) {
            String libraryPath;
            if (isJava8) {
                libraryPath = new File(javaPath, "lib" + File.separator + arch + File.separator + library).getPath();
            } else {
                libraryPath = javaPath + File.separator + "lib" + File.separator + library;
            }
            dlopen(libraryPath);
        }
        for (File file : locatedLibs) {
            dlopen(file.getAbsolutePath());
        }
    }

    public static ArrayList<File> locateLibs(File path) {
        ArrayList<File> returnValue = new ArrayList<>();
        locateLibsHelper(path, returnValue);
        return returnValue;
    }

    private static void locateLibsHelper(File path, ArrayList<File> locatedLibs) {
        File[] list = path.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isFile() && f.getName().endsWith(".so")) {
                    locatedLibs.add(f);
                } else if (f.isDirectory()) {
                    locateLibsHelper(f, locatedLibs);
                }
            }
        }
    }

    private static void setEnvironmentVariables(String home, String javaPath, String renderer, Context context, boolean highVersion) {
        setenv("HOME", home);
        setenv("JAVA_HOME", javaPath);
        setenv("LIBGL_ES", "2");
        setenv("LIBGL_MIPMAP", "3");
        setenv("LIBGL_NORMALIZE", "1");
        setenv("LIBGL_VSYNC", "1");
        setenv("LIBGL_NOINTOVLHACK", "1");
        setenv("H2CO3Launcher_NATIVEDIR", context.getApplicationInfo().nativeLibraryDir);
        if (renderer.equals(H2CO3Tools.GL_VIRGL)) {
            setVirGLEnvironmentVariables(context);
        } else {
            setGL4ESEnvironmentVariables(context, highVersion);
        }
        dlopen(context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
    }

    private static void setVirGLEnvironmentVariables(Context context) {
        String[] libraries = new String[]{context.getApplicationInfo().nativeLibraryDir + "/libOSMesa_8.so"};
        String libraryPaths = Arrays.stream(libraries)
                .map(library -> library.replace("{dir}", context.getApplicationInfo().nativeLibraryDir))
                .collect(Collectors.joining(":"));
        dlopen(libraryPaths);
        setenv("LIBGL_DRIVERS_PATH", context.getApplicationInfo().nativeLibraryDir);
        setenv("MESA_GL_VERSION_OVERRIDE", "4.3");
        setenv("MESA_GLSL_VERSION_OVERRIDE", "430");
        setenv("VIRGL_VTEST_SOCKET_NAME", VIRGL_TEST_SOCKET_PATH);
        setenv("GALLIUM_DRIVER", "virpipe");
        setenv("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
        setenv("LIBGL_STRING", "Holy-VirGLRenderer-Boat_H2CO3");
    }

    private static void setGL4ESEnvironmentVariables(Context context, boolean highVersion) {
        String[] libraries = new String[]{context.getApplicationInfo().nativeLibraryDir + "/libgl4es.so"};
        String libraryPaths = Arrays.stream(libraries)
                .map(library -> library.replace("{dir}", context.getApplicationInfo().nativeLibraryDir))
                .collect(Collectors.joining(":"));
        dlopen(libraryPaths);
        setenv("LIBGL_NAME", "libgl4es.so");
        setenv("LIBEGL_NAME", "libEGL.so");
        setenv("LIBGL_STRING", "Holy-GL4ES");
        if (highVersion) {
            setenv("LIBGL_GL", "32");
        }
    }

    private static void saveArgsToFile(String[] args) {
        String argsString = String.join("\n", args);
        FileTools.writeFile(new File(ARGS_FILE_PATH), argsString);
        setLogPipeReady();
    }

    public static void startVirGLService(String home, String tmpdir) {
        patchLinker();
        try {
            saveLogToPath(SERVICE_LOG_FILE_PATH);
            setenv("HOME", home);
            setenv("TMPDIR", tmpdir);
            setenv("VIRGL_VTEST_SOCKET_NAME", VIRGL_TEST_SOCKET_PATH);
            String[] libraries = new String[]{"libepoxy.so.0", "libvirglrenderer.so"};
            String libraryPaths = Arrays.stream(libraries)
                    .map(library -> library.replace("{dir}", H2CO3_LIB_DIR + "/virgl"))
                    .collect(Collectors.joining(":"));
            dlopen(libraryPaths);
            chdir(home);
            String[] finalArgs = {H2CO3_LIB_DIR + "/virgl/libvirgl_test_server.so", "--no-loop-or-fork", "--use-gles", "--socket-name", VIRGL_TEST_SOCKET_PATH};
            Log.e("H2CO3LauncherLoader", "Exited with code : " + dlexec(finalArgs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int launchJVM(String javaPath, List<String> args, String home) {
        patchLinker();
        try {
            setenv("HOME", home);
            setenv("JAVA_HOME", javaPath);
            String arch = getArchitectureString(Architecture.getDeviceArchitecture());
            String[] libraries = new String[]{"libfreetype.so", "jli/libjli.so", "server/libjvm.so", "libverify.so", "libjava.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so", "libfontmanager.so"};
            String libraryPaths = Arrays.stream(libraries)
                    .map(library -> library.replace("{dir}", javaPath + "/lib/" + arch))
                    .collect(Collectors.joining(":"));
            dlopen(libraryPaths);
            saveLogToPath(API_INSTALLER_LOG_FILE_PATH);
            chdir(home);
            Log.e("H2CO3LauncherLoader", "JVM Args: " + String.join(", ", args));
        } catch (Exception e) {
            Log.e("H2CO3LauncherLoader", "Error occurred: ", e);
            return 1;
        }
        return 0;
    }

    public static void setLogPipeReady() {
        receiveLog("invoke setLogPipeReady");
    }

    public static void receiveLog(String str) {
        if (logReceiver == null) {
            logReceiver = new DefaultLogReceiver();
        }
        logReceiver.pushLog(str);
    }

    public interface LogReceiver {
        void pushLog(String log);

        String getLogs();
    }

    private static class DefaultLogReceiver implements LogReceiver {
        private final List<String> logs = new ArrayList<>();

        @Override
        public void pushLog(String log) {
            logs.add(log);
        }

        @Override
        public String getLogs() {
            return String.join("\n", logs);
        }
    }
}