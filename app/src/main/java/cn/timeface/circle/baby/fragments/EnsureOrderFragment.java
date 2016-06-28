package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.api.models.objs.AddressObj;
import cn.timeface.circle.baby.api.models.objs.BookObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.api.models.responses.LessResponse;
import cn.timeface.circle.baby.events.AddressEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.payment.OrderInfoObj;
import cn.timeface.circle.baby.payment.PaymentFactory;
import cn.timeface.circle.baby.payment.PrepareOrderException;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.greenrobot.event.Subscribe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EnsureOrderFragment extends BaseFragment implements View.OnClickListener, IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_addaddress)
    RelativeLayout rlAddaddress;
    @Bind(R.id.express)
    TextView express;
    @Bind(R.id.tv_express1)
    TextView tvExpress1;
    @Bind(R.id.tv_express2)
    TextView tvExpress2;
    @Bind(R.id.rl_express)
    RelativeLayout rlExpress;
    @Bind(R.id.iv_book)
    ImageView ivBook;
    @Bind(R.id.tv_size)
    TextView tvSize;
    @Bind(R.id.ll_size)
    LinearLayout llSize;
    @Bind(R.id.color)
    TextView color;
    @Bind(R.id.tv_color)
    TextView tvColor;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.rl_color)
    RelativeLayout rlColor;
    @Bind(R.id.paper)
    TextView paper;
    @Bind(R.id.tv_paper)
    TextView tvPaper;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.rl_paper)
    RelativeLayout rlPaper;
    @Bind(R.id.tv_bind)
    TextView tvBind;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;
    @Bind(R.id.all)
    TextView all;
    @Bind(R.id.rmb)
    TextView rmb;
    @Bind(R.id.tv_price2)
    TextView tvPrice2;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tvname)
    TextView tvname;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.rl_address)
    RelativeLayout rlAddress;

    private BookObj bookObj;
    private MineBookObj mineBookObj;
    private int expressId;
    private AddressObj addressobj;
    private LessResponse lessResponse;

    public EnsureOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookObj = getArguments().getParcelable("BookObj");
        mineBookObj = getArguments().getParcelable("MineBookObj");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ensureorder, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        return view;
    }

    private void initData() {
        GlideUtil.displayImage(mineBookObj.getCoverImage(), ivBook);
        tvExpress1.setSelected(true);
        tvName.setText(mineBookObj.getTitle());
        tvSize.setText(bookObj.getSize());
        tvColor.setText(bookObj.getColor());
        tvPaper.setText(bookObj.getPaper());
        tvBind.setText(bookObj.getPack());
        tvCount.setText(bookObj.getNum() + "");


        rlAddaddress.setOnClickListener(this);
        tvExpress1.setOnClickListener(this);
        tvExpress2.setOnClickListener(this);
        tvSubmitOrder.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_addaddress:
                FragmentBridgeActivity.open(getContext(), "SelectAddressFragment");
                break;
            case R.id.tv_express1:
                tvExpress1.setSelected(true);
                tvExpress2.setSelected(false);
                expressId = 0;
                break;
            case R.id.tv_express2:
                tvExpress1.setSelected(false);
                tvExpress2.setSelected(true);
                expressId = 1;
                break;
            case R.id.tv_submit_order:
                if (TextUtils.isEmpty(tvAddress.getText().toString())) {
                    ToastUtil.showToast("请完善收货地址");
                    return;
                }
                bookObj.setBookType(mineBookObj.getType());
                ArrayList<BookObj> dataList = new ArrayList<>();
                dataList.add(bookObj);
                Gson gson = new Gson();
                String s = gson.toJson(dataList);

                apiService.addOrder(Integer.valueOf(addressobj.getId()), URLEncoder.encode(s), expressId,0)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(lessResponse -> {
                            if (lessResponse.success()) {
                                this.lessResponse = lessResponse;
                                startPayment();
                            } else {
                                ToastUtil.showToast(lessResponse.getInfo());
                            }
                        }, error -> {
                            Log.e(TAG, "addOrderNew:");
                        });
                break;
        }
    }

    private void startPayment() {
        OrderInfoObj orderInfoObj = new OrderInfoObj();
        orderInfoObj.setTradeNo(lessResponse.getOrderId());
        orderInfoObj.setPrice(lessResponse.getPrice()*bookObj.getNum());
        orderInfoObj.setSubject(mineBookObj.getTitle());
        orderInfoObj.setBody(mineBookObj.getTitle());
        try {
            PaymentFactory.newInstance(0).requestPayment(getActivity(), orderInfoObj);
        } catch (PrepareOrderException e) {
            Log.e(TAG, "startPayment: ", e);
        }
    }

        @Subscribe
        public void onEvent (Object event){
            if (event instanceof AddressEvent) {
                addressobj = ((AddressEvent) event).getObj();
                rlAddress.setVisibility(View.VISIBLE);
                tvname.setText(addressobj.getContacts());
                tvPhone.setText(addressobj.getContactsPhone());
                tvAddress.setText(addressobj.getAddress());
            }
        }
    }
