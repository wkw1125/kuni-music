package com.kwws.view.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Equalizer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.kuni.data.vo.SongVO;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 音乐后台播放服务
 *
 * @author Kw
 */
public class PlayService extends Service {
    private static final String TAG = "PlayService";

    // --------Data--------------
    private MediaPlayer mPlayer;
    private Equalizer mEqualizer;

    private SongVO mSong;
    private int mCurrentMilliseconds;// 当前播放位置，毫秒

    private PlayerBinder mBinder = new PlayerBinder();

    // 定时任务,每1秒为接收者发送播放状态
    private static TimerTask task;// 定时任务
    private static Timer timer;// 定时器

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new MediaPlayer();

        /*
         * 播放准备: prepareAsync()异步准备，不能马上start()，需在准备完成后start()
         * 类似，seekTo也只能在准备完毕之后进行 关于MediaPlayer的几个状态：
         * http://www.android100.org/html/201507/12/164270.html
         */
        mPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(mCurrentMilliseconds);
                mp.start();

                notifyOnStart(mSong, mCurrentMilliseconds / 1000);
                // 通知播放进度
                notifyPlayProgress(true);
            }
        });

        // 播放结束事件
        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                notifyPlayProgress(false);// 停止通知播放进度
                notifyOnCompletion(mSong);
            }
        });

        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBinder;
    }

    /**
     * 播放
     *
     * @param index        歌曲序号
     * @param milliseconds 播放起点（毫秒）
     */
    private void play(int milliseconds) {

        if (mSong == null) {
            Log.e(TAG, "播放歌曲为空");
            stop();
            return;
        }

        if (milliseconds > mSong.getDuration() * 1000) {
            Log.e(TAG, "播放进度超出时长");
            stop();
            return;
        }

        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(mSong.getLocation());

            mCurrentMilliseconds = milliseconds;
            mPlayer.prepareAsync();
            // mPlayer.seekTo(second);//为MediaPlayer添加OnCompletionListener，完成准备后自动播放
            // mPlayer.start();
            Log.i(TAG, "Play:" + mSong.getName());
        } catch (Exception e) {
            notifyError(mSong);// 播放错误
            // stop();
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 暂停
     */
    private void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            mCurrentMilliseconds = mPlayer.getCurrentPosition();

            notifyPlayProgress(false);// 停止通知播放进度
            notifyOnPause(mSong, mCurrentMilliseconds / 1000);
        }
    }

    /**
     * 继续播放
     */
    private void resume() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            notifyPlayProgress(true);// 通知播放进度
        }
    }

    /**
     * 停止
     */
    private void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            notifyPlayProgress(false);// 停止通知播放进度
        }
    }

    /**
     * 通知播放状态
     *
     * @param executeNotify 是否通知
     */
    private void notifyPlayProgress(boolean executeNotify) {
        if (executeNotify) {
            if (timer != null) {
                timer.cancel();
            }
            if (task != null) {
                task.cancel();
            }

            timer = new Timer();
            task = new TimerTask() {

                @Override
                public void run() {
                    mCurrentMilliseconds = mPlayer.getCurrentPosition();
                    notifyOnPlaying(mSong, mCurrentMilliseconds / 1000);
                }
            };
            timer.schedule(task, 300, 1000);// !!!!!!延迟一会再开始发送，不然会和start事件重合
        } else {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    /**
     * 播放服务绑定
     *
     * @author Kw
     */
    public class PlayerBinder extends Binder {
        /**
         * 设置播放歌曲
         *
         * @param song
         */
        public void setMusic(SongVO song) {
            mSong = song;
        }

        /**
         * 当前播放歌曲
         *
         * @return
         */
        public SongVO getMusic() {
            return mSong;
        }

        /**
         * 播放歌曲
         *
         * @param second 播放起点（秒）
         */
        public void play(int second) {
            PlayService.this.play(second * 1000);
        }

        /**
         * 暂停
         */
        public void pause() {
            PlayService.this.pause();
        }

        /**
         * 继续播放
         */
        public void resume() {
            PlayService.this.resume();
        }

        /**
         * 停止
         */
        public void stop() {
            PlayService.this.stop();
        }

        /**
         * 设置播放状态信息Handler
         *
         * @param listener
         */
        public void setStateMessageHandler(Handler listener) {
            mStateHandler = listener;
        }

        /**
         * 返回MP的AudioSessionId
         *
         * @return -1表示错误
         */
        public int getAudioSessionId() {
            if (mPlayer != null) {
                return mPlayer.getAudioSessionId();
            }
            return -1;
        }
    }

    public static final int MSG_STATE_ERROR = -1;
    public static final int MSG_STATE_START = 0;
    public static final int MSG_STATE_PLAYING = 1;
    public static final int MSG_STATE_PAUSE = 2;
    public static final int MSG_STATE_COMPLETE = 3;

    public static final String KEY_MUSIC = "music";
    public static final String KEY_POSITION = "position";

    private Handler mStateHandler;// 播放状态处理者

    /**
     * 通知开始播放（从暂停状态或重新播放）
     *
     * @param song     歌曲
     * @param position 开始位置(秒)
     */
    private void notifyOnStart(SongVO song, int position) {
        if (mStateHandler == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, song);
        bundle.putInt(KEY_POSITION, position);

        Message msg = Message.obtain();
        msg.what = MSG_STATE_START;
        msg.setData(bundle);

        mStateHandler.sendMessage(msg);// 发送
    }

    /**
     * 通知播放中
     *
     * @param song     歌曲
     * @param position 当前位置(秒)
     */
    private void notifyOnPlaying(SongVO song, int position) {
        if (mStateHandler == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, song);
        bundle.putInt(KEY_POSITION, position);

        Message msg = Message.obtain();
        msg.what = MSG_STATE_PLAYING;
        msg.setData(bundle);

        mStateHandler.sendMessage(msg);// 发送
    }

    /**
     * 通知暂停
     *
     * @param song
     * @param position 暂停位置(秒)
     */
    private void notifyOnPause(SongVO song, int position) {
        if (mStateHandler == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, song);
        bundle.putInt(KEY_POSITION, position);

        Message msg = Message.obtain();
        msg.what = MSG_STATE_PAUSE;
        msg.setData(bundle);

        mStateHandler.sendMessage(msg);// 发送
    }

    /**
     * 播放结束
     *
     * @param song 歌曲
     */
    private void notifyOnCompletion(SongVO song) {
        if (mStateHandler == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, song);

        Message msg = Message.obtain();
        msg.what = MSG_STATE_COMPLETE;
        msg.setData(bundle);

        mStateHandler.sendMessage(msg);// 发送
    }

    /**
     * 播放出现错误
     *
     * @param song 歌曲
     */
    private void notifyError(SongVO song) {
        if (mStateHandler == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, song);

        Message msg = Message.obtain();
        msg.what = MSG_STATE_ERROR;
        msg.setData(bundle);

        mStateHandler.sendMessage(msg);// 发送
    }
}
