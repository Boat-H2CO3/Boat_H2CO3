package org.koishi.launcher.h2co3.core.utils;

import android.content.Context;
import android.system.Os;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.koishi.launcher.h2co3.core.utils.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;

public class RuntimeUtils {

    public static boolean isLatest(String targetDir, String srcDir) throws IOException {
        File targetFile = new File(targetDir + "/version");
//        long version = Long.parseLong(IOUtils.readFullyAsString(RuntimeUtils.class.getResourceAsStream(srcDir + "/version")));
        return targetFile.exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void install(Context context, String targetDir, String srcDir) throws IOException {
        FileUtils.deleteDirectory(new File(targetDir));
        new File(targetDir).mkdirs();
        copyAssets(context, srcDir, targetDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void installJava(Context context, String targetDir, String srcDir) throws IOException {
        FileUtils.deleteDirectory(new File(targetDir));
        new File(targetDir).mkdirs();
        String universalPath = srcDir + "/universal.tar.xz";
        String archPath = srcDir + "/bin-" + Architecture.archAsString(Architecture.getDeviceArchitecture()) + ".tar.xz";
        String version = IOUtils.readFullyAsString(Objects.requireNonNull(RuntimeUtils.class.getResourceAsStream("/assets/" + srcDir + "/version")));
        uncompressTarXZ(context.getAssets().open(universalPath), new File(targetDir));
        uncompressTarXZ(context.getAssets().open(archPath), new File(targetDir));
        FileUtils.writeText(new File(targetDir + "/version"), version);
        patchJava(context, targetDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyAssets(Context context, String src, String dest) throws IOException {
        // 获取Assets目录下指定文件夹的所有文件和子文件夹的名称数组
        String[] fileNames = context.getAssets().list(src);
        if (fileNames != null && fileNames.length > 0) {
            // 创建目标文件夹
            File destFolder = new File(dest);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            // 遍历文件名数组
            for (String fileName : fileNames) {
                String srcPath = src + File.separator + fileName; // 源文件或文件夹的路径
                String destPath = dest + File.separator + fileName; // 目标文件或文件夹的路径

                // 判断是否是子文件夹，如果是则递归调用copyAssetsFolder方法复制子文件夹
                if (Objects.requireNonNull(context.getAssets().list(srcPath)).length > 0) {
                    copyAssets(context, srcPath, destPath);
                } else {
                    // 如果是文件，则复制到目标文件夹
                    InputStream in = context.getAssets().open(srcPath);
                    OutputStream out = new FileOutputStream(destPath);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.close();
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void uncompressTarXZ(final InputStream tarFileInputStream, final File dest) throws IOException {
        dest.mkdirs();
        TarArchiveInputStream tarIn = new TarArchiveInputStream(new XZCompressorInputStream(tarFileInputStream));
        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        while (tarEntry != null) {
            if (tarEntry.getSize() <= 20480) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignored) {

                }
            }
            File destPath = new File(dest, tarEntry.getName());
            if (tarEntry.isSymbolicLink()) {
                Objects.requireNonNull(destPath.getParentFile()).mkdirs();
                try {
                    Os.symlink(tarEntry.getLinkName().replace("..", dest.getAbsolutePath()), new File(dest, tarEntry.getName()).getAbsolutePath());
                } catch (Throwable e) {
                    Logging.LOG.log(Level.WARNING, e.getMessage());
                }
            } else if (tarEntry.isDirectory()) {
                destPath.mkdirs();
                destPath.setExecutable(true);
            } else if (!destPath.exists() || destPath.length() != tarEntry.getSize()) {
                Objects.requireNonNull(destPath.getParentFile()).mkdirs();
                destPath.createNewFile();
                FileOutputStream os = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = tarIn.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }
                os.close();
            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        FileUtils.copyFile(new File(context.getApplicationInfo().nativeLibraryDir, "libawt_xawt.so"), fileLib);
    }

}
