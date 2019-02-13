package com.kwws.view.audioplayer;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuni.R;
import com.kuni.data.vo.SongVO;
import com.kwws.view.audioplayer.AudioAdapter.OnItemRemovedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 弹出播放列表
 *
 * @author Kw
 */
public class PlaylistBar extends PopupWindow {

    // -------------Data----------------
    Context mContext;// 上下文
    List<SongVO> mSongs = new ArrayList<SongVO>();// 项集合
    AudioAdapter mAdapter;
    int mSelectedItemIndex = -1;// 选中序号

    // --------------View----------------
    ListView lvList;
    TextView tvSize;

    public PlaylistBar(Context context) {
        this(context, null);
    }

    public PlaylistBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout root = new LinearLayout(mContext);// 消除警告，虽然不知道有什么用
        View view = inflater.inflate(R.layout.my_audio_player_playlist, root);

        tvSize = (TextView) view.findViewById(R.id.tvSize);
        lvList = (ListView) view.findViewById(R.id.lvList);

        // 弹出列表
        initPopupView(view);

        // 列表
        mAdapter = new AudioAdapter(mContext, mSongs);
        mAdapter.setOnItemRemovedListener(new OnItemRemovedListener() {

            @Override
            public void afterItemRemoved(int oldIndex, SongVO oldSong) {
                tvSize.setText(displaySize(mSongs.size()));// 更新界面数量

                if (mListener != null) {
                    mListener.afterItemRemoved(oldIndex);
                }
            }
        });
        lvList.setAdapter(mAdapter);
        lvList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSelectedItemIndex = position;
                mAdapter.setSelectedIndex(mSelectedItemIndex);
                mAdapter.notifyDataSetInvalidated();//
                dismiss();

                if (mListener != null) {
                    mListener.onItemSelected(position);
                }
            }
        });

        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvClear: {
                        clear();
                        if (mListener != null) {
                            mListener.onClear();
                        }
                        dismiss();
                        break;
                    }
                    case R.id.tvClose: {
                        dismiss();
                        if (mListener != null) {
                            mListener.onClose();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        TextView tvClear = (TextView) view.findViewById(R.id.tvClear);
        TextView tvClose = (TextView) view.findViewById(R.id.tvClose);
        tvClear.setOnClickListener(listener);
        tvClose.setOnClickListener(listener);
    }

    /**
     * 初始化弹出列表
     *
     * @param contentView
     */
    private void initPopupView(View contentView) {
        // 设置PopWindow的View
        this.setContentView(contentView);
        // 弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
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
        this.setAnimationStyle(R.style.FadeFromBottom);
    }

    /**
     * 设置播放列表
     *
     * @param songs
     */
    public void setSongs(List<SongVO> songs) {
        if (mSongs != null) {
            mSongs.clear();
            mSongs.addAll(songs);
            mAdapter.notifyDataSetChanged();
            tvSize.setText(String.format("( %d )", mSongs.size()));
        }
    }

    /**
     * 设置当前播放序号
     *
     * @param index
     */
    public void setCurrentIndex(int index) {
        mSelectedItemIndex = index;
        mAdapter.setSelectedIndex(mSelectedItemIndex);
        mAdapter.notifyDataSetInvalidated();//
        lvList.setSelection(mSelectedItemIndex - 1);// 保留显示前1行
    }

    /**
     * 清空列表
     */
    private void clear() {
        if (mSongs != null) {
            mSongs.clear();
            mAdapter.notifyDataSetChanged();
            tvSize.setText(displaySize(mSongs.size()));
        }
    }

    /**
     * 显示列表大小
     *
     * @param size
     * @return
     */
    private String displaySize(int size) {
        return String.format(Locale.getDefault(), "( %d )", size);
    }

    /**
     * 列表操作监听
     *
     * @author Kw
     */
    public interface OnOperationListener {
        /**
         * 清空列表
         */
        void onClear();

        /**
         * 选中项目
         *
         * @param index 序号
         */
        void onItemSelected(int index);

        /**
         * 点击项目旁的删除按钮删除项目后
         *
         * @param index 删除前的序号
         */
        void afterItemRemoved(int index);

        /**
         * 关闭播放列表面板
         */
        void onClose();
    }

    private OnOperationListener mListener;

    /**
     * 设置列表操作监听
     *
     * @param listener
     */
    public void setOnOperationListener(OnOperationListener listener) {
        mListener = listener;
    }

}
