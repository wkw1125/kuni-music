package com.kwws.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库代理,对外屏蔽数据库具体实现
 *
 * @author Kw
 */
public class DBProxy {
    private MySQLiteHelper mSQLiteHelper;
    private SQLiteDatabase mDB;
    private static DBProxy mProxyInstance;// 单例模式

    /**
     * @param context
     * @param dbname
     */
    private DBProxy(Context context) {
        mSQLiteHelper = new MySQLiteHelper(context, 1);
    }

    /**
     * 获取代理实例（单例模式，DBProxy类只会初始化一次）
     *
     * @param context
     * @return
     */
    public static final DBProxy getInstance(Context context) {
        if (mProxyInstance == null) {
            mProxyInstance = new DBProxy(context);
        }
        return mProxyInstance;
    }

    /**
     * 打开数据库
     *
     * @return
     */
    public SQLiteDatabase open() {
        if (mDB == null) {
            mDB = mSQLiteHelper.getReadableDatabase();
        }
        return mDB;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
        }
        // mDB = null;
    }

    /**
     * 插入
     *
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mDB.insert(table, nullColumnHack, values);
    }

    /**
     * 删除
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public long delete(String table, String whereClause, String[] whereArgs) {
        return mDB.delete(table, whereClause, whereArgs);
    }

    /**
     * 修改
     *
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public long update(String table, ContentValues values, String whereClause,
                       String[] whereArgs) {
        return mDB.update(table, values, whereClause, whereArgs);
    }

    /**
     * 查询
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit) {
        return mDB.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy, limit);
    }

    /**
     * 执行SQL语句
     *
     * @param sql
     */
    public void execSQL(String sql) {
        mDB.execSQL(sql);
    }

    /**
     * 开始事务
     */
    public void beginTransaction() {
        mDB.beginTransaction();
    }

    /**
     * 结束事务
     */
    public void endTransaction() {
        mDB.endTransaction();
    }

    /**
     * 标记事务成功
     */
    public void setTransactionSuccessful() {
        mDB.setTransactionSuccessful();
    }
}
