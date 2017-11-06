
package com.zony.multidownload;

import android.content.Context;
import android.util.SparseArray;

import com.zony.multidownload.manager.ContorllerImpl;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadObserver;
import com.zony.multidownload.manager.IDownloadManager;
import com.zony.multidownload.protocal.Controller;
import com.zony.multidownload.thread.DownloadThread;
import com.zony.multidownload.thread.DownloadThreadBreakpoint;
import com.zony.multidownload.thread.DownloadThreadNoBreakpoint;
import com.zony.multidownload.thread.DownloadThreadPool;
import com.zony.multidownload.utils.LogUtil;

import java.util.List;

/**
 * @author zony
 * @time 20 Jan 2017 11:20:34
 */
public class HttpEngine implements IDownloadManager{
    private static final String TAG = "HttpEngine";

    private Context mContext;

    private static HttpEngine mHttpEngine;

    private DownloadThread downloadThread;

    private SparseArray<Controller> mControllerMap = new SparseArray<>();

    private HttpEngine(Context context) {
        LogUtil.i(TAG, "construct a engine");
        this.mContext = context;
    }

    public static HttpEngine instance(Context context) {
        if (mHttpEngine == null) {
            mHttpEngine = new HttpEngine(context);
        }
        return mHttpEngine;
    }

    public Controller requestFile(DownloadItem downloadItem, DownloadObserver observer) {
        if (downloadItem == null || observer == null) {
            throw new NullPointerException("http observer is can not null !");
        }
        LogUtil.i(TAG, "HtppEngine requestFile url: " + downloadItem.getUrl());
        Controller controller = new ContorllerImpl();
        DownloadThreadPool.getInstance().getThreadPoolExecutor().execute(
                getDownloadThread(downloadItem, observer, controller));
        mControllerMap.put(downloadItem.getId(), controller);
        return controller;
    }

    /**
     * 获取下载线程
     *
     * @param
     * @author zony
     * @time 17-6-8 下午3:22
     */
    private DownloadThread getDownloadThread(DownloadItem downloadItem, DownloadObserver observer,
                                             Controller controller) {
        if (downloadItem.isSupportBreakpointResume()) {
            downloadThread = new DownloadThreadBreakpoint(mContext, downloadItem, observer, controller);
        } else {
            downloadThread = new DownloadThreadNoBreakpoint(mContext, downloadItem, observer, controller);
        }
        return downloadThread;
    }

    @Override
    public void startDownload(DownloadItem item) {

    }

    @Override
    public void pauseDownload(DownloadItem item) {
        if (mControllerMap != null && mControllerMap.size() > 0) {
            mControllerMap.get(item.getId()).stop();
        }
    }

    @Override
    public void deleteDownload(DownloadItem item) {
        if (mControllerMap != null && mControllerMap.size() > 0) {
            mControllerMap.remove(item.getId());
        }
    }

    @Override
    public void startAllDownload(List<DownloadItem> items) {

    }

    @Override
    public void pauseAllDownload(List<DownloadItem> items) {

    }

    @Override
    public void deleteAllDownload(List<DownloadItem> items) {

    }
}
