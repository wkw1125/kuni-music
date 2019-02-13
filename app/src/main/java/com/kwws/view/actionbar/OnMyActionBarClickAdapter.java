package com.kwws.view.actionbar;

import android.view.View;

/**
 * MyActionBar事件适配器
 *
 * @author Kw
 */
public class OnMyActionBarClickAdapter implements OnMyActionBarClickListener {
    @Override
    public boolean onBackButtonClick(View view) {
        return true;
    }

    @Override
    public void onQueryButtonClick(View view) {
    }

    @Override
    public void onNewButtonClick(View view) {
    }

    @Override
    public void onMoreButtonClick(View view) {
    }

    @Override
    public void onTextButtonClick(View view) {
    }
}
