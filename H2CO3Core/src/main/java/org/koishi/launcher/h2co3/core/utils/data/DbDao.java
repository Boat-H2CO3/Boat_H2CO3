package org.koishi.launcher.h2co3.core.utils.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yi.huangxing on 17/12/13.类描述:  数据库的增删改查
 */

public class DbDao {

    private final Context context;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;

    public DbDao(Context context, String name) {
        this.context = context;
        init(name);
    }

    private void init(String name) {

        //实例化数据库SQLiteOpenHelper子类对象
        helper = new RecordSQLiteOpenHelper(context, name);
        // 第一次进入时查询所有的历史记录
        queryData("");
    }

    public List<String> queryData(String tempName) {
        List<String> data = new ArrayList<>();
        //模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);

        while (cursor.moveToNext()) {
            //注意这里的name跟建表的name统一
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            data.add(name);
        }
        cursor.close();
        return data;

    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    public boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 插入数据
     */
    public void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 插入数据
     */

    public void delete(String name) {
        // 获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        // 执行SQL
        int delete = db.delete("records", " name=?", new String[]{name});
        // 关闭数据库连接
        db.close();
    }

    /**
     * 清空数据
     */
    public void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }
}
