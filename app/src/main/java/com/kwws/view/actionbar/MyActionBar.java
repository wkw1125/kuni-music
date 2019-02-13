package com.kwws.view.actionbar;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuni.R;

/**
 * 顶部工具栏
 *
 * @author Kw
 * @version 2.2
 */
public class MyActionBar extends LinearLayout {

    // -------------Data----------------
    Context mContext;// 上下文

    // 可用状态
    private boolean mEnableTitleText = true;
    private String mTitle = "标题";
    private boolean mEnableBackButton = true;
    private boolean mEnableQueryButton = true;
    private boolean mEnableNewButton = true;
    private boolean mEnableMoreButton = true;
    private boolean mEnableTextButton = true;
    private String mButtonText = "确定";

    // -------------View----------------
    private View lyActionbar;// 工具栏布局
    private TextView tvText;// 标题
    private ImageButton ibtnBack;// 后退按钮
    private ImageButton ibtnQuery;// 搜索按钮
    private ImageButton ibtnNew;// 新建按钮
    private ImageButton ibtnMore;// 更多按钮
    private Button btnTextButton;// 文本按钮

    /**
     * 构造函数
     *
     * @param context
     */
    public MyActionBar(Context context) {
        // super(context);
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param attrs
     */
    public MyActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        // 解决自定义控件在Eclipse可视化编辑器中无法解析的问题
        // if (isInEditMode()) {
        // return;
        // }

        // 在构造函数中将Xml中定义的布局解析出来。
        lyActionbar = LayoutInflater.from(context).inflate(
                R.layout.my_actionbar, this, true);

        /*
         * 当使用嵌套的layout时，需要为嵌入的layout指定ID，并从父layout中用findViewById找到子layout。
         * 否则子layout的view无法响应事件。如，此处不应该使用下句直接获得R.layout.lyActionbar lyActionbar
         * = inflater.inflate(R.layout.lyActionbar, container, false);
         */

        tvText = (TextView) lyActionbar.findViewById(R.id.tvText);
        tvText.setText(getResources().getString(R.string.systitle));
        ibtnBack = (ImageButton) lyActionbar.findViewById(R.id.ibtnBack);
        ibtnQuery = (ImageButton) lyActionbar.findViewById(R.id.ibtnQuery);
        ibtnNew = (ImageButton) lyActionbar.findViewById(R.id.ibtnNew);
        ibtnMore = (ImageButton) lyActionbar.findViewById(R.id.ibtnMore);
        btnTextButton = (Button) lyActionbar.findViewById(R.id.btnTextButton);

        initViewEvent();
    }

    /**
     * 初始化组件事件
     */
    private void initViewEvent() {
        // 默认控件单击事件
        OnClickListener defaultClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ibtnBack: {
                        // 关闭输入法
                        Activity activity = (Activity) mContext;
                        InputMethodManager inputMethodManager = (InputMethodManager) activity
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            View focusView = activity.getCurrentFocus();// 获得当前活动组件，一般是输入框
                            if (focusView != null) {
                                inputMethodManager.hideSoftInputFromWindow(
                                        focusView.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }

                        // 监听事件
                        if (mMyActionBarClickListener != null) {
                            boolean closeActivity = mMyActionBarClickListener
                                    .onBackButtonClick(v);
                            if (closeActivity) {
                                ((Activity) mContext).finish();// 用户决定结束当前活动
                            }
                        } else {
                            ((Activity) mContext).finish();// 默认结束当前活动
                        }

                        break;
                    }
                    case R.id.ibtnQuery: {
                        if (mMyActionBarClickListener != null) {
                            mMyActionBarClickListener.onQueryButtonClick(v);
                        }
                        break;
                    }
                    case R.id.ibtnNew: {
                        if (mMyActionBarClickListener != null) {
                            mMyActionBarClickListener.onNewButtonClick(v);
                        }
                        break;
                    }
                    case R.id.ibtnMore: {
                        if (mMyActionBarClickListener != null) {
                            mMyActionBarClickListener.onMoreButtonClick(v);
                        }
                        break;
                    }
                    case R.id.btnTextButton: {
                        if (mMyActionBarClickListener != null) {
                            mMyActionBarClickListener.onTextButtonClick(v);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        ibtnBack.setOnClickListener(defaultClickListener);
        ibtnQuery.setOnClickListener(defaultClickListener);
        ibtnNew.setOnClickListener(defaultClickListener);
        ibtnMore.setOnClickListener(defaultClickListener);
        btnTextButton.setOnClickListener(defaultClickListener);
    }

    /**
     * 启用ActionBar按钮功能
     *
     * @param title        标题
     * @param backButton   后退按钮
     * @param searchButton 搜索按钮
     * @param newButton    新建按钮
     * @param moreButton   更多按钮
     * @param textButton   文本按钮
     */
    public void enable(boolean title, boolean backButton, boolean searchButton,
                       boolean newButton, boolean moreButton, boolean textButton) {
        enableTitle(title);
        enableBackButton(backButton);
        enableSearchButton(searchButton);
        enableNewButton(newButton);
        enableMoreButton(moreButton);
        enableTextButton(textButton);

        updateStyle();
    }

    /**
     * 根据状态更新UI
     */
    private void updateStyle() {
        tvText.setVisibility(mEnableTitleText ? View.VISIBLE : View.GONE);
        ibtnBack.setVisibility(mEnableBackButton ? View.VISIBLE : View.GONE);
        ibtnQuery.setVisibility(mEnableQueryButton ? View.VISIBLE : View.GONE);
        ibtnNew.setVisibility(mEnableNewButton ? View.VISIBLE : View.GONE);
        ibtnMore.setVisibility(mEnableMoreButton ? View.VISIBLE : View.GONE);
        btnTextButton.setVisibility(mEnableTextButton ? View.VISIBLE
                : View.GONE);
    }

    // ----------监听器--------------

    OnMyActionBarClickListener mMyActionBarClickListener = null;

    public void setOnMyActionBarClickListener(
            OnMyActionBarClickListener listener) {
        this.mMyActionBarClickListener = listener;

    }

    // ------------API------------

    /**
     * 标题
     *
     * @param enable
     */
    public void enableTitle(boolean enable) {
        mEnableTitleText = enable;
        updateStyle();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
        tvText.setText(mTitle);
    }

    /**
     * 后退按钮
     *
     * @param enable
     */
    public void enableBackButton(boolean enable) {
        mEnableBackButton = enable;
        updateStyle();
    }

    /**
     * 搜索按钮
     *
     * @param enable
     */
    public void enableSearchButton(boolean enable) {
        mEnableQueryButton = enable;
        updateStyle();
    }

    /**
     * 新建按钮
     *
     * @param enable
     */
    public void enableNewButton(boolean enable) {
        mEnableNewButton = enable;
        updateStyle();
    }

    /**
     * 更多按钮
     *
     * @param enable
     */
    public void enableMoreButton(boolean enable) {
        mEnableMoreButton = enable;
        updateStyle();
    }

    /**
     * 文本按钮
     *
     * @param enable
     */
    public void enableTextButton(boolean enable) {
        mEnableTextButton = enable;
        updateStyle();
    }

    /**
     * 设置文本按钮文本
     *
     * @param text
     */
    public void setTextButtonText(String text) {
        mButtonText = text;
        btnTextButton.setText(mButtonText);
    }

    /**
     * 设置文本按钮是否可单机
     *
     * @param isClickable
     */
    public void setTextButtonClickable(boolean isClickable) {
        btnTextButton.setEnabled(isClickable);
    }

    @Override
    public void setBackgroundColor(int color) {
        lyActionbar.findViewById(R.id.lyBackground).setBackgroundColor(color);
    }

    // -----------getter----------

    public TextView getTitleTextView() {
        return tvText;
    }

    public ImageButton getBackButton() {
        return ibtnBack;
    }

    public ImageButton getSearchButton() {
        return ibtnQuery;
    }

    public ImageButton getNewButton() {
        return ibtnNew;
    }

    public ImageButton getMoreButton() {
        return ibtnMore;
    }

    public Button getTextButton() {
        return btnTextButton;
    }

}
