package cn.timeface.circle.baby.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CloudAlbumActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.TimeLineGroupAdapter;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_message)
    ImageView ivMessage;
    @Bind(R.id.tv_changebaby)
    TextView tvChangebaby;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.ll_mine_info)
    RelativeLayout llMineInfo;
    @Bind(R.id.tv_album)
    TextView tvAlbum;
    @Bind(R.id.tv_milestone)
    TextView tvMilestone;
    @Bind(R.id.tv_relative)
    TextView tvRelative;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tv_toensurerelation)
    TextView tvToensurerelation;

    private int currentPage = 1;
    private String mParam1;
    private TimeLineGroupAdapter adapter;
    public BabyInfoResponse babyInfoResponse;
    private IPTRRecyclerListener ptrListener;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        setupPTR();
        initData();

        adapter = new TimeLineGroupAdapter(getActivity(), new ArrayList<>());
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        tvAlbum.setOnClickListener(this);
        tvMilestone.setOnClickListener(this);
        tvRelative.setOnClickListener(this);
        tvChangebaby.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        tvToensurerelation.setOnClickListener(this);

        reqData(1);

        return view;
    }

    private void setupPTR() {
        ptrListener = new IPTRRecyclerListener() {
            @Override
            public void onTFPullDownToRefresh(View refreshView) {
                currentPage = 1;
                reqData(currentPage);
            }

            @Override
            public void onTFPullUpToRefresh(View refreshView) {
                reqData(++currentPage);
            }

            @Override
            public void onScrollUp(int firstVisibleItem) {
            }

            @Override
            public void onScrollDown(int firstVisibleItem) {
            }
        };

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(getActivity(), contentRecyclerView, swipeRefreshLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                .tfPtrListener(ptrListener);
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(Color.TRANSPARENT).sizeResId(R.dimen.view_space_normal).build());
    }

    private void reqData(int currentPage) {
        apiService.timeline(currentPage, 10)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    tfptrListViewHelper.finishTFPTRRefresh();
                    if(timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()){
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START);
                    }
                    setDataList(timelineResponse.getDataList());
                }, error -> {
                    Log.e(TAG, "timeline:");
                    tfptrListViewHelper.finishTFPTRRefresh();
                });


    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        if(currentPage == 1){
            adapter.setListData(dataList);
        }else{
            adapter.getListData().addAll(dataList);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        tvName.setText(FastData.getBabyName());
        tvAge.setText(FastData.getBabyAge());
        GlideUtil.displayImage(FastData.getBabyAvatar(), ivAvatar);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_changebaby:
                FragmentBridgeActivity.open(getActivity(), "ChangeBabyFragment");
                break;
            case R.id.iv_message:
                FragmentBridgeActivity.open(getActivity(), "MessageFragment");
                break;
            case R.id.tv_relative:
                FragmentBridgeActivity.open(getActivity(), "FamilyMemberFragment");
                break;
            case R.id.iv_avatar:
                FragmentBridgeActivity.openBabyInfoFragment(getActivity(), FastData.getString("userObj", ""));
                break;
            case R.id.tv_milestone:
                FragmentBridgeActivity.open(getActivity(), "MilestoneFragment");
                break;
            case R.id.tv_toensurerelation:

                break;
            case R.id.tv_album:
                CloudAlbumActivity.open(getActivity());
                break;
        }
    }
}
