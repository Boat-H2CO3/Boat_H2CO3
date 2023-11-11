package org.koishi.launcher.h2co3.core.utils.gson.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonType {
    String property();

    JsonSubtype[] subtypes();
}
