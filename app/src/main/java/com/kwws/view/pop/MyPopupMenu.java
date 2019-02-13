package com.kwws.view.pop;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuni.R;

import java.util.ArrayList;

/**
 * 弹出菜单栏
 *
 * @author Kw
 */
public class MyPopupMenu extends PopupWindow {

    // -------------Data----------------
    Context mContext;// 上下文
    int mWidth = 300;// 默认宽度
    ArrayList<MenuItem> mItems;// 项集合

    // --------------View----------------
    LinearLayout mRootView;// 根视图

    public MyPopupMenu(Context context) {
        this(context, null);
    }

    public MyPopupMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mRootView = new LinearLayout(mContext);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        initView(mRootView, mWidth);
        mItems = new ArrayList<MenuItem>();
    }

    private void initView(View contentView, int width) {
        // 设置PopWindow的View
        this.setContentView(contentView);
        // 弹出窗体的宽
        this.setWidth(width);
        // 弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 弹出窗体可点击
        this.setFocusable(true);
        // 点back键和其他地方使其消失
        this.setOutsideTouchable(true);
        // 刷新状态（必须刷新否则无效）
        this.update();
        // 点back键和其他地方使其消失,设置了setBackgroundDrawable才能触发OnDismisslistener
        // 实例化一个ColorDrawable颜色为半透明
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        // 设置PopWindow弹出窗体动画效果
        // this.setAnimationStyle(android.R.style.Animation_Translucent);
        this.setAnimationStyle(R.style.AnimationPreview);
    }

    /**
     * 添加菜单项
     *
     * @param text 文本
     * @param icon 图标
     */
    public void addItem(String text, Drawable icon) {
        MenuItem item = new MenuItem(mItems.size(), text, icon);
        mItems.add(item);
        mRootView.addView(item.getView());
    }

    /**
     * 添加菜单项
     *
     * @param text 文本
     */
    public void addItem(String text) {
        addItem(text, null);
    }

    /**
     * 添加菜单项
     *
     * @param texts 文本数组
     * @param icons 图标数组
     */
    public void addItems(String[] texts, Drawable[] icons) {
        int min = texts.length;
        if (icons.length < min) {
            min = icons.length;
        }

        for (int i = 0; i < min; i++) {
            addItem(texts[i], icons[i]);
        }
    }

    /**
     * 添加菜单项
     *
     * @param texts 文本数组
     */
    public void addItems(String[] texts) {
        for (int i = 0; i < texts.length; i++) {
            addItem(texts[i]);
        }
    }

    /**
     * 清空菜单项
     */
    public void clearItems() {
        mRootView.removeAllViews();
        mItems.clear();
    }

    /**
     * 菜单项
     *
     * @author Kw
     */
    public class MenuItem {
        int mIndex;
        String mText;
        Drawable mIcon;
        boolean mVisible;

        LinearLayout llyItem;
        TextView tvText;
        ImageView ivIcon;

        /**
         * @param index
         * @param text
         * @param icon
         * @param visible
         */
        public MenuItem(int index, String text, Drawable icon) {
            mIndex = index;
            mText = text;
            mIcon = icon;
            mVisible = true;

            initView();
        }

        private void initView() {
            llyItem = (LinearLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.my_popup_menu_item, null);
            tvText = (TextView) llyItem.findViewById(R.id.tvText);
            ivIcon = (ImageView) llyItem.findViewById(R.id.ivIcon);

            setText(mText);
            setIcon(mIcon);
            setVisible(mVisible);

            llyItem.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean dismiss = false;
                    if (mListener != null) {
                        dismiss = mListener.OnItemClick(mIndex, MenuItem.this);
                    }
                    if (dismiss) {
                        MyPopupMenu.this.dismiss();
                    }
                }
            });
        }

        public int getIndex() {
            return mIndex;
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
            tvText.setText(mText);
        }

        public Drawable getIcon() {
            return mIcon;
        }

        /**
         * 设置菜单项的图标
         *
         * @param icon 图标，为null时visible为gone
         */
        public void setIcon(Drawable icon) {
            mIcon = icon;
            if (mIcon == null) {
                ivIcon.setVisibility(View.GONE);
            } else {
                ivIcon.setVisibility(View.VISIBLE);
                ivIcon.setImageDrawable(mIcon);
            }
        }

        public boolean isVisible() {
            return mVisible;
        }

        public void setVisible(boolean visible) {
            mVisible = visible;
            llyItem.setVisibility(this.mVisible ? View.VISIBLE : View.GONE);
        }

        public View getView() {
            return llyItem;
        }

    }

    /**
     * 菜单项单击监听器
     *
     * @author Kw
     */
    public interface OnItemClickListener {
        /**
         * 菜单项单击事件
         *
         * @param which 菜单项序号
         * @param item  菜单项
         * @return 返回true则在点击后关闭菜单
         */
        boolean OnItemClick(int which, MenuItem item);
    }

    private OnItemClickListener mListener;

    /**
     * 设置菜单项单击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
