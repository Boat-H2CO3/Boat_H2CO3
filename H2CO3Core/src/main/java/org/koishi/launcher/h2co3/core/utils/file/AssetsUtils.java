package org.koishi.launcher.h2co3.core.utils.file;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetsUtils {
    public static void copyAssets(Context context, String src, String dest) throws IOException {
        String[] fileNames = context.getAssets().list(src);
        if (fileNames != null && fileNames.length > 0) {
            File destFolder = new File(dest);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            for (String fileName : fileNames) {
                String srcPath = src + File.separator + fileName;
                String destPath = dest + File.separator + fileName;

                if (Objects.requireNonNull(context.getAssets().list(srcPath)).length > 0) {
                    copyAssets(context, srcPath, destPath);
                } else {
                    try (InputStream in = context.getAssets().open(srcPath);
                         OutputStream out = new FileOutputStream(destPath)) {
                        FileUtils.copyInputStreamToFile(in, new File(destPath));
                    }
                }
            }
        }
    }

    public static void extractZipFromAssets(Context context, String zipFileName, String destDir) throws IOException {
        try (InputStream inputStream = context.getAssets().open(zipFileName);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            int length;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                File entryFile = new File(destDir, entryName);

                if (zipEntry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    File parentDir = entryFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (OutputStream outputStream = new FileOutputStream(entryFile)) {
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }
}