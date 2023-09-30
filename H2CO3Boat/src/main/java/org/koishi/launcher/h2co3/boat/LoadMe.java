package org.koishi.launcher.h2co3.boat;

import static org.koishi.launcher.h2co3.core.utils.Architecture.ARCH_ARM;
import static org.koishi.launcher.h2co3.core.utils.Architecture.ARCH_ARM64;
import static org.koishi.launcher.h2co3.core.utils.Architecture.ARCH_X86;
import static org.koishi.launcher.h2co3.core.utils.Architecture.ARCH_X86_64;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import org.koishi.launcher.h2co3.boat.function.H2CO3Callback;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.cainiaohh.CHTools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class LoadMe {

    // 设置H2CO3_LIB_DIR和mReceiver的静态变量
    public static String H2CO3_LIB_DIR;

    // 加载本地库
    static {
        System.loadLibrary("h2co3_boat");
    }

    // 声明native方法
    public static native int chdir(String path);
    public static native void redirectStdio(String file);
    public static native void setenv(String name, String value);
    public static native int dlopen(String name);
    public static native void patchLinker();
    public static native void setupExitTrap(Context context);
    public static native int dlexec(String[] args);

    // 启动Minecraft的方法
    @SuppressLint("SuspiciousIndentation")
    public static void launchMinecraft(Handler handler, Context context, String javaPath, String home, boolean highVersion, Vector<String> args, String renderer, String gameDir, H2CO3Callback callback) {
        // 在handler中执行callback的onStart方法
        handler.post(callback::onStart);

        // 设置H2CO3_LIB_DIR
        H2CO3_LIB_DIR = CHTools.RUNTIME_DIR + "/h2co3_boat";

        // 判断设备架构并设置arch变量
        int architecture = Architecture.getDeviceArchitecture();
        String arch = switch (architecture) {
            case ARCH_ARM -> "aarch32";
            case ARCH_X86 -> "i386";
            case ARCH_X86_64 -> "amd64";
            default -> "aarch64";
        };

        boolean isJava17 = javaPath.endsWith("jre_17");

        // 获取nativeLibraryDir
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;

        try {
            // 设置环境变量
            setenv("HOME", home);
            setenv("JAVA_HOME", javaPath);
            setenv("LIBGL_ES", "2");
            setenv("LIBGL_MIPMAP", "3");
            setenv("LIBGL_NORMALIZE", "1");
            setenv("LIBGL_VSYNC", "1");
            setenv("LIBGL_NOINTOVLHACK", "1");

            // 根据renderer设置环境变量
            if (renderer.equals("VirGL")) {
                dlopen(nativeDir + "/libOSMesa_8.so");
                dlopen(nativeDir + "/libEGL.so");
                setenv("LIBGL_DRIVERS_PATH", H2CO3_LIB_DIR + "/virgl/");
                setenv("MESA_GL_VERSION_OVERRIDE", "4.3");
                setenv("MESA_GLSL_VERSION_OVERRIDE", "430");
                setenv("VIRGL_VTEST_SOCKET_NAME", context.getCacheDir().getAbsolutePath() + "/.virgl_test");
                setenv("GALLIUM_DRIVER", "virpipe");
                setenv("MESA_GLSL_CACHE_DIR", context.getCacheDir().getAbsolutePath());
                setenv("LIBGL_STRING", "Holy-VirGLRenderer");
            } else {
                dlopen(nativeDir + "/libgl4es_114.so");
                dlopen(nativeDir + "/libEGL.so");
                setenv("LIBGL_NAME", "libgl4es_114.so");
                setenv("LIBEGL_NAME", "libEGL.so");
                setenv("LIBGL_STRING", "Holy-GL4ES");
                if (highVersion) {
                    setenv("LIBGL_GL", "32");
                }
            }

            // 加载本地库
            if (isJava17) {
                loadNativeLibraries(javaPath, "server", "libjvm.so", "libjava.so", "libjli.so", "libverify.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so", "libfreetype.so", "libfontmanager.so", "libawt_headless.so");
            } else {
                loadNativeLibraries(javaPath, arch, "libfreetype.so", "libpng16.so.16", "libfontmanager.so", "libpng16.so", "jli/libjli.so", "server/libjvm.so", "libverify.so", "libjava.so", "libnet.so", "libnio.so", "libawt.so", "libawt_headless.so");
            }
            dlopen(nativeDir + "/libopenal.so");
            dlopen(nativeDir + "/libglfw.so");
            dlopen(nativeDir + "/liblwjgl.so");

            setupExitTrap(context);

            redirectStdio(CHTools.LOG_DIR + "/client_output.txt");
            chdir(gameDir);

            String[] finalArgs = new String[args.size()];
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                if (!arg.equals(" ")) {
                    sb.append(arg).append("\n");
                }
            }
            FileTools.writeFile(new File(CHTools.LOG_DIR + "/BoatArgs.txt"), sb.toString());

            int exitCode = dlexec(finalArgs);
            System.out.println("OpenJDK exited with code : " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> callback.onError(e));
        }
    }

    private static void loadNativeLibraries(String javaPath, String arch, String... libraries) {
        for (String library : libraries) {
            dlopen(javaPath + "/lib/" + arch + "/" + library);
        }
    }

    public static void startVirGLService(Context context, String home, String tmpdir) {

        H2CO3_LIB_DIR = CHTools.RUNTIME_DIR + "/h2co3_boat";

        patchLinker();

        try {
            redirectStdio(CHTools.LOG_DIR + "/h2co3_service_log.txt");

            setenv("HOME", home);
            setenv("TMPDIR", tmpdir);
            setenv("VIRGL_VTEST_SOCKET_NAME", context.getCacheDir().getAbsolutePath() + "/.virgl_test");

            dlopen(H2CO3_LIB_DIR + "/virgl/libepoxy.so.0");
            dlopen(H2CO3_LIB_DIR + "/virgl/libvirglrenderer.so");

            chdir(home);
            String[] finalArgs = new String[]{H2CO3_LIB_DIR + "/virgl/libvirgl_test_server.so",
                    "--no-loop-or-fork",
                    "--use-gles",
                    "--socket-name",
                    context.getCacheDir().getAbsolutePath() + "/.virgl_test"};
            System.out.println("Exited with code : " + dlexec(finalArgs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int launchJVM(String javaPath, ArrayList<String> args, String home) {
        patchLinker();

        try {
            setenv("HOME", home);
            setenv("JAVA_HOME", javaPath);

            String arch = switch (Architecture.getDeviceArchitecture()) {
                case ARCH_ARM -> "aarch32";
                case ARCH_ARM64 -> "aarch64";
                case ARCH_X86 -> "i386";
                case ARCH_X86_64 -> "amd64";
                default -> "";
            };

            dlopen(javaPath + "/lib/" + arch + "/libfreetype.so");
            dlopen(javaPath + "/lib/" + arch + "/jli/libjli.so");
            dlopen(javaPath + "/lib/" + arch + "/server/libjvm.so");
            dlopen(javaPath + "/lib/" + arch + "/libverify.so");
            dlopen(javaPath + "/lib/" + arch + "/libjava.so");
            dlopen(javaPath + "/lib/" + arch + "/libnet.so");
            dlopen(javaPath + "/lib/" + arch + "/libnio.so");
            dlopen(javaPath + "/lib/" + arch + "/libawt.so");
            dlopen(javaPath + "/lib/" + arch + "/libawt_headless.so");
            dlopen(javaPath + "/lib/" + arch + "/libfontmanager.so");

            redirectStdio(CHTools.LOG_DIR + "/h2co3_api_installer_log.txt");
            chdir(home);

            StringBuilder finalArgs = new StringBuilder();
            for (String arg : args) {
                if (!arg.equals(" ")) {
                    finalArgs.append(arg).append(" ");
                    System.out.println("JVM Args:" + arg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

}





