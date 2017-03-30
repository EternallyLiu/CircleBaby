package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wechat.photopicker.fragment.BigImageFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TimeLineActivityMenuDialog;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.CommentObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleCommentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.CircleTimeLineDetailAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.TimeLikeUserList;
import cn.timeface.circle.baby.ui.circle.timelines.bean.TitleBean;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleTimeLineEditEvent;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleTimeLineGridStagger;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.images.views.ImageActionDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineDetailAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.LikeUserList;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.ui.timelines.views.GridStaggerLookup;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;
import rx.Observable;
import rx.functions.Func1;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineDetailActivitiy extends BaseAppCompatActivity implements BaseAdapter.OnItemClickLister, BaseAdapter.LoadDataFinish, SwipeRefreshLayout.OnRefreshListener, ImageActionDialog.ClickCallBack, TextView.OnEditorActionListener, View.OnFocusChangeListener, View.OnClickListener {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.empty)
    EmptyDataView empty;
    @Bind(R.id.back_up)
    ImageView backUp;
    @Bind(R.id.et_commment)
    EditText etCommment;
    @Bind(R.id.add_like)
    SelectImageView addLike;
    @Bind(R.id.add_comment)
    ImageView addComment;
    @Bind(R.id.rl_botton)
    RelativeLayout rlBotton;

    private long commmentId;
    private boolean commentable = false;

    private AlertDialog dialog;
    private CircleTimelineObj currentTimeLineObj;

    private CircleTimeLineDetailAdapter adapter;
    private GridLayoutManager layoutmanager;
    private CircleTimeLineGridStagger lookup;
    private InputMethodManager manager;
    private Menu currentMenu;
    private ImageActionDialog actionDialog;
    private DeleteDialog deleteDialog;
    private long currentTimeLineId = 0;

    public static void open(Context context, CircleTimelineObj timelineObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CircleTimelineObj.class.getSimpleName(), timelineObj);
        context.startActivity(new Intent(context, CircleTimeLineDetailActivitiy.class).putExtras(bundle));
    }

    public static void open(Context context, long currentTimeLineId) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", currentTimeLineId);
        context.startActivity(new Intent(context, CircleTimeLineDetailActivitiy.class).putExtras(bundle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_timeface_detail);
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("动态详情");
        currentTimeLineObj = getIntent().getParcelableExtra(CircleTimelineObj.class.getSimpleName());
        currentTimeLineId = getIntent().getLongExtra("id", 0);
        if (currentTimeLineObj != null)
            currentTimeLineId = currentTimeLineObj.getCircleTimelineId();
        init();
        reqData();
//        initRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void init() {
        swipeRefresh.setOnRefreshListener(this);
        etCommment.setOnEditorActionListener(this);
        etCommment.setOnFocusChangeListener(this);
    }

    private void initRecyclerView() {
        if (adapter == null) {
            adapter = new CircleTimeLineDetailAdapter(this);
            int columCount = 4;
            layoutmanager = new GridLayoutManager(this, columCount);
//        if (currentTimeLineObj.getMediaList() != null && currentTimeLineObj.getMediaList().size() > 0) {
            lookup = new CircleTimeLineGridStagger(1, currentTimeLineObj.getMediaList().size(), columCount);
//        }
            adapter.setItemClickLister(this);
            adapter.setLoadDataFinish(this);
            adapter.setLookup(lookup);
            layoutmanager.setSpanSizeLookup(lookup);
            contentRecyclerView.setLayoutManager(layoutmanager);
            contentRecyclerView.setAdapter(adapter);
        }
        contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = layoutmanager.findFirstVisibleItemPosition();
                backUp.setVisibility(firstVisibleItemPosition > (lookup.getBeginCount() > 0 ? lookup.getBeginCount() - 1 : 0) ? View.VISIBLE : View.GONE);
            }
        });
        ArrayList<Object> contentList = new ArrayList<>();
        contentList.add(new TitleBean(currentTimeLineObj.getTitle(), currentTimeLineObj.getPublisher()));
        contentList.addAll(currentTimeLineObj.getMediaList());
        if (!TextUtils.isEmpty(currentTimeLineObj.getContent()))
            contentList.add(currentTimeLineObj.getContent());

        if (currentTimeLineObj.getLikeList().size() > 0) {
            TimeLikeUserList likeUserList = new TimeLikeUserList(currentTimeLineObj.getLikeList());
            contentList.add(likeUserList);
        }
        if (currentTimeLineObj.getcommentList().size() > 0)
            contentList.addAll(currentTimeLineObj.getcommentList());
        adapter.addList(true, contentList);
        addLike.setChecked(currentTimeLineObj.getLike() % 2 == 1 ? true : false);
        if (commentable)
            showKeyboard();
        else hideKeyboard();
    }

    private void reqData() {
        addSubscription(apiService.queryCircleTimeLineDetail(currentTimeLineObj.getCircleTimelineId()).compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(circleTimeLineDetailResponse -> addLike.setEnabled(true))
                .doOnNext(circleTimeLineDetailResponse -> swipeRefresh.setRefreshing(false))
                .subscribe(circleTimeLineDetailResponse -> {
                    if (circleTimeLineDetailResponse.success() && circleTimeLineDetailResponse.getCircleTimelineInfo() != null) {
                        currentTimeLineObj = circleTimeLineDetailResponse.getCircleTimelineInfo();
                        EventBus.getDefault().post(new CircleTimeLineEditEvent(currentTimeLineObj));
                        initRecyclerView();
                    } else ToastUtil.showToast(this, circleTimeLineDetailResponse.getInfo());
                }, throwable -> {
                    addLike.setEnabled(true);
                    swipeRefresh.setRefreshing(false);
                }));
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void showKeyboard() {
        if (manager == null)
            manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(etCommment, 0);
    }

    private void doMenu() {
        if (currentMenu != null && currentTimeLineObj.getMediaList().size() > GridStaggerLookup.MAX_MEDIA_SIZE_SHOW_GRID)
            currentMenu.findItem(R.id.action_smail_image).setVisible(true);
        else if (currentMenu != null)
            currentMenu.findItem(R.id.action_smail_image).setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline_detail, menu);
        currentMenu = menu;
        doMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            if (actionDialog == null) {
                actionDialog = new ImageActionDialog(this);
                actionDialog.setClickListener(this);
                actionDialog.isShared(true);
                actionDialog.isDownload(false);
                actionDialog.isDelete(true);
                actionDialog.isEdit(true);
            }
            actionDialog.show();
        }
        if (item.getItemId() == R.id.action_smail_image) {
            lookup.setShowSmail(!lookup.isShowSmail());
            adapter.notifyDataSetChanged();
//            if (adapter.getRealItemSize() >= 0)
//                contentRecyclerView.scrollToPosition(0);
            if (lookup.isShowSmail())
                item.setTitle("查看大图");
            else item.setTitle("浏览小图");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (manager == null)
            manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void addLike() {
        addLike.setEnabled(false);
        addSubscription(apiService.circleLike(currentTimeLineObj.getCircleTimelineId(), currentTimeLineObj.getLike() == 0 ? 1 : 0)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        reqData();
                    } else ToastUtil.showToast(this, baseResponse.getInfo());
                }, throwable -> {
                    LogUtil.showError(throwable);
                }));
    }

    @OnClick({R.id.add_like, R.id.add_comment, R.id.back_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_like:
                addLike();
                break;
            case R.id.add_comment:
                if (commmentId > 0 && TextUtils.isEmpty(etCommment.getText().toString())) {
                    commmentId = 0;
                    etCommment.setHint("我也说一句");
                    etCommment.setText("");
                }
                etCommment.setFocusable(true);
                etCommment.setFocusableInTouchMode(true);
                showKeyboard();
                break;
            case R.id.back_up:
                if (contentRecyclerView != null && adapter.getRealItemSize() > 0)
                    contentRecyclerView.scrollToPosition(0);
                break;
            case R.id.rl_cancel:
                dialog.dismiss();
                break;
            case R.id.rl_action:
                CircleCommentObj commment = (CircleCommentObj) view.getTag(R.string.tag_obj);
                dialog.dismiss();
                if (commment.getCommentUserInfo().getCircleUserId() == FastData.getCircleUserId()) {
                    //删除评论操作
                    new AlertDialog.Builder(this)
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
                    etCommment.setHint("回复 " + commment.getCommentUserInfo().getCircleNickName() + " ：");
                    commmentId = commment.getCommentId();
                    etCommment.setFocusable(true);
                    etCommment.setFocusableInTouchMode(true);
                    showKeyboard();
                }
                break;
        }
    }

    private void deleteComment(CircleCommentObj commment) {
        addSubscription(apiService.deleteComment(commment.getCommentId()).compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        adapter.deleteItem(commment);
                        currentTimeLineObj.getcommentList().remove(commment);
                    } else ToastUtil.showToast(this, baseResponse.getInfo());
                }, throwable -> LogUtil.showError(throwable)));
    }

    public View initCommentMenu(CircleCommentObj comment) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_comment_menu, null);
        LinearLayout backView = (LinearLayout) view.findViewById(R.id.ll_publish_menu);
        backView.setBackgroundColor(getResources().getColor(R.color.trans));
        RelativeLayout tvAction = (RelativeLayout) view.findViewById(R.id.rl_action);
        TextView tv = (TextView) view.findViewById(R.id.tv_action);
        RelativeLayout tvCancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        if (comment.getCommentUserInfo().getCircleUserId() == FastData.getCircleUserId()) {
            tv.setText("删除");
        }
        tvAction.setTag(R.string.tag_obj, comment);
        tvAction.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Object item = adapter.getItem(position);
        if (item instanceof CircleMediaObj) {
            FragmentBridgeActivity.openBigimageFragment(this, 0, MediaObj.getMediaArray(currentTimeLineObj.getMediaList()), MediaObj.getUrls(currentTimeLineObj.getMediaList()), position, BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR, true, false);
        } else if (item instanceof CircleCommentObj) {
            dialog = new AlertDialog.Builder(this).setView(initCommentMenu((CircleCommentObj) item)).show();
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
    }

    @Override
    public void loadFinish(int code) {

    }

    @Subscribe
    public void onEvent(CircleTimeLineEditEvent event) {
        if (event.getType() == 0 && event.getTimelineObj() != null) {
            currentTimeLineObj = event.getTimelineObj();
            initRecyclerView();
        } else if (event.getType() == 1 && event.getTimelineObj() != null && event.getTimelineObj().getCircleTimelineId() == currentTimeLineObj.getCircleTimelineId())
            finish();
    }

    @Override
    public void onRefresh() {
        reqData();
    }

    private void readComment() {
        for (int i = 0; i < currentTimeLineObj.getcommentList().size(); i++) {
            if (adapter.containObj(currentTimeLineObj.getcommentList().get(i)))
                continue;
            adapter.addList(currentTimeLineObj.getcommentList().get(i));
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
        apiService.circleComment(currentTimeLineObj.getCircleTimelineId(), Uri.encode(s), System.currentTimeMillis(), commmentId > 0 ? commmentId + "" : "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(circleCommentResponse -> {
                    if (circleCommentResponse.success()) {
//                        readComment();
                        if (!adapter.containObj(circleCommentResponse.getCommentInfo()))
                            adapter.addList(circleCommentResponse.getCommentInfo());
                        hideKeyboard();
                        ToastUtil.showToast(circleCommentResponse.getInfo());
                        if (circleCommentResponse.success()) {
                            etCommment.setText("");
                            commmentId = 0;
                        }
                    }
                }, error -> {
                    error.printStackTrace();
                });
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
    public void click(View view, int type) {
        switch (type) {
            case 1:
                PublishActivity.open(this, currentTimeLineObj);
                break;
            case 2:
                if (deleteDialog == null) {
                    deleteDialog = new DeleteDialog(this);
                    deleteDialog.setMessage("您确定删除这条圈动态么？");
                    deleteDialog.setMessageGravity(Gravity.CENTER);
                }
                deleteDialog.setSubmitListener(() -> addSubscription(apiService.deleteCircleTimeLine(currentTimeLineObj.getCircleTimelineId())
                        .compose(SchedulersCompat.applyIoSchedulers()).subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                EventBus.getDefault().post(new CircleTimeLineEditEvent(1, currentTimeLineObj));
                                finish();
                            } else ToastUtil.showToast(this, baseResponse.getInfo());
                        }, throwable -> LogUtil.showError(throwable))));
                if (!deleteDialog.isShowing())
                    deleteDialog.show();
                break;
            case 4:

                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showKeyboard();
        } else hideKeyboard();
    }
}
