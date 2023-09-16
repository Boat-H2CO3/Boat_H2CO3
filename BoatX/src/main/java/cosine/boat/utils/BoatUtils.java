package cosine.boat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BoatUtils {

    public static File createFile(String filePath) {
        File file = new File(filePath);
        return BoatUtils.createFile(file);
    }

    public static File createFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        Objects.requireNonNull(file.getParentFile()).mkdirs();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static byte[] readFile(String filePath) {
        return BoatUtils.readFile(new File(filePath));
    }

    public static byte[] readFile(File file) {
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(file);
            byte[] result = new byte[(int) file.length()];
            fis.read(result);
            fis.close();
            return result;
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean writeFile(File file, byte[] bytes) {

        file = BoatUtils.createFile(file);

        if (file == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean writeFile(File file, String str) {

        boolean retval;
        retval = BoatUtils.writeFile(file, str.getBytes(StandardCharsets.UTF_8));
        return retval;
    }

    public static boolean writeFile(String outFile, String str) {
        return writeFile(new File(outFile), str);
    }
}
