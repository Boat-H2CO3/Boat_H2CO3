package org.koishi.launcher.h2co3.core.utils.gson.tools;

public @interface JsonSubtype {
    Class<?> clazz();

    String name();
}
