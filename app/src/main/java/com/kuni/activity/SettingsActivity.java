package com.kuni.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kuni.R;
import com.kwws.view.actionbar.OnMyActionBarClickAdapter;

/**
 * 应用设置页面
 *
 * @author Kw
 */
public class SettingsActivity extends MyActionBarActivity {

    // -------------Data----------------
    SharedPreferences mSharedPreferences;
    Editor mEditor;
    String mSettingsFileName = "settings";
    final String KEY_DIR = "songdir";
    final String KEY_TEST = "test";

    // -------------View----------------
    // 设置
    EditText etSongDir;
    EditText etTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSharedPreferences = getSharedPreferences(mSettingsFileName,
                MODE_PRIVATE);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // 工具栏
        getMyActionBar().enable(true, true, false, false, false, true);
        getMyActionBar().setTitle(getResources().getString(R.string.settings));
        getMyActionBar().setTextButtonText(
                getResources().getString(R.string.save));
        getMyActionBar().setTextButtonClickable(false);// 页面文本内容改变时可保存
        getMyActionBar().setOnMyActionBarClickListener(
                new OnMyActionBarClickAdapter() {
                    @Override
                    public void onTextButtonClick(View view) {
                        mEditor = mSharedPreferences.edit();
                        mEditor.putString(KEY_DIR, etSongDir.getText()
                                .toString());
                        mEditor.putString(KEY_TEST, etTest.getText().toString());
                        mEditor.commit();

                        Toast.makeText(getApplicationContext(), "已保存",
                                Toast.LENGTH_SHORT).show();
                        SettingsActivity.this.finish();
                    }
                });

        // 设置
        etSongDir = (EditText) findViewById(R.id.etDir);
        etSongDir.setText(mSharedPreferences.getString(KEY_DIR, ""));
        etTest = (EditText) findViewById(R.id.etTest);
        etTest.setText(mSharedPreferences.getString(KEY_TEST, ""));

        // 文本变化时，启用保存按钮
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getMyActionBar().setTextButtonClickable(true);
            }
        };
        etSongDir.addTextChangedListener(watcher);
        etTest.addTextChangedListener(watcher);
    }
}
