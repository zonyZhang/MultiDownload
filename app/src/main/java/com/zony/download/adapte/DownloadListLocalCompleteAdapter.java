package com.zony.download.adapte;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zony.download.R;
import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadUtil;

import java.util.List;

public class DownloadListLocalCompleteAdapter
        extends RecyclerView.Adapter<DownloadListLocalCompleteAdapter.ViewHolder> {
    private final static String TAG = "DownloadListLocalCompleteAdapter";

    private Context mContext;

    private List<DownloadItem> mDatas;

    public DownloadListLocalCompleteAdapter(Context context, List<DownloadItem> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = View.inflate(mContext, R.layout.adapte_downloadlistlocal_complete, null);
        return new ViewHolder(itemview);
    }

    public void setDatas(List<DownloadItem> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 获取当前item数据
        final DownloadItem downloadItem = mDatas.get(position);
        holder.mTitle.setText(downloadItem.getName() + ", ID:" + downloadItem.getId());

        holder.mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadUtil.deleteDownload(mContext, downloadItem);
                setDatas(DownloadDao.getInstance(mContext).getDownloadedItems());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button mBtnDel;

        TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.downloadlistlocal_complete_title);
            mBtnDel = (Button) itemView.findViewById(R.id.downloadlistlocal_complete_btn_del);
        }
    }
}
