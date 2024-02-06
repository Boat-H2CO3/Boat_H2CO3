package org.koishi.launcher.h2co3.core.utils;

import static org.koishi.launcher.h2co3.core.utils.file.FileTools.uncompressTarXZ;

import android.content.Context;

import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RuntimeUtils {

    public static boolean isLatest(String targetDir, String srcDir) throws IOException {
        File targetFile = new File(targetDir + "/version");
        long version = Long.parseLong(IOUtils.readFullyAsString(RuntimeUtils.class.getResourceAsStream(srcDir + "/version")));
        return targetFile.exists() && Long.parseLong(FileTools.readText(targetFile)) == version;
    }

    public static void install(Context context, String targetDir, String srcDir) throws IOException {
        FileTools.deleteDirectory(new File(targetDir));
        new File(targetDir).mkdirs();
        AssetsUtils.copyAssets(context, srcDir, targetDir);
    }

    public static void installJava(Context context, String targetDir, String srcDir) throws IOException {
        FileTools.deleteDirectory(new File(targetDir));
        new File(targetDir).mkdirs();
        String universalPath = srcDir + "/universal.tar.xz";
        String archPath = srcDir + "/bin-" + Architecture.archAsString(Architecture.getDeviceArchitecture()) + ".tar.xz";
        String version = IOUtils.readFullyAsString(Objects.requireNonNull(RuntimeUtils.class.getResourceAsStream("/assets/" + srcDir + "/version")));
        uncompressTarXZ(context.getAssets().open(universalPath), new File(targetDir));
        uncompressTarXZ(context.getAssets().open(archPath), new File(targetDir));
        FileTools.writeText(new File(targetDir + "/version"), version);
        patchJava(context, targetDir);
    }

    public static void patchJava(Context context, String javaPath) throws IOException {
        Pack200Utils.unpack(context.getApplicationContext().getApplicationInfo().nativeLibraryDir, javaPath);
        File dest = new File(javaPath);
        if (!dest.exists())
            return;
        String libFolder = JREUtils.getJreLibDir(javaPath);
        File ftIn = new File(dest, libFolder + "/libfreetype.so.6");
        File ftOut = new File(dest, libFolder + "/libfreetype.so");
        if (ftIn.exists() && (!ftOut.exists() || ftIn.length() != ftOut.length())) {
            ftIn.renameTo(ftOut);
        }
        File fileLib = new File(dest, "/" + libFolder + "/libawt_xawt.so");
        fileLib.delete();
        FileTools.copyFile(new File(context.getApplicationInfo().nativeLibraryDir, "libawt_xawt.so"), fileLib);
    }

}
