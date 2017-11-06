package com.zony.multidownload.utils;

import com.zony.multidownload.domain.DownloadItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zony on 17-6-5.
 */

public class SortUtil {

    /**
     * 文件名称排序
     */
    /**
     * 文件名称排序，按照windows文件排序算法
     *
     * @param
     * @author zony
     * @time 17-11-3 下午3:19
     */
    public static void sortByName(List<DownloadItem> downloadItemList, final boolean asc) {
        final int sortCode = asc ? -1 : 1;
        // System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(downloadItemList, new Comparator<DownloadItem>() {

            @Override
            public int compare(DownloadItem item1, DownloadItem item2) {
                int cmp = WindowsExplorerSort.compare(item1.getName(), item2.getName());
                if (cmp < 0) {
                    return sortCode;
                } else if (cmp > 0) {
                    return -sortCode;
                } else {
                    return 0;
                }
            }
        });
    }
}
