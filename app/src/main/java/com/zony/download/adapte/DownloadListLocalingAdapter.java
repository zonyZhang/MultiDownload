package com.zony.download.adapte;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zony.download.R;
import com.zony.download.utils.UiUtil;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadUtil;
import com.zony.multidownload.utils.LogUtil;

import java.util.List;

public class DownloadListLocalingAdapter
        extends RecyclerView.Adapter<DownloadListLocalingAdapter.ViewHolder> {
    private final static String TAG = "DownloadListLocalingAdapter";

    private Context mContext;

    private List<DownloadItem> mDatas;

    public DownloadListLocalingAdapter(Context context, List<DownloadItem> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void setDatas(List<DownloadItem> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = View.inflate(mContext, R.layout.adapte_downloadlistlocal_ing, null);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 获取当前item数据
        final DownloadItem downloadItem = mDatas.get(position);
        holder.mTitle
                .setText(downloadItem.getName() + ", state: " + downloadItem.getDownloadState());
        LogUtil.i(TAG, "onBindViewHolder" + downloadItem.getName() + ", time: "
                + downloadItem.getCreateTime());
        holder.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadUtil.deleteDownload(mContext, downloadItem);
            }
        });
        holder.mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mToggleButton.isChecked()) {
                    DownloadUtil.startDownload(mContext, downloadItem);
                } else {
                    DownloadUtil.pauseDownload(mContext, downloadItem);
                }
            }
        });
        UiUtil.updateDownloadUi(downloadItem, holder.mProgressBar, holder.mTextPercent,
                holder.mTextSize, holder.mToggleButton);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mItemView;

        ProgressBar mProgressBar;

        TextView mTitle, mTextPercent, mTextSize, mCancel;

        ToggleButton mToggleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.downloadlistlocal_ing_title);
            mCancel = (TextView) itemView.findViewById(R.id.downloadlistlocal_ing_cancel);
            mProgressBar = (ProgressBar) itemView
                    .findViewById(R.id.downloadlistlocal_ing_progressbar);
            mTextPercent = (TextView) itemView.findViewById(R.id.downloadlistlocal_ing_percent);
            mTextSize = (TextView) itemView.findViewById(R.id.downloadlistlocal_ing_size);
            mToggleButton = (ToggleButton) itemView.findViewById(R.id.downloadlistlocal_ing_state);
        }
    }
}
