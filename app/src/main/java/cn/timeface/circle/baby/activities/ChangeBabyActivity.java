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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.ChangebabyAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.api.models.responses.BabyInfoListResponse;
import cn.timeface.circle.baby.support.api.services.OpenUploadServices;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.beans.BabyAttentionEvent;
import cn.timeface.circle.baby.ui.babyInfo.beans.BabyChanged;
import cn.timeface.circle.baby.ui.babyInfo.fragments.CreateBabyFragment;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.TFOpen;
import cn.timeface.open.TFOpenConfig;
import cn.timeface.open.api.bean.obj.TFOUserObj;

public class ChangeBabyActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_createbaby)
    TextView tvCreatebaby;
    @Bind(R.id.tv_focusbaby)
    TextView tvFocusbaby;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    private ChangebabyAdapter adapter;
    private BabyInfoListResponse babyInfoListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_changebaby);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ChangebabyAdapter(this, new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(adapter);
        tfStateView.setOnRetryListener(() -> reqData());
        tfStateView.loading();
//        reqData();

        tvCreatebaby.setOnClickListener(this);
        tvFocusbaby.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        reqData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, ChangeBabyActivity.class);
        context.startActivity(intent);
    }

    private void reqData() {
        apiService.queryBabyInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(babyInfoListResponse -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    this.babyInfoListResponse = babyInfoListResponse;
                    setDataList(babyInfoListResponse.getDataList());
                }, throwable -> {
                    if (tfStateView != null) {
                        tfStateView.showException(throwable);
                    }
                    Log.e(TAG, "queryBabyInfoList:",throwable);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_createbaby:
//                CreateBabyActivity.open(this,false);
                FragmentBridgeActivity.open(this, CreateBabyFragment.class.getSimpleName());
                break;
            case R.id.tv_focusbaby:
                InviteCodeActivity.open(this);
                break;
            case R.id.rl_baby:
                UserObj info = (UserObj) v.getTag(R.string.tag_ex);
                changeBaby(info);
                break;
        }
    }

    private void changeBaby(UserObj info) {
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
    }

    private void initOpen() {
        TFOUserObj tfoUserObj = new TFOUserObj();
        tfoUserObj.setAvatar(FastData.getAvatar());
        tfoUserObj.setGender(FastData.getBabyGender());
        tfoUserObj.setNick_name(FastData.getBabyName());
        tfoUserObj.setPhone(FastData.getAccount());
        tfoUserObj.setUserId(FastData.getUserId());
//        GlobalSetting.getInstance().init(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj, BuildConfig.DEBUG);
//        GlobalSetting.getInstance().setUploadServices(new OpenUploadServices());
        TFOpen.init(this, new TFOpenConfig.Builder(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj)
                .debug(BuildConfig.DEBUG).build()
        );
    }

    @Override
    public void onBackPressed() {
        int babyId = FastData.getBabyId();
        if(babyId == 0 && babyInfoListResponse != null){
            int size = babyInfoListResponse.getDataList().size();
            if(size>0){
                UserObj userObj = babyInfoListResponse.getDataList().get(0);
                changeBaby(userObj);
            }else{
                ToastUtil.showToast("你还没有宝宝，请先创建或关注一个宝宝");
            }
        }else{
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onEvent(BabyChanged changed){
        changeBaby(changed.getUserObj());
        EventBus.getDefault().post(new BabyAttentionEvent(changed.getBuilder()));
    }

}
