package com.zony.multidownload.thread;

import android.content.Context;
import android.os.SystemClock;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadObserver;
import com.zony.multidownload.protocal.Controller;
import com.zony.multidownload.utils.DownLoadConstant;
import com.zony.multidownload.utils.DownLoadConstant.DownloadState;
import com.zony.multidownload.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

import static com.zony.multidownload.utils.DownLoadConstant.FILE_BUFFER_LEN;

/**
 * （断点续传） Created by zony on 17-6-8.
 */

public class DownloadThreadBreakpoint extends DownloadThread {
    private static final String TAG = "DownloadThreadBreakpoin";

    public DownloadThreadBreakpoint(Context context, DownloadItem downloadItem,
                                    DownloadObserver observer, Controller controller) {
        super(context, downloadItem, observer, controller);
    }

    /**
     * 读取服务器文件（断点续传）
     *
     * @param
     * @author zony
     * @time 17-6-1 下午3:22
     */
    @Override
    protected boolean readFiles(Controller controller, InputStream in, DownloadItem downloadItem,
                                long totalSize, DownloadObserver observer) {
        if (in == null) {
            LogUtil.e(TAG, " inputStream is null !!!");
            return false;
        }

        String filePath = downloadItem.getFileSaveFullPath();
        File downloadFile = new File(filePath);
        if (!downloadFile.exists()) {
            try {
                if (false == downloadFile.createNewFile()) {
                    LogUtil.w(TAG, "failed to createNewFile !!!");
                    return false;
                }
            } catch (IOException e) {
                LogUtil.w(TAG, "createNewFile exception");
                e.printStackTrace();
                return false;
            }
        }

        BufferedInputStream bufferedIn = null;
        byte[] buf = new byte[FILE_BUFFER_LEN];
        try {
            RandomAccessFile fileAccess = new RandomAccessFile(filePath, "rwd");
            long downloadedSize = fileAccess.length();
            fileAccess.seek(downloadedSize);

            bufferedIn = new BufferedInputStream(in);
            notifyUiStartTime = SystemClock.uptimeMillis();
            while (!controller.isStoped()) {
                int bufferReadLen = bufferedIn.read(buf, 0, FILE_BUFFER_LEN);
                if (bufferReadLen == -1) {
                    break;
                }
                downloadedSize += bufferReadLen;
                downloadSizeByTime += bufferReadLen;
                fileAccess.write(buf, 0, bufferReadLen);
                setDonwingData(downloadItem, totalSize, downloadedSize, observer);
            }

            if (downloadedSize == totalSize) {
                return true;
            } else {
                if (controller.isStoped()) {
                    downloadItem.setDownloadState(DownloadState.PAUSE);
                    downloadItem.setResponseState(DownLoadConstant.State.PAUSE);
                } else {
                }
                LogUtil.i(TAG,
                        "DownloadThread readFileBreakpoint request is stoped or engine uninited");
            }

        } catch (Exception e) {
            LogUtil.w(TAG, "Exception:" + e.toString());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bufferedIn != null) {
                    bufferedIn.close();
                }
            } catch (IOException e) {
                LogUtil.w(TAG, "when close ouput failed to readFile by " + e.toString());
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void uRLConnectionSet(HttpURLConnection urlConnection) {
        long downloadedSize = 0;
        File file = new File(mDownloadItem.getFileSaveFullPath());
        if (file.exists()) {
            downloadedSize = file.length();
        }

        urlConnection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
        LogUtil.i(TAG, "DownloadThread download from:" + downloadedSize);
    }
}
