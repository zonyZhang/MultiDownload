package com.zony.download.adapte;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class DownloadListLocalAdapter extends FragmentPagerAdapter {
    private String[] mTitles;

    private List<Fragment> mFragmentList;

    public DownloadListLocalAdapter(FragmentManager fm, String[] titles, List<Fragment> fragmentList) {
        super(fm);
        this.mTitles = titles;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
//        if (position == 0) {
//            return DownloadListLocalingFragment.newInstance("DownloadListLocaling", "0");
//        } else if (position == 1) {
//            return DownloadListLocalCompleteFragment.newInstance("DownloadListLocalComplete", "0");
//        }
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
