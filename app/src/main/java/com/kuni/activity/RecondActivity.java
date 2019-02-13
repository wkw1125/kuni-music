package com.kuni.activity;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kuni.R;

import java.io.IOException;

public class RecondActivity extends MyActionBarActivity {

    // 录音
    MediaRecorder recorder;
    String mFilePath = "kunirec";

    Button btnRecord;
    Button btnPlay;

    // 播放
    MediaPlayer mPlayer;

    //
    boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recond);

        btnRecord = (Button) findViewById(R.id.button1);
        btnPlay = (Button) findViewById(R.id.button2);

        btnRecord.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    // 当前正在录音,则停止
                    stop();
                    btnRecord.setText("停止录音");

                } else {
                    // 当前不在录音，则开始录音
                    ready();
                    record();

                    btnRecord.setText("开始录音");
                }
                mIsRecording = !mIsRecording;
            }
        });

        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        btnPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        String.format("播放:%s", mFilePath), Toast.LENGTH_SHORT)
                        .show();

                try {
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                    }
                    mPlayer.reset();
                    mPlayer.setDataSource(mFilePath);
                    mPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ready() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/testrec.3gp";
        recorder.setOutputFile(mFilePath);

    }

    public void record() {

        try {
            recorder.prepare();
            recorder.start(); // Recording is now started
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void stop() {
        recorder.stop();
        recorder.reset(); // You can reuse the object by going back to
        // setAudioSource() step
        recorder.release(); // Now the object cannot be reused
    }

}
