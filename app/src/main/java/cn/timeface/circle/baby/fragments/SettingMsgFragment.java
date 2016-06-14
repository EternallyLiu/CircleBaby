package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.Remember;

public class SettingMsgFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_baby)
    RelativeLayout rlBaby;
    @Bind(R.id.iv_newtime)
    ImageView ivNewtime;
    @Bind(R.id.rl_newtime)
    RelativeLayout rlNewtime;
    @Bind(R.id.iv_comment)
    ImageView ivComment;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.iv_activity)
    ImageView ivActivity;
    @Bind(R.id.rl_avtivity)
    RelativeLayout rlAvtivity;
    @Bind(R.id.iv_newmember)
    ImageView ivNewmember;
    @Bind(R.id.rl_newmember)
    RelativeLayout rlNewmember;

    public SettingMsgFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settingmsg, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        return view;
    }

    private void initData() {
        if(Remember.getInt("newtime",1)==0){
            ivNewtime.setImageResource(R.drawable.swichoff);
        }
        if(Remember.getInt("comment",1)==0){
            ivComment.setImageResource(R.drawable.swichoff);
        }
        if(Remember.getInt("activity",1)==0){
            ivActivity.setImageResource(R.drawable.swichoff);
        }
        if(Remember.getInt("newmember",1)==0){
            ivNewmember.setImageResource(R.drawable.swichoff);
        }
        rlBaby.setOnClickListener(this);
        rlNewtime.setOnClickListener(this);
        rlComment.setOnClickListener(this);
        rlAvtivity.setOnClickListener(this);
        rlNewmember.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_baby:

                break;
            case R.id.rl_newtime:
                if(Remember.getInt("newtime",1)==0){
                    ivNewtime.setImageResource(R.drawable.swichon);
                    Remember.putInt("newtime",1);
                }else{
                    ivNewtime.setImageResource(R.drawable.swichoff);
                    Remember.putInt("newtime",0);
                }
                break;
            case R.id.rl_comment:
                if(Remember.getInt("comment",1)==0){
                    ivComment.setImageResource(R.drawable.swichon);
                    Remember.putInt("comment",1);
                }else{
                    ivComment.setImageResource(R.drawable.swichoff);
                    Remember.putInt("comment",0);
                }
                break;
            case R.id.rl_avtivity:
                if(Remember.getInt("activity",1)==0){
                    ivActivity.setImageResource(R.drawable.swichon);
                    Remember.putInt("activity",1);
                }else{
                    ivActivity.setImageResource(R.drawable.swichoff);
                    Remember.putInt("activity",0);
                }
                break;
            case R.id.rl_newmember:
                if(Remember.getInt("newmember",1)==0){
                    ivNewmember.setImageResource(R.drawable.swichon);
                    Remember.putInt("newmember",1);
                }else{
                    ivNewmember.setImageResource(R.drawable.swichoff);
                    Remember.putInt("newmember",0);
                }
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
