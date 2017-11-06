package com.zony.download.utils;

import android.content.Context;
import android.content.Intent;

import com.zony.download.activity.DownloadListActivity;

import static com.zony.download.utils.Constant.DownloadActivityBundle.IS_DOWNLOADED_LIST;

/**
 * Created by zony on 17-6-5.
 */

public class CommonUtil {

    /**
     * 启动DownloadListActivity
     *
     * @param isDownloadedList 是否为已下载列表
     * @author zony
     * @time 17-10-20 下午5:10
     */
    public static void startDownloadListActivity(Context context, boolean isDownloadedList) {
        Intent intent = new Intent(context, DownloadListActivity.class);
        intent.putExtra(IS_DOWNLOADED_LIST, isDownloadedList);
        context.startActivity(intent);
    }
}
