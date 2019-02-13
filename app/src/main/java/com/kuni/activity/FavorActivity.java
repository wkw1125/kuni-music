package com.kuni.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.kuni.R;
import com.kuni.data.adapter.SongAdapter;
import com.kuni.data.dao.SongDAO;
import com.kuni.data.vo.SongVO;

import java.util.ArrayList;
import java.util.List;

public class FavorActivity extends MyActionBarActivity {

    // -------------Data----------------
    List<SongVO> mSongVOs;
    SongAdapter mSongAdapter;

    // -------------View----------------
    // 列表
    ListView lvMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);

        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFavorList();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSongVOs = new ArrayList<SongVO>();
        mSongAdapter = new SongAdapter(getApplicationContext(), mSongVOs);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // 工具栏
        getMyActionBar().enable(true, true, false, false, false, false);
        getMyActionBar().setTitle(getResources().getString(R.string.mylist));

        // 列表
        lvMusicList = (ListView) findViewById(R.id.lvMusicList);
        lvMusicList.setAdapter(mSongAdapter);
        lvMusicList.setLongClickable(true);
        lvMusicList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // 返回选中歌曲
                Intent intent = new Intent();
                intent.putExtra(SongVO.getVoName(),
                        (SongVO) parent.getItemAtPosition(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        lvMusicList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // 长按菜单
                String items[] = {"移出歌单"};
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        FavorActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                removeFromFavor(position);
                                break;
                            }
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }

                });
                builder.create().show();

                return true;// 返回true，长按事件将不触发单击事件
            }
        });
    }

    /**
     * 读取数据库歌单（异步任务）
     */
    protected void getFavorList() {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SongDAO dao = new SongDAO(getApplicationContext());
                mSongVOs.clear();
                mSongVOs.addAll(dao.queryAll());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mSongAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * 从歌单数据库中删除（异步任务）
     *
     * @param position
     */
    private void removeFromFavor(int position) {
        new AsyncTask<SongVO, Integer, Void>() {

            @Override
            protected Void doInBackground(SongVO... params) {
                SongDAO dao = new SongDAO(getApplicationContext());
                dao.delete(params[0]);
                mSongVOs.remove(params[0]);// 本地删除，重新载入时才从数据库刷新
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mSongAdapter.notifyDataSetChanged();
            }
        }.execute(mSongVOs.get(position));
    }
}
