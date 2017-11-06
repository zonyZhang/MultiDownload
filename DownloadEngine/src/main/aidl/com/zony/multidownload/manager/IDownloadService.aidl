// IDownloadService.aidl
package com.zony.multidownload.manager;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.manager.IDownloadServiceCallback;

interface IDownloadService {
    void startDownload(in DownloadItem item);
    void pauseDownload(in DownloadItem item);
    void deleteDownload(in DownloadItem item);
    void startAllDownload(in List<DownloadItem> items);
    void pauseAllDownload(in List<DownloadItem> items);
    void deleteAllDownload(in List<DownloadItem> items);
    void registerDownloadCallback(IDownloadServiceCallback observer);
    void unregisterDownloadCallback(IDownloadServiceCallback observer);
}
