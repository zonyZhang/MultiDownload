
package com.zony.multidownload.utils;

/**
 * 下载相关Constant
 *
 * @author zony
 * @time 2 Nov 2015 18:24:43
 */
public interface DownLoadConstant {
    //可同时下载的任务数（核心线程数）
    int CORE_POOL_SIZE = 1;

    //缓存队列的大小（最大线程数）
    int MAX_POOL_SIZE = 20;

    //非核心线程闲置的超时时间（秒），如果超时则会被回收
    long KEEP_ALIVE = 10L;

    // 缓冲区大小4k
    int FILE_BUFFER_LEN = 4 * 1024;

    // 重试次数
    int NET_TRY_CONN_TIMES = 5;

    // 连接超时
    int CONNECT_TIMEOUT = 20 * 1000;

    // 读取超时
    int TRANSMIT_TIMEOUT = 20 * 1000;

    // 刷新进度时间(单位毫秒)
    int NOTIFY_PROGRESS_TIME = 1000;

    // 下载路径
    String DOWNLOAD_PATH = DownloadFileUtil.getDownLoadPath();

    // 这个是每个Provider的标识，在Manifest中使用
    String AUTHORITY = "com.zony.multidownload.provider";

    /**
     * 下载状态
     */
    interface DownloadState {
        int WAIT = 0; // 等待下载

        int START = 1; // 下载开始

        int ING = 2; // 下载中

        int PAUSE = 3; // 下载暂停

        int FAIL = 4; // 下载失败

        int COMPLETE = 5; // 下载完成
    }

    /**
     * 服务器返回状态
     */
    interface State {
        int SERVER_WAIT = -1;// 未开始下载，等待状态

        int OK = 1;

        int BAD_URL = 2;

        int TIME_OUT = 3;

        int REQUEST_FAILED = 4;

        int IO_ERROR = 5;

        int PAUSE = 6;// 暂停状态

        int UNKNOWN = 7;
    }

    /**
     * 下载command
     */
    interface DownloadCommand {
        String DL_COMMAND = "download_command";

        int START_DOWNLOAD = 1;

        int PAUSE_DOWNLOAD = 2;

        int DELETE_DOWNLOAD = 3;

        int START_ALL_DOWNLOADS = 4;

        int PAUSE_ALL_DOWNLOADS = 5;

        int DELETE_ALL_DOWNLOAD_ING = 6;

        int DELETE_ALL_DOWNLOAD_COMPLETE = 7;
    }

    interface DownloadBundle {
        String DOWNLOAD_ITEM = "downloadItem";
    }

}
