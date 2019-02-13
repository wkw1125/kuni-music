package com.kwws.view.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuni.R;
import com.kuni.data.vo.SongVO;

import java.util.List;

/**
 * 歌曲Listview适配器
 *
 * @author Kw
 */
public class AudioAdapter extends BaseAdapter {

    private List<SongVO> mData;
    private Context mContext;

    public AudioAdapter(Context context, List<SongVO> songs) {
        mContext = context;
        mData = songs;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // 在此最好返回数据的唯一标识，在一些特定情况下使用到
        return mData.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            // 项目布局
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.my_audio_player_playlist_item, parent, false);

            holder.song = (TextView) convertView.findViewById(R.id.tvSong);
            holder.singer = (TextView) convertView.findViewById(R.id.tvSinger);
            holder.isPlaying = (ImageView) convertView
                    .findViewById(R.id.ivIcon);
            holder.delete = (ImageView) convertView.findViewById(R.id.ivDelete);
            holder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int oldPosition = Integer.valueOf(v.getTag().toString());
                    SongVO oldSong = mData.get(oldPosition);

                    // 删除选中序号之前的项时，选中序号-1
                    if (mSelectedIndex > oldPosition) {
                        mSelectedIndex--;
                    }

                    mData.remove(oldPosition);
                    notifyDataSetChanged();

                    if (mListener != null) {
                        mListener.afterItemRemoved(oldPosition, oldSong);
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongVO vo = mData.get(position);
        holder.song.setText(vo.getName());
        holder.singer.setText(" - " + vo.getSinger());

        if (position == mSelectedIndex) {
            holder.song.setTextColor(mContext.getResources().getColor(
                    R.color.deepskyblue));
            holder.singer.setTextColor(mContext.getResources().getColor(
                    R.color.deepskyblue));
            holder.isPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.song.setTextColor(mContext.getResources().getColor(
                    R.color.wx_text_black));
            holder.singer.setTextColor(mContext.getResources().getColor(
                    R.color.wx_text_gray));
            holder.isPlaying.setVisibility(View.GONE);
        }

        holder.delete.setTag(position);// 绑定删除按钮的项目序号

        return convertView;
    }

    class ViewHolder {
        TextView song;
        TextView singer;
        ImageView isPlaying;
        ImageView delete;
    }

    private int mSelectedIndex;

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int index) {
        this.mSelectedIndex = index;
    }

    public interface OnItemRemovedListener {
        void afterItemRemoved(int oldIndex, SongVO oldSong);
    }

    OnItemRemovedListener mListener;

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        mListener = listener;
    }
}
