package com.priv.sepp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.priv.sepp.widget.SwipeLayout;
import com.priv.sepp.widget.SwipeLayoutManager;

import java.util.ArrayList;

import m3u8downloader.M3U8Downloader;
import m3u8downloader.bean.M3U8Task;

public class VideoListAdapter extends ArrayAdapter<M3U8Task> implements SwipeLayout.OnSwipeStateChangeListener {
    private Context context;
    private OnItemIconClickListener itemIconClickListener;
    private OnDeleteListener mListener;
    private boolean pauseUpdate;

    public interface OnDeleteListener {
        void onDelete(int i);
    }

    public interface OnItemIconClickListener {
        void onItemIconClick(int i);
    }

    private static class ViewHolder {
        private ImageView iv;
        private ImageView ivStatu;
        private TextView progressTv;
        private TextView stateTv;
        private SwipeLayout swipeLayout;
        private TextView tvDelete;
        private TextView urlName;

        public ViewHolder(View view) {
            this.tvDelete = (TextView) view.findViewById(R.id.tv_delete);
            this.swipeLayout = (SwipeLayout) view.findViewById(R.id.swipelayout);
            this.urlName = (TextView) view.findViewById(R.id.url_tv);
            this.stateTv = (TextView) view.findViewById(R.id.state_tv);
            this.progressTv = (TextView) view.findViewById(R.id.progress_tv);
            this.iv = (ImageView) view.findViewById(R.id.imageView);
            this.ivStatu = (ImageView) view.findViewById(R.id.ivStatu);
        }

        public static ViewHolder getHolder(View view) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder != null) {
                return viewHolder;
            }
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return viewHolder;
        }
    }

    public void onClose(Object obj) {
    }

    public void onOpen(Object obj) {
    }

    public boolean isPauseUpdate() {
        return this.pauseUpdate;
    }

    public void setPauseUpdate(boolean z) {
        this.pauseUpdate = z;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.mListener = onDeleteListener;
    }

    public VideoListAdapter(@NonNull Context context, @LayoutRes int i, @NonNull M3U8Task[] m3U8TaskArr) {
        super(context, i, m3U8TaskArr);
        this.context = context;
    }

    public VideoListAdapter(@NonNull Context context, @LayoutRes int i, @NonNull ArrayList<M3U8Task> arrayList) {
        super(context, i, arrayList);
        this.context = context;
    }

    @NonNull
    public View getView(final int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        M3U8Task m3U8Task = (M3U8Task) getItem(i);
        if (view == null) {
            view = View.inflate(this.context, R.layout.item_listview, null);
        }
        ViewHolder holder = ViewHolder.getHolder(view);
        holder.urlName.setText(m3U8Task.getName());
        setStateText(holder.stateTv, m3U8Task);
        setProgressText(holder.progressTv, m3U8Task);
        holder.iv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VideoListAdapter.this.itemIconClickListener != null) {
                    VideoListAdapter.this.itemIconClickListener.onItemIconClick(i);
                }
            }
        });
        Glide.with(getContext()).load(m3U8Task.getIcon()).into(holder.iv);
        if (m3U8Task.getState() == 3) {
            holder.stateTv.setText("已完成");
            holder.stateTv.setText(TimerUtils.getTime(m3U8Task.getSecs()));
            holder.ivStatu.setBackgroundResource(R.drawable.video_click_play_selector);
        }
        if (m3U8Task.getTotalTs() == m3U8Task.getCurTs() && m3U8Task.getTotalTs() > 0) {
            holder.stateTv.setText("已完成");
            holder.stateTv.setText(TimerUtils.getTime(m3U8Task.getSecs()));
            holder.ivStatu.setBackgroundResource(R.drawable.video_click_play_selector);
            holder.progressTv.setText(m3U8Task.getFileSize());
        } else if (m3U8Task.getState() != 3) {
            holder.ivStatu.setBackgroundResource(R.drawable.exo_controls_pause);
        }
        holder.swipeLayout.setTag(Integer.valueOf(i));
        holder.swipeLayout.setOnSwipeStateChangeListener(this);
        holder.tvDelete.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideoListAdapter.this.mListener.onDelete(i);
                VideoListAdapter.this.notifyDataSetChanged();
                SwipeLayoutManager.getInstance().closeCurrentLayout();
                SwipeLayoutManager.getInstance().clearCurrentLayout();
                Context access$800 = VideoListAdapter.this.context;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("第");
                stringBuilder.append(i);
                stringBuilder.append("个已删除");
                Toast.makeText(access$800, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void setProgressText(TextView textView, M3U8Task m3U8Task) {
        int state = m3U8Task.getState();
        StringBuilder stringBuilder;
        if (state != 5) {
            switch (state) {
                case 2:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("已缓存：");
                    stringBuilder.append(String.format("%.1f ", new Object[]{Float.valueOf(m3U8Task.getProgress() * 100.0f)}));
                    stringBuilder.append("%       速度：");
                    stringBuilder.append(m3U8Task.getFormatSpeed());
                    textView.setText(stringBuilder.toString());
                    return;
                case 3:
                    textView.setText(m3U8Task.getFormatTotalSize());
                    return;
                default:
                    textView.setText("未缓存");
                    return;
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("已缓存：");
        stringBuilder.append(String.format("%.1f ", new Object[]{Float.valueOf(m3U8Task.getProgress() * 100.0f)}));
        stringBuilder.append("%");
        stringBuilder.append(m3U8Task.getFormatTotalSize());
        textView.setText(stringBuilder.toString());
    }

    private void setStateText(TextView textView, M3U8Task m3U8Task) {
        if (M3U8Downloader.getInstance().checkM3U8IsExist(m3U8Task.getUrl())) {
            textView.setText("已完成");
            textView.setText(TimerUtils.getTime(m3U8Task.getSecs()));
            return;
        }
        int state = m3U8Task.getState();
        if (state != -1) {
            switch (state) {
                case 1:
                    textView.setText("准备中");
                    break;
                case 2:
                    textView.setText("正在下载");
                    break;
                case 3:
                    textView.setText("下载完成");
                    break;
                case 4:
                    textView.setText("下载异常，点击重试");
                    break;
                case 5:
                    textView.setText("暂停中");
                    break;
                case 6:
                    textView.setText("存储空间不足");
                    break;
                default:
                    textView.setText("未下载");
                    break;
            }
        }
        textView.setText("等待中");
    }

    public void notifyChanged(M3U8Task[] m3U8TaskArr, M3U8Task m3U8Task) {
        if (!isPauseUpdate()) {
            for (int i = 0; i < getCount(); i++) {
                if (((M3U8Task) getItem(i)).equals(m3U8Task)) {
                    m3U8TaskArr[i] = m3U8Task;
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void notifyChanged(ArrayList<M3U8Task> arrayList, M3U8Task m3U8Task) {
        if (!isPauseUpdate()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("total:");
            stringBuilder.append(getCount());
            stringBuilder.append(" , t:");
            stringBuilder.append(arrayList.size());
            stringBuilder.append("  len:");
            stringBuilder.append(m3U8Task.getSecs());
            Log.e("OnDeleteListener>>", stringBuilder.toString());
            for (int i = 0; i < getCount(); i++) {
                if (((M3U8Task) getItem(i)).equals(m3U8Task)) {
                    arrayList.set(i, m3U8Task);
                    notifyDataSetChanged();
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setOnItemIconClickListener(OnItemIconClickListener onItemIconClickListener) {
        this.itemIconClickListener = onItemIconClickListener;
    }
}
