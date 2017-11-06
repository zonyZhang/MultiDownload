package com.zony.multidownload.thread;

import android.content.Context;
import android.os.SystemClock;

import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadObserver;
import com.zony.multidownload.protocal.Controller;
import com.zony.multidownload.protocal.Request;
import com.zony.multidownload.utils.DownloadCommonUtil;
import com.zony.multidownload.utils.DownloadFileUtil;
import com.zony.multidownload.utils.LogUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.zony.multidownload.utils.DownLoadConstant.CONNECT_TIMEOUT;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState;
import static com.zony.multidownload.utils.DownLoadConstant.NET_TRY_CONN_TIMES;
import static com.zony.multidownload.utils.DownLoadConstant.NOTIFY_PROGRESS_TIME;
import static com.zony.multidownload.utils.DownLoadConstant.State;
import static com.zony.multidownload.utils.DownLoadConstant.TRANSMIT_TIMEOUT;

public abstract class DownloadThread implements Runnable {

    public static final String TAG = "DownloadThread";

    private Context mContext;

    protected DownloadItem mDownloadItem;

    private DownloadObserver mObserver;

    private Controller mController;

    // 记录开始下载的时间
    private long startTime = 0;

    // 记录某时间段的下载长度
    protected long downloadSizeByTime = 0;

    // 下载过程中更新ui的开始时间
    protected long notifyUiStartTime;

    private DownloadDao mDb;

    public DownloadThread(Context context, DownloadItem downloadItem, DownloadObserver observer,
            Controller controller) {
        mContext = context;
        mDownloadItem = downloadItem;
        mObserver = observer;
        mController = controller;
        startTime = SystemClock.uptimeMillis();
        downloadSizeByTime = 0;
        mDb = DownloadDao.getInstance(mContext);
    }

    @Override
    public void run() {
        mDownloadItem.setDownloadState(DownloadState.START);
        if (mDownloadItem.getUrl() == null) {
            mDownloadItem.setResponseState(State.BAD_URL);
            notifyUser(mObserver, mDownloadItem);
            return;
        }

        if (!DownloadFileUtil.isFileFullPathValid(mDownloadItem.getFileSaveFullPath())) {
            LogUtil.w(TAG, "DownloadThread file path is not valid");
            mDownloadItem.setResponseState(State.IO_ERROR);
            notifyUser(mObserver, mDownloadItem);
            return;
        }

        int retryTime = NET_TRY_CONN_TIMES;
        boolean retryFlag = retryTime > 0;
        while (retryFlag) {
            LogUtil.i(TAG, "DownloadThread retryTime:" + retryTime);
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = getUrlConnection();
                int respCode = urlConnection.getResponseCode();
                LogUtil.i(TAG, "DownloadThread respCode:" + respCode);
                mDownloadItem.setResponseCode(respCode);
                long totalSize = 0;
                if (respCode == HttpURLConnection.HTTP_OK
                        || respCode == HttpURLConnection.HTTP_PARTIAL) {
                    totalSize = getTotalSizeFileHttpResponseHeader(urlConnection);
                    if (totalSize == 0) {
                        LogUtil.w(TAG, "DownloadThread totalSize == 0, DownloadState.FAIL");
                        mDownloadItem.setResponseState(State.UNKNOWN);
                        mDownloadItem.setDownloadState(DownloadState.FAIL);
                    } else {
                        LogUtil.i(TAG, "DownloadThread totalSize != 0");
                        InputStream is = urlConnection.getInputStream();
                        mDownloadItem.setMimeType(getMimeType(urlConnection));

                        LogUtil.i(TAG, "DownloadThread isSupportBreakpointResume:"
                                + mDownloadItem.isSupportBreakpointResume());
                        if (readFiles(mController, is, mDownloadItem, totalSize, mObserver)) {
                            mDownloadItem.setResponseState(State.OK);
                            mDownloadItem.setDownloadState(DownloadState.COMPLETE);
                            LogUtil.i(TAG, "DownloadThread DownloadState.COMPLETE");
                            LogUtil.i("uTime", "DownloadThread readFileBreakpoint usedTime : "
                                    + (SystemClock.uptimeMillis() - startTime));
                        } else if (mDownloadItem.getDownloadState() != DownloadState.PAUSE) {
                            LogUtil.i(TAG, "DownloadThread != DownloadState.PAUSE");
                            mDownloadItem.setResponseState(State.UNKNOWN);
                            mDownloadItem.setDownloadState(DownloadState.FAIL);
                        }
                        is.close();
                    }
                } else {
                    LogUtil.i(TAG, "DownloadThread DownloadState.FAIL");
                    mDownloadItem.setDownloadState(DownloadState.FAIL);
                    mDownloadItem.setResponseState(State.REQUEST_FAILED);
                }
                retryTime = 0;
            } catch (Exception e) {
                mDownloadItem.setDownloadState(DownloadState.FAIL);
                if (e instanceof IllegalStateException) {
                    mDownloadItem.setResponseState(State.REQUEST_FAILED);
                    retryTime = 0;
                } else if (e instanceof MalformedURLException) {
                    mDownloadItem.setResponseState(State.BAD_URL);
                    retryTime = 0;
                } else if (e instanceof SocketTimeoutException) {
                    retryTime--;
                    mDownloadItem.setResponseState(State.TIME_OUT);
                } else {
                    retryTime = 0;
                    mDownloadItem.setResponseState(State.REQUEST_FAILED);
                }
                LogUtil.w(TAG, "failed to do http: " + e.toString());
                e.printStackTrace();
            } finally {
                if (retryTime == 0) {
                    retryFlag = false;
                } else {
                    LogUtil.w(TAG, "retry to do http req ...  ");
                    retryFlag = true;
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        notifyUser(mObserver, mDownloadItem);
    }

    /**
     * 获取HttpURLConnection
     *
     * @param
     * @author zony
     * @time 17-6-1 下午3:21
     */
    private HttpURLConnection getUrlConnection() throws Exception {
        HttpURLConnection urlConnection = null;
        URL url = new URL(mDownloadItem.getUrl());
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setReadTimeout(TRANSMIT_TIMEOUT);
        if (mDownloadItem.getRequestType() == Request.Type.POST) {
            LogUtil.i(TAG, "DownloadThread POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            if (mDownloadItem.getPostContent() != null) {
                urlConnection.getOutputStream().write(mDownloadItem.getPostContent().getBytes());
            }
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }

        uRLConnectionSet(urlConnection);
        return urlConnection;
    }

    /**
     * 读取服务器文件（断点|非断点）
     *
     * @param
     * @author zony
     * @time 17-6-8 下午3:01
     */
    protected abstract boolean readFiles(Controller controller, InputStream in,
            DownloadItem downloadItem, long totalSize, DownloadObserver observer);

    /**
     * HttpURLConnection相关配置
     *
     * @param
     * @author zony
     * @time 17-6-8 下午3:48
     */
    protected abstract void uRLConnectionSet(HttpURLConnection urlConnection);

    /**
     * 通知用户监听
     *
     * @param
     * @author zony
     * @time 17-6-1 下午3:21
     */
    protected void notifyUser(DownloadObserver observer, DownloadItem downloadItem) {
        if (observer != null && mDb != null) {
            LogUtil.i(TAG, "DownloadThread setDonwingData:" + downloadItem.toString());
            mDb.updateDownloadItem(downloadItem);
            observer.onDownloadObserver(downloadItem);
        }
    }

    /**
     * 设置下载过程中相关数据
     *
     * @param
     * @author zony
     * @time 17-5-27 下午5:46
     */
    protected void setDonwingData(DownloadItem downloadItem, long totalSize, long downloadedSize,
            DownloadObserver observer) {
        int usedTime = (int) ((SystemClock.uptimeMillis() - startTime) / 1000);
        // 下载速度
        int downloadSpeed = 0;
        if (usedTime != 0) {
            downloadSpeed = (int) ((downloadSizeByTime / usedTime) / 1024);
        }

        if (downloadedSize <= totalSize) {
            downloadItem.setTotalSize(totalSize);
            downloadItem.setDownloadSize(downloadedSize);
            downloadItem.setResponseState(State.OK);
            downloadItem.setDownloadState(DownloadState.ING);
            downloadItem.setDownloadSpeed(downloadSpeed);
            if (downloadSpeed > 0) {
                downloadItem.setRemainTime((totalSize - downloadedSize) / downloadSpeed / 1024);
            }

            if (SystemClock.uptimeMillis() - notifyUiStartTime > NOTIFY_PROGRESS_TIME) {// 减少UI负载
                notifyUiStartTime = SystemClock.uptimeMillis();
                notifyUser(observer, downloadItem);
            }
        }
    }

    /**
     * 获取文件总大小
     *
     * @return
     * @author zony
     * @time 2014-9-5
     */
    private long getTotalSizeFileHttpResponseHeader(HttpURLConnection urlConnection) {
        String rangeContent = "content-range";
        String totalString = null;
        List<String> rangeList = null;
        Map<String, List<String>> map = urlConnection.getHeaderFields();
        // 包含content-range证明可以断点续传
        if (map != null && map.size() > 0 && map.containsKey(rangeContent)) {
            LogUtil.i(TAG, "urlConnection.getHeaderFields() containsKey content-range");
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                if (rangeContent.equalsIgnoreCase(key)) {
                    rangeList = entry.getValue();
                    break;
                }
            }
            if (rangeList != null && rangeList.size() > 0) {
                String content = rangeList.get(0);
                int i = content.lastIndexOf("/");
                totalString = content.substring(i + 1);
            }
        } else {
            totalString = urlConnection.getHeaderField("Content-Length");
        }
        if (DownloadCommonUtil.isPureNumber(totalString)) {
            long totalLong = Long.parseLong(totalString);
            LogUtil.i(TAG, "DownloadThread getTotalSizeFileHttpResponseHeader :" + totalLong);
            return totalLong;
        }
        return 0;
    }

    /**
     * get mime type, e.g. text/html.
     *
     * @param conn
     * @return mime
     */
    private String getMimeType(HttpURLConnection conn) {
        String contentType = conn.getContentType();
        if (contentType == null || contentType.length() == 0) {
            return null;
        }
        if (!contentType.contains(";")) {
            return contentType;
        }

        String[] array = contentType.split(";");
        String mime = array[0].trim();
        if (mime.length() == 0) {
            return null;
        }
        LogUtil.i(TAG, "DownloadThread getMimeType:" + mime);
        return mime;
    }
}
