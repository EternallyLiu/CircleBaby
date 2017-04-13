package cn.timeface.circle.baby.ui.timelines.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.VideoPlayActivity;
import cn.timeface.circle.baby.dialogs.TimeLineActivityMenuDialog;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.events.TimelineEditEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.CommentObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineDetailAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.LikeUserList;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.ui.timelines.views.GridStaggerLookup;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;
import rx.Observable;
import rx.functions.Func1;

/**
 * author : wangshuai Created on 2017/2/7
 * email : wangs1992321@gmail.com
 */
public class TimeFaceDetailFragment extends BaseFragment implements BaseAdapter.LoadDataFinish, BaseAdapter.OnItemClickLister, View.OnClickListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, TextWatcher {


    @Bind(R.id.back_up)
    ImageView backUp;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.empty)
    EmptyDataView empty;
    @Bind(R.id.et_commment)
    EditText etCommment;
    @Bind(R.id.add_like)
    SelectImageView addLike;
    @Bind(R.id.add_comment)
    ImageView addComment;
    @Bind(R.id.rl_botton)
    RelativeLayout rlBotton;
    @Bind(R.id.tv_submit_comment)
    TextView tvSubmitComment;
    private TimeLineObj currentTimeLineObj = null;
    private TimeLineDetailAdapter adapter = null;
    private TFPTRRecyclerViewHelper helper;
    private AlertDialog dialog;
    private int commmentId;
    private int allDetailsListPosition = -1;
    private InputMethodManager manager;
    private GridStaggerLookup lookup;
    private LikeUserList likeUserList;

    private boolean isEditor;
    private boolean commentable = false;
    private GridLayoutManager layoutmanager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(TimeLineObj.class.getName())) {
            currentTimeLineObj = bundle.getParcelable(TimeLineObj.class.getName());
        }
        isEditor = bundle.getBoolean("isEditor", true);
        if (bundle != null && bundle.containsKey("allDetailsListPosition"))
            allDetailsListPosition = bundle.getInt("allDetailsListPosition", -1);
        commentable = bundle.getBoolean("commentable", false);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_timeface_detail, container, false);

        ButterKnife.bind(this, contentView);
        setActionBar(toolbar);
        initActionBar();
        toolbar.setTitle(FastData.getBabyObj().getNickName());
        initRecyclerView();
        etCommment.setOnEditorActionListener(this);
        etCommment.setOnFocusChangeListener(this);
        etCommment.addTextChangedListener(this);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqData();
            }
        });
        backUp.setOnClickListener(this);
        reqData();
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public static void open(Context context, int timeId) {
        ApiFactory.getApi().getApiService().queryBabyTimeDetail(timeId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timeDetailResponse -> {
                    if (timeDetailResponse.success())
                        open(context, timeDetailResponse.getTimeInfo());
                }, throwable -> LogUtil.showError(throwable));
    }

    public static void open(Context context, TimeLineObj timeLineObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TimeLineObj.class.getName(), timeLineObj);
        FragmentBridgeActivity.open(context, TimeFaceDetailFragment.class.getSimpleName(), bundle);
    }

    public static void open(Context context, TimeLineObj timeLineObj, boolean isEditor) {
        LogUtil.showLog(timeLineObj == null ? "null" : timeLineObj.getMediaList().size() + "");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isEditor", isEditor);
        bundle.putParcelable(TimeLineObj.class.getName(), timeLineObj);
        FragmentBridgeActivity.open(context, TimeFaceDetailFragment.class.getSimpleName(), bundle);
    }

    public static void open(Context context, int allDetailsListPosition, TimeLineObj timeLineObj) {
        LogUtil.showLog(timeLineObj == null ? "null" : timeLineObj.getMediaList().size() + "");
        Bundle bundle = new Bundle();
        bundle.putInt("allDetailsListPosition", allDetailsListPosition);
        bundle.putParcelable(TimeLineObj.class.getName(), timeLineObj);
        FragmentBridgeActivity.open(context, TimeFaceDetailFragment.class.getSimpleName(), bundle);
    }

    public static void open(Context context, int allDetailsListPosition, boolean commentable, TimeLineObj timeLineObj) {
        LogUtil.showLog(timeLineObj == null ? "null" : timeLineObj.getMediaList().size() + "");
        Bundle bundle = new Bundle();
        bundle.putBoolean("commentable", commentable);
        bundle.putInt("allDetailsListPosition", allDetailsListPosition);
        bundle.putParcelable(TimeLineObj.class.getName(), timeLineObj);
        FragmentBridgeActivity.open(context, TimeFaceDetailFragment.class.getSimpleName(), bundle);
    }

    private void reqData() {
        apiService.queryBabyTimeDetail(currentTimeLineObj.getTimeId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timeDetailResponse -> {
                    if (timeDetailResponse.success()) {
                        currentTimeLineObj = timeDetailResponse.getTimeInfo();
                        initRecyclerView();
                        EventBus.getDefault().post(currentTimeLineObj);
                        doMenu();
                    }
                    swipeRefresh.setRefreshing(false);
//                    helper.finishTFPTRRefresh();
                }, error -> {
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                    Log.e("TimeLineDetailActivity", "queryBabyTimeDetail:");
                    error.printStackTrace();
                });
    }

    private void initRecyclerView() {
        if (adapter == null) {
            adapter = new TimeLineDetailAdapter(getActivity());
            int columCount = 4;
            layoutmanager = new GridLayoutManager(getActivity(), columCount);
//        if (currentTimeLineObj.getMediaList() != null && currentTimeLineObj.getMediaList().size() > 0) {
            lookup = new GridStaggerLookup(currentTimeLineObj.getMediaList().size(), adapter.getItemCount(), columCount);
//        }
            adapter.setItemClickLister(this);
            adapter.setLoadDataFinish(this);
            adapter.setLookup(lookup);
            layoutmanager.setSpanSizeLookup(lookup);
            contentRecyclerView.setLayoutManager(layoutmanager);
            contentRecyclerView.setAdapter(adapter);
            contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int firstVisibleItemPosition = layoutmanager.findFirstVisibleItemPosition();
                    if (lookup.isShowSmail()) {
                        if (firstVisibleItemPosition / lookup.getColumCount() > 1) {
                            backUp.setVisibility(View.VISIBLE);
                        } else backUp.setVisibility(View.GONE);
                    } else
                        backUp.setVisibility(firstVisibleItemPosition > 0 ? View.VISIBLE : View.GONE);
                }
            });
        }
        ArrayList<Object> contentList = new ArrayList<>();
        contentList.addAll(currentTimeLineObj.getMediaList());
        if (!TextUtils.isEmpty(currentTimeLineObj.getContent()))
            contentList.add(currentTimeLineObj.getContent());

        if (currentTimeLineObj.getLikeList().size() > 0) {
            likeUserList = new LikeUserList(currentTimeLineObj.getLikeList());
            contentList.add(likeUserList);
        }
        if (currentTimeLineObj.getCommentList().size() > 0)
            contentList.addAll(currentTimeLineObj.getCommentList());
        adapter.addList(true, contentList);
        addLike.setChecked(currentTimeLineObj.getLike() % 2 == 1 ? true : false);
        if (commentable)
            showKeyboard();
        else hideKeyboard();
    }

    private Menu currentMenu;

    private void doMenu() {
        if (currentMenu != null && currentTimeLineObj.getMediaList().size() > GridStaggerLookup.MAX_MEDIA_SIZE_SHOW_GRID)
            currentMenu.findItem(R.id.action_smail_image).setVisible(true);
        else if (currentMenu != null)
            currentMenu.findItem(R.id.action_smail_image).setVisible(false);
        currentMenu.findItem(R.id.action_more).setVisible(isEditor);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_timeline_detail, menu);
        currentMenu = menu;
        doMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            new TimeLineActivityMenuDialog(getActivity()).setAllDetailsListPosition(allDetailsListPosition).share(currentTimeLineObj);
        } else if (item.getItemId() == R.id.action_smail_image) {
            lookup.setShowSmail(!lookup.isShowSmail());
            adapter.notifyDataSetChanged();
//            if (adapter.getRealItemSize() >= 0)
//                contentRecyclerView.scrollToPosition(0);
            if (lookup.isShowSmail())
                item.setTitle(R.string.look_big_pic_list);
            else item.setTitle(R.string.look_smail_pic_list);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.add_comment)
    public void comment(View view) {
        if (commmentId > 0 && TextUtils.isEmpty(etCommment.getText().toString())) {
            commmentId = 0;
            etCommment.setHint("我也说一句");
            etCommment.setText("");
        }
        etCommment.setFocusable(true);
        etCommment.setFocusableInTouchMode(true);
        showKeyboard();
    }

    @OnClick(R.id.add_like)
    public void like(View view) {
        view.setClickable(false);
        apiService.like(currentTimeLineObj.getTimeId(), (currentTimeLineObj.getLike() + 1) % 2)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(baseResponse -> view.setClickable(true))
                .subscribe(response -> {
                    if (response.success()) {
                        reqData();
                    }
                }, error -> {
                    LogUtil.showError(error);
                });
    }

    @Override
    public void loadFinish(int code) {
    }

    @Override
    public void onItemClick(View view, int position) {
        Object item = adapter.getItem(position);
        if (item instanceof CommentObj) {
            dialog = new AlertDialog.Builder(getActivity()).setView(initCommentMenu((CommentObj) item)).show();
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
        } else if (item instanceof MediaObj) {
            doMediaClick(position, (MediaObj) item);
        }
    }

    private void doMediaClick(int position, MediaObj mediaObj) {
        if (!TextUtils.isEmpty(mediaObj.getVideoUrl())) {
            VideoPlayActivity.open(getActivity(), mediaObj.getVideoUrl());
        } else if (!TextUtils.isEmpty(mediaObj.getImgUrl())) {
            ArrayList<MediaObj> medias = medias();
            FragmentBridgeActivity.openBigimageFragment(getActivity(), allDetailsListPosition, medias,
                    urls(medias), position, true, isEditor ? currentTimeLineObj.getAuthor().equals(FastData.getUserInfo()) : false);
        }
    }

    private ArrayList<MediaObj> medias() {
        ArrayList<MediaObj> list = new ArrayList<>();
        for (int i = 0; i < currentTimeLineObj.getMediaList().size(); i++) {
            if (!currentTimeLineObj.getMediaList().get(i).isVideo())
                list.add(currentTimeLineObj.getMediaList().get(i));

        }
        return list;
    }

    private ArrayList<String> urls(ArrayList<MediaObj> medias) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < medias.size(); i++) {
            list.add(medias.get(i).getImgUrl());
        }
        return list;
    }

    public View initCommentMenu(CommentObj comment) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_comment_menu, null);
        LinearLayout backView = (LinearLayout) view.findViewById(R.id.ll_publish_menu);
        backView.setBackgroundColor(getResources().getColor(R.color.trans));
        RelativeLayout tvAction = (RelativeLayout) view.findViewById(R.id.rl_action);
        TextView tv = (TextView) view.findViewById(R.id.tv_action);
        RelativeLayout tvCancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        if (comment.getUserInfo().getUserId().equals(FastData.getUserId())) {
            tv.setText("删除");
        }
        tvAction.setTag(R.string.tag_obj, comment);
        tvAction.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_up:
                if (contentRecyclerView != null && adapter.getRealItemSize() > 0)
                    contentRecyclerView.scrollToPosition(0);
                break;
            case R.id.rl_cancel:
                dialog.dismiss();
                break;
            case R.id.rl_action:
                CommentObj commment = (CommentObj) v.getTag(R.string.tag_obj);
                dialog.dismiss();
                if (commment.getUserInfo().getUserId().equals(FastData.getUserId())) {
                    //删除评论操作
                    new AlertDialog.Builder(getActivity())
                            .setTitle("确定删除这条评论吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(commment);
                        }
                    }).show();
                } else {
                    //回复操作
                    etCommment.requestFocus();
                    etCommment.setHint("回复 " + commment.getUserInfo().getRelationName() + " ：");
                    commmentId = commment.getCommentId();
                    etCommment.setFocusable(true);
                    etCommment.setFocusableInTouchMode(true);
                    showKeyboard();
                }
                break;
        }
    }

    /**
     * 删除评论
     *
     * @param commment
     */
    private void deleteComment(CommentObj commment) {
        apiService.delComment(commment.getCommentId())
                .filter(new Func1<BaseResponse, Boolean>() {
                    @Override
                    public Boolean call(BaseResponse response) {
                        return response.success();
                    }
                })
                .flatMap(baseResponse -> apiService.queryBabyTimeDetail(currentTimeLineObj.getTimeId()))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    ToastUtil.showToast(response.getInfo());
                    if (response.success()) {
                        currentTimeLineObj = response.getTimeInfo();
                        adapter.deleteItem(commment);
                    }
                }, error -> {
                    Log.e(TAG, "delComment:");
                    error.printStackTrace();
                });
    }

    private void showKeyboard() {
        if (manager == null)
            manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(etCommment, 0);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (manager == null)
            manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Subscribe
    public void onEvent(MediaUpdateEvent event) {
        if (currentTimeLineObj.getMediaList().contains(event.getMediaObj())) {
            List<MediaObj> mediaList = currentTimeLineObj.getMediaList();
            int indexOf = mediaList.indexOf(event.getMediaObj());
            LogUtil.showLog("indexOf=====>" + indexOf);
            mediaList.get(indexOf).setTips(event.getMediaObj().getTips());
            mediaList.get(indexOf).setFavoritecount(event.getMediaObj().getFavoritecount());
            mediaList.get(indexOf).setIsFavorite(event.getMediaObj().getIsFavorite());
        }
    }

    @Subscribe
    public void onEvent(TimeEditPhotoDeleteEvent event) {
        Observable.defer(() -> Observable.just(event)).map(event1 -> {
            if (event.getMediaObj() != null) return event.getMediaObj();
            else return new MediaObj(event.getUrl(), event.getUrl());
        })
                .filter(mediaObj -> currentTimeLineObj.getMediaList().contains(mediaObj))
                .map(mediaObj -> currentTimeLineObj.getMediaList().get(currentTimeLineObj.getMediaList().indexOf(mediaObj)))
                .flatMap(mediaObj -> {
                    event.setMediaObj(mediaObj);
                    return apiService.delTimeOfMedia(mediaObj.getId(), currentTimeLineObj.getTimeId());
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        adapter.deleteItem(event.getMediaObj());
                    }
                }, throwable -> LogUtil.showError(throwable));
    }

    @Subscribe
    public void onEvent(Object event) {
        if (event instanceof DeleteTimeLineEvent) {
            getActivity().finish();
        } else if (event instanceof TimelineEditEvent) {
            reqData();
        }

    }

    /**
     * 发送评论
     */
    private void sendComment() {
        String s = etCommment.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtil.showToast("请填写评论内容");
            return;
        }
        tvSubmitComment.setEnabled(false);
        apiService.comment(URLEncoder.encode(s), System.currentTimeMillis(), currentTimeLineObj.getTimeId(), commmentId)
                .filter(new Func1<BaseResponse, Boolean>() {
                    @Override
                    public Boolean call(BaseResponse response) {
                        return response.success();
                    }
                })
                .flatMap(baseResponse -> apiService.queryBabyTimeDetail(currentTimeLineObj.getTimeId()))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timeDetailResponse -> {
                    if (timeDetailResponse.success()) {
                        currentTimeLineObj = timeDetailResponse.getTimeInfo();
                        EventBus.getDefault().post(new CommentSubmit(allDetailsListPosition, commmentId, currentTimeLineObj));
                        readComment();
                        hideKeyboard();
                        ToastUtil.showToast(timeDetailResponse.getInfo());
                        if (timeDetailResponse.success()) {
                            etCommment.setText("");
                        }
                    }
                    tvSubmitComment.setEnabled(true);
                }, error -> {
                    tvSubmitComment.setEnabled(true);
                    Log.e(TAG, "comment");
                    error.printStackTrace();
                });
    }

    private void readComment() {
        for (int i = 0; i < currentTimeLineObj.getCommentList().size(); i++) {
            if (adapter.containObj(currentTimeLineObj.getCommentList().get(i)))
                continue;
            adapter.addList(currentTimeLineObj.getCommentList().get(i));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEND:
                sendComment();
                break;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showKeyboard();
            if (etCommment != null)
                doSubmitApear(etCommment.getText().toString());
        } else {
            if (tvSubmitComment != null) tvSubmitComment.setVisibility(View.GONE);
            hideKeyboard();
        }

    }

    @OnClick(R.id.tv_submit_comment)
    public void onViewClicked() {
        sendComment();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private void doSubmitApear(String s) {
        if (s != null && !TextUtils.isEmpty(s.trim())) {
            tvSubmitComment.setVisibility(View.VISIBLE);
        } else tvSubmitComment.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {
        doSubmitApear(s.toString());
    }
}



