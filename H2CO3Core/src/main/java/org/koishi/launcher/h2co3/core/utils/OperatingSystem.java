package org.koishi.launcher.h2co3.core.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public enum OperatingSystem {
    WINDOWS("windows"),
    LINUX("linux"),
    OSX("osx"),
    UNKNOWN("universal");

    public static final Charset NATIVE_CHARSET;

    static {
        String nativeEncoding = System.getProperty("native.encoding");
        Charset nativeCharset = Charset.defaultCharset();

        if (nativeEncoding != null && !nativeEncoding.equalsIgnoreCase(nativeCharset.name())) {
            try {
                nativeCharset = Charset.forName(nativeEncoding);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }

        if (nativeCharset == StandardCharsets.UTF_8 || nativeCharset == StandardCharsets.US_ASCII) {
            nativeCharset = StandardCharsets.UTF_8;
        } else if ("GBK".equalsIgnoreCase(nativeCharset.name()) || "GB2312".equalsIgnoreCase(nativeCharset.name())) {
            nativeCharset = Charset.forName("GB18030");
        }

        NATIVE_CHARSET = nativeCharset;
    }

    private final String checkedName;

    OperatingSystem(String checkedName) {
        this.checkedName = checkedName;
    }

    public static boolean isNameValid(String name) {
        return !name.isEmpty() && !name.equals(".") && name.indexOf('/') == -1 && name.indexOf('\0') == -1;
    }

    public String getCheckedName() {
        return checkedName;
    }
}