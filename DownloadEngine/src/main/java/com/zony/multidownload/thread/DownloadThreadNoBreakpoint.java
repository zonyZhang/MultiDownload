package com.zony.multidownload.thread;

import android.content.Context;
import android.os.SystemClock;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadObserver;
import com.zony.multidownload.protocal.Controller;
import com.zony.multidownload.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static com.zony.multidownload.utils.DownLoadConstant.FILE_BUFFER_LEN;

/**
 * （非断点续传）
 * Created by zony on 17-6-8.
 */

public class DownloadThreadNoBreakpoint extends DownloadThread {
    private static final String TAG = "DownloadThreadNoBreakpo";
    public DownloadThreadNoBreakpoint(Context context, DownloadItem downloadItem, DownloadObserver observer, Controller controller) {
        super(context, downloadItem, observer, controller);
    }


    /**
     * 读取服务器文件（非断点续传）
     *
     * @param
     * @author zony
     * @time 17-6-1 下午3:22
     */
    @Override
    protected boolean readFiles(Controller controller, InputStream in, DownloadItem downloadItem, long totalSize, DownloadObserver observer) {
        if (in == null) {
            LogUtil.e(TAG, " inputStream is null !!!");
            return false;
        }

        String filePath = downloadItem.getFileSaveFullPath();
        File downloadFile = new File(filePath);
        if (downloadFile.exists() == true) {
            if (false == downloadFile.delete()) {
                LogUtil.e(TAG, " failed to delete the existed file !!!");
                return false;
            }
        }

        try {
            if (false == downloadFile.createNewFile()) {
                LogUtil.w(TAG, " failed to createNewFile !!!");
                return false;
            } else {
                LogUtil.i(TAG, "create a new file, " + filePath);
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "createNewFile exception");
            e.printStackTrace();
            return false;
        }

        boolean ok = false;
        OutputStream ouput = null;
        try {
            ouput = new FileOutputStream(downloadFile);
            byte tmpBuffer[] = new byte[FILE_BUFFER_LEN];
            int bufferReadLen = 0;
            long downloadedSize = 0;
            notifyUiStartTime = SystemClock.uptimeMillis();
            do {
                bufferReadLen = in.read(tmpBuffer);
                if (bufferReadLen > 0) {
                    downloadedSize += bufferReadLen;
                    downloadSizeByTime += bufferReadLen;
                    ouput.write(tmpBuffer, 0, bufferReadLen);
                    setDonwingData(downloadItem, totalSize, downloadedSize, observer);
                }
            } while (bufferReadLen != -1 && !controller.isStoped());
            ouput.flush();
            if (bufferReadLen == -1)
                ok = true;
            else {
                LogUtil.i(TAG, "readFile request is stoped or engine uninited");
                ok = false;
            }

        } catch (FileNotFoundException e) {
            LogUtil.w(TAG, "FileNotFoundException");
            e.printStackTrace();
            ok = false;
        } catch (IOException e) {
            LogUtil.w(TAG, "IOException :" + e.toString());
            e.printStackTrace();
            ok = false;
        } finally {
            try {
                if (ouput != null)
                    ouput.close();
            } catch (IOException e) {
                LogUtil.w(TAG, "when close ouput failed to readFile by " + e.toString());
                e.printStackTrace();
                ok = false;
            }
        }

        LogUtil.i(TAG, "wrote file Len: " + downloadFile.length()
                + "  total content length: " + totalSize);
        return ok;
    }

    @Override
    protected void uRLConnectionSet(HttpURLConnection urlConnection) {

    }
}
