package org.koishi.launcher.h2co3.core.utils.data;

import android.provider.BaseColumns;

/**
 * 2012-8-12
 */
public final class DBUser {

    public static final class User implements BaseColumns {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String API = "api";
        public static final String ISSAVED = "issaved";
    }

}
