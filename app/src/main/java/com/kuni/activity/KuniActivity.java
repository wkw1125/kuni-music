package com.kuni.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.kuni.R;
import com.kuni.data.adapter.SongAdapter;
import com.kuni.data.dao.SongDAO;
import com.kuni.data.vo.SongVO;
import com.kwws.helper.MyContentProviderHelper;
import com.kwws.helper.SharedPreferencesHelper;
import com.kwws.view.actionbar.OnMyActionBarClickAdapter;
import com.kwws.view.audioplayer.MyAudioPlayer;
import com.kwws.view.audioplayer.MyAudioPlayer.OnPlayerStateChangedListener;
import com.kwws.view.audioplayer.MyAudioPlayer.PlayMode;
import com.kwws.view.pop.MyPopupMenu;
import com.kwws.view.pop.MyPopupMenu.MenuItem;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KuniActivity extends MyActionBarActivity {
    public static final String TAG = "MainActivity";

    // -------------Data----------------
    List<SongVO> mSongVOs;
    SongAdapter mSongAdapter;

    int mScreenWidth;// 屏幕宽度，用于设定弹出菜单宽度

    boolean mIsFirstStart = true;// 首次启动
    boolean mIsFirstScan = true;// 首次扫描歌曲

    PlayMode mPlayMode = PlayMode.Order;
    final String PLAY_MODE_STRINGS[] = {"单曲循环", "顺序播放", "列表循环", "随机播放"};
    // -------------View----------------
    ListView lvMusicList;// 列表
    MyAudioPlayer myPlayer;// 播放条
    MyPopupMenu myMenu;// 弹出菜单

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuni);

        // 获取屏幕大小
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mScreenWidth = size.x;

        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        myPlayer.close();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mIsFirstStart) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.scantip),
                    Toast.LENGTH_SHORT).show();
            scanAudios();
            mIsFirstStart = false;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSongVOs = new ArrayList<SongVO>();
        // mSongVOs.add(new SongVO(0, "蜗牛", "周杰伦", 298));
        // mSongVOs.add(new SongVO(1, "约定", "周慧", 283));
        // mSongVOs.add(new SongVO(2, "Dilemma", "Nelly", 295));

        mSongAdapter = new SongAdapter(getApplicationContext(), mSongVOs);

        // 读取配置：播放模式
        SharedPreferencesHelper helper = SharedPreferencesHelper
                .getInstance(getApplicationContext());
        int mode = helper.getInt(SharedPreferencesHelper.KEY_PLAY_MODE, -1);
        if (mode != -1) {
            mPlayMode = PlayMode.values()[mode];
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // 工具栏
        getMyActionBar().enable(true, false, false, false, true, false);
        getMyActionBar().setTitle(getResources().getString(R.string.app_name));
        getMyActionBar().setOnMyActionBarClickListener(
                new OnMyActionBarClickAdapter() {
                    @Override
                    public void onMoreButtonClick(View view) {
                        // 以ActionBar左下角为锚点坐位移进行显示
                        // x偏移量过大时，PopupWindow貌似会往右挤，但不会超出屏幕
                        myMenu.showAsDropDown(getMyActionBar(), mScreenWidth, 0);
                    }
                });

        // 列表
        lvMusicList = (ListView) findViewById(R.id.lvMusicList);
        lvMusicList.setAdapter(mSongAdapter);
        // mSongAdapter.setSelectedIndex(0);

        lvMusicList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mSongAdapter.setSelectedIndex(position);
                mSongAdapter.notifyDataSetChanged();

                // 用户可能改变了播放列表，因此每次点击当前列表，设置播放列表
                if (mIsPlayerListChanged) {
                    // 恢复播放当前列表
                    myPlayer.setPlayList(mSongVOs);
                    mIsPlayerListChanged = false;
                }
                myPlayer.play(position, 0);
            }
        });

        lvMusicList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // 长按菜单
                String items[] = {"加入歌单", "点个赞"};
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        KuniActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                addToFavor(position);
                                break;
                            }
                            default:
                                Toast.makeText(getApplicationContext(), "赞!",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }

                });
                builder.create().show();

                return true;// true长按将不触发单击
            }
        });

        // 播放条
        myPlayer = (MyAudioPlayer) findViewById(R.id.audioPlayer);
        myPlayer.setPlayList(mSongVOs);
        myPlayer.setPlayMode(mPlayMode);
        myPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {

            @Override
            public void onPlayListChanged(int count) {
                mIsPlayerListChanged = true;
            }

            @Override
            public void onCurrentIndexChanged(int index) {
                // 若当前播放的曲目是否在用户音乐列表中，选中
                int i = mSongAdapter.indexOf(myPlayer.getPlayList().get(index));
                if (i != -1) {
                    mSongAdapter.setSelectedIndex(i);
                    mSongAdapter.notifyDataSetChanged();
                } else {
                    mSongAdapter.setSelectedIndex(-1);
                    mSongAdapter.notifyDataSetChanged();
                }
            }
        });

        // 弹出菜单
        myMenu = new MyPopupMenu(this);
        myMenu.setWidth(mScreenWidth / 2);

        myMenu.addItem("扫描歌曲",
                getResources().getDrawable(R.drawable.ic_action_refresh));
        myMenu.addItem("我的歌单",
                getResources().getDrawable(R.drawable.ic_action_important));
        myMenu.addItem(PLAY_MODE_STRINGS[mPlayMode.ordinal()], getResources()
                .getDrawable(R.drawable.ic_action_repeat));
        myMenu.addItem("均衡器",
                getResources().getDrawable(R.drawable.ic_action_important));
        myMenu.addItem("设置",
                getResources().getDrawable(R.drawable.ic_action_settings));
        myMenu.addItem("录音",
                getResources().getDrawable(R.drawable.ic_action_settings));

        myMenu.setOnItemClickListener(new MyPopupMenu.OnItemClickListener() {

            @Override
            public boolean OnItemClick(int which, final MenuItem item) {
                switch (which) {
                    case 0: {
                        scanAudios();
                        return true;
                    }
                    case 1: {
                        Intent intent = new Intent(KuniActivity.this,
                                FavorActivity.class);
                        startActivityForResult(intent, REQUESTCODE_SELECT_FAVOR);
                        return true;
                    }
                    case 2: {
                        // 播放模式
                        final String[] items = PLAY_MODE_STRINGS;
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                KuniActivity.this);
                        builder.setItems(items,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        PlayMode mode = PlayMode.Order;
                                        switch (which) {
                                            case 0: {
                                                mode = PlayMode.Repeat;
                                                break;
                                            }
                                            case 1: {
                                                mode = PlayMode.Order;
                                                break;
                                            }
                                            case 2: {
                                                mode = PlayMode.Loop;
                                                break;
                                            }
                                            case 3: {
                                                mode = PlayMode.Random;
                                                break;
                                            }
                                            default:
                                                break;
                                        }

                                        myPlayer.setPlayMode(mode);
                                        Toast.makeText(getApplicationContext(),
                                                items[which],
                                                Toast.LENGTH_SHORT).show();
                                        item.setText(items[which]);
                                        dialog.dismiss();

                                        // 保存配置
                                        SharedPreferencesHelper helper = SharedPreferencesHelper
                                                .getInstance(getApplicationContext());
                                        helper.setInt(
                                                SharedPreferencesHelper.KEY_PLAY_MODE,
                                                mode.ordinal());
                                    }

                                });

                        builder.create().show();
                        return true;
                    }
                    case 3: {

                        Intent intent = new Intent(KuniActivity.this,
                                AEActivity.class);
                        intent.putExtra("ASID", myPlayer.getAudioSessionId());
                        startActivity(intent);
                        return true;
                    }
                    case 4: {
                        Intent intent = new Intent(KuniActivity.this,
                                SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    case 5: {
                        Intent intent = new Intent(KuniActivity.this,
                                RecondActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private static final int MSG_UPDATE_LIST = 0;
    private static final String KEY_NEW_LIST = "newlist";

    /**
     * 扫描手机音乐（子线程）
     */
    protected void scanAudios() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                MyContentProviderHelper helper = new MyContentProviderHelper(
                        getApplicationContext());

                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_NEW_LIST,
                        (Serializable) helper.getAudios());

                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_LIST;
                msg.setData(bundle);

                mHandler.sendMessage(msg);// 发送
            }
        }).start();// 注意，不是run。用run变成了函数调用
    }

    /**
     * 加入歌单数据库（子线程）
     *
     * @param position 歌曲序号
     */
    private void addToFavor(int position) {
        final SongVO vo = mSongVOs.get(position);

        // 异步任务
        // 泛型参数:
        // Params:在AsyncTask.execute()传入，可变长参数，跟doInBackground()参数一致，为耗时任务提供参数
        // Progress:执行的进度，跟onProgressUpdate() 的参数一致，一般情况为Integer
        // Result:耗时操作返回结果，跟doInBackground返回的参数类型一致，且跟onPostExecute方法参数一致
        new AsyncTask<SongVO, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                // 在doInBackground之前调用，在UI线程内执行，可以显示进度条
            }

            @Override
            protected Boolean doInBackground(SongVO... params) {
                // 在后台执行的耗时操作
                try {
                    SongDAO dao = new SongDAO(getApplicationContext());
                    dao.add(params[0]);// 只传入了一个参数
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // 在doInBackground执行中，且在调用publishProgress方法时，在UI线程内执行，用于更新进度
            }

            // 在doInBackground完成之后调用，在UI线程内执行，可将耗时操作结果返回
            @Override
            protected void onPostExecute(Boolean result) {
                String msg = result ? "添加成功" : "添加失败";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            protected void onCancelled(Boolean result) {
                // 在用户取消任务时调用
            }

            @Override
            protected void onCancelled() {
                // 在用户取消任务时调用
            }
        }.execute(vo);
    }

    private boolean mIsPlayerListChanged = false;

    private static final int REQUESTCODE_SELECT_FAVOR = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUESTCODE_SELECT_FAVOR: {
                if (resultCode == Activity.RESULT_OK) {
                    SongVO vo = (SongVO) data.getSerializableExtra(SongVO
                            .getVoName());
                    List<SongVO> list = new ArrayList<SongVO>();
                    list.add(vo);
                    myPlayer.setPlayList(list);
                    mIsPlayerListChanged = true;
                    if (myPlayer.getPlayList().size() > 0) {
                        myPlayer.play(0, 0);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private static Boolean mIsExiting = false;// 双击退出程序

    @Override
    public void onBackPressed() {
        if (!mIsExiting) {
            mIsExiting = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mIsExiting = false; // 取消退出
                }
            }, 2000);

        } else {
            finish();
            System.exit(0);
        }
    }

    MyHandler mHandler = new MyHandler(this);

    /**
     * 消息处理（无内存泄漏风险写法） http://blog.csdn.net/lincyang/article/details/46875157
     *
     * @author Kw
     */
    private static class MyHandler extends Handler {
        WeakReference<KuniActivity> reference;

        public MyHandler(KuniActivity activity) {
            reference = new WeakReference<KuniActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            KuniActivity activity = reference.get();
            activity.handleMessage(msg);
        }

    }

    /**
     * 供给Handler处理消息
     *
     * @param list
     */
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_LIST: {
                @SuppressWarnings("unchecked")
                List<SongVO> list = (List<SongVO>) msg.getData()
                        .getSerializable(KEY_NEW_LIST);

                // mSongVOs = helper.getAudios();//该写法无法刷新
                // http://blog.csdn.net/wkw1125/article/details/54430019
                mSongVOs.clear();
                mSongVOs.addAll(list);
                // mSongAdapter.notifyDataSetInvalidated();//?
                mSongAdapter.notifyDataSetChanged();

                mIsPlayerListChanged = true;

                if (mIsFirstScan) {
                    mIsFirstScan = false;
                    myPlayer.setPlayList(mSongVOs);// 仅在第一次加载列表时主动设置播放器的列表，之后单击列表中的项目时更新
                }

                String tip = mSongVOs.size() == 0 ? "未发现本地音频" : String.format(
                        "发现%s个本地音频", mSongVOs.size());
                Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT)
                        .show();
                break;
            }
            default:
                break;
        }

    }
}
