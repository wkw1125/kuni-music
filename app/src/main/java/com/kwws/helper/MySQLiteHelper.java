package com.kwws.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuni.data.vo.SongVO;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "KuniFavor.db";

    /**
     * @param context 上下文
     * @param version 数据库版本
     */
    public MySQLiteHelper(Context context, int version) {
        // 数据库路径：先SD后内部
        // context.getExternalCacheDir() + File.separator + DBNAME
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库后，对数据库的操作
        db.execSQL(SongVO.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // db.execSQL(SongVO.SQL_CREATE_TABLE);
        }
    }

}
