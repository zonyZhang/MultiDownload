// IDownloadServiceCallback.aidl
package com.zony.multidownload.manager;

import com.zony.multidownload.domain.DownloadItem;

interface IDownloadServiceCallback {
	void updateProgress(in List<DownloadItem> list);
	void delDownloadFileSuc(in boolean isDelAll);
}