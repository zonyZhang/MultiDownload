package com.zony.multidownload.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadListObserver;
import com.zony.multidownload.manager.DownloadBind;
import com.zony.multidownload.manager.DownloadManager;
import com.zony.multidownload.utils.DownLoadConstant.DownloadBundle;
import com.zony.multidownload.utils.DownLoadConstant.DownloadCommand;

/**
 * 下载相关工具类
 *
 * @author zony
 * @time 17-6-8 下午5:15
 */
public class DownloadUtil {

    /**
     * 开始下载
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:09
     */
    public static void startDownload(Context context, DownloadItem item) {
        startDownloadCommand(context, item, DownloadCommand.START_DOWNLOAD);
    }

    /**
     * 开始下载，监听下载进度
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:09
     */
    public static void startDownload(Context context, DownloadItem item,
            DownloadListObserver observer) {
        startDownloadCommand(context, item, DownloadCommand.START_DOWNLOAD);
        new DownloadBind(context, observer).bind();
    }

    /**
     * 暂停下载
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:10
     */
    public static void pauseDownload(Context context, DownloadItem item) {
        startDownloadCommand(context, item, DownloadCommand.PAUSE_DOWNLOAD);
    }

    /**
     * 删除下载，包含本地已经缓存文件
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:10
     */
    public static void deleteDownload(Context context, DownloadItem item) {
        startDownloadCommand(context, item, DownloadCommand.DELETE_DOWNLOAD);
    }

    /**
     * 开始全部下载
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:10
     */
    public static void startAllDownload(Context context) {
        startDownloadCommand(context, null, DownloadCommand.START_ALL_DOWNLOADS);
    }

    /**
     * 暂停全部下载
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:10
     */
    public static void pauseAllDownload(Context context) {
        startDownloadCommand(context, null, DownloadCommand.PAUSE_ALL_DOWNLOADS);
    }

    /**
     * 删除全部下载中
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:11
     */
    public static void deleteAllDownloading(Context context) {
        Intent intent = new Intent(context, DownloadManager.class);
        Bundle extras = new Bundle();
        extras.putInt(DownloadCommand.DL_COMMAND, DownloadCommand.DELETE_ALL_DOWNLOAD_ING);
        intent.putExtras(extras);
        context.startService(intent);
    }

    /**
     * 删除全部下载完成
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:11
     */
    public static void deleteAllDownloadComplete(Context context) {
        Intent intent = new Intent(context, DownloadManager.class);
        Bundle extras = new Bundle();
        extras.putInt(DownloadCommand.DL_COMMAND, DownloadCommand.DELETE_ALL_DOWNLOAD_COMPLETE);
        intent.putExtras(extras);
        context.startService(intent);
    }

    /**
     * 启动下载相关命令
     *
     * @param
     * @author zony
     * @time 17-10-18 下午5:05
     */
    private static void startDownloadCommand(Context context, DownloadItem item, int command) {
        Intent intent = new Intent(context, DownloadManager.class);
        Bundle extras = new Bundle();
        if (item != null) {
            extras.putSerializable(DownloadBundle.DOWNLOAD_ITEM, item);
        }
        extras.putInt(DownloadCommand.DL_COMMAND, command);
        intent.putExtras(extras);
        context.startService(intent);
    }

}
