package org.koishi.launcher.h2co3.core.utils.file;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class AssetsUtils {
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
}
