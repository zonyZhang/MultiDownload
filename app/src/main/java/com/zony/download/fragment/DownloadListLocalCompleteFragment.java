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
import android.widget.Button;

import com.zony.download.R;
import com.zony.download.adapte.DownloadListLocalCompleteAdapter;
import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownloadUtil;

import java.util.List;

/**
 * 下载完成
 */
public class DownloadListLocalCompleteFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private RecyclerView mRecyclerView;

    private DownloadListLocalCompleteAdapter mAdapter;

    private Button delAllBtn;

    private List<DownloadItem> downloadedList;

    public DownloadListLocalCompleteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the
     * provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadListLocalCompleteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadListLocalCompleteFragment newInstance(String param1, String param2) {
        DownloadListLocalCompleteFragment fragment = new DownloadListLocalCompleteFragment();
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

        updateUi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloadlistlocal_complete, container, false);
    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view
                .findViewById(R.id.downloadlistlocal_complete_recycleview);
        delAllBtn = (Button) view.findViewById(R.id.downloadlistlocal_complete_btn_del_all);
        mAdapter = new DownloadListLocalCompleteAdapter(getActivity(), downloadedList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        delAllBtn.setOnClickListener(this);
    }

    /**
     * 刷新UI
     *
     * @param
     * @author zony
     * @time 17-10-30 下午4:23
     */
    protected void updateUi() {
        downloadedList = DownloadDao.getInstance(getActivity()).getDownloadedItems();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.setDatas(downloadedList);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.downloadlistlocal_complete_btn_del_all:
                DownloadUtil.deleteAllDownloadComplete(getActivity());
                updateUi();
                break;
        }
    }
}
