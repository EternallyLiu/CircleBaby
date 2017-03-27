package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.images.views.FlowLayout;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/3/25
 * email : wangs1992321@gmail.com
 */
public class HomeWorkActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_detail)
    TextView tvDetail;
    @Bind(R.id.gl_image_list)
    GridLayout glImageList;
    @Bind(R.id.tag_list)
    FlowLayout tagList;
    @Bind(R.id.tv_tearcher_tip)
    TextView tvTearcherTip;
    @Bind(R.id.tv_tearcher_review)
    TextView tvTearcherReview;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private CircleHomeworkObj homeworkObj;
    private LayoutInflater inflater;
    private int paddingImage;

    public static void open(Context context, CircleHomeworkObj homeworkObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CircleHomeworkObj.class.getSimpleName(), homeworkObj);
        context.startActivity(new Intent(context, HomeWorkActivity.class).putExtras(bundle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
    }

    private void init() {
        homeworkObj = getIntent().getParcelableExtra(CircleHomeworkObj.class.getSimpleName());
        LogUtil.showLog("json:" + JSONUtils.parse2JSONString(homeworkObj));
        title.setText(DateUtil.formatDate("MM月dd日 EE", homeworkObj.getCreateDate()));
        tvName.setText(homeworkObj.getBabyInfo().getRealName());
        tvDetail.setText(homeworkObj.getContent());
        tvTearcherReview.setVisibility(TextUtils.isEmpty(homeworkObj.getTeacherReview()) ? View.GONE : View.VISIBLE);
        tvTearcherTip.setVisibility(TextUtils.isEmpty(homeworkObj.getTeacherReview()) ? View.GONE : View.VISIBLE);
        tvTearcherReview.setText(homeworkObj.getTeacherReview());
        tvTitle.setText(homeworkObj.getTitle());
        doMediaList(glImageList, 0, homeworkObj.getMediaList());

        tagList.removeAllViews();
        TextView textView;
        int count = homeworkObj.getNotations().size();
        if (inflater == null) inflater = LayoutInflater.from(this);
        for (int i = 0; i < count; i++) {
            textView = (TextView) inflater.inflate(R.layout.review_tag, tagList, false);
            textView.setText(homeworkObj.getNotations().get(i));
            tagList.addView(textView);
        }

    }


    private void doMediaList(GridLayout gridLayout, int position, List<? extends MediaObj> list) {
        if (list.size() <= 0) {
            gridLayout.setVisibility(View.GONE);
            return;
        }
        gridLayout.setVisibility(View.VISIBLE);
        if (inflater == null) inflater = LayoutInflater.from(this);
        paddingImage = (int) (getResources().getDimension(R.dimen.size_2));
        if (gridLayout.getChildCount() > 0) gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        View view;
        for (int i = 0; i < list.size(); i++) {
            view = inflater.inflate(R.layout.time_line_list_image, gridLayout, false);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) view.getLayoutParams();
            if (params == null)
                params = new GridLayout.LayoutParams();
            params.width = App.mScreenWidth / 3;
            params.height = App.mScreenWidth / 3;
            view.setLayoutParams(params);
            view.setPadding(paddingImage, paddingImage, paddingImage, paddingImage);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setTag(R.id.recycler_item_click_tag, i);
            icon.setTag(R.id.recycler_item_input_tag, position);
            icon.setOnClickListener(this);
            GlideUtil.displayImage(list.get(i).getImgUrl(), icon, true);
            gridLayout.addView(view);
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        try {
            int position = (int) v.getTag(R.id.recycler_item_input_tag);
            int index = (int) v.getTag(R.id.recycler_item_click_tag);
            CircleContentObj item = homeworkObj;
            FragmentBridgeActivity.openBigimageFragment(this, 0, MediaObj.getMediaArray(item.getMediaList()), MediaObj.getUrls(item.getMediaList()), index, BigImageFragment.CIRCLE_MEDIA_IMAGE_NONE, true, false);
        } catch (Exception e) {
        }
    }
}
