package cn.timeface.circle.baby.ui.circle.timelines.dialog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.RelateBabyAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleBabyObj;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import rx.Observable;
import rx.Subscription;

/**
 * author : wangshuai Created on 2017/3/20
 * email : wangs1992321@gmail.com
 */
public class CircleBabyDialog extends BaseDialog implements View.OnClickListener, BaseAdapter.OnItemClickLister {

    private RecyclerView contentRecyclerView;
    private Button btnSubmit;
    private CircleMediaObj currentMediaObj = null;
    private List<Long> babyIds = new ArrayList<>(0);
    private List<Long> cancleCircleId = new ArrayList<>(0);

    private Subscription currentSubscription;

    private RelateBabyAdapter adapter = null;

    public CircleBabyDialog(Context context, CircleMediaObj mediaObj) {
        this(context);
        setMediaObj(mediaObj);
    }

    private CircleBabyDialog(Context context) {
        super(context);
        init();
    }

    private CircleBabyDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private CircleBabyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle_baby, null);
        this.setContentView(mView);
        contentRecyclerView = (RecyclerView) mView.findViewById(R.id.content_recycler_view);
        btnSubmit = (Button) mView.findViewById(R.id.btn_submit);
        adapter = new RelateBabyAdapter(getContext());
        contentRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        contentRecyclerView.setAdapter(adapter);
        adapter.setItemClickLister(this);
        btnSubmit.setOnClickListener(this);


        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        p.height = App.mScreenHeight / 2;
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    @Override
    public void onClick(View v) {
        if (getCircleBabyCallBack() != null) {
            String babys = JSONUtils.parse2JSONString(adapter.getDataBaby());
            getCircleBabyCallBack().circleResult(babys, currentMediaObj.getId());
        }
        dismiss();
    }

    @Override
    public void onItemClick(View view, int position) {
        GetCircleAllBabyObj babyObj = adapter.getItem(position);
        LogUtil.showLog("baby:" + JSONUtils.parse2JSONString(babyObj));
        if (babyObj.getBaseType() == 0) {
            FlipImageView ivSelect = ViewHolder.getView(view, R.id.iv_select);
            if (ivSelect.getStatus() != RelateBabyAdapter.STATUS_FINAL) {
                if (babyObj.getSelected() == 0) {
                    babyObj.setSelected(1);
                    ivSelect.setVisibility(View.VISIBLE);
                    ivSelect.changeStatus(RelateBabyAdapter.STATUS_SELECT);
                } else {
                    babyObj.setSelected(0);
                    ivSelect.setVisibility(View.GONE);
                    ivSelect.changeStatus(RelateBabyAdapter.STATUS_NONE);
                }
            }
        }
    }

    @Override
    public void dismiss() {
        if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
            currentSubscription.unsubscribe();
            currentSubscription = null;
        }
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        if (currentMediaObj != null)
            currentSubscription = ApiFactory.getApi().getApiService().getCircleAllBaby(FastData.getCircleId(), 1, currentMediaObj.getId())
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(getCircleAllBabyObjQueryCirclePhotoResponse -> {
                        if (getCircleAllBabyObjQueryCirclePhotoResponse.success()) {
                            adapter.addList(true, getCircleAllBabyObjQueryCirclePhotoResponse.getDataList());
                        } else {
                            ToastUtil.showToast(getContext(), getCircleAllBabyObjQueryCirclePhotoResponse.getInfo());
                            dismiss();
                        }
                    }, throwable -> {
                        dismiss();
                        LogUtil.showError(throwable);
                    });
    }

    public void setMediaObj(CircleMediaObj mediaObj) {
        if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
            currentSubscription.unsubscribe();
            currentSubscription = null;
        }
        if (mediaObj != null) {
            currentMediaObj = mediaObj;
        }
    }

    private CircleBabyCallBack circleBabyCallBack;

    public CircleBabyCallBack getCircleBabyCallBack() {
        return circleBabyCallBack;
    }

    public void setCircleBabyCallBack(CircleBabyCallBack circleBabyCallBack) {
        this.circleBabyCallBack = circleBabyCallBack;
    }

    public interface CircleBabyCallBack {
        public void circleResult(String babys, long mediaId);
    }

}
