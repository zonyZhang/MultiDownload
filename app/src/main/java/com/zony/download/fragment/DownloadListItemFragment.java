package com.zony.download.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.zony.download.R;
import com.zony.download.adapte.DownloadListItemAdapter;
import com.zony.download.utils.CommonUtil;
import com.zony.download.utils.DataUtil;
import com.zony.download.view.MoveImageView;
import com.zony.multidownload.db.DownloadDao;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownLoadConstant;
import com.zony.multidownload.utils.DownloadUtil;
import com.zony.multidownload.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要下载项页面
 *
 * @param
 * @author zony
 * @time 17-10-24 上午10:55
 */
public class DownloadListItemFragment extends Fragment implements View.OnClickListener,
        Animator.AnimatorListener, DownloadListItemAdapter.AddClickListener {

    private static final String TAG = "DownloadListItemFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    private RelativeLayout rootView;
    private RecyclerView recyclerView;

    private DownloadListItemAdapter downloadListAdapter;

    private List<DownloadItem> downloadList;

    private List<DownloadItem> selectList;// 需要选中列表

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    public DownloadListItemFragment() {
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
    public static DownloadListItemFragment newInstance(String param1, String param2) {
        DownloadListItemFragment fragment = new DownloadListItemFragment();
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
        selectList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloadlist_item, container, false);
    }


    private Button showDownloadList;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = (RelativeLayout) view.findViewById(R.id.root_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleview_download_list);
        showDownloadList = (Button) view.findViewById(R.id.show_downloaded_list);
        showDownloadList.setOnClickListener(this);
        view.findViewById(R.id.download_all).setOnClickListener(this);
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
    }

    private void initData() {
        getDownloadData(false);
        if (downloadListAdapter == null) {
            downloadListAdapter = new DownloadListItemAdapter(getActivity(), downloadList);
            downloadListAdapter.setListener(this);
            recyclerView.setAdapter(downloadListAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            downloadListAdapter.setDatas(downloadList);
        }
    }

    /**
     * @param isGetNoDownload 是否获取未下载项
     * @author zony
     * @time 17-11-1 上午11:30
     */
    private void getDownloadData(boolean isGetNoDownload) {
        downloadList = DataUtil.getDownloadItemList();
        List<DownloadItem> downloadedList = DownloadDao.getInstance(getActivity())
                .getAllDownloadItems();

        // 无重复并集
        downloadList.removeAll(downloadedList);
        if (!isGetNoDownload) {
            downloadList.addAll(downloadedList);
        }
        SortUtil.sortByName(downloadList, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_downloaded_list:
                CommonUtil.startDownloadListActivity(getActivity(), true);
                break;
            case R.id.download_all:
                getDownloadData(true);
                if (downloadList != null && downloadList.size() > 0 && getActivity() != null) {
                    for (DownloadItem downloadItem : downloadList) {
                        DownloadUtil.startDownload(getActivity(), downloadItem);
                    }
                }

                selectList.clear();
                List<DownloadItem> downloadList = DataUtil.getDownloadItemList();
                for (DownloadItem downloadItem : downloadList) {
                    downloadItem.setDownloadState(DownLoadConstant.DownloadState.WAIT);
                    selectList.add(downloadItem);
                }
                downloadListAdapter.setDatas(selectList);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this fragment
     * to allow an interaction in this fragment to be communicated to the activity
     * and potentially other fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addView(View addV) {
        int[] childCoordinate = new int[2];
        int[] parentCoordinate = new int[2];
        int[] shopCoordinate = new int[2];
        // 1.分别获取被点击View、父布局、购物车在屏幕上的坐标xy。
        addV.getLocationInWindow(childCoordinate);
        rootView.getLocationInWindow(parentCoordinate);
        showDownloadList.getLocationInWindow(shopCoordinate);

        // 2.自定义ImageView 继承ImageView
        MoveImageView img = new MoveImageView(getActivity());
        img.setImageResource(R.mipmap.ic_launcher);
        // 3.设置img在父布局中的坐标位置
        img.setX(childCoordinate[0] - parentCoordinate[0]);
        img.setY(childCoordinate[1] - parentCoordinate[1]);
        // 4.父布局添加该Img
        rootView.addView(img);

        // 5.利用 二次贝塞尔曲线 需首先计算出 MoveImageView的2个数据点和一个控制点
        PointF startP = new PointF();
        PointF endP = new PointF();
        PointF controlP = new PointF();
        // 开始的数据点坐标就是 addV的坐标
        startP.x = childCoordinate[0] - parentCoordinate[0];
        startP.y = childCoordinate[1] - parentCoordinate[1];
        // 结束的数据点坐标就是 shopImg的坐标
        endP.x = shopCoordinate[0] - parentCoordinate[0];
        endP.y = shopCoordinate[1] - parentCoordinate[1];
        // 控制点坐标 x等于 购物车x；y等于 addV的y
        controlP.x = endP.x;
        controlP.y = startP.y;

        // 启动属性动画
        ObjectAnimator animator = ObjectAnimator.ofObject(img, "mPointF",
                new PointFTypeEvaluator(controlP), startP, endP);
        animator.setDuration(700);
        animator.addListener(this);
        animator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        // 动画结束后 父布局移除 img
        Object target = ((ObjectAnimator) animation).getTarget();
        rootView.removeView((View) target);
        // shopImg 开始一个放大动画
        Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.item_scale);
        showDownloadList.startAnimation(scaleAnim);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    /**
     * 自定义估值器
     */
    public class PointFTypeEvaluator implements TypeEvaluator<PointF> {
        /**
         * 每个估值器对应一个属性动画，每个属性动画仅对应唯一一个控制点
         */
        PointF control;

        /**
         * 估值器返回值
         */
        PointF mPointF = new PointF();

        public PointFTypeEvaluator(PointF control) {
            this.control = control;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            return getBezierPoint(startValue, endValue, control, fraction);
        }

        /**
         * 二次贝塞尔曲线公式
         *
         * @param start 开始的数据点
         * @param end 结束的数据点
         * @param control 控制点
         * @param t float 0-1
         * @return 不同t对应的PointF
         */
        private PointF getBezierPoint(PointF start, PointF end, PointF control, float t) {
            mPointF.x = (1 - t) * (1 - t) * start.x + 2 * t * (1 - t) * control.x + t * t * end.x;
            mPointF.y = (1 - t) * (1 - t) * start.y + 2 * t * (1 - t) * control.y + t * t * end.y;
            return mPointF;
        }
    }
}
