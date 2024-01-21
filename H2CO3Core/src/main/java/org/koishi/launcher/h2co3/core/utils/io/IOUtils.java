package org.koishi.launcher.h2co3.core.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This utility class consists of some util methods operating on InputStream/OutputStream.
 *
 * @author huangyuhui
 */
public final class IOUtils {

    private IOUtils() {
    }

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * Read all bytes to a buffer from the given input stream. The stream will not be closed.
     *
     * @param inputStream the InputStream being read.
     * @return all bytes read from the stream
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] readFullyWithoutClosing(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        copyTo(inputStream, result);
        return result.toByteArray();
    }

    /**
     * Read all bytes to a buffer from the given input stream, and close the input stream finally.
     *
     * @param inputStream the InputStream being read, closed finally.
     * @return all bytes read from the stream
     * @throws IOException if an I/O error occurs.
     */
    public static ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        try (InputStream is = inputStream) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            copyTo(is, result);
            return result;
        }
    }

    public static byte[] readFullyAsByteArray(InputStream inputStream) throws IOException {
        return readFully(inputStream).toByteArray();
    }

    public static String readFullyAsString(InputStream inputStream) throws IOException {
        return readFully(inputStream).toString();
    }

    public static String readFullyAsString(InputStream inputStream, Charset charset) throws IOException {
        return readFully(inputStream).toString(charset);
    }

    public static void write(String text, OutputStream outputStream) throws IOException {
        write(text.getBytes(), outputStream);
    }

    public static void write(byte[] bytes, OutputStream outputStream) throws IOException {
        copyTo(new ByteArrayInputStream(bytes), outputStream);
    }

    public static void copyTo(InputStream src, OutputStream dest) throws IOException {
        copyTo(src, dest, new byte[DEFAULT_BUFFER_SIZE]);
    }

    public static void copyTo(InputStream src, OutputStream dest, byte[] buffer) throws IOException {
        int length;
        while ((length = src.read(buffer)) != -1) {
            dest.write(buffer, 0, length);
        }
    }
}