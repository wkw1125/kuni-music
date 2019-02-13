package com.kuni.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kuni.data.vo.SongVO;
import com.kwws.helper.DBProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * SongVO数据访问对象
 *
 * @author Kw
 */
public class SongDAO {
    private DBProxy mDBProxy;

    public SongDAO(Context context) {
        mDBProxy = DBProxy.getInstance(context);
    }

    /**
     * 条件查询：全部
     *
     * @return
     */
    public List<SongVO> queryAll() {
        List<SongVO> vos = null;

        mDBProxy.open();
        Cursor cursor = mDBProxy.query(SongVO.getTableName(), null, null, null,
                null, null, null, null);
        if (cursor != null) {
            vos = convertToVO(cursor);
            cursor.close();
        }
        // mDBProxy.close();
        return vos;
    }

    /**
     * 增加
     *
     * @param vo
     * @return 影响行数
     */
    public long add(SongVO vo) {
        mDBProxy.open();
        long result = mDBProxy.insert(SongVO.getTableName(), null,
                convertToCV(vo));
        // mDBProxy.close();
        return result;
    }

    /**
     * 删除
     *
     * @param vo
     * @return 影响行数
     */
    public long update(SongVO vo) {
        mDBProxy.open();
        long result = mDBProxy.update(SongVO.getTableName(), convertToCV(vo),
                SongVO.getPKFieldName() + "=?",
                new String[]{String.valueOf(vo.getId())});
        // mDBProxy.close();
        return result;
    }

    /**
     * 按ID删除
     *
     * @param vos
     */
    public void delete(List<SongVO> vos) {
        // delete from t_user where _id in (1, 2, 3);
        mDBProxy.open();
        StringBuilder pks = new StringBuilder();
        for (int i = 0; i < vos.size(); i++) {
            pks.append(vos.get(i).getId()).append(",");
        }
        if (pks.length() > 0) {
            // 去掉最后一个逗号,
            pks = pks.deleteCharAt(pks.length() - 1);
        }

        String sql = String.format("delete from %s where %s in (%s)",
                SongVO.getTableName(), SongVO.getPKFieldName(), pks.toString());
        mDBProxy.execSQL(sql);
        // mDBProxy.close();
    }

    /**
     * 按ID删除
     *
     * @param vo
     */
    public void delete(SongVO vo) {
        mDBProxy.open();
        mDBProxy.delete(SongVO.getTableName(), SongVO.getPKFieldName() + "=?",
                new String[]{String.valueOf(vo.getId())});
        // mDBProxy.close();
    }

    /**
     * 遍历Cursor生成相应VO列表
     *
     * @param cursor
     * @return
     */
    public List<SongVO> convertToVO(Cursor cursor) {
        List<SongVO> vos = new ArrayList<SongVO>();
        if (cursor.moveToFirst()) {
            do {
                SongVO vo = new SongVO();
                vo.setValue(cursor.getInt(cursor
                                .getColumnIndexOrThrow(SongVO._ID)), cursor
                                .getString(cursor.getColumnIndexOrThrow(SongVO.VNAME)),
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(SongVO.VSINGER)),
                        cursor.getInt(cursor
                                .getColumnIndexOrThrow(SongVO.IDURATION)),
                        cursor.getString(cursor
                                .getColumnIndexOrThrow(SongVO.VLOCATION)));
                vos.add(vo);
            } while (cursor.moveToNext());
        }
        return vos;
    }

    /**
     * 将VO打包为ContentValues
     *
     * @param vo
     * @return
     */
    public ContentValues convertToCV(SongVO vo) {
        ContentValues cv = new ContentValues();
        cv.put(SongVO._ID, vo.getId());
        cv.put(SongVO.VNAME, vo.getName());
        cv.put(SongVO.VSINGER, vo.getSinger());
        cv.put(SongVO.IDURATION, vo.getDuration());
        cv.put(SongVO.VLOCATION, vo.getLocation());
        return cv;
    }
}
