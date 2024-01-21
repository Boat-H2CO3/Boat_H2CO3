package org.koishi.launcher.h2co3.download;

public class DownloadItem {
    private final String name;
    private final String path;
    private final String url;
    private final int size;
    private int progress;
    private boolean isCompleted;
    private int retryCount;

    public DownloadItem(String name, String path, String url, int size) {
        this.name = name;
        this.path = path;
        this.url = url;
        this.size = size;
        this.progress = 0;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}