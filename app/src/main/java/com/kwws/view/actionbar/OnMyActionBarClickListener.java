package com.kwws.view.actionbar;

import android.view.View;

/**
 * MyActionBar事件监听器
 *
 * @author Kw
 */
public interface OnMyActionBarClickListener {

    /**
     * 后退按钮事件
     *
     * @param view
     * @return 返回true时关闭当前Activity
     */
    boolean onBackButtonClick(View view);

    /**
     * 搜索按钮事件
     *
     * @param view
     */
    void onQueryButtonClick(View view);

    /**
     * 新建按钮事件
     *
     * @param view
     */
    void onNewButtonClick(View view);

    /**
     * 更多按钮事件
     *
     * @param view
     */
    void onMoreButtonClick(View view);

    /**
     * 文本按钮事件
     *
     * @param view
     */
    void onTextButtonClick(View view);

}
