package com.zony.download.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zony.download.R;
import com.zony.download.adapte.DownloadListLocalingAdapter;
import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadUtil;

import java.util.List;

/**
 * 正在下载页面
 */
public class DownloadListLocalingFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private RecyclerView recyclerView;

    private DownloadListLocalingAdapter downloadedListAdapter;

    private List<DownloadItem> downloadingList;

    public DownloadListLocalingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the
     * provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadListLocalingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadListLocalingFragment newInstance(String param1, String param2) {
        DownloadListLocalingFragment fragment = new DownloadListLocalingFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloadlistlocal_ing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleview_downloaded_list);
        view.findViewById(R.id.start_all_download).setOnClickListener(this);
        view.findViewById(R.id.pause_all_download).setOnClickListener(this);
        view.findViewById(R.id.delete_all_download).setOnClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        initData();
    }

    private void initData() {
        downloadingList = DownloadDao.getInstance(getActivity()).getDownloadingItemsByName();
        // 初始化Recyclerview
        downloadedListAdapter = new DownloadListLocalingAdapter(getActivity(), downloadingList);
        recyclerView.setAdapter(downloadedListAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    }

    /**
     * 刷新正在下载界面
     *
     * @param
     * @author zony
     * @time 17-10-26 下午4:50
     */
    public void updateUi(final List<DownloadItem> observerList) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (downloadedListAdapter != null) {
                    downloadedListAdapter.setDatas(observerList);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_all_download:
                DownloadUtil.startAllDownload(getActivity());
                break;
            case R.id.pause_all_download:
                DownloadUtil.pauseAllDownload(getActivity());
                break;
            case R.id.delete_all_download:
                DownloadUtil.deleteAllDownloading(getActivity());
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
