package cn.timeface.circle.baby.fragments;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.ChangeBabyActivity;
import cn.timeface.circle.baby.activities.CloudAlbumActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MileStoneActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.adapters.TimeLineGroupAdapter;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.CommentObj;
import cn.timeface.circle.baby.api.models.objs.RecommendObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.events.ActionCallBackEvent;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.IconCommentClickEvent;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.InputMethodRelative;
import cn.timeface.circle.baby.views.TFStateView;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Func1;

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
    RatioImageView tvAlbum;
    @Bind(R.id.tv_milestone)
    RatioImageView tvMilestone;
    @Bind(R.id.tv_relative)
    RatioImageView tvRelative;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.iv_cover_bg)
    ImageView ivCoverBg;
    @Bind(R.id.iv_dot)
    ImageView ivDot;
    //    @Bind(R.id.rlRecyclerView)
//    PullRefreshLoadRecyclerView rlRecyclerView;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    @Bind(R.id.appbar)
    AppBarLayout appbar;

    private boolean rlCommentShow;
    private int currentPage = 1;
    private String mParam1;
    private TimeLineGroupAdapter adapter;
    public BabyInfoResponse babyInfoResponse;
    private IPTRRecyclerListener ptrListener;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    private boolean enableAnimation = true;
    private boolean bottomMenuShow = true;

    private List<TimeLineGroupObj> tempList;
    AnimatorSet animatorSet = new AnimatorSet();
    private int commentId = 0;
    private int replacePosition;
    private int listPos;
    private TimeLineObj timeLineObj;
    private InputMethodRelative rlComment;
    private EditText etCommment;
    private Button btnSend;
    private AlertDialog dialog;
    private ArrayList<TimeLineGroupObj> lists;

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
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        setupPTR();
        initData();
//        ((TimeLineDetailActivity)TimeLineDetailActivity.activity).setReplaceDataListener(this);
        adapter = new TimeLineGroupAdapter(getActivity(), new ArrayList<>());
        contentRecyclerView.setAdapter(adapter);

        tfStateView.setOnRetryListener(() -> reqData(1));
        tfStateView.loading();

        tvAlbum.setOnClickListener(this);
        tvMilestone.setOnClickListener(this);
        tvRelative.setOnClickListener(this);
        tvChangebaby.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);

        reqData(1);

        initMsg();
        if (FastData.getBabyId() != 0) {
            updateLoginInfo();
        }
        return view;
    }

    private void updateLoginInfo() {
        apiService.updateLoginInfo()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                }, throwable -> {
                    Log.e(TAG, "updateLoginInfo:");
                });
    }

    private void initMsg() {
        apiService.noReadMsg()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(unReadMsgResponse -> {
                    if (unReadMsgResponse.success()) {
                        if (unReadMsgResponse.getUnreadMessageCount() > 0) {
                            ivDot.setVisibility(View.VISIBLE);
                        } else {
                            ivDot.setVisibility(View.GONE);
                        }
                    }
                }, error -> {
                    Log.e(TAG, "noReadMsg:");
                    error.printStackTrace();
                });
    }

    //    @TargetApi(Build.VERSION_CODES.M)
    private void setupPTR() {
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(10);
        contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    appbar.setExpanded(false);
                    if (enableAnimation && bottomMenuShow) {
                        bottomMenuShow = false;
                        ObjectAnimator anim = ObjectAnimator.ofFloat(((TabMainActivity) getActivity()).getFootMenuView(),
                                "translationY",
                                0,
                                ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight());
                        animatorSet.playTogether(anim);
                        animatorSet.start();
                    }
                } else if (dy < 0) {
                    appbar.setExpanded(true);
                    if (enableAnimation && !bottomMenuShow) {
                        bottomMenuShow = true;
                        Animator anim3 = ObjectAnimator.ofFloat(((TabMainActivity) getActivity()).getFootMenuView(),
                                "translationY",
                                ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight(),
                                0);
                        animatorSet.playTogether(anim3);
                        animatorSet.start();
                    }
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
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FROM_END)
                .tfPtrListener(ptrListener);
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.bg30)).sizeResId(R.dimen.view_space_normal).build());

    }

    private void reqData(int page) {
        apiService.timeline(page, 10)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    tfptrListViewHelper.finishTFPTRRefresh();

                    if (Remember.getBoolean("showtimelinehead", true) && currentPage == 1 && adapter.getHeaderCount() == 0 && timelineResponse.getRecommendCard() != null) {
                        if (!TextUtils.isEmpty(timelineResponse.getRecommendCard().getBgPicUrl())) {
                            adapter.addHeader(initHeadView(timelineResponse.getRecommendCard()));
                        }
                    }
                    if (timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()) {
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.DISABLED);
                    }
                    tempList = timelineResponse.getDataList();
                    setDataList(timelineResponse.getDataList());
                }, error -> {
                    if (tfStateView != null) {
                        tfStateView.showException(error);
                    }
                    Log.e(TAG, "timeline:");
                    tfptrListViewHelper.finishTFPTRRefresh();
                    error.printStackTrace();
                });
    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        lists = new ArrayList<>();
        for (TimeLineGroupObj obj : dataList) {
            if (obj.getTimeLineList().size() > 0) {
                lists.add(obj);
            }
        }
        tempList = lists;
//        if(lists.size()==0){
//            showNoDataView(true);
//        }else{
//            showNoDataView(false);
//        }
        if (lists.size() < 3) {
            if (enableAnimation && !bottomMenuShow) {
                bottomMenuShow = true;
                Animator anim3 = ObjectAnimator.ofFloat(((TabMainActivity) getActivity()).getFootMenuView(),
                        "translationY",
                        ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight(),
                        0);
                animatorSet.playTogether(anim3);
                animatorSet.start();
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
                ChangeBabyActivity.open(getActivity());
                break;
            case R.id.iv_message:
                FragmentBridgeActivity.open(getActivity(), "MessageFragment");
                break;
            case R.id.tv_relative:
                FragmentBridgeActivity.open(getActivity(), "FamilyMemberFragment");
                break;
            case R.id.iv_avatar:
                FragmentBridgeActivity.openBabyInfoFragment(getActivity(), FastData.getUserInfo());
                break;
            case R.id.tv_milestone:
//                FragmentBridgeActivity.open(getActivity(), "MilestoneFragment");
                MileStoneActivity.open(getActivity());
                break;
            case R.id.tv_album:
                CloudAlbumActivity.open(getActivity());
                break;
            case R.id.btn_send:
                String s = etCommment.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ToastUtil.showToast("请填写评论内容");
                    return;
                }
                btnSend.setClickable(false);
                Remember.putString("sendComment", s);
                apiService.comment(URLEncoder.encode(s), System.currentTimeMillis(), timeLineObj.getTimeId(), commentId)
                        .filter(new Func1<BaseResponse, Boolean>() {
                            @Override
                            public Boolean call(BaseResponse response) {
                                return response.success();
                            }
                        })
                        .flatMap(baseResponse -> apiService.queryBabyTimeDetail(timeLineObj.getTimeId()))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(timeDetailResponse -> {
                            if (timeDetailResponse.success()) {
                                Remember.putString("sendComment", "");
                                dialog.dismiss();
                                timeLineObj = timeDetailResponse.getTimeInfo();
                                hideKeyboard();
                                ToastUtil.showToast(timeDetailResponse.getInfo());
                                if (replacePosition >= 0 && listPos >= 0) {
                                    replaceList(replacePosition, listPos, timeLineObj);
                                }
                            }
                            if (btnSend != null)
                                btnSend.setClickable(false);
                        }, error -> {
                            Log.e(TAG, "comment");
                            error.printStackTrace();
                        });
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
                    for (BookTypeListObj bookTypeListObj : obj.getNewWorkObj()) {
                        if (bookTypeListObj.getType() == 5) {
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 2:
                    for (BookTypeListObj bookTypeListObj : obj.getNewWorkObj()) {
                        if (bookTypeListObj.getType() == 1) {
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 3:
                    for (BookTypeListObj bookTypeListObj : obj.getNewWorkObj()) {
                        if (bookTypeListObj.getType() == 3) {
                            FragmentBridgeActivity.openAddBookFragment(getContext(), bookTypeListObj);
                        }
                    }
                    break;
                case 4:
                    for (BookTypeListObj bookTypeListObj : obj.getNewWorkObj()) {
                        if (bookTypeListObj.getType() == 2) {
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

    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        contentRecyclerView.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(IconCommentClickEvent event) {
        replacePosition = event.getReplacePosition();
        listPos = event.getListPos();
        timeLineObj = event.getTimeLineObj();
        commentId = 0;
        editComment();
        etCommment.requestFocus();
        etCommment.setHint("说点什么吧");
    }

    private void editComment() {
        dialog = new AlertDialog.Builder(getActivity()).setView(initCommentEdit()).show();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();

        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    private View initCommentEdit() {
        View view = View.inflate(getActivity(), R.layout.view_send_comment, null);
        rlComment = (InputMethodRelative) view.findViewById(R.id.rl_comment);
        etCommment = (EditText) view.findViewById(R.id.et_commment);
        btnSend = (Button) view.findViewById(R.id.btn_send);

        String sendComment = Remember.getString("sendComment", "");
        if (!TextUtils.isEmpty(sendComment)) {
            etCommment.setText(sendComment);
        }
        etCommment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                Remember.putString("sendComment", s1);
            }
        });
        btnSend.setOnClickListener(this);
        return view;
    }

    @Subscribe
    public void onEvent(ActionCallBackEvent event) {
        CommentObj comment = event.getComment();
        timeLineObj = event.getTimeLineObj();
        replacePosition = event.getReplacePosition();
        listPos = event.getListPos();
        commentId = comment.getCommentId();
        editComment();
        etCommment.requestFocus();
        etCommment.setHint("回复 " + comment.getUserInfo().getRelationName() + " ：");
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
