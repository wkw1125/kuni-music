package com.kuni.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

import com.kuni.R;
import com.kwws.view.actionbar.MyActionBar;

/**
 * 顶部带MyActionBar的Activity.继承该类的Activity的布局均处于MyActionBar下方
 *
 * @author Kw
 */
public class MyActionBarActivity extends Activity {

    // ----View----
    // 工具栏
    protected MyActionBar actionBar;

    // 根布局
    protected LinearLayout rootLinearLayout;
    // 内容布局
    protected LinearLayout contentLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        // setContentView(R.layout.activity_my_action_bar);
        initContentView(R.layout.activity_my_action_bar);

        // 工具栏
        actionBar = (MyActionBar) findViewById(R.id.actionbar);
        contentLinearLayout = (LinearLayout) findViewById(R.id.lyContent);
        setActionBar(actionBar);
    }

    /**
     * 初始化contentview
     */
    private void initContentView(int layoutResID) {
        // 获取控件容器
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup.removeAllViews();

        // 新建一个LinearLayout作为根节点，方便子布局加入
        rootLinearLayout = new LinearLayout(this);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
        viewGroup.addView(rootLinearLayout);

        // 父布局加入根节点
        LayoutInflater.from(this).inflate(layoutResID, rootLinearLayout, true);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, contentLinearLayout,
                true);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        contentLinearLayout.addView(view, params);
    }

    @Override
    public void setContentView(View view) {
        contentLinearLayout.addView(view);
    }

    /**
     * 设置ActionBar样式
     *
     * @param actionBar
     */
    protected void setActionBar(MyActionBar actionBar) {
        actionBar.enable(true, false, false, false, true, false);
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setBackgroundColor(getResources().getColor(
                R.color.deepskyblue));
    }

    /**
     * 获取MyActionBar工具条
     *
     * @return
     */
    public MyActionBar getMyActionBar() {
        return actionBar;
    }

    /**
     * 获取布局根节点
     *
     * @return
     */
    public ViewGroup getRootView() {
        return rootLinearLayout;
    }

    /**
     * 获取内容布局节点
     *
     * @return
     */
    public ViewGroup getContentView() {
        return contentLinearLayout;
    }

}
