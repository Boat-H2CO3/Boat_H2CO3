package org.koishi.launcher.h2co3.core.utils;

import static org.koishi.launcher.h2co3.core.utils.Architecture.ARCH_X86;
import static org.koishi.launcher.h2co3.core.utils.Architecture.is64BitsDevice;

import android.content.Context;
import android.util.ArrayMap;

import org.koishi.launcher.h2co3.core.utils.Architecture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class BoatLauncher {
    private static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(javaPath + "/release"));
        String currLine;
        while ((currLine = jreReleaseReader.readLine()) != null) {
            if (currLine.contains("=")) {
                String[] keyValue = currLine.split("=");
                jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        jreReleaseReader.close();
        return jreReleaseMap;
    }

    public static String getJreLibDir(String javaPath) throws IOException {
        String jreArchitecture = readJREReleaseProperties(javaPath).get("OS_ARCH");
        if (Architecture.archAsInt(jreArchitecture) == ARCH_X86) {
            jreArchitecture = "i386/i486/i586";
        }
        String jreLibDir = "/lib";
        if (jreArchitecture == null) {
            throw new IOException("Unsupported architecture!");
        }
        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, "lib/" + arch);
            if (file.exists() && file.isDirectory()) {
                jreLibDir = "/lib/" + arch;
            }
        }
        return jreLibDir;
    }

    private static String getJvmLibDir(String javaPath) throws IOException {
        String jvmLibDir;
        File jvmFile = new File(javaPath + getJreLibDir(javaPath) + "/server/libjvm.so");
        jvmLibDir = jvmFile.exists() ? "/server" : "/client";
        return jvmLibDir;
    }

    private static String getLibraryPath(Context context, String javaPath) throws IOException {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        String libDirName = is64BitsDevice() ? "lib64" : "lib";
        String jreLibDir = getJreLibDir(javaPath);
        String jvmLibDir = getJvmLibDir(javaPath);
        String jliLibDir = "/jli";
        String split = ":";
        return javaPath +
                jreLibDir +
                split +

                javaPath +
                jreLibDir +
                jliLibDir +
                split +

                javaPath +
                jreLibDir +
                jvmLibDir +
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

                nativeDir;
    }
}
