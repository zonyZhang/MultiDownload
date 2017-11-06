package com.zony.multidownload.thread;

import android.content.Context;

import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadFileDelObserver;
import com.zony.multidownload.utils.LogUtil;

import java.io.File;

public class DownloadFileDelThread implements Runnable {

    public static final String TAG = "DownloadThread";

    private Context mContext;

    private DownloadItem mDownloadItem;

    private DownloadFileDelObserver mDelObserver;

    public DownloadFileDelThread(Context context, DownloadItem downloadItem,
            DownloadFileDelObserver delObserver) {
        mContext = context;
        mDownloadItem = downloadItem;
        mDelObserver = delObserver;
    }

    @Override
    public void run() {
        deleteDir(new File(mDownloadItem.getFileSaveFullPath()));
    }

    public void deleteDir(final File file) {
        try {
            if (mContext == null) {
                LogUtil.i(TAG, "DownloadFileDelThread context is null !");
                mDelObserver.onDownloadFileDelObserver(false);
                return;
            }
            // 删除数据库记录
            DownloadDao.getInstance(mContext).deleteDownloadItem(mDownloadItem);
            if (file.isFile() && file.exists()) {
                LogUtil.i(TAG, "DownloadFileDelThread deleteDir file.delete: " + file.getPath());
                file.delete();
                mDelObserver.onDownloadFileDelObserver(true);
                return;
            }
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
            file.delete();
            mDelObserver.onDownloadFileDelObserver(true);
        } catch (Exception e) {
            mDelObserver.onDownloadFileDelObserver(false);
            e.printStackTrace();
        }
    }
}
