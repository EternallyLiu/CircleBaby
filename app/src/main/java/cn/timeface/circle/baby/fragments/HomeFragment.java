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
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.adapters.TimeLineGroupAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.RecommendObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements View.OnClickListener{
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

    private List<TimeLineGroupObj> tempList;

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
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        setupPTR();
        initData();
//        ((TimeLineDetailActivity)TimeLineDetailActivity.activity).setReplaceDataListener(this);
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
                    if(Remember.getBoolean("showtimelinehead",true)&&currentPage==1&&adapter.getHeaderCount()==0&&timelineResponse.getRecommendObj()!=null){
                        adapter.addHeader(initHeadView(timelineResponse.getRecommendObj()));
                    }
                    if (timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()) {
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START);
                    }

                    tempList = timelineResponse.getDataList();
                    setDataList(timelineResponse.getDataList());
                }, error -> {
                    Log.e(TAG, "timeline:");
                    tfptrListViewHelper.finishTFPTRRefresh();
                });


    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        ArrayList<TimeLineGroupObj> lists = new ArrayList<>();
        for(TimeLineGroupObj obj : dataList){
            if(obj.getTimeLineList().size()>0){
                lists.add(obj);
            }
        }
        if(currentPage == 1){
            adapter.setListData(lists);
        }else{
            adapter.getListData().addAll(lists);
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
        EventBus.getDefault().unregister(this);
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

    public View initHeadView(RecommendObj obj){
        View view = View.inflate(getContext(), R.layout.view_timeline_head, null);
        ImageView ivImage = (ImageView) view.findViewById(R.id.iv_image);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        TextView tvType = (TextView) view.findViewById(R.id.tv_type);

        GlideUtil.displayImage(obj.getBgPicUrl(), ivImage);
        tvContent.setText(obj.getRecommendContent());
        switch (obj.getActionType()){
            case 0:
                //照片发布
                tvType.setText("发布照片吧》");
                break;
            case 1:
                //照片书
                tvType.setText("照片书》");
                break;
            case 2:
                //成长书
                tvType.setText("成长书》");
                break;
            case 3:
                //识图卡片书
                tvType.setText("识图卡片书》");
                break;
            case 4:
                //日记书
                tvType.setText("日记书》");
                break;

        }

        tvType.setOnClickListener(v -> {
            if(obj.getActionType()==0){
                //照片发布
                PublishActivity.open(getContext(), PublishActivity.PHOTO);
            }else{
                apiService.getBabyBookWorksTypeList()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(bookTypeListResponse -> {
                            if (bookTypeListResponse.success()) {
                                switch (obj.getActionType()){
                                    case 1:
                                        //照片书
                                        for(BookTypeListObj item : bookTypeListResponse.getDataList()){
                                            if(item.getType()==5){
                                                FragmentBridgeActivity.openAddBookFragment(getContext(), item);
                                            }
                                        }
                                        break;
                                    case 2:
                                        //成长书
                                        for(BookTypeListObj item : bookTypeListResponse.getDataList()){
                                            if(item.getType()==1){
                                                FragmentBridgeActivity.openAddBookFragment(getContext(), item);
                                            }
                                        }
                                        break;
                                    case 3:
                                        //识图卡片书
                                        for(BookTypeListObj item : bookTypeListResponse.getDataList()){
                                            if(item.getType()==3){
                                                FragmentBridgeActivity.openAddBookFragment(getContext(), item);
                                            }
                                        }
                                        break;
                                    case 4:
                                        //日记书
                                        for(BookTypeListObj item : bookTypeListResponse.getDataList()){
                                            if(item.getType()==2){
                                                FragmentBridgeActivity.openAddBookFragment(getContext(), item);
                                            }
                                        }
                                        break;
                                }
                            }
                        }, error -> {
                            Log.e(TAG, "getBabyBookWorksTypeList:");
                        });
            }

        });
        ivClose.setOnClickListener(v -> {
            view.setVisibility(View.GONE);
            Remember.putBoolean("showtimelinehead", false);
            adapter.removeHeader(view);
        });
        return view;
    }

    @Subscribe
    public void onEvent(CommentSubmit commentSubmit) {

        replaceList(commentSubmit.getReplacePosition(), commentSubmit.getListPos(), commentSubmit.getTimeLineObj());
    }

    public void replaceList(int replacePosition, int listPos, TimeLineObj timeLineObj) {
        if (tempList.size() > replacePosition){
            if (tempList.get(replacePosition).getTimeLineList().size() > listPos){
                tempList.get(replacePosition).getTimeLineList().remove(listPos);
                tempList.get(replacePosition).getTimeLineList().add(listPos, timeLineObj);
            }
        }
        adapter.setListData(tempList);
        adapter.notifyDataSetChanged();
    }
}
