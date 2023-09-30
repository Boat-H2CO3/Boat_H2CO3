package org.koishi.launcher.h2co3.core.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pack200Utils {

    private static final Logger LOG = Logger.getLogger(Pack200Utils.class.getName());

    /**
     * Unpacks all .pack files into .jar
     *
     * @param nativeLibraryDir The native lib path, required to execute the unpack200 binary
     * @param dir              The path of the directory which contains .pack file
     */
    public static void unpack(String nativeLibraryDir, String dir) {
        File basePath = new File(dir);
        Collection<File> files = FileUtils.listFiles(basePath, new String[]{"pack"}, true);

        File workdir = new File(nativeLibraryDir);

        ProcessBuilder processBuilder = new ProcessBuilder().directory(workdir);
        for (File packFile : files) {
            try {
                String jarFilePath = packFile.getAbsolutePath().replace(".pack", "");
                Process process = processBuilder.command("./libunpack200.so", "-r", packFile.getAbsolutePath(), jarFilePath).start();
                process.waitFor();
            } catch (InterruptedException | IOException e) {
                LOG.log(Level.WARNING, "Failed to unpack files in " + dir, e);
            }
        }
    }

    /**
     * Unpacks .pack file into .jar
     *
     * @param nativeLibraryDir The native lib path, required to execute the unpack200 binary
     * @param in               Input file path
     * @param out              Output file path
     */
    public static void unpack(String nativeLibraryDir, String in, String out) {
        try {
            File workdir = new File(nativeLibraryDir);
            ProcessBuilder processBuilder = new ProcessBuilder().directory(workdir);
            Process process = processBuilder.command("./libunpack200.so", "-r", in, out).start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            LOG.log(Level.WARNING, "Failed to unpack file: " + in, e);
        }
    }

}