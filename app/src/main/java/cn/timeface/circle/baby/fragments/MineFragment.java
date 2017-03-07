package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CartActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MineInfoActivity;
import cn.timeface.circle.baby.activities.OrderListActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.MineBookActivityV2;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    /**
     * 显示宝宝头像的最大数量
     */
    private static final int MAX_BABY_ICON_COUNT = 5;
    @Bind(R.id.tv_mine)
    TextView tvMine;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.ll_mine_info)
    RelativeLayout llMineInfo;
    @Bind(R.id.ll_mine_product)
    RelativeLayout llMineProduct;
    @Bind(R.id.ll_mine_order)
    RelativeLayout llMineOrder;
    @Bind(R.id.ll_mine_car)
    RelativeLayout llMineCar;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_set)
    RelativeLayout llSet;
    @Bind(R.id.ll_baby)
    LinearLayout llBaby;
    @Bind(R.id.ll_message)
    RelativeLayout llMessage;
    @Bind(R.id.msg_tip)
    TextView msgTip;
    @Bind(R.id.iv_dot)
    ImageView ivDot;
    @Bind(R.id.tv_count_msg)
    TextView tvCountMsg;
    @Bind(R.id.tv_count_production)
    TextView tvCountProduction;
    @Bind(R.id.tv_count_cart)
    TextView tvCountCart;
    @Bind(R.id.tv_count_order)
    TextView tvCountOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        initData();

        llSet.setOnClickListener(this);
        llMineProduct.setOnClickListener(this);
        llMineCar.setOnClickListener(this);
        llMineInfo.setOnClickListener(this);
        llMineOrder.setOnClickListener(this);
        llMessage.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        initData();
        LogUtil.showLog("mainThread:" + Thread.currentThread().getName());
        reqMine();
        addSubscription(BabyObj.getCurrentUserBabyObjs()
                .subscribeOn(Schedulers.io())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (llBaby.getChildCount() > 0)
                        llBaby.removeAllViews();
                    int lastIndex = list.size() > MAX_BABY_ICON_COUNT ? MAX_BABY_ICON_COUNT - 1 : list.size() - 1;
                    for (int i = 0; i <= lastIndex; i++) {
                        llBaby.addView(getBabyImageView(list.get(i)));
                    }
                }));
        // 解决某些异常情况导致FootMenu消失
        if (getActivity() instanceof TabMainActivity) {
            ((TabMainActivity) getActivity()).showFootMenu();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public MineFragment() {
    }

    public static MineFragment newInstance(String param1) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private void initData() {
        GlideUtil.displayImageCircle(FastData.getAvatar(), R.drawable.ic_launcher, ivAvatar);
        tvName.setText(FastData.getUserName());
        initUnreadMessage();
    }

    private LinearLayout.LayoutParams babyImageLayoutParams = null;
    private int babyImageWidth, babyImageHeigh;

    private ImageView getBabyImageView(BabyObj babyObj) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (babyImageHeigh <= 0 || babyImageWidth <= 0) {
            babyImageHeigh = (int) getActivity().getResources().getDimension(R.dimen.size_30);
            babyImageWidth = (int) getActivity().getResources().getDimension(R.dimen.size_30);
        }
        if (babyImageLayoutParams == null) {
            babyImageLayoutParams = new LinearLayout.LayoutParams(babyImageWidth, babyImageHeigh);
            babyImageLayoutParams.rightMargin = 10;
        }
        imageView.setLayoutParams(babyImageLayoutParams);
        GlideUtil.displayImageCircle(babyObj.getAvatar(), R.drawable.ic_launcher, imageView);
        return imageView;

    }

    private void initUnreadMessage() {
        apiService.noReadMsg()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(unReadMsgResponse -> {
                    if (unReadMsgResponse.success()) {
                        if (unReadMsgResponse.getUnreadMessageCount() > 0) {
                            ivDot.setVisibility(View.VISIBLE);
                        } else {
                            ivDot.setVisibility(View.GONE);
                        }
                    }
                }, error -> {
                    LogUtil.showError(error);
                });
    }

    // 更新 我的 相关数量及小红点
    private void reqMine() {
        Subscription s = apiService.queryMine()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        ivDot.setVisibility(response.getMessagecount() > 0 ?
                                View.VISIBLE : View.GONE);

                        tvCountMsg.setVisibility(response.getMessagecount() > 0 ?
                                View.VISIBLE : View.GONE);
                        tvCountMsg.setText(String.valueOf(response.getMessagecount()));
                        tvCountProduction.setVisibility(response.getWorkcount() > 0 ?
                                View.VISIBLE : View.GONE);
                        tvCountProduction.setText(String.valueOf(response.getWorkcount()));
                        tvCountCart.setVisibility(response.getPrintcarcount() > 0 ?
                                View.VISIBLE : View.GONE);
                        tvCountCart.setText(String.valueOf(response.getPrintcarcount()));
                        tvCountOrder.setVisibility(response.getOrdercount() > 0 ?
                                View.VISIBLE : View.GONE);
                        tvCountOrder.setText(String.valueOf(response.getOrdercount()));
                    }
                }, error -> {
                    Log.e(TAG, "noReadMsg:");
                    error.printStackTrace();
                });
        addSubscription(s);
    }

    @Subscribe
    public void onEvent(UnreadMsgEvent event) {
        initUnreadMessage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_message:
                FragmentBridgeActivity.open(getActivity(), "MessageFragment");
                break;
            case R.id.ll_set:
                FragmentBridgeActivity.open(getContext(), "SettingFragment");
                break;
            case R.id.ll_mine_info:
                MineInfoActivity.open(getActivity());
                break;
            case R.id.ll_mine_product:
//                MineBookActivity.open(getActivity());
                MineBookActivityV2.open(getActivity());
                break;
            case R.id.ll_mine_order:
                OrderListActivity.open(getContext());
                break;
            case R.id.ll_mine_car:
                CartActivity.open(getActivity());
                break;
        }
    }
}
