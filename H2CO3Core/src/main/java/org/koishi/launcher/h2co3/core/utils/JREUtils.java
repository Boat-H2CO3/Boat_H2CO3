package org.koishi.launcher.h2co3.core.utils;

import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class JREUtils {
    private static final String RELEASE_FILE_NAME = "release";
    private static final String LIB_DIR_NAME = "lib";
    private static final String JVM_LIB_NAME = "libjvm.so";
    private static final String SERVER_DIR_NAME = "server";
    private static final String CLIENT_DIR_NAME = "client";

    private static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        try (BufferedReader jreReleaseReader = new BufferedReader(new FileReader(javaPath + File.separator + RELEASE_FILE_NAME))) {
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
        String jreLibDir = File.separator + LIB_DIR_NAME;
        if (jreArchitecture == null) {
            throw new IOException("Unsupported architecture!");
        }
        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, LIB_DIR_NAME + File.separator + arch);
            if (file.exists() && file.isDirectory()) {
                jreLibDir = File.separator + LIB_DIR_NAME + File.separator + arch;
            }
        }
        return jreLibDir;
    }

    private static String getJvmLibDir(String javaPath) throws IOException {
        String jvmLibDir;
        File jvmFile = new File(javaPath + getJreLibDir(javaPath) + File.separator + SERVER_DIR_NAME + File.separator + JVM_LIB_NAME);
        jvmLibDir = jvmFile.exists() ? File.separator + SERVER_DIR_NAME : File.separator + CLIENT_DIR_NAME;
        return jvmLibDir;
    }
}