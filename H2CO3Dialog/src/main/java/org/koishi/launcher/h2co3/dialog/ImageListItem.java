package org.koishi.launcher.h2co3.dialog;

import android.graphics.drawable.Drawable;

public class ImageListItem {

    private String name;
    private Drawable image;
    private Object data;

    public ImageListItem() {
    }

    public ImageListItem(String name, Drawable image) {
        this.name = name;
        this.image = image;
    }

    public ImageListItem(String name, Drawable image, Object data) {
        this.name = name;
        this.image = image;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImageListItem{" +
                "name='" + name + '\'' +
                ", image=" + image +
                ", data=" + data +
                '}';
    }
}
