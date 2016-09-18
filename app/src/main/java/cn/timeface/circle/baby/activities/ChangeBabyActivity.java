package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.ChangebabyAdapter;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.api.services.OpenUploadServices;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.GlobalSetting;
import cn.timeface.open.api.models.objs.TFOUserObj;

public class ChangeBabyActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_createbaby)
    TextView tvCreatebaby;
    @Bind(R.id.tv_focusbaby)
    TextView tvFocusbaby;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private ChangebabyAdapter adapter;

    public static void open(Context context) {
        Intent intent = new Intent(context, ChangeBabyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_changebaby);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ChangebabyAdapter(this, new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(adapter);

//        reqData();

        tvCreatebaby.setOnClickListener(this);
        tvFocusbaby.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        reqData();
        super.onResume();
    }

    private void reqData() {
        apiService.queryBabyInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(babyInfoListResponse -> {
                    setDataList(babyInfoListResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "queryBabyInfoList:");
                });

    }

    private void setDataList(List<UserObj> dataList) {
        ArrayList<UserObj> userObjs = new ArrayList<>();
        for (UserObj user : dataList){
            if(!TextUtils.isEmpty(user.getUserId()) && user.getBabyObj().getBabyId()!=0){
                userObjs.add(user);
            }
        }
        adapter.setListData(userObjs);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_createbaby:
                CreateBabyActivity.open(this,false);
                break;
            case R.id.tv_focusbaby:
                InviteCodeActivity.open(this);
                break;
            case R.id.rl_baby:
                UserObj info = (UserObj) v.getTag(R.string.tag_ex);
                FastData.setUserInfo(info);
                apiService.updateLoginInfo()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                                Remember.putBoolean("showtimelinehead", true);
                                EventBus.getDefault().post(new HomeRefreshEvent());
                                EventBus.getDefault().post(new ConfirmRelationEvent());
                                EventBus.getDefault().post(new UnreadMsgEvent());
                                initOpen();
                                this.finish();
                        }, throwable -> {
                            Log.e(TAG, "updateLoginInfo:");
                        });
                break;
        }
    }

    private void initOpen() {
        TFOUserObj tfoUserObj = new TFOUserObj();
        tfoUserObj.setAvatar(FastData.getAvatar());
        tfoUserObj.setGender(FastData.getBabyGender());
        tfoUserObj.setNick_name(FastData.getBabyName());
        tfoUserObj.setPhone(FastData.getAccount());
        GlobalSetting.getInstance().init(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj, new OpenUploadServices());
    }

    @Override
    public void onBackPressed() {
        ToastUtil.showToast("请选择或创建关注宝宝");
    }
}
