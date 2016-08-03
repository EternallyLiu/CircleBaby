package cn.timeface.circle.baby.fragments;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CloudAlbumActivity;
import cn.timeface.circle.baby.activities.ConfirmRelationActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MileStoneActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.adapters.TimeLineGroupAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.RecommendObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.objs.WorkObj;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
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
    @Bind(R.id.iv_cover_bg)
    RatioImageView ivCoverBg;
    @Bind(R.id.ll_layout)
    LinearLayout llLayout;
    @Bind(R.id.fl_layout)
    FrameLayout flLayout;
    @Bind(R.id.iv_dot)
    ImageView ivDot;

    private int currentPage = 1;
    private String mParam1;
    private TimeLineGroupAdapter adapter;
    public BabyInfoResponse babyInfoResponse;
    private IPTRRecyclerListener ptrListener;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    private boolean enableAnimation = true;
    private boolean bottomMenuShow;

    private List<TimeLineGroupObj> tempList;
    AnimatorSet animatorSet = new AnimatorSet();
    private int y;

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

        initMsg();

        return view;
    }

    private void initMsg() {
        apiService.noReadMsg()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(unReadMsgResponse -> {
                    if (unReadMsgResponse.success()) {
                        if (unReadMsgResponse.getUnreadMessageCount() > 0) {
                            ivDot.setVisibility(View.VISIBLE);
                        }
                    }
                }, error -> {
                    Log.e(TAG, "noReadMsg:");
                    error.printStackTrace();
                });
    }

    private void setupPTR() {
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(400);
        contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (enableAnimation && bottomMenuShow) {
                        bottomMenuShow = false;
                        ObjectAnimator anim = ObjectAnimator.ofFloat(((TabMainActivity) getActivity()).getFootMenuView(),
                                "translationY",
                                0,
                                ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight());
//                        ObjectAnimator anim2 = ObjectAnimator.ofFloat(llLayout, "translationY", 0, -toolbar.getMeasuredHeight());
                        animatorSet.playTogether(anim);
                        animatorSet.start();
                    }
                } else {
                    if (enableAnimation && !bottomMenuShow) {
                        bottomMenuShow = true;
                        Animator anim3 = ObjectAnimator.ofFloat(((TabMainActivity) getActivity()).getFootMenuView(),
                                "translationY",
                                ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight(),
                                0);
//                        ObjectAnimator anim4 = ObjectAnimator.ofFloat(llLayout, "translationY", -toolbar.getMeasuredHeight(), 0);
                        animatorSet.playTogether(anim3);
                        animatorSet.start();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //拖动
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //静止
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //滑动
                        break;
                }
            }
        });


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
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.bg30)).sizeResId(R.dimen.view_space_normal).build());
    }

    private void reqData(int page) {
        apiService.timeline(page, 10)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    tfptrListViewHelper.finishTFPTRRefresh();

                    if (Remember.getBoolean("showtimelinehead", true) && currentPage == 1 && adapter.getHeaderCount() == 0 && timelineResponse.getRecommendCard() != null) {
                        if(!TextUtils.isEmpty(timelineResponse.getRecommendCard().getBgPicUrl())){
                            adapter.addHeader(initHeadView(timelineResponse.getRecommendCard()));
                        }
                    }
                    if (timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()) {
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START);
                    }
                    tempList = timelineResponse.getDataList();
                    setDataList(timelineResponse.getDataList());
                }, error -> {
                    Log.e(TAG, "timeline:");
                    tfptrListViewHelper.finishTFPTRRefresh();
                    error.printStackTrace();
                });


    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        ArrayList<TimeLineGroupObj> lists = new ArrayList<>();
        for (TimeLineGroupObj obj : dataList) {
            if (obj.getTimeLineList().size() > 0) {
                lists.add(obj);
            }
        }
        if (currentPage == 1) {
            adapter.setListData(lists);
        } else {
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
        if (TextUtils.isEmpty(FastData.getRelationName())) {
            tvToensurerelation.setVisibility(View.VISIBLE);
        }
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
//                FragmentBridgeActivity.open(getActivity(), "MilestoneFragment");
                MileStoneActivity.open(getActivity());
                break;
            case R.id.tv_toensurerelation:
                Intent intent = new Intent(getActivity(), ConfirmRelationActivity.class);
                String code = Remember.getString("code", "");
                intent.putExtra("code", code);
                startActivity(intent);
                tvToensurerelation.setVisibility(View.GONE);
                break;
            case R.id.tv_album:
                CloudAlbumActivity.open(getActivity());
                break;
        }
    }

    public View initHeadView(RecommendObj obj) {
        View view = View.inflate(getContext(), R.layout.view_timeline_head, null);
        ImageView ivImage = (ImageView) view.findViewById(R.id.iv_image);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);

        GlideUtil.displayImage(obj.getBgPicUrl(), ivImage);
        tvContent.setText(obj.getRecommendContent());

        view.setOnClickListener(v -> {
            switch (obj.getActionType()) {
                case 0:
                    PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    break;
                case 1:
                    for (WorkObj work : obj.getNewWorkObj()) {
                        if (work.getType() == 5) {
                            BookTypeListObj bookTypeListObj = new BookTypeListObj(work);
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 2:
                    for (WorkObj work : obj.getNewWorkObj()) {
                        if (work.getType() == 1) {
                            BookTypeListObj bookTypeListObj = new BookTypeListObj(work);
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 3:
                    for (WorkObj work : obj.getNewWorkObj()) {
                        if (work.getType() == 3) {
                            BookTypeListObj bookTypeListObj = new BookTypeListObj(work);
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 4:
                    for (WorkObj work : obj.getNewWorkObj()) {
                        if (work.getType() == 2) {
                            BookTypeListObj bookTypeListObj = new BookTypeListObj(work);
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
            }
        });
        ivClose.setOnClickListener(v -> {
            Remember.putBoolean("showtimelinehead", false);
            adapter.removeHeader(view);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    @Subscribe
    public void onEvent(CommentSubmit commentSubmit) {

        replaceList(commentSubmit.getReplacePosition(), commentSubmit.getListPos(), commentSubmit.getTimeLineObj());
    }

    @Subscribe
    public void onEvent(HomeRefreshEvent event) {
        reqData(1);
    }

    @Subscribe
    public void onEvent(UnreadMsgEvent event) {
        initMsg();
    }

    public void replaceList(int replacePosition, int listPos, TimeLineObj timeLineObj) {

        if (tempList.size() > replacePosition) {
            if (tempList.get(replacePosition).getTimeLineList().size() > listPos) {
                tempList.get(replacePosition).getTimeLineList().remove(listPos);
                tempList.get(replacePosition).getTimeLineList().add(listPos, timeLineObj);
            }
        }
        adapter.setListData(tempList);
        adapter.notifyDataSetChanged();
    }
}
