package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

public class CircleCardView extends FrameLayout {

    @Bind(R.id.iv_cover)
    RoundedImageView ivCover;
    @Bind(R.id.tv_tag)
    TextView tvTag;
    @Bind(R.id.tv_circle_name)
    TextView tvCircleName;
    @Bind(R.id.tv_circle_desc)
    TextView tvCircleDesc;

    private boolean showJoinType = false;

    public CircleCardView(Context context) {
        super(context);
        init();
    }

    public CircleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_circle_card, this);
        ButterKnife.bind(view);
    }

    public void setShowJoinType(boolean showJoinType) {
        this.showJoinType = showJoinType;
        if (tvTag != null) {
            tvTag.setVisibility(showJoinType ? VISIBLE : GONE);
        }
    }

    public void setContent(GrowthCircleObj circleObj) {
        tvCircleName.setText(circleObj.getCircleName());
        tvCircleDesc.setText("成员：" + circleObj.getMemberCount() + "人   |   照片："
                + circleObj.getMediaCount() + "张");

        tvTag.setVisibility(showJoinType ? VISIBLE : GONE);
        tvTag.setText(circleObj.isCreator() ? "我创建的" : "我加入的");

        Glide.with(getContext())
                .load(circleObj.getCicleCoverUrl())
                .centerCrop()
                .into(ivCover);
    }

}
