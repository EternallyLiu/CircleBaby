package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.MessageAdapter;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

public class FamilyMemberInfoFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_relation)
    TextView tvRelation;
    @Bind(R.id.iv_relation)
    ImageView ivRelation;
    @Bind(R.id.rl_relation)
    RelativeLayout rlRelation;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_nickname)
    ImageView ivNickname;
    @Bind(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @Bind(R.id.btn_delete)
    Button btnDelete;
    private MessageAdapter adapter;
    private UserObj userObj;
    private AlertDialog dialog;

    public FamilyMemberInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userObj = getArguments().getParcelable("userObj");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_familymemberinfo, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        toolbar.setTitle(R.string.family_Info);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tvRelation.setText(userObj.getRelationName());
        tvNickname.setText(userObj.getNickName());

        initView(FastData.getUserInfo().getIsCreator() == 1);

        return view;
    }

    public void initView(boolean isCreator) {
        if (isCreator) {
            //创建者
            ivRelation.setVisibility(View.VISIBLE);
            ivNickname.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            rlRelation.setOnClickListener(this);
//            rlNickname.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

        } else {
            //关注者
            ivRelation.setVisibility(View.INVISIBLE);
            ivNickname.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_relation:
                String relation = tvRelation.getText().toString();
                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_RELATIONNAME, relation);
                break;
//            case R.id.rl_nickname:
//                String nickname = tvNickname.getText().toString();
//                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_NICKNAME, nickname);
//                break;
            case R.id.btn_delete:
                dialog = new AlertDialog.Builder(getContext()).setView(initDeleteView()).show();
                dialog.setCanceledOnTouchOutside(false);
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
            case R.id.btn_ok:
                dialog.dismiss();
                //删除亲友
                apiService.delRelationship(userObj.getUserId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            ToastUtil.showToast(response.getInfo());
                            if (response.success()) {
                                getActivity().finish();
                            }else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        }, throwable -> {
                            Log.e(TAG, "queryBabyInfo:");
                        });
                break;
        }
    }

    private View initDeleteView() {
        View view = View.inflate(getActivity(),R.layout.view_dialog, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        tvMsg.setText("确定删除 " + userObj.getRelationName() + " 吗？");
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        return view;
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
                case TypeConstants.EDIT_RELATIONNAME:
                    tvRelation.setText(input);
                    break;
                case TypeConstants.EDIT_NICKNAME:
                    tvNickname.setText(input);
                    break;
            }
            edit();
        }
    }

    public void edit(){
        if (FastData.getUserInfo().getIsCreator() == 1) {
            String nickName = tvNickname.getText().toString();
            String relationName = tvRelation.getText().toString();
            String userId = userObj.getUserId();
            //修改亲友信息
            apiService.updateFamilyRelationshipInfo(nickName, relationName, userId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        ToastUtil.showToast(response.info);
                    }, throwable -> {
                        Log.e(TAG, "queryBabyInfo:");
                    });
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
