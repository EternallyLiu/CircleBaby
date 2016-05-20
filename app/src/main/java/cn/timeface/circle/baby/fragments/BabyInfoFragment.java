package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wechat.photopicker.PickerPhotoActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.MessageAdapter;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class BabyInfoFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_delete)
    TextView tvDelete;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_name)
    ImageView ivName;
    @Bind(R.id.rl_name)
    RelativeLayout rlName;
    @Bind(R.id.tv_brithday)
    TextView tvBrithday;
    @Bind(R.id.iv_brithday)
    ImageView ivBrithday;
    @Bind(R.id.rl_brithday)
    RelativeLayout rlBrithday;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rl_gender)
    RelativeLayout rlGender;
    @Bind(R.id.tv_blood)
    TextView tvBlood;
    @Bind(R.id.iv_blood)
    ImageView ivBlood;
    @Bind(R.id.rl_blood)
    RelativeLayout rlBlood;
    private MessageAdapter adapter;
    private BabyObj babyObj;
    public int gender;

    public BabyInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        babyObj = (BabyObj) getArguments().getSerializable("babyObj");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_babyinfo, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvName.setText(babyObj.getName());
        tvAge.setText(babyObj.getAge());
        tvBrithday.setText(babyObj.getBithday());
        tvBlood.setText(TextUtils.isEmpty(babyObj.getBlood()) ? "未填写" : babyObj.getBlood());
        rbGirl.setChecked(babyObj.getGender() == 0);
        rbBoy.setChecked(babyObj.getGender() == 1);

        initView(FastData.getUserInfo().getIsCreator() == 1);
        tvDelete.setOnClickListener(this);

        return view;
    }

    public void initView(boolean isCreator) {
        if (isCreator) {
            //创建者
            tvDelete.setText("删除");
            ivName.setVisibility(View.VISIBLE);
            ivBrithday.setVisibility(View.VISIBLE);
            ivBlood.setVisibility(View.VISIBLE);

            rlName.setOnClickListener(this);
            rlBrithday.setOnClickListener(this);
            rlBlood.setOnClickListener(this);
            rbGirl.setOnClickListener(this);
            rbBoy.setOnClickListener(this);

        } else {
            //关注者
            tvDelete.setText("取消关注");
            ivName.setVisibility(View.INVISIBLE);
            ivBrithday.setVisibility(View.INVISIBLE);
            rlBlood.setVisibility(View.GONE);
            rbBoy.setClickable(false);
            rbGirl.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_name:
                String name = tvName.getText().toString();
                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_NAME, name);
                break;
            case R.id.rl_brithday:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvBrithday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.rl_blood:
                String blood = tvBlood.getText().toString();
                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_BLOOD, blood);
                break;
            case R.id.rb_boy:
                gender = 1;
                rbGirl.setChecked(false);
                break;
            case R.id.rb_girl:
                gender = 0;
                rbBoy.setChecked(false);
                break;
            case R.id.tv_delete:
                if(FastData.getUserInfo().getIsCreator() == 1){
                    //删除宝宝

                }else{
                    //取消关注宝宝

                }

                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String input = "";
            if (data.hasExtra("data")) {
                input = data.getStringExtra("data");
            }
            switch (requestCode) {
                case TypeConstants.EDIT_NAME:
                    tvName.setText(input);
                    break;
                case TypeConstants.EDIT_BLOOD:
                    tvBlood.setText(input);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(FastData.getUserInfo().getIsCreator() == 1){
            String name = tvName.getText().toString();
            String brithday = tvBrithday.getText().toString();
            String blood = tvBlood.getText().toString();
            //修改宝宝信息

        }


        ButterKnife.unbind(this);
    }
}
