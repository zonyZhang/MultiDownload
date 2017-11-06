package com.zony.download.adapte;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zony.download.R;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadUtil;
import com.zony.multidownload.utils.LogUtil;

import java.util.List;

public class DownloadListItemAdapter
        extends RecyclerView.Adapter<DownloadListItemAdapter.ViewHolder> {
    private final static String TAG = "DownloadListItemAdapter";

    private Context mContext;

    private List<DownloadItem> mDatas;

    public DownloadListItemAdapter(Context context, List<DownloadItem> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void setDatas(List<DownloadItem> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = View.inflate(mContext, R.layout.adapte_downloadlist_item, null);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LogUtil.i(TAG, "onBindViewHolder");
        // 获取当前item数据
        final DownloadItem downloadItem = mDatas.get(position);
        holder.mTitle.setText(downloadItem.getName());
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.mTitle.isSelected()) {
                    if (mListener != null) {
                        mListener.addView(view);
                    }
                    DownloadUtil.startDownload(mContext, downloadItem);
                    holder.mTitle.setSelected(true);
                } else {
                    Toast.makeText(mContext, "It is downloaded !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        int downloadState = downloadItem.getDownloadState();
        LogUtil.i(TAG, "DownloadListItemAdapter onBindViewHolder: " + downloadItem.getName()
                + ", state: " + downloadState);
        if (downloadState == -1) {
            holder.mTitle.setSelected(false);
        } else {
            holder.mTitle.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.download_title);
        }
    }


    private AddClickListener mListener;

    public void setListener(AddClickListener listener) {
        mListener = listener;
    }

    public interface AddClickListener {
        void addView(View v);
    }

}
