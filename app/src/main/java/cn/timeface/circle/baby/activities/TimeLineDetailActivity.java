package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

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
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private TimeLineObj timelineobj;

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
        timelineobj = getIntent().getParcelableExtra("timelineobj");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(timelineobj.getAuthor().getBabyObj().getName());

        tvContent.setText(timelineobj.getContent());
        tvAuthor.setText(timelineobj.getAuthor().getNickName());
        tvDate.setText(DateUtil.getTime2(timelineobj.getDate()));

        if (timelineobj.getMediaList().size() == 1) {
            gv.setVisibility(View.GONE);
            ivCover.setVisibility(View.VISIBLE);
            String url = timelineobj.getMediaList().get(0).getImgUrl();
            GlideUtil.displayImage(url, ivCover);
        } else {
            ivCover.setVisibility(View.GONE);
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
            for (UserObj u : timelineobj.getLikeList()) {
                ImageView imageView = initPraiseItem();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    FragmentBridgeActivity.openUserInfoFragment(v.getContext(), u);
                    }
                });
                llGoodListUsersBar.addView(imageView);
                GlideUtil.displayImage(u.getAvatar(), imageView);
            }
        } else {
            hsv.setVisibility(View.GONE);
        }
        if(timelineobj.getCommentCount() >0){
            int comments = timelineobj.getCommentCount();
            for (int i = 0; i < comments; i++) {
                CommentObj commentObj = timelineobj.getCommentList().get(i);
                llCommentWrapper.addView(initCommentItemView(commentObj));
            }
        }else{
            llCommentWrapper.setVisibility(View.GONE);
        }

        btnSend.setOnClickListener(this);

    }




    private  ImageView initPraiseItem() {
        CircleImageView imageView = new CircleImageView(this);
        imageView.setImageResource(R.color.gray_pressed);
        int width = getResources().getDimensionPixelSize(R.dimen.size_36);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        int margin = getResources().getDimensionPixelSize(R.dimen.size_2);
        params.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(params);
        return imageView;
    }

    private TextView initCommentItemView(CommentObj comment) {
        TextView textView = new TextView(this);
        int size = getResources().getDimensionPixelSize(R.dimen.size_2);
        textView.setPadding(size, size, size, size);
        textView.setText(Html.fromHtml("<font color='#00b6f8'>name</font>".replace("name", comment.getUserInfo().getNickName()) + ":" + comment.getContent()));
        textView.setTextColor(getResources().getColorStateList(R.color.text_color3));
//        textView.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_small_4));
        textView.setLineSpacing(0, 1.2f);
        return textView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                String s = etCommment.getText().toString();
                apiService.comment(s,System.currentTimeMillis(),timelineobj.getTimeId(),timelineobj.getAuthor().getUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        ToastUtil.showToast(response.getInfo());
                    }, throwable -> {
                        Log.e(TAG, "comment:");
                    });
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
}
