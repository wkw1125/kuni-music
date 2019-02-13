package com.kuni.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuni.R;
import com.kuni.Toolkit;
import com.kuni.data.vo.SongVO;

import java.util.List;

/**
 * 歌曲Listview适配器
 *
 * @author Kw
 */
public class SongAdapter extends BaseAdapter {

    private List<SongVO> mData;
    private Context mContext;

    public SongAdapter(Context context, List<SongVO> songs) {
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
        // 在此最好返回数据的唯一标识，在一些特定情况下使用到（比如筛选数据）
        return mData.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            // 项目布局
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_song, parent, false);

            holder.song = (TextView) convertView.findViewById(R.id.tvSong);
            holder.singer = (TextView) convertView.findViewById(R.id.tvSinger);
            holder.duration = (TextView) convertView
                    .findViewById(R.id.tvDuration);
            holder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SongVO vo = mData.get(position);
        holder.song.setText(vo.getName());
        holder.singer.setText(vo.getSinger());
        holder.duration.setText(Toolkit.displayTime(vo.getDuration()));
        holder.icon.setImageResource(R.drawable.music);

        if (position == mSelectedIndex) {
            holder.song.setTextColor(mContext.getResources().getColor(
                    R.color.deepskyblue));
            holder.singer.setTextColor(mContext.getResources().getColor(
                    R.color.deepskyblue));
            holder.icon.setImageResource(R.drawable.list_item_music_playing);
        } else {
            holder.song.setTextColor(mContext.getResources().getColor(
                    R.color.wx_text_black));
            holder.singer.setTextColor(mContext.getResources().getColor(
                    R.color.wx_text_gray));
            holder.icon.setImageResource(R.drawable.music);
        }

        return convertView;
    }

    class ViewHolder {
        TextView song;
        TextView singer;
        TextView duration;
        ImageView icon;
    }

    private int mSelectedIndex = -1;

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int index) {
        this.mSelectedIndex = index;
    }

    public int indexOf(SongVO vo) {
        // SongVO已重写equals与hashCode。
        return mData.indexOf(vo);
    }
}
