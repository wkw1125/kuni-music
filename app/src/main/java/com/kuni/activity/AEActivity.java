package com.kuni.activity;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kuni.R;

/**
 * @author Kw
 */
public class AEActivity extends MyActionBarActivity {
    // 本程序需以下
    // <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    // <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
    private static final String TAG = "AEActivity";

    private LinearLayout mLinearLayout;

    private Equalizer mEqualizer;
    private int mAudioSessionId;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // 工具栏
        getMyActionBar().enable(true, true, false, false, false, false);
        getMyActionBar().setTitle(getResources().getString(R.string.eq));

        mAudioSessionId = getIntent().getIntExtra("ASID", -1);

        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(mLinearLayout);

        setupEqualizerFxAndUI();
    }

    private void setupEqualizerFxAndUI() {
        // Create the Equalizer object (an AudioEffect subclass) and attach it
        // to our media player,
        // with a default priority (0).
        mEqualizer = new Equalizer(0, mAudioSessionId);
        mEqualizer.setEnabled(true);

        short bands = mEqualizer.getNumberOfBands();

        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;

            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000)
                    + " Hz");
            mLinearLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(band));

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(band,
                            (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            mLinearLayout.addView(row);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //		if (isFinishing() && mMediaPlayer != null) {
        //			mVisualizer.release();
        //			mEqualizer.release();
        //			mMediaPlayer.release();
        //			mMediaPlayer = null;
        //		}
    }
}