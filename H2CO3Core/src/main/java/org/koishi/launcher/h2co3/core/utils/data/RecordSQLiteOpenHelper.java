package org.koishi.launcher.h2co3.core.utils.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yi.huangxing on 17/12/13.类描述:
 */

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String name = "record.db";
    private static final Integer version = 1;


    public RecordSQLiteOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面只有一列name来存储历史记录：
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
