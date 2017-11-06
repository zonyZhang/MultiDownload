
package com.zony.multidownload.manager;


import com.zony.multidownload.domain.DownloadItem;

import java.util.List;

public interface IDownloadManager {
    void startDownload(DownloadItem item);

    void pauseDownload(DownloadItem item);

    void deleteDownload(DownloadItem item);

    void startAllDownload(List<DownloadItem> items);

    void pauseAllDownload(List<DownloadItem> items);

    void deleteAllDownload(List<DownloadItem> items);
}
