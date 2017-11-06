package com.zony.multidownload.interfaces;

import com.zony.multidownload.domain.DownloadItem;

import java.util.List;

/**
 * 下载UI列表监听
 *
 * @author zony
 * @time 17-5-31 上午11:13
 */
public interface DownloadListObserver {

    /**
     * 下载列表监听
     *
     * @param list 下载列表
     * @author zony
     * @time 17-10-31 上午10:18
     */
    void onDownloadListObserver(List<DownloadItem> list);

    /**
     * 删除下载文件及数据库监听
     *
     * @param isDelAll 是否为删除全部
     * @author zony
     * @time 17-10-31 上午10:17
     */
    void delDownloadFileSuc(boolean isDelAll);
}
