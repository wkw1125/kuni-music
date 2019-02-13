package com.kwws.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.kuni.data.vo.SongVO;

import java.util.ArrayList;
import java.util.List;

/**
 * MyContentProviderHelper
 *
 * @author Kw
 */
public class MyContentProviderHelper {

    Context mContext;

    public MyContentProviderHelper(Context context) {
        mContext = context;
    }

    /**
     * 使用ContentProvider扫描手机上的音频文件
     *
     * @return
     */
    public List<SongVO> getAudios() {
        // 要查询的列
        String[] projection = {MediaStore.Audio.Media.IS_MUSIC,// 是否音乐文件，0为否
                MediaStore.Audio.Media._ID,// 唯一ID
                MediaStore.Audio.Media.DISPLAY_NAME,// 文件名，含后缀
                MediaStore.Audio.Media.TITLE, // 标题
                MediaStore.Audio.Media.ARTIST,// 歌手
                MediaStore.Audio.Media.DURATION, // 时长（毫秒）
                MediaStore.Audio.Media.DATA, // 路径
                MediaStore.Audio.Media.ALBUM_ID // 专辑ID，用于获取封面
        };
        String orderBy = MediaStore.Audio.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // 查询系统ContentProvider
        Cursor cursor = mContext.getContentResolver().query(uri, projection,
                null, null, orderBy);

        // 包装数据
        List<SongVO> vos = new ArrayList<SongVO>();
        if (null != cursor) {
            while (cursor.moveToNext()) {
                // isMusic，0为否
                int isMusic = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                // ID
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                // 有标题显示标题，无标题显示文件名去后缀
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                if (title == null || title.length() == 0) {
                    String fileName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    if (fileName.contains(".")) {
                        title = fileName
                                .substring(0, fileName.lastIndexOf("."));
                    }
                }

                // 歌手
                String singer = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                // 单位转秒
                int duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000;

                // 文件位置
                String location = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                // 查询专辑封面
                String albumArt = queryAlbumArt(cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));

                SongVO vo = new SongVO();
                vo.setValue(id, title, singer, duration, location);
                vo.setAlbumArt(albumArt);
                vos.add(vo);

            }
            cursor.close();
        }
        return vos;
    }

    /**
     * 获取专辑封面
     *
     * @param albumID 专辑ID
     * @return
     */
    private String queryAlbumArt(int albumID) {
        Uri albumUri = Uri.parse(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                + "/" + albumID);
        Cursor albumCursor = mContext.getContentResolver().query(albumUri,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART}, null, null,
                null);
        String albumArt = null;
        while (albumCursor.moveToNext()) {
            albumArt = albumCursor.getString(0);
        }
        albumCursor.close();
        return albumArt;
    }
}
