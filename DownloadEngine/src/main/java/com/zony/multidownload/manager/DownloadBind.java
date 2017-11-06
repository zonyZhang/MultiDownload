package com.zony.multidownload.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadListObserver;
import com.zony.multidownload.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class DownloadBind {
    private static final String TAG = "DownloadBind";

    private Context mContext;

    private boolean isBound;

    private ServiceBinder binder;

    private static IDownloadService remoteService;

    private DownloadListObserver observerList;

    /**
     * 监听下载
     * 
     * @param
     * @author zony
     * @time 17-10-19 下午2:25
     */
    public DownloadBind(Context context, DownloadListObserver observerList) {
        super();
        this.mContext = context;
        this.observerList = observerList;
        if (binder == null) {
            binder = new ServiceBinder(this, mCallback);
        }
    }

    private static class ServiceBinder implements ServiceConnection {

        WeakReference<DownloadBind> refrence;

        IDownloadServiceCallback callback;

        public ServiceBinder(DownloadBind downloadsBind, IDownloadServiceCallback callback) {
            refrence = new WeakReference<DownloadBind>(downloadsBind);
            this.callback = callback;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IDownloadService.Stub.asInterface(service);
            try {
                remoteService.registerDownloadCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            LogUtil.d(TAG, "DownloadBind remote download service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "DownloadBind remote download service disconnected");
            if (remoteService != null) {
                try {
                    remoteService.unregisterDownloadCallback(callback);
                    remoteService = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void bind() {
        if (isBound) {
            LogUtil.d(TAG, "DownloadBind isBound, return!");
            return;
        }
        LogUtil.d(TAG, "DownloadBind bind");
        Intent intent = new Intent(mContext, DownloadManager.class);
        if (mContext != null) {
            mContext.bindService(intent, binder, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void unBind() {
        if (!isBound) {
            LogUtil.d(TAG, "DownloadBind unBind, return!");
            return;
        }
        LogUtil.d(TAG, "DownloadBind unBind");
        if (remoteService != null && mCallback != null) {
            try {
                remoteService.unregisterDownloadCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mContext != null && binder != null) {
            mContext.unbindService(binder);
            binder = null;
            remoteService = null;
            mCallback = null;
            isBound = false;
        }
    }

    private IDownloadServiceCallback mCallback = new IDownloadServiceCallback.Stub() {

        @Override
        public void updateProgress(List<DownloadItem> list) throws RemoteException {
            if (list == null) {
                unBind();
                LogUtil.w(TAG, "DownloadBind mCallback downloadItem is null !");
                return;
            }

            if (observerList != null) {
                observerList.onDownloadListObserver(list);
            }
        }

        @Override
        public void delDownloadFileSuc(boolean isDelAll) throws RemoteException {
            if (observerList != null) {
                observerList.delDownloadFileSuc(isDelAll);
            }
        }
    };
}
