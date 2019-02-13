package com.kwws.view.audioplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kuni.R;
import com.kuni.data.vo.SongVO;
import com.kwws.view.audioplayer.PlayService.PlayerBinder;
import com.kwws.view.audioplayer.PlaylistBar.OnOperationListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 音频播放器
 *
 * @author Kw
 * @version 1.0
 */
public class MyAudioPlayer extends LinearLayout {
    public static final String TAG = "MyAudioPlayer";
    // -------------Data----------------
    Context mContext;
    List<SongVO> mSongs = new ArrayList<SongVO>();// 播放列表
    boolean mIsPlaying; // 播放状态
    boolean mIsPause;// 是否被暂停（区别暂停/停止）
    int mCurrentIndex = 0;// 正在播放的歌曲序号
    PlayMode mPlayMode = PlayMode.Order;// 播放模式
    boolean mIsSeekbarTracking = false;// 用户是否在拖动进度条

    PlayerBinder mPlayBinder;// 播放服务绑定
    boolean mIsBind;// 是否已绑定服务，用于解绑
    ServiceConnection mPlayServiceConnection;// 播放服务连接

    // -------------View----------------
    PlaylistBar mPlayList;// 弹出播放列表

    SeekBar seekBar;
    TextView tvStart;
    TextView tvEnd;

    ImageView ivIcon;
    TextView tvSong;
    TextView tvSinger;

    ImageView ivPlay;
    ImageView ivNext;
    ImageView ivList;

    /**
     * 播放模式
     *
     * @author Kw
     */
    public enum PlayMode {
        /**
         * 单曲循环
         */
        Repeat,
        /**
         * 顺序播放
         */
        Order,
        /**
         * 列表循环
         */
        Loop,
        /**
         * 随机播放
         */
        Random
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public MyAudioPlayer(Context context) {
        // super(context);
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public MyAudioPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        // 解决自定义控件在Eclipse可视化编辑器中无法解析的问题
        // if (isInEditMode()) {
        // return;
        // }

        // 在构造函数中将Xml中定义的布局解析出来。
        View lyPlayer = LayoutInflater.from(context).inflate(
                R.layout.my_audio_player, this, true);

        seekBar = (SeekBar) lyPlayer.findViewById(R.id.seekBar);
        tvStart = (TextView) lyPlayer.findViewById(R.id.tvStart);
        tvEnd = (TextView) lyPlayer.findViewById(R.id.tvEnd);

        ivIcon = (ImageView) lyPlayer.findViewById(R.id.ivIcon);
        tvSong = (TextView) lyPlayer.findViewById(R.id.tvSong);
        tvSinger = (TextView) lyPlayer.findViewById(R.id.tvSinger);

        ivPlay = (ImageView) lyPlayer.findViewById(R.id.ivPlay);
        ivList = (ImageView) lyPlayer.findViewById(R.id.ivList);
        ivNext = (ImageView) lyPlayer.findViewById(R.id.ivNext);

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mIsPlaying = false;
        mCurrentIndex = 0;

        // 播放服务绑定
        mPlayServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIsBind = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIsBind = true;
                mPlayBinder = (PlayerBinder) service;
                if (mSongs.size() > 0) {
                    mCurrentIndex = 0;
                    mPlayBinder.setMusic(mSongs.get(mCurrentIndex));
                }
                mPlayBinder.setStateMessageHandler(mHandler);
            }
        };

        Intent serviceIntent = new Intent(mContext, PlayService.class);
        mContext.bindService(serviceIntent, mPlayServiceConnection,
                Context.BIND_AUTO_CREATE);// 绑定播放服务
    }

    /**
     * 初始化界面事件
     */
    private void initView() {

        // 播放按钮
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivPlay: {
                        if (mIsPlaying) {
                            pause();// 暂停
                        } else {
                            if (mIsPause) {
                                resume();// 继续播放
                            } else {
                                play(mCurrentIndex, 0);// 从头播放
                            }
                        }
                        break;
                    }
                    case R.id.ivList: {
                        showPlayList();
                        break;
                    }
                    case R.id.ivNext: {
                        next();
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        ivPlay.setOnClickListener(listener);
        ivList.setOnClickListener(listener);
        ivNext.setOnClickListener(listener);

        // 进度条
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 放开滑块触发
                mIsSeekbarTracking = false;
                play(mCurrentIndex, seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 点击滑块触发
                mIsSeekbarTracking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // 滑块正在滑动触发
                tvStart.setText(displayTime(seekBar.getProgress()));
            }
        });

        mPlayList = new PlaylistBar(mContext);
        mPlayList.setOnOperationListener(new OnOperationListener() {

            @Override
            public void onItemSelected(int index) {
                if (index != mCurrentIndex) {
                    play(index, 0);
                }
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onClear() {
                stop();
                mSongs.clear();
                displaySongList();
                if (mListener != null) {
                    mListener.onPlayListChanged(mSongs.size());
                }
            }

            @Override
            public void afterItemRemoved(int index) {
                if (index == mCurrentIndex) {
                    stop();
                    mSongs.remove(index);
                    play(mCurrentIndex, 0);// 下一首
                } else if (index > mCurrentIndex) {
                    mSongs.remove(index);
                } else {
                    mSongs.remove(index);
                    mCurrentIndex--;
                }
                if (mListener != null) {
                    mListener.onPlayListChanged(mSongs.size());
                }
            }
        });
    }

    /**
     * 播放歌曲
     *
     * @param index  歌曲序号
     * @param second 起点（秒）
     */
    public void play(int index, int second) {
        if (mSongs == null || mSongs.size() == 0) {
            Toast.makeText(mContext, "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 序号修正
        index = index % mSongs.size();
        if (index < 0) {
            index += mSongs.size();
        }
        mCurrentIndex = index;
        SongVO song = mSongs.get(mCurrentIndex);

        // 界面显示
        displaySongInfo(song);
        updateProgress(second);
        ivPlay.setImageResource(R.drawable.selector_playbar_pause);
        mPlayList.setCurrentIndex(index);

        // 播放服务
        mPlayBinder.setMusic(song);
        mPlayBinder.play(second);
        mIsPlaying = true;
        mIsPause = false;

        if (mListener != null) {
            mListener.onCurrentIndexChanged(index);
        }
        Log.i(TAG, String.format("开始播放 %s @ %ds,ASID=%d", song.getName(),
                second, mPlayBinder.getAudioSessionId()));
    }

    /**
     * 根据歌曲更新界面：歌名、歌手、总时长、专辑封面
     *
     * @param song 歌曲
     */
    private void displaySongInfo(SongVO song) {
        if (!song.getName().equals(tvSong.getText())) {// 由于走马灯效果，在内容相同时不重置
            tvSong.setText(song.getName());//
            tvSong.setSelected(true);// 触发走马灯效果
        }
        if (!song.getSinger().equals(tvSinger.getText())) {
            tvSinger.setText(song.getSinger());
            tvSinger.setSelected(true);// 触发走马灯效果
        }
        tvEnd.setText(displayTime(song.getDuration()));
        seekBar.setMax(song.getDuration());

        // 设置封面图片
        boolean findAlbumArt = false;
        if (song.getAlbumArt() != null) {
            Bitmap bm = BitmapFactory.decodeFile(song.getAlbumArt());
            if (bm != null) {
                ivIcon.setImageBitmap(bm);
                findAlbumArt = true;
            }
        }
        if (!findAlbumArt) {
            ivIcon.setImageResource(R.drawable.logo);
        }
    }

    /**
     * 更新播放进度：时间文本与进度条
     *
     * @param second
     */
    private void updateProgress(int second) {
        seekBar.setProgress(second);
        tvStart.setText(displayTime(second));
    }

    /**
     * 下一首
     */
    public void next() {
        if (mPlayMode == PlayMode.Repeat || mPlayMode == PlayMode.Loop) {
            // 特殊处理，单曲循环、顺序播放时，强制播放下一首
            play(++mCurrentIndex, 0);
        } else {
            // 列表循环、随机播放
            autoPlay(mPlayMode);
        }

    }

    /**
     * 上一首
     */
    public void previous() {
        play(--mCurrentIndex, 0);
    }

    /**
     * 显示播放列表
     */
    private void showPlayList() {
        if (mPlayList != null) {
            mPlayList.showAtLocation(
                    MyAudioPlayer.this.findViewById(R.id.playbar),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mPlayBinder.pause();
        ivPlay.setImageResource(R.drawable.selector_playbar_play);
        mIsPlaying = false;
        mIsPause = true;
    }

    /**
     * 继续播放
     */
    public void resume() {
        mPlayBinder.resume();
        ivPlay.setImageResource(R.drawable.selector_playbar_pause);
        mIsPlaying = true;
        mIsPause = false;
    }

    /**
     * 停止播放
     */
    public void stop() {
        mPlayBinder.stop();
        ivPlay.setImageResource(R.drawable.selector_playbar_play);
        updateProgress(0);
        mIsPlaying = false;
        mIsPause = false;
    }

    /**
     * 设置播放列表
     *
     * @param songs
     */
    public void setPlayList(List<SongVO> songs) {
        if (mSongs != null) {
            mSongs.clear();
            mSongs.addAll(songs);
            if (!mIsPlaying && !mIsPause) {
                // 停止状态更新默认歌曲显示
                displaySongList();
            }

            if (mPlayList != null) {
                mPlayList.setSongs(mSongs);
            }
            if (mListener != null) {
                mListener.onPlayListChanged(mSongs.size());
            }
        }
    }

    /**
     * 根据歌曲列表数据更新界面
     */
    private void displaySongList() {
        if (mSongs != null) {
            if (mSongs.size() > 0) {
                displaySongInfo(mSongs.get(0));
                updateProgress(0);
            } else {
                SongVO fake = new SongVO();
                fake.setValue(-1, "酷你音乐", "", 0, null);
                displaySongInfo(fake);
                updateProgress(0);
            }
        }
    }

    /**
     * 获取播放列表
     *
     * @return
     */
    public List<SongVO> getPlayList() {
        return mSongs;
    }

    /**
     * 获取当前播放歌曲序号
     *
     * @return
     */
    public int getCurrent() {
        return mCurrentIndex;
    }

    /**
     * 获取当前播放歌曲
     *
     * @return
     */
    public SongVO getCurrentMusic() {
        if (mSongs != null && mCurrentIndex < mSongs.size()) {
            return mSongs.get(getCurrent());
        }
        return null;
    }

    /**
     * 播放模式
     *
     * @return
     */
    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    /**
     * 设置播放模式
     *
     * @param playMode
     */
    public void setPlayMode(PlayMode playMode) {
        this.mPlayMode = playMode;
    }

    /**
     * 关闭播放器，释放资源
     */
    public void close() {
        if (mIsBind) {
            mContext.unbindService(mPlayServiceConnection);// 解绑服务需要判断绑定状态，否则多次解绑将异常
            mIsBind = false;
        }
    }

    public int getAudioSessionId() {
        return mPlayBinder == null ? -1 : mPlayBinder.getAudioSessionId();
    }

    /**
     * 播放器时间显示
     *
     * @param second 秒
     * @return
     */
    protected String displayTime(int second) {
        return String.format(Locale.getDefault(), "%01d:%02d", second / 60,
                second % 60);
    }

    // 接收播放服务的消息
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<MyAudioPlayer> reference;

        public MyHandler(MyAudioPlayer player) {
            reference = new WeakReference<MyAudioPlayer>(player);
        }

        @Override
        public void handleMessage(Message msg) {
            MyAudioPlayer player = reference.get();
            player.handleMessage(msg);
        }

    }

    /**
     * 供给Handler处理消息
     *
     * @param msg
     */
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case PlayService.MSG_STATE_START: {
                break;
            }
            case PlayService.MSG_STATE_PLAYING: {
                // SongVO song = (SongVO)
                // msg.getData().getSerializable(PlayService.KEY_MUSIC);
                int position = msg.getData().getInt(PlayService.KEY_POSITION);
                // 当用户不在拖动进度条时，更新
                if (!mIsSeekbarTracking) {
                    updateProgress(position);
                }
                // Log.i(TAG, "播放  " + song.getName() + displayTime(position));
                break;
            }
            case PlayService.MSG_STATE_PAUSE: {
                SongVO song = (SongVO) msg.getData().getSerializable(
                        PlayService.KEY_MUSIC);
                int position = msg.getData().getInt(PlayService.KEY_POSITION);
                Log.i(TAG, "暂停 " + song.getName() + displayTime(position));
                break;
            }
            case PlayService.MSG_STATE_COMPLETE: {
                // 播放完成时，根据播放模式播放下一首歌曲

                SongVO song = (SongVO) msg.getData().getSerializable(
                        PlayService.KEY_MUSIC);
                Log.i(TAG, "结束  " + song.getName());

                mIsPlaying = false;
                mIsPause = false;// 停止播放，但不是暂停

                autoPlay(mPlayMode);
                break;
            }
            case PlayService.MSG_STATE_ERROR: {
                SongVO song = (SongVO) msg.getData().getSerializable(
                        PlayService.KEY_MUSIC);
                Toast.makeText(mContext, "歌曲文件错误:" + song.getName(),
                        Toast.LENGTH_LONG).show();
                play(++mCurrentIndex, 0);// 播放下一首
                break;
            }
            default:
                break;
        }
    }

    /**
     * 自动播放时，根据播放模式播放下首歌曲
     *
     * @param mode
     */
    private void autoPlay(PlayMode mode) {
        switch (mode) {
            case Repeat: {
                play(mCurrentIndex, 0);
                break;
            }
            case Order: {
                if (mCurrentIndex != mSongs.size() - 1) {
                    play(++mCurrentIndex, 0);
                } else {
                    stop();
                }
                break;
            }
            case Loop: {
                play(++mCurrentIndex, 0);
                break;
            }
            case Random: {
                Random random = new Random();
                int r = mCurrentIndex;
                if (mSongs.size() > 1) {// 至少2首歌才随机，且不会随机到当前歌曲
                    while (mCurrentIndex == r) {
                        r = random.nextInt(mSongs.size());
                    }
                }
                mCurrentIndex = r;
                play(mCurrentIndex, 0);
                break;
            }
        }
    }

    /**
     * 播放器状态监听器
     *
     * @author Kw
     */
    public interface OnPlayerStateChangedListener {
        /**
         * 播放列表改变
         *
         * @param count 列表曲目数量
         */
        void onPlayListChanged(int count);

        /**
         * 当前播放曲目发生变化
         *
         * @param index 曲目序号
         */
        void onCurrentIndexChanged(int index);
    }

    private OnPlayerStateChangedListener mListener;

    public void setOnPlayerStateChangedListener(
            OnPlayerStateChangedListener mListener) {
        this.mListener = mListener;
    }

}
