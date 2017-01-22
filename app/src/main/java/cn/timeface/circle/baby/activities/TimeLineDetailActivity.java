package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TimeLineActivityMenuDialog;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.TimelineEditEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.CommentObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.InputMethodRelative;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Func1;

public class TimeLineDetailActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus, InputMethodRelative.OnSizeChangedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.gv)
    GridView gv;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.iv_video)
    ImageView ivVideo;
    @Bind(R.id.tv_author)
    TextView tvAuthor;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.ll_good_list_users_bar)
    LinearLayout llGoodListUsersBar;
    @Bind(R.id.layout_comment)
    LinearLayout layoutComment;
    @Bind(R.id.hsv)
    HorizontalScrollView hsv;
    @Bind(R.id.ll_comment_wrapper)
    LinearLayout llCommentWrapper;
    @Bind(R.id.tv_more_comment)
    TextView tvMoreComment;
    @Bind(R.id.et_commment)
    EditText etCommment;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.rl_single)
    RelativeLayout rlSingle;
    @Bind(R.id.tv_milestone)
    TextView tvMilestone;
    @Bind(R.id.tv_commentcount)
    TextView tvCommentcount;
    @Bind(R.id.tv_likecount)
    TextView tvLikecount;
    @Bind(R.id.icon_like)
    ImageView iconLike;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.rl_comment)
    InputMethodRelative rlComment;
    @Bind(R.id.scroll_view)
    ScrollView scrollView;
    @Bind(R.id.ll_layout)
    LinearLayout layout;
    @Bind(R.id.rl_layout)
    RelativeLayout relativeLayout;
    @Bind(R.id.ll_commentLikeWrapper)
    LinearLayout ll_commentLikeWrapper;

    private TimeLineObj timelineobj;
    private AlertDialog dialog;
    private static int normalColor;
    private int commmentId = 0;
    private PopupWindow popupWindow;

    private int replacePosition;
    private int listPos;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelinedetail);

        activity = this;

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        normalColor = getResources().getColor(R.color.gray_normal);
        etCommment.clearFocus();
        initView();
        changeInputMethodSize();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public static void open(Context context, TimeLineObj item, int replacePos, int position) {
        Intent intent = new Intent(context, TimeLineDetailActivity.class);
        intent.putExtra("timelineobj", item);
        intent.putExtra("replacePos", replacePos);
        intent.putExtra("listPos", position);
        context.startActivity(intent);
    }

    public static void open(Context context, TimeLineObj item) {
        Intent intent = new Intent(context, TimeLineDetailActivity.class);
        intent.putExtra("timelineobj", item);
        context.startActivity(intent);
    }

    public static void open(Context context, int timeId) {
        apiService.queryBabyTimeDetail(timeId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timeDetailResponse -> {
                    if (timeDetailResponse.success()) {
                        Intent intent = new Intent(context, TimeLineDetailActivity.class);
                        intent.putExtra("timelineobj", timeDetailResponse.getTimeInfo());
                        context.startActivity(intent);
                    }
                }, error -> {
                    Log.e("TimeLineDetailActivity", "queryBabyTimeDetail:");
                    error.printStackTrace();
                });

    }

    private void initView() {
        timelineobj = getIntent().getParcelableExtra("timelineobj");
        replacePosition = getIntent().getIntExtra("replacePos", -1);
        listPos = getIntent().getIntExtra("listPos", -1);
        tvContent.setText(timelineobj.getContent());
        tvAuthor.setText(timelineobj.getAuthor().getRelationName());
        tvDate.setText(DateUtil.formatDate("MM-dd kk:mm", timelineobj.getDate()));
        getSupportActionBar().setTitle(timelineobj.getAuthor().getBabyObj().getName());
        iconLike.setSelected(timelineobj.getLike() == 1 ? true : false);

        if (!TextUtils.isEmpty(timelineobj.getMilestone())) {
            tvMilestone.setVisibility(View.VISIBLE);
            tvMilestone.setText(timelineobj.getMilestone());
        }

        if (timelineobj.getMediaList().size() == 1) {
            gv.setVisibility(View.GONE);
            ivCover.setVisibility(View.VISIBLE);
            String url = timelineobj.getMediaList().get(0).getImgUrl();
            GlideUtil.displayImage(url, ivCover);
            int width = Remember.getInt("width", 0) * 3;
            ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            if (timelineobj.getType() == 2) {
                layoutParams.height = (int) (width * 1.4);
            }
            ivCover.setLayoutParams(layoutParams);
            ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (timelineobj.getType() != 1) {
                ivCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> strings = new ArrayList<>();
                        ArrayList<MediaObj> mediaObjs = new ArrayList<>();
                        strings.add(url);
                        mediaObjs.add(timelineobj.getMediaList().get(0));
                        FragmentBridgeActivity.openBigimageFragment(v.getContext(), mediaObjs, strings, 0, true, false);
                    }
                });
            }
        } else {
            rlSingle.setVisibility(View.GONE);
        }

        if (timelineobj.getMediaList().size() > 1) {
            gv.setVisibility(View.VISIBLE);
            List<MediaObj> imgObjList = timelineobj.getMediaList();
            ArrayList<MediaObj> mediaObjs=new ArrayList<>();
            ArrayList<String> urls = new ArrayList<>();
            for (MediaObj mediaObj : imgObjList) {
                urls.add(mediaObj.getImgUrl());
                mediaObjs.add(mediaObj);
            }
            MyAdapter myAdapter = new MyAdapter(this, urls, mediaObjs);
            gv.setAdapter(myAdapter);
            ViewGroup.LayoutParams layoutParams = gv.getLayoutParams();
            layoutParams.height = Remember.getInt("width", 0);
            if (timelineobj.getMediaList().size() == 2) {
                gv.setNumColumns(2);
                layoutParams.height = Remember.getInt("width", 0) * 3 / 2;
            }
            if (timelineobj.getMediaList().size() > 3) {
                gv.setNumColumns(3);
                layoutParams.height = Remember.getInt("width", 0) * 2;
            }
            if (timelineobj.getMediaList().size() > 6) {
                gv.setNumColumns(3);
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }
            gv.setLayoutParams(layoutParams);

        } else {
            gv.setVisibility(View.GONE);
        }

        tvLikecount.setText(timelineobj.getLikeList().size() + "");
        tvCommentcount.setText(timelineobj.getCommentList().size() + "");

        if (timelineobj.getLikeCount() > 0) {
            hsv.setVisibility(View.VISIBLE);
            llGoodListUsersBar.removeAllViews();
            for (UserObj u : timelineobj.getLikeList()) {
                ImageView imageView = initPraiseItem();
                llGoodListUsersBar.addView(imageView);
                GlideUtil.displayImage(u.getAvatar(), imageView, R.drawable.ic_launcher);
            }
        } else {
            llGoodListUsersBar.removeAllViews();
            hsv.setVisibility(View.GONE);
        }
        if (timelineobj.getCommentList().size() > 0) {
            llCommentWrapper.setVisibility(View.VISIBLE);
            llCommentWrapper.removeAllViews();
            int comments = timelineobj.getCommentList().size();
            for (int i = 0; i < comments; i++) {
                CommentObj commentObj = timelineobj.getCommentList().get(i);
                llCommentWrapper.addView(initCommentItemView(commentObj));
            }
        } else {
            llCommentWrapper.removeAllViews();
            llCommentWrapper.setVisibility(View.GONE);
        }
        if (timelineobj.getLikeList().size() == 0 && timelineobj.getCommentList().size() == 0) {
            ll_commentLikeWrapper.setVisibility(View.GONE);
        } else {
            ll_commentLikeWrapper.setVisibility(View.VISIBLE);
        }

        if (timelineobj.getType() == 1) {
            ivVideo.setVisibility(View.VISIBLE);
            int width = Remember.getInt("width", 0) * 3;
            ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = (int) (width * 0.5);
            ivCover.setLayoutParams(layoutParams);
            ivVideo.setLayoutParams(layoutParams);
            ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            rlSingle.setOnClickListener(this);
        }
        btnSend.setOnClickListener(this);
        iconLike.setOnClickListener(this);
//        rlComment.setOnSizeChangedListenner(this);
    }

    private void changeInputMethodSize() {
        View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = decorView.getRootView().getHeight();
                int heightDifference = screenHeight - rect.bottom;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
                params.setMargins(0, 0, 0, heightDifference);
                relativeLayout.requestLayout();
            }
        });
    }

    private ImageView initPraiseItem() {
        CircleImageView imageView = new CircleImageView(this);
        imageView.setImageResource(R.color.gray_pressed);
        int width = getResources().getDimensionPixelSize(R.dimen.size_36);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        int margin = getResources().getDimensionPixelSize(R.dimen.size_2);
        params.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(params);
        return imageView;
    }

    private View initCommentItemView(CommentObj comment) {
        View view = getLayoutInflater().inflate(R.layout.view_comment, null);
        TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        SpannableStringBuilder msb = new SpannableStringBuilder();
        String relationName = comment.getUserInfo().getRelationName();
        msb.append(relationName)
                .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), 0, relationName.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//第一个人名
        if (comment.getToUserInfo() != null && !TextUtils.isEmpty(comment.getToUserInfo().getRelationName())) {
            msb.append("回复");
            msb.append(comment.getToUserInfo().getRelationName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToUserInfo().getRelationName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        msb.append("：").append(comment.getContent());
        tvComment.setText(msb);
        tvTime.setText(DateUtil.formatDate("MM-dd kk:mm", comment.getCommentDate()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(TimeLineDetailActivity.this).setView(initCommentMenu(comment)).show();
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
        });
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            new TimeLineActivityMenuDialog(this).share(timelineobj);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:

                String s = etCommment.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    ToastUtil.showToast("请填写评论内容");
                    return;
                }
                btnSend.setClickable(false);
                apiService.comment(URLEncoder.encode(s), System.currentTimeMillis(), timelineobj.getTimeId(), commmentId)
                        .filter(new Func1<BaseResponse, Boolean>() {
                            @Override
                            public Boolean call(BaseResponse response) {
                                return response.success();
                            }
                        })
                        .flatMap(baseResponse -> apiService.queryBabyTimeDetail(timelineobj.getTimeId()))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(timeDetailResponse -> {
                            if (timeDetailResponse.success()) {
                                timelineobj = timeDetailResponse.getTimeInfo();
                                reLoadCommend();
                                hideKeyboard();
                                ToastUtil.showToast(timeDetailResponse.getInfo());
                                if (replacePosition >= 0 && listPos >= 0) {
                                    EventBus.getDefault().post(new CommentSubmit(replacePosition, listPos, timelineobj));

//                                    listener.replaceList(replacePosition, listPos, timelineobj);
                                }
                                if (timeDetailResponse.success()) {
                                    etCommment.setText("");
                                }
                            }
                            btnSend.setClickable(true);
                        }, error -> {
                            Log.e(TAG, "comment");
                            error.printStackTrace();
                        });
                break;
            case R.id.rl_single:
                VideoPlayActivity.open(this, timelineobj.getMediaList().get(0).getVideoUrl());
                break;
            case R.id.icon_like:
                iconLike.setClickable(false);
                int p = iconLike.isSelected() == true ? 0 : 1;
                apiService.like(timelineobj.getTimeId(), p)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                if (p == 1) {//之前为点赞
                                    ll_commentLikeWrapper.setVisibility(View.VISIBLE);
                                    boolean isContains = false;
                                    timelineobj.setLike(1);
                                    int likeCount = timelineobj.getLikeList().size();
                                    timelineobj.setLikeCount(likeCount + 1);
                                    tvLikecount.setText(likeCount + 1 + "");
                                    for (UserObj u : timelineobj.getLikeList()) {
                                        if (u.getUserId() != null) {
                                            if (u.getUserId().equals(FastData.getUserInfo().getUserId())) {
                                                isContains = true;
                                                break;
                                            }
                                        }

                                    }
                                    if (!isContains) {
                                        timelineobj.getLikeList().add(FastData.getUserInfo());
                                    }

                                    iconLike.setSelected(true);
                                    hsv.setVisibility(View.VISIBLE);
                                    ImageView imageView = initPraiseItem();
                                    llGoodListUsersBar.addView(imageView);

                                    EventBus.getDefault().post(new CommentSubmit(replacePosition, listPos, timelineobj));

                                    GlideUtil.displayImage(FastData.getAvatar(), imageView, R.drawable.ic_launcher);
                                } else {//之前已点赞

                                    timelineobj.setLike(0);
                                    int likeCount = timelineobj.getLikeList().size();
                                    timelineobj.setLikeCount(likeCount - 1);
                                    tvLikecount.setText(likeCount - 1 + "");
                                    for (UserObj u : timelineobj.getLikeList()) {
                                        if (u.getUserId() != null) {
                                            if (u.getUserId().equals(FastData.getUserId())) {
                                                timelineobj.getLikeList().remove(u);
                                            }
                                        }
                                    }

                                    EventBus.getDefault().post(new CommentSubmit(replacePosition, listPos, timelineobj));

                                    iconLike.setSelected(false);
                                    llGoodListUsersBar.removeAllViews();
                                    if (timelineobj.getLikeCount() == 0) {
                                        hsv.setVisibility(View.GONE);
                                        if (timelineobj.getCommentCount() == 0) {
                                            ll_commentLikeWrapper.setVisibility(View.GONE);
                                        }
                                    } else if (timelineobj.getLikeCount() == 1 && timelineobj.getLikeList().get(0).getUserId().equals(FastData.getUserId())) {
                                        hsv.setVisibility(View.GONE);
                                        if (timelineobj.getCommentCount() == 0) {
                                            ll_commentLikeWrapper.setVisibility(View.GONE);
                                        }
                                    } else {
                                        hsv.setVisibility(View.VISIBLE);
                                        for (UserObj u : timelineobj.getLikeList()) {
                                            ImageView imageView = initPraiseItem();
                                            if (!u.getUserId().equals(FastData.getUserId())) {
                                                llGoodListUsersBar.addView(imageView);
                                            }
                                            GlideUtil.displayImage(u.getAvatar(), imageView, R.drawable.ic_launcher);
                                        }
                                    }
                                }
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                            iconLike.setClickable(true);
                        }, error -> {
                            iconLike.setClickable(true);
                            Log.e(TAG, "like:");
                            error.printStackTrace();
                        });

                break;
            case R.id.rl_cancel:
                dialog.dismiss();
                break;
            case R.id.rl_action:
                CommentObj commment = (CommentObj) v.getTag(R.string.tag_obj);
                dialog.dismiss();
                if (commment.getUserInfo().getUserId().equals(FastData.getUserId())) {
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
                            apiService.delComment(commment.getCommentId())
                                    .filter(new Func1<BaseResponse, Boolean>() {
                                        @Override
                                        public Boolean call(BaseResponse response) {
                                            return response.success();
                                        }
                                    })
                                    .flatMap(baseResponse -> apiService.queryBabyTimeDetail(timelineobj.getTimeId()))
                                    .compose(SchedulersCompat.applyIoSchedulers())
                                    .subscribe(response -> {
                                        ToastUtil.showToast(response.getInfo());
                                        if (response.success()) {
                                            //重新加载评论列表
                                            timelineobj = response.getTimeInfo();
                                            reLoadCommend();
                                            if (replacePosition >= 0 && listPos >= 0) {
                                                EventBus.getDefault().post(new CommentSubmit(replacePosition, listPos, timelineobj));
                                            }
                                        }
                                    }, error -> {
                                        Log.e(TAG, "delComment:");
                                        error.printStackTrace();
                                    });
                        }
                    }).show();
                } else {
                    //回复操作
                    etCommment.requestFocus();
                    etCommment.setHint("回复 " + commment.getUserInfo().getRelationName() + " ：");
                    commmentId = commment.getCommentId();

                }
                break;
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        if (event instanceof DeleteTimeLineEvent) {
            finish();
        } else if (event instanceof TimelineEditEvent) {
            finish();
            open(this, timelineobj.getTimeId());
        }

    }

    @Override
    public void onSizeChange(boolean param, int w, int h) {
        if (param) {//键盘弹出时
            rlComment.setPadding(0, -10, 0, 0);
            layout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        } else { //键盘隐藏时
            rlComment.setPadding(0, 0, 0, 0);

        }
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<String> urls;
        ArrayList<MediaObj> mediaObjs;

        public MyAdapter(Context context, ArrayList<String> urls, ArrayList<MediaObj> mediaObjs) {
            this.urls = urls;
            this.mediaObjs = mediaObjs;
        }

        @Override
        public int getCount() {
            return urls.size() > 9 ? 9 : urls.size();
        }

        @Override
        public Object getItem(int position) {
            return urls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(TimeLineDetailActivity.this, R.layout.item_image_and_count, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
            TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
            if (position == 8 && urls.size() > 9) {
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(urls.size() - 9 + "+");
            }
            int width = Remember.getInt("width", 0);
            if (urls.size() == 2) {
                width = Remember.getInt("width", 0) * 3 / 2;
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            tvCount.setLayoutParams(params);
            GlideUtil.displayImage(urls.get(position), iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), mediaObjs, urls, position, true, false);
                }
            });
            return view;
        }
    }

    public View initCommentMenu(CommentObj comment) {
        View view = getLayoutInflater().inflate(R.layout.view_comment_menu, null);
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

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 重新加载评论
     */
    private void reLoadCommend() {
        tvCommentcount.setText(timelineobj.getCommentList().size() + "");
        if (timelineobj.getCommentList().size() > 0) {
            ll_commentLikeWrapper.setVisibility(View.VISIBLE);
            llCommentWrapper.setVisibility(View.VISIBLE);
            llCommentWrapper.removeAllViews();
            int comments = timelineobj.getCommentList().size();
            for (int i = 0; i < comments; i++) {
                CommentObj commentObj = timelineobj.getCommentList().get(i);
                llCommentWrapper.addView(initCommentItemView(commentObj));
            }
        } else {
            llCommentWrapper.removeAllViews();
            llCommentWrapper.setVisibility(View.GONE);
            if (timelineobj.getLikeCount() == 0) {
                ll_commentLikeWrapper.setVisibility(View.GONE);
            }
        }
    }

    public ReplaceDataListener listener;

    public void setReplaceDataListener(ReplaceDataListener listener) {
        this.listener = listener;
    }

    public interface ReplaceDataListener {
        void replaceList(int replacePosition, int listPos, TimeLineObj timeLineObj);
    }

}
