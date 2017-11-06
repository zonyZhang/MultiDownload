package com.zony.multidownload.interfaces;

import com.zony.multidownload.domain.DownloadItem;

/**
 * 下载监听(DownloadThread中使用)
 *
 * @author zony
 * @time 17-5-31 上午11:13
 */
public interface DownloadObserver {
    void onDownloadObserver(DownloadItem downloadItem);
}
