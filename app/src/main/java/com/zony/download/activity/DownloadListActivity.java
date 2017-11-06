package com.zony.download.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.zony.download.R;
import com.zony.download.fragment.DownloadListItemFragment;
import com.zony.download.fragment.DownloadListLocalFragment;

import static com.zony.download.utils.Constant.DownloadActivityBundle.IS_DOWNLOADED_LIST;

/**
 * Created by zony on 17-6-5.
 */

public class DownloadListActivity extends BaseActivity {

    private boolean isDownloadedList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Intent intent = getIntent();
        if (intent != null) {
            isDownloadedList = intent.getBooleanExtra(IS_DOWNLOADED_LIST, false);
        }

        if (isDownloadedList) {
            ft.replace(R.id.content, DownloadListLocalFragment.newInstance("DownloadedList", "0"));
        } else {
            ft.replace(R.id.content, DownloadListItemFragment.newInstance("DownloadList", "0"));
        }
        ft.commitAllowingStateLoss();
    }

}
