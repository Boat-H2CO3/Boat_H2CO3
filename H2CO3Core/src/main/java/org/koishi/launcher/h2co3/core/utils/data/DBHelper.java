package org.koishi.launcher.h2co3.core.utils.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.koishi.launcher.h2co3.core.utils.data.DBUser.User;

/**
 * 操作数据库辅助类
 *
 * @author 2012-8-12
 */
public class DBHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "example.db";
    public static final String USER_TABLE_NAME = "user_table";
    public static final String[] USER_COLS = {User.USERNAME, User.PASSWORD, User.API,
            User.ISSAVED};
    private final DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
        establishDb();
    }

    /**
     * 打开数据库
     */
    private void establishDb() {
        if (this.db == null) {
            this.db = this.dbOpenHelper.getWritableDatabase();
        }
    }

    /**
     * 插入一条记录
     */
    public void insertOrUpdate(String userName, String password, String api, int isSaved) {
        boolean isUpdate = false;
        String[] usernames = queryAllUserName();
        for (String username : usernames) {
            if (userName.equals(username)) {
                isUpdate = true;
                break;
            }
        }
        long id = -1;
        if (isUpdate) {
            id = update(userName, password, api, isSaved);
        } else {
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put(User.USERNAME, userName);
                values.put(User.PASSWORD, password);
                values.put(User.API, api);
                values.put(User.ISSAVED, isSaved);
                id = db.insert(USER_TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 删除一条记录
     *
     * @param userName 用户名
     */
    public void delete(String userName) {
    }

    /**
     * 更新一条记录
     *
     * @return 更新过后记录的id -1表示更新不成功
     */
    public long update(String username, String password, String api, int isSaved) {
        ContentValues values = new ContentValues();
        values.put(User.USERNAME, username);
        values.put(User.PASSWORD, password);
        values.put(User.API, api);
        values.put(User.ISSAVED, isSaved);
        return db.update(USER_TABLE_NAME, values, User.USERNAME + " = '"
                + username + "'", null);
    }

    /**
     * 根据用户名查询密码
     *
     * @param username 用户名
     * @return Hashmap 查询的记录
     */
    @SuppressLint("Range")
    public String queryPasswordByName(String username) {
        String sql = "select * from " + USER_TABLE_NAME + " where "
                + User.USERNAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(sql, null);
        String password = "";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            password = cursor.getString(cursor.getColumnIndex(User.PASSWORD));
        }

        return password;
    }

    /**
     * 根据用户名查询密码
     *
     * @param username 用户名
     * @return Hashmap 查询的记录
     */

    @SuppressLint("Range")
    public String queryApiByName(String username) {
        String sql = "select * from " + USER_TABLE_NAME + " where "
                + User.USERNAME + " = '" + username + "'";
        Cursor cursor5 = db.rawQuery(sql, null);
        String api = "";
        if (cursor5.getCount() > 0) {
            cursor5.moveToFirst();
            api = cursor5.getString(cursor5.getColumnIndex(User.API));
        }

        return api;
    }

    /**
     * 记住密码选项框是否被选中
     */
    @SuppressLint("Range")
    public int queryIsSavedByName(String username) {
        String sql = "select * from " + USER_TABLE_NAME + " where "
                + User.USERNAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(sql, null);
        int isSaved = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            isSaved = cursor.getInt(cursor.getColumnIndex(User.ISSAVED));
        }
        return isSaved;
    }

    /**
     * 查询所有用户名
     *
     * @return 所有记录的list集合
     */
    @SuppressLint("Range")
    public String[] queryAllUserName() {
        if (db != null) {
            Cursor cursor = db.query(USER_TABLE_NAME, null, null, null, null,
                    null, null);
            int count = cursor.getCount();
            String[] userNames = new String[count];
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    userNames[i] = cursor.getString(cursor
                            .getColumnIndex(User.USERNAME));
                    cursor.moveToNext();
                }
            }
            return userNames;
        } else {
            return new String[0];
        }

    }

    /**
     * 关闭数据库
     */
    public void cleanup() {
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
    }

    /**
     * 数据库辅助类
     */
    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + USER_TABLE_NAME + " (" + User._ID
                    + " integer primary key, " + User.USERNAME + " text, "
                    + User.PASSWORD + " text, " + User.API + " text, " + User.ISSAVED + " INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
            onCreate(db);
        }

    }

}
