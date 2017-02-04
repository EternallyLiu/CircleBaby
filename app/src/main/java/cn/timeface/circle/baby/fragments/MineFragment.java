package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CartActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MineBookActivity;
import cn.timeface.circle.baby.activities.MineInfoActivity;
import cn.timeface.circle.baby.activities.OrderListActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
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

    public MineFragment() {
    }

    public static MineFragment newInstance(String param1) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void initData() {
        GlideUtil.displayImage(FastData.getAvatar(), ivAvatar, R.drawable.ic_launcher);
        tvName.setText(FastData.getUserName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        GlideUtil.displayImageCircle(babyObj.getAvatar(), imageView);
        return imageView;

    }

    @Override
    public void onResume() {
        initData();
        LogUtil.showLog("mainThread:" + Thread.currentThread().getName());
        addSubscription(BabyObj.getCurrentUserBabyObjs()
                .subscribeOn(Schedulers.io())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (llBaby.getChildCount() > 0)
                        llBaby.removeAllViews();
                    for (BabyObj babyObj : list) {
                        llBaby.addView(getBabyImageView(babyObj));
                    }
                }));
        super.onResume();
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
                MineBookActivity.open(getActivity());
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
