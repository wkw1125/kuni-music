package com.kwws.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences助手类
 *
 * @author Kw
 */
public class SharedPreferencesHelper {
    public static final String FILE_NAME = "settings";
    public static final String KEY_PLAY_MODE = "playmode";

    private static SharedPreferencesHelper mHelperInstance;// 单例模式
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context mContext;

    /**
     * 获取SharedPreferences助手（单例模式，DBProxy类只会初始化一次）
     *
     * @param content
     * @param xmlFileName
     * @return
     */
    public static final SharedPreferencesHelper getInstance(Context content) {
        if (mHelperInstance == null) {
            mHelperInstance = new SharedPreferencesHelper(content, FILE_NAME);
        }
        return mHelperInstance;
    }

    /**
     * SharedPreferences助手类
     *
     * @param content     上下文
     * @param xmlFileName 文件名
     */
    private SharedPreferencesHelper(Context content, String xmlFileName) {
        mContext = content;
        sp = mContext.getSharedPreferences(xmlFileName, Activity.MODE_PRIVATE);
    }

    /**
     * 写入字符串
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取字符串
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * 写入整型
     *
     * @param key
     * @param value
     */
    public void setInt(String key, int value) {
        editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 读取整型
     *
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * 获取编辑器
     *
     * @return
     */
    public SharedPreferences.Editor getEditor() {
        return editor;
    }

}
