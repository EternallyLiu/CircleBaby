package cn.timeface.circle.baby.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.CommentObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.IconTextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TimeLineDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

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
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.ll_menu)
    LinearLayout llMenu;
    @Bind(R.id.tv_milestone)
    TextView tvMilestone;
    @Bind(R.id.icon_like)
    IconTextView iconLike;
    @Bind(R.id.icon_comment)
    IconTextView iconComment;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;

    private TimeLineObj timelineobj;
    private AlertDialog dialog;
    private static int normalColor;
    private int commmentId = 0;

    public static void open(Context context, TimeLineObj item) {
        Intent intent = new Intent(context, TimeLineDetailActivity.class);
        intent.putExtra("timelineobj", item);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelinedetail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        normalColor = getResources().getColor(R.color.gray_normal);
        etCommment.clearFocus();
        initView();
    }

    private void initView() {
        timelineobj = (TimeLineObj) getIntent().getParcelableExtra("timelineobj");
        tvContent.setText(timelineobj.getContent());
        tvAuthor.setText(timelineobj.getAuthor().getRelationName());
        tvDate.setText(DateUtil.getTime2(timelineobj.getDate()));
        tvTitle.setText(timelineobj.getAuthor().getBabyObj().getName());
        iconLike.setTextColor(timelineobj.getLike() == 1 ? Color.RED : normalColor);
        if (!TextUtils.isEmpty(timelineobj.getMilestone())) {
            tvMilestone.setVisibility(View.VISIBLE);
            tvMilestone.setText(timelineobj.getMilestone());
        }

        if (timelineobj.getMediaList().size() == 1) {
            gv.setVisibility(View.GONE);
            ivCover.setVisibility(View.VISIBLE);
            String url = timelineobj.getMediaList().get(0).getImgUrl();
            GlideUtil.displayImage(url, ivCover);
        } else {
            rlSingle.setVisibility(View.GONE);
        }

        if (timelineobj.getMediaList().size() > 1) {
            gv.setVisibility(View.VISIBLE);
            List<MediaObj> imgObjList = timelineobj.getMediaList();
            ArrayList<String> urls = new ArrayList<>();
            for (MediaObj mediaObj : imgObjList) {
                urls.add(mediaObj.getImgUrl());
            }
            MyAdapter myAdapter = new MyAdapter(this, urls);
            gv.setAdapter(myAdapter);
            ViewGroup.LayoutParams layoutParams = gv.getLayoutParams();
            layoutParams.height = Remember.getInt("width", 0);
            if (timelineobj.getMediaList().size() > 3) {
                layoutParams.height = Remember.getInt("width", 0) * 2;
            }
            if (timelineobj.getMediaList().size() > 6) {
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }
            gv.setLayoutParams(layoutParams);

        } else {
            gv.setVisibility(View.GONE);
        }


        if (timelineobj.getLikeCount() > 0) {
            hsv.setVisibility(View.VISIBLE);
            llGoodListUsersBar.removeAllViews();
            for (UserObj u : timelineobj.getLikeList()) {
                ImageView imageView = initPraiseItem();
                llGoodListUsersBar.addView(imageView);
                GlideUtil.displayImage(u.getAvatar(), imageView);
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

        if (timelineobj.getType() == 1) {
            ivVideo.setVisibility(View.VISIBLE);
            int width = Remember.getInt("width", 0);
            ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            ivCover.setLayoutParams(layoutParams);
            ivVideo.setLayoutParams(layoutParams);
            ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            rlSingle.setOnClickListener(this);
        }
        btnSend.setOnClickListener(this);
        llMenu.setOnClickListener(this);
        iconLike.setOnClickListener(this);
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
        TextView tvRelation = (TextView) view.findViewById(R.id.tv_relation);
        TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvRelation.setText(comment.getUserInfo().getRelationName());
        tvComment.setText(comment.getContent());
        tvTime.setText(DateUtil.formatDate("MM-dd HH:mm", comment.getCommentDate()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(TimeLineDetailActivity.this).setView(initCommentMenu(comment)).show();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
            }
        });
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
                apiService.comment(URLEncoder.encode(s), System.currentTimeMillis(), timelineobj.getTimeId(),commmentId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            hideKeyboard();
                            ToastUtil.showToast(response.getInfo());
                            if (response.success()) {
                                etCommment.setText("");
                            }
                        }, error -> {
                            Log.e(TAG, "comment:");
                        });
                break;
            case R.id.rl_single:
                Intent intent = new Intent(this, VideoPlayActivity.class);
                intent.putExtra("media", timelineobj.getMediaList().get(0));
                startActivity(intent);
                break;
            case R.id.ll_menu:
                dialog = new AlertDialog.Builder(this).setView(initMenu()).show();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                break;
            case R.id.icon_like:
                int p = iconLike.getCurrentTextColor() == Color.RED ? 0 : 1;
                apiService.like(timelineobj.getTimeId(), p)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            ToastUtil.showToast(response.getInfo());
                            if (response.success()) {
                                if (p == 1) {
                                    iconLike.setTextColor(Color.RED);
                                    hsv.setVisibility(View.VISIBLE);

                                    ImageView imageView = initPraiseItem();
                                    llGoodListUsersBar.addView(imageView);
                                    GlideUtil.displayImage(FastData.getAvatar(), imageView);
                                } else {
                                    iconLike.setTextColor(normalColor);
                                    llGoodListUsersBar.removeAllViews();
                                    if (timelineobj.getLikeCount() == 0) {
                                        hsv.setVisibility(View.GONE);
                                    } else if (timelineobj.getLikeCount() == 1 && timelineobj.getLikeList().get(0).getUserId().equals(FastData.getUserId())) {
                                        hsv.setVisibility(View.GONE);
                                    } else {
                                        hsv.setVisibility(View.VISIBLE);
                                        for (UserObj u : timelineobj.getLikeList()) {
                                            ImageView imageView = initPraiseItem();
                                            if (!u.getUserId().equals(FastData.getUserId())) {
                                                llGoodListUsersBar.addView(imageView);
                                            }
                                            GlideUtil.displayImage(u.getAvatar(), imageView);
                                        }
                                    }
                                }
                            }
                        }, error -> {
                            Log.e(TAG, "like:");
                        });

                break;
            case R.id.tv_edit:

                break;
            case R.id.tv_delete:
                new AlertDialog.Builder(this)
                        .setTitle("确定删除这条记录吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiService.delTime(timelineobj.getTimeId())
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(response -> {
                                    ToastUtil.showToast(response.getInfo());
                                    if (response.success()) {
                                        finish();
                                    }
                                }, error -> {
                                    Log.e(TAG, "delTime:");
                                });
                    }
                }).show();
                break;
            case R.id.tv_download:

                break;
            case R.id.tv_share:

                break;
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_action:
                CommentObj commment = (CommentObj) v.getTag(R.string.tag_obj);
                dialog.dismiss();
                if(commment.getUserInfo().getUserId().equals(FastData.getUserId())){
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
                                    .compose(SchedulersCompat.applyIoSchedulers())
                                    .subscribe(response -> {
                                        ToastUtil.showToast(response.getInfo());
                                        if (response.success()) {
                                            //重新加载评论列表

                                        }
                                    }, error -> {
                                        Log.e(TAG, "delComment:");
                                    });
                        }
                    }).show();
                }else{
                    //回复操作
                    etCommment.requestFocus();
                    etCommment.setHint("回复 " + commment.getUserInfo().getRelationName() + " ：");
                    commmentId = commment.getCommentId();
                }
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<String> urls;

        public MyAdapter(Context context, ArrayList<String> urls) {
            this.urls = urls;
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
            View view = View.inflate(TimeLineDetailActivity.this, R.layout.item_image, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
            int width = Remember.getInt("width", 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            GlideUtil.displayImage(urls.get(position), iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), urls, position);
                }
            });
            return view;
        }
    }

    public View initMenu() {
        View view = getLayoutInflater().inflate(R.layout.view_timedetail_menu, null);
        TextView tvEdit = (TextView) view.findViewById(R.id.tv_edit);
        TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
        TextView tvShare = (TextView) view.findViewById(R.id.tv_share);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        if(timelineobj.getType()==1){
            TextView tvDownload = (TextView) view.findViewById(R.id.tv_download);
            tvDownload.setVisibility(View.VISIBLE);
            tvDownload.setOnClickListener(this);
        }
        tvEdit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }

    public View initCommentMenu(CommentObj comment){
        View view = getLayoutInflater().inflate(R.layout.view_comment_menu, null);
        TextView tvAction = (TextView) view.findViewById(R.id.tv_action);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        if(comment.getUserInfo().getUserId().equals(FastData.getUserId())){
            tvAction.setText("删除");
        }
        tvAction.setTag(R.string.tag_obj,comment);
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
}
