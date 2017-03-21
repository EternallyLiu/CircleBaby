package cn.timeface.circle.baby.ui.circle.photo.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;

/**
 * 圈照片筛选条件（按时间，按发布者，按活动，按@圈的人）
 * Created by lidonglin on 2017/3/15.
 */
public class SelectCirclePhotoTypeDialog extends BasePresenterFragment implements View.OnClickListener {

    @Bind(R.id.tv_type_time)
    TextView tvTypeTime;
    @Bind(R.id.line_time)
    View lineTime;
    @Bind(R.id.tv_type_user)
    TextView tvTypeUser;
    @Bind(R.id.line_user)
    View lineUser;
    @Bind(R.id.tv_type_activity)
    TextView tvTypeActivity;
    @Bind(R.id.line_activity)
    View lineActivity;
    @Bind(R.id.tv_type_at)
    TextView tvTypeAt;
    @Bind(R.id.line_at)
    View lineAt;
    @Bind(R.id.rl_root)
    RelativeLayout rlRoot;

    @Bind({R.id.tv_type_time, R.id.tv_type_user, R.id.tv_type_activity, R.id.tv_type_at})
    TextView[] textViews;
    @Bind({R.id.line_time, R.id.line_user, R.id.line_activity, R.id.line_at})
    View[] lines;

    CirclePhotoTypeListener circlePhotoTypeListener;
    public interface CirclePhotoTypeListener {
        void selectTypeTime();

        void selectTypeUser();

        void selectTypeActivity();

        void selectTypeAt();

        void setTypeText(String title);//设置标题名称

        void dismiss();
    }

    public SelectCirclePhotoTypeDialog() {
    }

    public static SelectCirclePhotoTypeDialog newInstance(SelectCirclePhotoTypeDialog.CirclePhotoTypeListener circlePhotoTypeListener){
        SelectCirclePhotoTypeDialog selectCirclePhotoTypeDialog = new SelectCirclePhotoTypeDialog();
        selectCirclePhotoTypeDialog.setCirclePhotoTypeListener(circlePhotoTypeListener);
        return selectCirclePhotoTypeDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_circle_photo_type, container, false);
        ButterKnife.bind(this, view);
        tvTypeTime.setOnClickListener(this);
        tvTypeUser.setOnClickListener(this);
        tvTypeActivity.setOnClickListener(this);
        tvTypeAt.setOnClickListener(this);
        rlRoot.setOnClickListener(this);

        setItemSelect(tvTypeActivity.getId());
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_type_time:
                circlePhotoTypeListener.selectTypeTime();
                setItemSelect(view.getId());
                break;

            case R.id.tv_type_user:
                circlePhotoTypeListener.selectTypeUser();
                setItemSelect(view.getId());
                break;

            case R.id.tv_type_activity:
                circlePhotoTypeListener.selectTypeActivity();
                setItemSelect(view.getId());
                break;

            case R.id.tv_type_at:
                circlePhotoTypeListener.selectTypeAt();
                setItemSelect(view.getId());
                break;

            case R.id.rl_root:
                circlePhotoTypeListener.dismiss();
                break;
        }
    }

    public void setCirclePhotoTypeListener(CirclePhotoTypeListener circlePhotoTypeListener) {
        this.circlePhotoTypeListener = circlePhotoTypeListener;
    }

    public void setItemSelect(int resId) {
        for(int i = 0 ; i<textViews.length;i++){
            TextView textView = textViews[i];
            if(textView.getId() == resId){
                textView.setVisibility(View.GONE);
                lines[i].setVisibility(View.GONE);
            }else{
                textView.setVisibility(View.VISIBLE);
                lines[i].setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
