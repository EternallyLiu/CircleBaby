package cn.timeface.circle.baby.fragments;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.BabyInfoActivity;
import cn.timeface.circle.baby.activities.ChangeBabyActivity;
import cn.timeface.circle.baby.activities.CloudAlbumActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MileStoneActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.PicSaveCompleteEvent;
import cn.timeface.circle.baby.events.StartUploadEvent;
import cn.timeface.circle.baby.events.TimelineEditEvent;
import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel_Table;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.support.api.models.objs.RecommendObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.kiths.KithFragment;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineGroupListAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.ui.timelines.beans.TimeGroupSimpleBean;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.ui.timelines.views.TimerImageView;
import cn.timeface.circle.baby.views.TFStateView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements View.OnClickListener, BaseAdapter.LoadDataFinish, EmptyDataView.EmptyCallBack {
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
    @Bind(R.id.tv_progress)
    TextView mTvprogress;
    @Bind(R.id.empty_view)
    EmptyDataView emptyView;

    private boolean rlCommentShow;
    private int currentPage = 1;
    private TimeLineGroupListAdapter adapter;
    public BabyInfoResponse babyInfoResponse;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    private boolean enableAnimation = true;
    private boolean bottomMenuShow = true;

    AnimatorSet animatorSet = new AnimatorSet();
    private int commentId = 0;
    private int replacePosition;
    private int listPos;
    private TimeLineObj timeLineObj;
    private EditText etCommment;
    private Button btnSend;
    private AlertDialog dialog;
    private TextView tvProgress;
    private AlertDialog progressDialog;
    private View picChangeView;
    private static final int PAGE_SIZE = 30;

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
            String mParam1 = getArguments().getString(ARG_PARAM1);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        toolbar.setOnClickListener(this);
        setupPTR();
        initData();
//        ((TimeLineDetailActivity)TimeLineDetailActivity.activity).setReplaceDataListener(this);
        adapter = new TimeLineGroupListAdapter(getActivity());
        adapter.setLoadDataFinish(this);
        contentRecyclerView.setAdapter(adapter);

        tfStateView.setOnRetryListener(() -> reqData(1));
        tfStateView.loading();

        tvAlbum.setOnClickListener(this);
        tvMilestone.setOnClickListener(this);
        tvRelative.setOnClickListener(this);
        tvChangebaby.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        ivMessage.setVisibility(View.GONE);
        ivAvatar.setOnClickListener(this);

        currentPage = 1;
        reqData(currentPage);

//        initMsg();
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


    //    @TargetApi(Build.VERSION_CODES.M)
    private void setupPTR() {
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(200);
        /*contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0) {
                    return;
                } else if (dy > 0) {
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
                } else {
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
        });*/


        IPTRRecyclerListener ptrListener = new IPTRRecyclerListener() {
            @Override
            public void onTFPullDownToRefresh(View refreshView) {
                currentPage = 1;
                reqData(currentPage);
            }

            @Override
            public void onTFPullUpToRefresh(View refreshView) {
                if (currentPage <= 1)
                    currentPage = 1;
                if (adapter.getRealItemSize() < PAGE_SIZE * currentPage) {
                    reqData(currentPage);
                } else
                    reqData(++currentPage);
            }

            @Override
            public void onScrollUp(int firstVisibleItem) {
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
            }

            @Override
            public void onScrollDown(int firstVisibleItem) {
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
        };

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(getActivity(), contentRecyclerView, swipeRefreshLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                .tfPtrListener(ptrListener);
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).color(getResources().getColor(R.color.bg30)).sizeResId(R.dimen.view_space_normal).build());

    }

    private void reqData(int page) {
        apiService.timeline(page, PAGE_SIZE)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    tfptrListViewHelper.finishTFPTRRefresh();

                    if (Remember.getBoolean("showtimelinehead", true) && currentPage == 1 && adapter.getHeaderCount() == 0 && timelineResponse.getRecommendCard() != null) {
//                        if (!TextUtils.isEmpty(timelineResponse.getRecommendCard().getBgPicUrl())) {
//                            adapter.addHeader(initHeadView(timelineResponse.getRecommendCard()));
//                        }
                    }
                    if (timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()) {
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH);
                    }
                    setDataList(timelineResponse.getDataList(), currentPage == 1);
                }, error -> {
                    if (tfStateView != null) {
                        tfStateView.showException(error);
                    }
                    Log.e(TAG, "timeline:");
                    tfptrListViewHelper.finishTFPTRRefresh();
                    error.printStackTrace();
                });
    }

    private void reqData(int page, int pageSize) {
        apiService.timeline(page, pageSize)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    tfptrListViewHelper.finishTFPTRRefresh();

                    if (Remember.getBoolean("showtimelinehead", true) && currentPage == 1 && adapter.getHeaderCount() == 0 && timelineResponse.getRecommendCard() != null) {
//                        if (!TextUtils.isEmpty(timelineResponse.getRecommendCard().getBgPicUrl())) {
//                            adapter.addHeader(initHeadView(timelineResponse.getRecommendCard()));
//                        }
                    }
                    if (timelineResponse.getCurrentPage() == timelineResponse.getTotalPage()) {
                        tfptrListViewHelper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH);
                    }
                    if (timelineResponse.success()) {
                        currentPage = pageSize / PAGE_SIZE;
                        currentPage++;
                    }
                    setDataList(timelineResponse.getDataList(), true);
                }, error -> {
                    if (tfStateView != null) {
                        tfStateView.showException(error);
                    }
                    tfptrListViewHelper.finishTFPTRRefresh();
                    LogUtil.showError(error);
                });
    }

    private void setDataList(List<TimeLineGroupObj> dataList, boolean flag) {
        if (flag || currentPage == 1) {
            flag = true;
        }
        ArrayList<BaseObj> list = new ArrayList<>();
        for (TimeLineGroupObj groupObj : dataList) {
            TimeGroupSimpleBean bean = new TimeGroupSimpleBean(groupObj.getAge(), groupObj.getDateEx(), groupObj.getDate());
            if (flag)
                list.add(bean);
            else if (!adapter.containObj(bean))
                list.add(bean);
            for (TimeLineObj timeLineObj : groupObj.getTimeLineList()) {
                if (flag)
                    list.add(timeLineObj);
                else if (!adapter.containObj(timeLineObj))
                    list.add(timeLineObj);
            }
        }
        if (list.size() < 3) {
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

        if (flag) {
            adapter.addList(true, list);
        } else {
            adapter.addList(list);
        }
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        tvName.setText(FastData.getBabyObj().getNickName());
        tvAge.setText(FastData.getBabyAge());
        GlideUtil.displayImage(FastData.getBabyAvatar(), ivAvatar);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    private static long clickToolBarLastTime = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload:
                PublishActivity.open(getActivity(), PublishActivity.PHOTO);
                break;
            case R.id.toolbar:
                LogUtil.showLog("clicl toolbar");
                if (System.currentTimeMillis() - clickToolBarLastTime <= 500 && adapter.getRealItemSize() > 0)
                    contentRecyclerView.scrollToPosition(0);
                clickToolBarLastTime = System.currentTimeMillis();
                break;
            case R.id.tv_changebaby:
                ChangeBabyActivity.open(getActivity());
                break;
            case R.id.iv_message:
                FragmentBridgeActivity.open(getActivity(), "MessageFragment");
                break;
            case R.id.tv_relative:
                FragmentBridgeActivity.open(getActivity(), KithFragment.class.getSimpleName());
                break;
            case R.id.iv_avatar:
                BabyInfoActivity.open(getActivity());
                break;
            case R.id.tv_milestone:
                MileStoneActivity.open(getActivity());
                break;
            case R.id.tv_album:
                CloudAlbumActivity.open(getActivity());
                break;
            case R.id.btn_send:
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
    public void onEvent(HomeRefreshEvent event) {
        if (event.getTimeId() > 0)
            timeLineUpdate(event.getTimeId());
        else {
            initData();
            currentPage = 1;
            reqData(currentPage);

        }
    }


    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        contentRecyclerView.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }


    @Subscribe
    public void onEvent(StartUploadEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog();
                mTvprogress.setText("上传中");
                mTvprogress.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe
    public void onEvent(UploadEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int progress = event.getProgress();
                mTvprogress.setText("上传中 " + progress + "%");
                if (tvProgress != null)
                    tvProgress.setText(progress + "%");
                if (progress == 100) {
                    mTvprogress.setVisibility(View.GONE);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    if (event.isComplete() && event.getTimeId() > 0)
                        timeLineUpdate(event.getTimeId());
                    else {
                        currentPage = 1;
                        reqData(currentPage);
                    }
                    ToastUtil.showToast("发布成功");
                }
                if (event.isComplete()) {
                    mTvprogress.setVisibility(View.GONE);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (event.isComplete() && event.getTimeId() > 0) {
                        LogUtil.showLog("event currentTimeId=====" + event.getTimeId());
                        timeLineUpdate(event.getTimeId());
                    } else {
                        currentPage = 1;
                        reqData(currentPage);
                    }
                    ToastUtil.showToast("发布成功");
                }

            }
        });

    }


    private void progressDialog() {
        progressDialog = new AlertDialog.Builder(getActivity()).setView(initProgress()).show();
        progressDialog.setCanceledOnTouchOutside(false);
        Window window = progressDialog.getWindow();
//        WindowManager m = window.getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = window.getAttributes();

//        p.width = d.getWidth();
//        window.setAttributes(p);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    private View initProgress() {
        View view = View.inflate(getActivity(), R.layout.view_upload_progress, null);
        ImageView ivLoad = (ImageView) view.findViewById(R.id.pb_loading);
        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        ((Animatable) ivLoad.getDrawable()).start();
        return view;
    }

    /**
     * 回调处理mediaObj对象发生变更的处理结果
     *
     * @param mediaUpdateEvent
     */
    @Subscribe
    public void onEvent(MediaUpdateEvent mediaUpdateEvent) {
        if (mediaUpdateEvent.getAllDetailsListPosition() < 0)
            return;
        TimeLineObj timeLineObj = adapter.getItem(mediaUpdateEvent.getAllDetailsListPosition());
        boolean flag = timeLineObj.getMediaList().contains(mediaUpdateEvent.getMediaObj());
        LogUtil.showLog("flag:" + flag);
        if (flag) {
            int index = timeLineObj.getMediaList().indexOf(mediaUpdateEvent.getMediaObj());
            timeLineObj.getMediaList().get(index).setTips(mediaUpdateEvent.getMediaObj().getTips());
            timeLineObj.getMediaList().get(index).setIsFavorite(mediaUpdateEvent.getMediaObj().getIsFavorite());
            timeLineObj.getMediaList().get(index).setFavoritecount(mediaUpdateEvent.getMediaObj().getFavoritecount());
        }
    }

    /**
     * 处理相册图片发生变化
     *
     * @param list
     */
    private void initPicChange(List<String> list) {
        if (list != null && list.size() > 0) {
            LogUtil.showLog("change:" + JSONUtils.parse2JSONString(list));
            if (picChangeView == null)
                picChangeView = View.inflate(getActivity(), R.layout.home_header_photo_added, null);
            TimerImageView imageView = (TimerImageView) picChangeView.findViewById(R.id.pic_array);
            TextView addCount = (TextView) picChangeView.findViewById(R.id.pic_add_count);
            TextView upload = (TextView) picChangeView.findViewById(R.id.upload);
            String count = list.size() + "";
            String content = String.format("手机新增了 %s 张照片！", count);
            LogUtil.showLog("content:" + content);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(content);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F59650"));
            builder.setSpan(colorSpan, content.indexOf(count), content.indexOf(count) + count.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            addCount.setText(builder);
            upload.setOnClickListener(this);
            imageView.setList(list);
            adapter.removeHeader(picChangeView);
            adapter.addHeader(picChangeView);
            adapter.notifyDataSetChanged();
            contentRecyclerView.scrollToPosition(0);
        }
    }

    @Subscribe
    public void onEvent(PicSaveCompleteEvent pic) {
        PhotoModel.getAllPhotoId().compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(list -> {
                    LogUtil.showLog(list);
                    if (!TextUtils.isEmpty(list))
                        apiService.mediaBackup(list)
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(response -> {
                                    if (response.success()) {
                                        List<PhotoModel> models = SQLite.select().from(PhotoModel.class).where(PhotoModel_Table.photo_id.in(response.getDataList()))
                                                .queryList();
                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        for (int i = 0; i < models.size(); i++) {
                                            arrayList.add(models.get(i).getLocalPath());
                                        }
                                        initPicChange(arrayList);
                                    }
                                }, error -> {
                                    Log.e("test", "error:" + error.getMessage(), error);
                                });
                }, error -> {
                    LogUtil.showError(error);
                });

    }

    /**
     * 当前需要滑动的时光id
     */
    private int currentTimeId = 0;

    /**
     * 处理发布或者编辑、删除操作之后首页数据响应
     *
     * @param timeId
     */
    private void timeLineUpdate(int timeId) {
        LogUtil.showLog("timeLineUpdate timeId===" + timeId);
        apiService.timeOfpage(PAGE_SIZE, timeId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        currentTimeId = timeId;
                        LogUtil.showLog("timeLineUpdate currentTimeId===" + currentTimeId);
                        currentPage = 1;
                        reqData(currentPage, response.getPageNo() * PAGE_SIZE);
                    }
                });
    }

    @Subscribe
    public void onEvent(TimelineEditEvent event) {
        if (event instanceof DeleteTimeLineEvent) {
            adapter.deleteTimeLine(event.getTimeId());
        }
    }

    @Subscribe
    public void onEvent(TimeLineObj timeLineObj) {
        LogUtil.showLog("event timeLineObj----" + adapter.getPosition(timeLineObj));
        adapter.updateItem(timeLineObj);
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

    @Override
    public void loadfinish(int code) {
        emptyView.setVisibility(View.GONE);
        if (code == BaseAdapter.UPDATE_DATA_ADD_LIST_CENTER) {
            LogUtil.showLog("currentTimeId=="+currentPage);
            LogUtil.showLog("positiion==="+adapter.findPosition(currentTimeId));
            if (currentTimeId > 0)
                contentRecyclerView.scrollToPosition(adapter.findPosition(currentTimeId));
            currentTimeId = 0;
        } else if (code != BaseAdapter.DELETE_ALL && adapter.getRealItemSize() <= 0) {
            if (tfStateView == null || tfStateView.getVisibility() != View.VISIBLE) {
                String relativeName = FastData.getRelationName();
                BabyObj babyObj = FastData.getBabyObj();
                StringBuilder sb = new StringBuilder();
                SpannableStringBuilder builder = new SpannableStringBuilder();
                sb.append(String.format("%s 还没有成长记录哦", babyObj.getNickName()));
                builder.append(String.format("%s 还没有成长记录哦", babyObj.getNickName()));
                builder.setSpan(SpannableUtils.getTextColor(Color.RED), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                sb.append(String.format("\n %s 赶紧导入 %s 的照片了", relativeName, babyObj.getNickName()));
                builder.append(String.format("\n %s 赶紧导入 %s 的照片了", relativeName, babyObj.getNickName()));
                builder.setSpan(SpannableUtils.getTextColor(Color.RED), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                builder.setSpan(SpannableUtils.getTextSize(getActivity(), R.dimen.text_medium), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(SpannableUtils.getTextColor(getActivity(), R.color.sea_buckthorn), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                emptyView.getErrorTitle().setText(builder);
                emptyView.getEmptyIcon().setVisibility(View.GONE);
                emptyView.setRetry(true);
                emptyView.setErrorRetryText("导入手机里的照片");
                emptyView.setEmptyCallBack(this);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void retry() {
        PublishActivity.open(getActivity(), PublishActivity.PHOTO);
    }
}
