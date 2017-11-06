package com.zony.download;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zony.download.activity.BaseActivity;
import com.zony.download.utils.CommonUtil;
import com.zony.download.utils.UiUtil;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadListObserver;
import com.zony.multidownload.utils.DownLoadConstant.DownloadState;
import com.zony.multidownload.utils.DownloadUtil;
import com.zony.multidownload.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.zony.download.utils.DataUtil.getDownloadItem;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    private ProgressBar mPgb4;

    private TextView mText4Percent, mText4Size;

    private ToggleButton mTb4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPgb4 = (ProgressBar) findViewById(R.id.download_progressbar);
        mText4Percent = (TextView) findViewById(R.id.download_percent);
        mText4Size = (TextView) findViewById(R.id.download_size);
        mTb4 = (ToggleButton) findViewById(R.id.download_state);
        mTb4.setOnClickListener(this);

        findViewById(R.id.download_list).setOnClickListener(this);
        findViewById(R.id.downloaded_list).setOnClickListener(this);
        initPermission();
    }

    public void cancelDown(View view) {
        LogUtil.i(TAG, "cancelDown");
        DownloadUtil.deleteDownload(this, getDownloadItem());
        mPgb4.setProgress(0);
        mTb4.setChecked(false);
        mText4Percent.setText("%0");
        mText4Size.setText("0M/0M");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_list:
                LogUtil.i(TAG, "downloadList");
                CommonUtil.startDownloadListActivity(this, false);
                break;
            case R.id.downloaded_list:
                CommonUtil.startDownloadListActivity(this, true);
                break;
            case R.id.download_state:
                if (mTb4.isChecked()) {
                    final DownloadItem downloadItem = getDownloadItem();
                    DownloadUtil.startDownload(this, downloadItem, new DownloadListObserver() {
                        @Override
                        public void onDownloadListObserver(
                                final List<DownloadItem> downloadItemList) {
                            updateUi(downloadItem, downloadItemList);
                        }

                        @Override
                        public void delDownloadFileSuc(boolean isDelAll) {
                            LogUtil.i(TAG, "MainActivity delDownloadFileSuc");
                        }
                    });
                } else {
                    DownloadUtil.pauseDownload(this, getDownloadItem());
                }
                break;
        }
    }

    private void updateUi(final DownloadItem downloadItem,
            final List<DownloadItem> downloadItemList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "MainActivity onDownloadObserver : " + downloadItemList.size());
                if (downloadItemList != null && downloadItemList.size() != 0) {
                    for (DownloadItem downloadItem : downloadItemList) {
                        LogUtil.i(TAG, "MainActivity onDownloadObserver getDownloadPercent: "
                                + downloadItem.getDownloadPercent());
                        UiUtil.updateDownloadUi(downloadItem, mPgb4, mText4Percent, mText4Size,
                                mTb4);
                    }
                } else {
                    downloadItem.setDownloadState(DownloadState.COMPLETE);
                    UiUtil.updateDownloadUi(downloadItem, mPgb4, mText4Percent, mText4Size, mTb4);
                }
            }
        });
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
