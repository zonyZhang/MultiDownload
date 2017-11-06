package com.zony.download.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zony.download.R;
import com.zony.download.adapte.DownloadListLocalAdapter;
import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.interfaces.DownloadListObserver;
import com.zony.multidownload.manager.DownloadBind;
import com.zony.multidownload.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadListLocalFragment extends Fragment
        implements TabLayout.OnTabSelectedListener, View.OnClickListener {

    private static final String TAG = "DownloadListLocalFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private TabLayout mTablayout;

    private ViewPager mViewPager;

    private List<Fragment> mFragmentList;

    private String[] titles = {
            "正在下载", "下载完成"
    };

    private DownloadBind mDownloadBind;

    private DownloadListLocalAdapter mDownloadListLocalAdapter;

    private DownloadListLocalingFragment mDownloadListLocalingFragment;

    private DownloadListLocalCompleteFragment mDownloadListLocalCompleteFragment;

    public DownloadListLocalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the
     * provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GlideBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadListLocalFragment newInstance(String param1, String param2) {
        DownloadListLocalFragment fragment = new DownloadListLocalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloadlistlocal, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initData() {
        mFragmentList = new ArrayList<>();
        mDownloadListLocalingFragment = DownloadListLocalingFragment
                .newInstance("DownloadListLocaling", "0");
        mDownloadListLocalCompleteFragment = DownloadListLocalCompleteFragment
                .newInstance("DownloadListLocalComplete", "0");
        mFragmentList.add(mDownloadListLocalingFragment);
        mFragmentList.add(mDownloadListLocalCompleteFragment);
        mDownloadBind = new DownloadBind(getActivity(), new DownloadListObserver() {
            @Override
            public void onDownloadListObserver(final List<DownloadItem> list) {
                if (mDownloadListLocalingFragment != null) {
                    mDownloadListLocalingFragment.updateUi(list);
                }
            }

            @Override
            public void delDownloadFileSuc(boolean isDelAll) {
                LogUtil.i(TAG, "DownloadListLocalFragment delDownloadFileSuc: " + isDelAll);

                if (mTablayout.getSelectedTabPosition() == 0) {
                    if (mDownloadListLocalingFragment != null) {
                        mDownloadListLocalingFragment.updateUi(
                                DownloadDao.getInstance(getActivity()).getDownloadingItemsByName());
                    }
                } else {
                    if (mDownloadListLocalCompleteFragment != null) {
                        mDownloadListLocalCompleteFragment.updateUi();
                    }
                }
            }
        });
        mDownloadBind.bind();
    }

    private void initViews(View view) {
        mTablayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        // ViewPager的适配器
        mDownloadListLocalAdapter = new DownloadListLocalAdapter(this.getFragmentManager(), titles,
                mFragmentList);
        mViewPager.setAdapter(mDownloadListLocalAdapter);
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.addOnTabSelectedListener(this);
        mTablayout.setOnClickListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        LogUtil.i(TAG, "onTabSelected: " + tab.getText());
        switch (tab.getPosition()) {
            case 0:
                // if (mDownloadListLocalingFragment != null) {
                // mDownloadListLocalingFragment.updateUi(null);
                // }
                break;
            case 1:
                if (mDownloadListLocalCompleteFragment != null) {
                    mDownloadListLocalCompleteFragment.updateUi();
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        LogUtil.i(TAG, "onTabUnselected: " + tab.getText());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        LogUtil.i(TAG, "onTabReselected");
    }

    @Override
    public void onClick(View view) {
        LogUtil.i(TAG, "onClick");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadBind != null) {
            mDownloadBind.unBind();
        }
    }
}
