package org.koishi.launcher.h2co3.core.utils;

public class Version {
    private final String versionName;
    private final String versionType;
    private final String versionUrl;
    private final String versionSha1;


    public Version(String versionName, String versionType, String versionUrl, String versionSha1) {
        this.versionName = versionName;
        this.versionType = versionType;
        this.versionUrl = versionUrl;
        this.versionSha1 = versionSha1;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionType() {
        return versionType;
    }

    public String getVersionUrl() {
        return versionUrl;
    }

    public String getVersionSha1() {
        return versionSha1;
    }
}