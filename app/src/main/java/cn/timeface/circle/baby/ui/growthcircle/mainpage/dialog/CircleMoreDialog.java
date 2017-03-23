package cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.circle.activities.CircleBookActivity;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.activity.GroupMembersActivity;
import cn.timeface.circle.baby.ui.circle.photo.activities.CirclePhotoActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleInfoActivity;

public class CircleMoreDialog extends DialogFragment {

    @Bind(R.id.tv_circle_info)
    TextView tvCircleInfo;
    @Bind(R.id.tv_share)
    TextView tvShare;
    @Bind(R.id.tv_production)
    TextView tvProduction;
    @Bind(R.id.tv_photos)
    TextView tvPhotos;
    @Bind(R.id.tv_member_manager)
    TextView tvMemberManager;
    @Bind(R.id.tv_publish_homework)
    TextView tvPublishHomework;

    private GrowthCircleObj circleObj;

    public static CircleMoreDialog newInstance(GrowthCircleObj circleObj) {
        CircleMoreDialog dialog = new CircleMoreDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("circle_obj", circleObj);
        dialog.setArguments(bundle);
        return dialog;
    }

    public CircleMoreDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circleObj = getArguments().getParcelable("circle_obj");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_circle_more, container, false);
        ButterKnife.bind(this, view);

        tvPublishHomework.setVisibility(FastData.getCircleUserInfo().isTeacher() ?
                View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.trans);
        getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_circle_info, R.id.tv_share, R.id.tv_production,
            R.id.tv_photos, R.id.tv_member_manager, R.id.tv_publish_homework})
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.tv_circle_info:
                CircleInfoActivity.open(getContext(), circleObj);
                break;
            case R.id.tv_share:

                break;
            case R.id.tv_production:
                CircleBookActivity.open(getActivity(), circleObj.getCircleId());
                break;
            case R.id.tv_photos:
                CirclePhotoActivity.open(getContext(), circleObj.getCircleId());
                break;
            case R.id.tv_member_manager:
                GroupMembersActivity.open(getContext(), circleObj);
                break;
            case R.id.tv_publish_homework:

                break;
        }
    }

}
