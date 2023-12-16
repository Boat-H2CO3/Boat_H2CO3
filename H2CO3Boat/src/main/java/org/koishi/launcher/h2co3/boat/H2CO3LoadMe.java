package org.koishi.launcher.h2co3.boat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.koishi.launcher.h2co3.boat.function.H2CO3BoatCallback;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class H2CO3LoadMe {

    private static final String H2CO3_LIB_DIR = H2CO3Tools.RUNTIME_DIR + "/h2co3_boat";

    public static WeakReference<LogReceiver> logReceiver;

    static {
        System.loadLibrary("h2co3_boat");
    }

    public static native int chdir(String path);
    public static native void redirectStdio(String file);
    public static native void setenv(String name, String value);
    public static native int dlopen(String name);
    public static native void patchLinker();
    public static native void setupExitTrap(Context context);
    public static native int dlexec(String[] args);


    @SuppressLint("SuspiciousIndentation")
    public static void launchMinecraft(Handler handler, Context context, String javaPath, String home, boolean highVersion, Vector<String> args, String renderer, String gameDir, H2CO3BoatCallback callback) {
        handler.post(callback::onStart);

        int architecture = Architecture.getDeviceArchitecture();
        String arch = getArchitectureString(architecture);

        boolean isJava17 = javaPath.endsWith("jre_17");

        patchLinker();

        try {
            setEnvironmentVariables(home, javaPath, renderer, context, highVersion);

            loadNativeLibraries(javaPath, arch, isJava17);

            setupExitTrap(context);

            redirectStdio(H2CO3Tools.LOG_DIR + "/client_output.txt");
            chdir(gameDir);

            String[] finalArgs = args.stream().filter(arg -> !arg.equals(" ")).toArray(String[]::new);
            saveArgsToFile(finalArgs);

            Log.d("H2CO3.launchMinecraft", String.valueOf(args));

            int exitCode = dlexec(finalArgs);
            receiveLog("OpenJDK exited with code : " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> callback.onError(e));
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

    private static void setEnvironmentVariables(String home, String javaPath, String renderer, Context context, boolean highVersion) {
        setenv("HOME", home);
        setenv("JAVA_HOME", javaPath);
        setenv("LIBGL_ES", "2");
        setenv("LIBGL_MIPMAP", "3");
        setenv("LIBGL_NORMALIZE", "1");
        setenv("LIBGL_VSYNC", "1");
        setenv("LIBGL_NOINTOVLHACK", "1");

        if (renderer.equals("VirGL")) {
            setVirGLEnvironmentVariables(context);
        } else {
            setGL4ESEnvironmentVariables(context, highVersion);
        }
        dlopen(context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
    }

    private static void setVirGLEnvironmentVariables(Context context) {
        dlopen(context.getApplicationInfo().nativeLibraryDir + "/libOSMesa_8.so");
        setenv("LIBGL_DRIVERS_PATH", context.getApplicationInfo().nativeLibraryDir);
        setenv("MESA_GL_VERSION_OVERRIDE", "4.3");
        setenv("MESA_GLSL_VERSION_OVERRIDE", "430");
        setenv("VIRGL_VTEST_SOCKET_NAME", context.getCacheDir().getAbsolutePath() + "/.virgl_test");
        setenv("GALLIUM_DRIVER", "virpipe");
        setenv("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
        setenv("LIBGL_STRING", "Holy-VirGLRenderer");
    }

    private static void setGL4ESEnvironmentVariables(Context context, boolean highVersion) {
        dlopen(context.getApplicationInfo().nativeLibraryDir + "/libgl4es_114.so");
        setenv("LIBGL_NAME", "libgl4es_114.so");
        setenv("LIBEGL_NAME", "libEGL.so");
        setenv("LIBGL_STRING", "Holy-GL4ES");
        if (highVersion) {
            setenv("LIBGL_GL", "32");
        }
    }

    private static void loadNativeLibraries(String javaPath, String arch, boolean isJava17) {
        List<String> libraries;
        if (isJava17) {
            libraries = List.of("server", "libjvm.so", "libjava.so", "libjli.so", "libverify.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so", "libfreetype.so", "libfontmanager.so", "libawt_headless.so");
        } else {
            libraries = List.of("libfreetype.so", "libpng16.so.16", "libfontmanager.so", "libpng16.so", "jli/libjli.so", "server/libjvm.so", "libverify.so", "libjava.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so");
        }

        libraries.forEach(library -> dlopen(javaPath + "/lib/" + arch + "/" + library));
    }

    private static void saveArgsToFile(String[] args) {
        String argsString = String.join("\n", args);
        FileTools.writeFile(new File(H2CO3Tools.LOG_DIR + "/BoatArgs.txt"), argsString);
        receiveLog(argsString);
    }

    public static void startVirGLService(Context context, String home, String tmpdir) {
        patchLinker();

        try {
            redirectStdio(H2CO3Tools.LOG_DIR + "/h2co3_service_log.txt");

            setenv("HOME", home);
            setenv("TMPDIR", tmpdir);
            setenv("VIRGL_VTEST_SOCKET_NAME", context.getCacheDir().getAbsolutePath() + "/.virgl_test");

            List.of("libepoxy.so.0", "libvirglrenderer.so").forEach(library -> dlopen(H2CO3_LIB_DIR + "/virgl/" + library));

            chdir(home);
            String[] finalArgs = {H2CO3_LIB_DIR + "/virgl/libvirgl_test_server.so", "--no-loop-or-fork", "--use-gles", "--socket-name", context.getCacheDir().getAbsolutePath() + "/.virgl_test"};
            receiveLog("Exited with code : " + dlexec(finalArgs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int launchJVM(String javaPath, ArrayList<String> args, String home) {
        patchLinker();

        try {
            setenv("HOME", home);
            setenv("JAVA_HOME", javaPath);

            String arch = getArchitectureString(Architecture.getDeviceArchitecture());

            List.of("libfreetype.so", "jli/libjli.so", "server/libjvm.so", "libverify.so", "libjava.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so", "libfontmanager.so").forEach(library -> dlopen(javaPath + "/lib/" + arch + "/" + library));

            redirectStdio(H2CO3Tools.LOG_DIR + "/h2co3_api_installer_log.txt");
            chdir(home);

            StringBuilder finalArgs = new StringBuilder();
            args.stream().filter(arg -> !arg.equals(" ")).forEach(arg -> {
                finalArgs.append(arg).append(" ");
                receiveLog("JVM Args:" + arg);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static void receiveLog(String str) {
        if (logReceiver == null || logReceiver.get() == null) {
            Log.e("H2CO3BoatLoadMe.receiveLog", "LogReceiver is null. So use default receiver.");
            logReceiver = new WeakReference<>(new LogReceiver() {
                final StringBuilder builder = new StringBuilder();

                @Override
                public void pushLog(String log) {
                    Log.e("H2CO3BoatLoadMe.receiveLog", log);
                    builder.append(log);
                }

                @Override
                public String getLogs() {
                    return builder.toString();
                }
            });
        } else {
            logReceiver.get().pushLog(str);
        }
    }

    public interface LogReceiver {
        void pushLog(String log);

        String getLogs();
    }

}