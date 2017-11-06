package com.zony.download.utils;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadCommonUtil;
import com.zony.multidownload.utils.LogUtil;

import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.COMPLETE;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.FAIL;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.ING;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.PAUSE;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.START;
import static com.zony.multidownload.utils.DownLoadConstant.DownloadState.WAIT;

/**
 * Created by zony on 17-6-5.
 */

public class UiUtil {

    private static final String TAG = "UiUtil";

    /**
     * 更新下载速度等ui
     *
     * @param downloadItem
     * @author zony
     * @time 2014-9-24
     */
    public static void updateDownloadUi(DownloadItem downloadItem, ProgressBar progressBar,
            TextView txPercent, TextView tvSize, ToggleButton toggleButton) {
        progressBar.setProgress(0);
        txPercent.setText("0%");
        int respCode = downloadItem.getResponseCode();
        LogUtil.i(TAG, "name: " + downloadItem.getName() + ", respCode: " + respCode);
        // switch (downloadItem.getResponseState()) {
        // case BAD_URL:
        // tvSize.setText("BAD_URL RESPCODE:" + respCode);
        // break;
        // case TIME_OUT:
        // tvSize.setText("TIME_OUT RESPCODE:" + respCode);
        // break;
        // case REQUEST_FAILED:
        // tvSize.setText("REQUEST_FAILED RESPCODE:" + respCode);
        // break;
        // case SERVER_WAIT:
        // tvSize.setText("SERVER_WAIT:" + respCode);
        // break;
        // case IO_ERROR:
        // tvSize.setText("IO_ERROR RESPCODE:" + respCode);
        // break;
        // default:
        // updateSpeedUi(downloadItem, progressBar, txPercent, tvSize, toggleButton);
        // break;
        // }
        updateSpeedUi(downloadItem, progressBar, txPercent, tvSize, toggleButton);
    }

    private static void updateSpeedUi(DownloadItem downloadItem, ProgressBar progressBar,
            TextView txPercent, TextView tvSize, ToggleButton toggleButton) {
        int m_Percent = downloadItem.getDownloadPercent();
        progressBar.setProgress(m_Percent);
        txPercent.setText(m_Percent + "%");
        toggleButton.setChecked(false);
        int downloadState = downloadItem.getDownloadState();
        switch (downloadState) {
            case WAIT:
            case START:
                tvSize.setText("WAIT");
                break;
            case FAIL:
                tvSize.setText("FAIL: " + downloadItem.getResponseCode());
                break;
            case COMPLETE:
                tvSize.setText("COMPLETE");
                break;
            case ING:
                toggleButton.setChecked(true);
                int downloadSpeed = downloadItem.getDownloadSpeed();
                if (downloadSpeed == 0) {
                    tvSize.setText("Connecting...");
                } else {
                    String remainTime = DownloadCommonUtil
                            .getRemainTime(downloadItem.getRemainTime());
                    String speed = DownloadCommonUtil.formatSpeedSize(downloadSpeed);
                    tvSize.setText(
                            String.format("%.2fM", downloadItem.getDownLoadedSize() / 1024 / 1024f)
                                    + "/"
                                    + DownloadCommonUtil.formatFileSize(downloadItem.getTotalSize())
                                    + "\n" + remainTime + "\n" + speed);
                }
                break;
            case PAUSE:
                toggleButton.setChecked(false);
                tvSize.setText("Pause");
                break;

        }
    }
}
