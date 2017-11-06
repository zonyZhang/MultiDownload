package com.zony.multidownload.manager;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.SparseArray;

import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadObserver;
import com.zony.multidownload.protocal.Controller;
import com.zony.multidownload.thread.DownloadThread;
import com.zony.multidownload.thread.DownloadThreadBreakpoint;
import com.zony.multidownload.thread.DownloadThreadNoBreakpoint;
import com.zony.multidownload.thread.DownloadThreadPool;
import com.zony.multidownload.utils.DownLoadConstant.DownloadBundle;
import com.zony.multidownload.utils.DownLoadConstant.DownloadCommand;
import com.zony.multidownload.utils.DownLoadConstant.DownloadState;
import com.zony.multidownload.utils.DownloadCommonUtil;
import com.zony.multidownload.utils.DownloadFileUtil;
import com.zony.multidownload.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zony on 17-6-8.
 */

public class DownloadManager extends Service implements IDownloadManager {

    private static final String TAG = "DownloadManager";

    private DownloadThread downloadThread;

    // 下载控制器
    private SparseArray<ContorllerImpl> controllerList;

    // 下载列表
    private List<DownloadItem> downloadItemList;

    private DownloadBinder binder;

    private RemoteCallbackList<IDownloadServiceCallback> callbackList = new RemoteCallbackList<>();

    private IDownloadServiceCallback mCallback;

    private DownloadDao mDb;

    private boolean isDelAll = false;// 是否删除全部文件

    @Override
    public void onCreate() {
        super.onCreate();
        controllerList = new SparseArray<>();
        downloadItemList = new CopyOnWriteArrayList<>();
        binder = new DownloadBinder(this);
        mDb = DownloadDao.getInstance(this);
    }

    @Override
    public synchronized void startDownload(DownloadItem item) {
        item.setDownloadState(DownloadState.WAIT);
        ContorllerImpl controller = new ContorllerImpl();
        controller.setStop(false);
        controllerList.put(item.getId(), controller);

        if (!downloadItemList.contains(item)) {
            downloadItemList.add(item);
        }
        mDb.insertDownloadItem(item);
        DownloadThreadPool.getInstance().getThreadPoolExecutor()
                .execute(getDownloadThread(item, new DownloadObserver() {
                    @Override
                    public void onDownloadObserver(DownloadItem downloadItem) {
                        if (mCallback != null) {
                            try {
                                LogUtil.i(TAG,
                                        "DownloadManager onDownloadObserver startDownload updateProgress: "
                                                + downloadItem.getDownloadPercent()
                                                + ", downloadState: "
                                                + downloadItem.getDownloadState()
                                                + ", downloadItemList: " + downloadItemList.size()
                                                + ", downloadName: " + downloadItem.getName());
                                // 为节省开销，只更新下载中item
                                mCallback.updateProgress(mDb.getDownloadingItemsByName());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, controller));
    }

    @Override
    public synchronized void pauseDownload(DownloadItem item) {
        LogUtil.i(TAG, "DownloadManager pauseDownload:" + item.getName());
        if (!DownloadCommonUtil.isSparseArrayEmpty(controllerList)) {
            Controller controller = controllerList.get(item.getId());
            if (controller != null) {
                controller.stop();
                controllerList.remove(item.getId());
            }
        }

        if (!downloadItemList.isEmpty()) {
            for (Iterator<DownloadItem> iter = downloadItemList.iterator(); iter.hasNext();) {
                DownloadItem downloadItem = iter.next();
                if (downloadItem.getId() == item.getId()) {
                    downloadItemList.remove(downloadItem);
                }
            }
        }
    }

    @Override
    public synchronized void deleteDownload(final DownloadItem item) {
        LogUtil.i(TAG, item.toString());

        mDb.deleteDownloadItem(item);
        pauseDownload(item);

        // 删除下载目录
        DownloadFileUtil.deleteDir(new File(item.getFileSaveFullPath()));
        if (!isDelAll && mCallback != null) {
            try {
                mCallback.delDownloadFileSuc(isDelAll);
                LogUtil.i(TAG,
                        "DownloadManager deleteDownload:" + item.getName() + " delDownloadFileSuc");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void startAllDownload(List<DownloadItem> items) {
        for (DownloadItem item : items) {
            startDownload(item);
        }
    }

    @Override
    public synchronized void pauseAllDownload(List<DownloadItem> items) {
        for (DownloadItem item : items) {
            pauseDownload(item);
        }
    }

    @Override
    public synchronized void deleteAllDownload(List<DownloadItem> items) {
        isDelAll = true;
        for (DownloadItem item : items) {
            deleteDownload(item);
        }
        if (mCallback != null) {
            try {
                mCallback.delDownloadFileSuc(isDelAll);
                isDelAll = false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
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
            downloadThread = new DownloadThreadBreakpoint(this, downloadItem, observer, controller);
        } else {
            downloadThread = new DownloadThreadNoBreakpoint(this, downloadItem, observer,
                    controller);
        }
        return downloadThread;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        int command = bundle.getInt(DownloadCommand.DL_COMMAND);
        switch (command) {
            case DownloadCommand.START_DOWNLOAD:
                DownloadItem item = (DownloadItem) bundle
                        .getSerializable(DownloadBundle.DOWNLOAD_ITEM);
                startDownload(item);
                break;
            case DownloadCommand.PAUSE_DOWNLOAD:
                item = (DownloadItem) bundle.getSerializable(DownloadBundle.DOWNLOAD_ITEM);
                pauseDownload(item);
                break;
            case DownloadCommand.DELETE_DOWNLOAD:
                item = (DownloadItem) bundle.getSerializable(DownloadBundle.DOWNLOAD_ITEM);
                deleteDownload(item);
                break;
            case DownloadCommand.START_ALL_DOWNLOADS:
                startAllDownload(mDb.getDownloadingItemsByCreatTime());
                break;
            case DownloadCommand.PAUSE_ALL_DOWNLOADS:
                pauseAllDownload(mDb.getDownloadingItemsByCreatTime());
                break;
            case DownloadCommand.DELETE_ALL_DOWNLOAD_ING:
                deleteAllDownload(mDb.getDownloadingItemsByName());
                break;
            case DownloadCommand.DELETE_ALL_DOWNLOAD_COMPLETE:
                deleteAllDownload(mDb.getDownloadedItems());
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!DownloadCommonUtil.isSparseArrayEmpty(controllerList)) {
            controllerList.clear();
            controllerList = null;
        }

        if (!downloadItemList.isEmpty()) {
            downloadItemList.clear();
            downloadItemList = null;
        }

        if (callbackList != null) {
            callbackList.kill();
            callbackList = null;
        }

        if (mCallback != null) {
            mCallback = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class DownloadBinder extends IDownloadService.Stub {

        WeakReference<DownloadManager> refrence;

        DownloadManager downloadManager;

        DownloadBinder(DownloadManager service) {
            refrence = new WeakReference<DownloadManager>(service);
            downloadManager = refrence.get();
        }

        @Override
        public void startDownload(DownloadItem item) throws RemoteException {
            downloadManager.startDownload(item);
        }

        @Override
        public void pauseDownload(DownloadItem item) throws RemoteException {
            downloadManager.pauseDownload(item);
        }

        @Override
        public void deleteDownload(DownloadItem item) throws RemoteException {
            downloadManager.deleteDownload(item);
        }

        @Override
        public void startAllDownload(List<DownloadItem> items) throws RemoteException {
            downloadManager.startAllDownload(items);
        }

        @Override
        public void pauseAllDownload(List<DownloadItem> items) throws RemoteException {
            downloadManager.pauseAllDownload(items);
        }

        @Override
        public void deleteAllDownload(List<DownloadItem> items) throws RemoteException {
            downloadManager.deleteAllDownload(items);
        }

        @Override
        public void registerDownloadCallback(IDownloadServiceCallback callback)
                throws RemoteException {
            LogUtil.i(TAG, "DownloadManager DownloadBinder registerDownloadCallback");
            if (callback != null) {
                callbackList.register(callback);
                mCallback = callback;
            }
        }

        @Override
        public void unregisterDownloadCallback(IDownloadServiceCallback callback)
                throws RemoteException {
            LogUtil.i(TAG, "DownloadManager DownloadBinder unregisterDownloadCallback");
            if (callback != null) {
                callbackList.unregister(callback);
            }
        }
    }
}
