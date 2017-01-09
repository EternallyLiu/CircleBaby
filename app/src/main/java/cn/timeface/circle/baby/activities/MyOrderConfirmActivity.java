package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MyOrderConfirmAdapter;
import cn.timeface.circle.baby.adapters.OrderDispatchAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.constants.URLConstant;
import cn.timeface.circle.baby.dialogs.SelectPayWayDialog;
import cn.timeface.circle.baby.events.AddAddressFinishEvent;
import cn.timeface.circle.baby.events.CartCouponCodeEvent;
import cn.timeface.circle.baby.events.OrderCancelEvent;
import cn.timeface.circle.baby.events.PayResultEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.payment.alipay.AliPay;
import cn.timeface.circle.baby.support.api.models.AddressItem;
import cn.timeface.circle.baby.support.api.models.CouponItem;
import cn.timeface.circle.baby.support.api.models.DistrictModel;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.support.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.support.api.models.responses.PrintFullSiteCouponObj;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.ErrorViewContent;
import cn.timeface.circle.baby.views.HttpStatusCodes;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * @author WXW
 * @from 2015/5/21
 * @TODO 确认订单
 */
public class MyOrderConfirmActivity extends BaseAppCompatActivity implements IEventBus {

    private static final int REQUEST_CODE_SCAN_PV_CODE = 1001;
    @Bind(R.id.tv_order_amount)
    TextView mOrderAmount;
    @Bind(R.id.btn_submit_order)
    Button mBtnSubmit;

    private int COUPON_CODE_LENGTH = 8;//印书码长度
    private float POINT_USE_RATIO = 0.5f; //积分使用比例，如0.5

    public static boolean isOpen = false;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.stateView)
    TFStateView mStateView;
    @Bind(R.id.pull_refresh_list)
    RecyclerView mPullRefreshList;

    private View headerView;
    private View footerView;

    private TextView mReceiver;
    private TextView mReceiverPhone;
    private TextView mReceiverAddress;

    private RelativeLayout mAddAddressLayout;
    private RelativeLayout mAddressInfoLayout;
    private RecyclerView mRvDispatch;
    private OrderDispatchAdapter mRvDispatchAdapter;
    private LinearLayout mDispatchSelectLayout;

    private RelativeLayout rlPVCode;
    private TextView tvPVCodeDesc;
    private TextView tvPVCodeCancel;
    private ImageView ivArrow;
    private RelativeLayout mRlFullSiteCoupon;//全站优惠信息
    private TextView mTvFullSiteCoupon;
    private TextView mTvFullSiteCouponMoney;

    private LinearLayout llUsePointRoot;
    private LinearLayout llUseCouponsRoot;
    private LinearLayout llUseCouponCodesRoot;

    private CheckBox mIsUsePoints;
    private LinearLayout mUsePointsLayout;
    private EditText mExChangePoints;
    private TextView mExChangedPints;
    private TextView mPointUse;
    private CheckBox mServiceItem;
    // 印书券
    private CheckBox cbUseCoupons;
    private LinearLayout llUseCoupons;
    private Spinner spinnerCoupons;
    private CheckBox cbUseCouponCodes;
    private LinearLayout llUseCouponCodes;
    private EditText etCouponCodes;
    private TextView tvCouponCodes;

    private List<MyOrderBookItem> dataList = new ArrayList<>();
    private MyOrderConfirmAdapter adapter;

    private String orderId = "";
    private String addressId = "";
    private int dispatchPosition;
    private float expressPrice; //快递费用
    private float bookPrice; //书本总额
    private float orderPrice; //订单总额
    private float pointPrice = 0; //使用积分抵用的钱
    //    private float usePointMax;//使用积分最大额度
    private int usePointMax;//使用积分最大额度
    private boolean isUsePoint = false;
    private int userPoint; //用户当前积分
    private int exchangeRate; //积分兑换钱得兑换率，如100
    private PrintParamResponse dispatchList; // 快递信息
    //    private PrintCouponObj couponObj; // 印书券
    //    private PrintCouponObj couponCodeObj; // 印书码
    private CouponItem couponObj; // 印书券
    private CouponItem couponCodeObj; // 印书码
    private CouponItem pvCodeObj; // 商家优惠码
    private PrintFullSiteCouponObj fullSiteCouponObj; //全站优惠信息
    private boolean fullSiteCouponEnable; //是否使用全站优惠
    private int original = 0;

    private TFProgressDialog progressDialog;

    private int orderStatus;//订单状态
    private String orderSummary = "";//订单描述
    private Timer timer = new Timer(true);
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (progressDialog != null) progressDialog.dismiss();
                    PaySuccessActivity.open(MyOrderConfirmActivity.this, orderId,
                            orderPrice, orderStatus, orderSummary);
                    finish();
                    break;
                case 2:
                    if (orderStatus == TypeConstant.STATUS_CHECKING) {
                        timer.cancel();
                        progressDialog.dismiss();
                        PaySuccessActivity.open(MyOrderConfirmActivity.this, orderId,
                                orderPrice, orderStatus, orderSummary);
                        finish();
                    }
                    break;
            }
        }
    };
    private List<PrintPropertyTypeObj> baseObjs;
    private int expressId;

    public static void open(Context context, String orderId) {
        if (!isOpen) {
            Intent intent = new Intent(context, MyOrderConfirmActivity.class);
            intent.putExtra("orderId", orderId);
            context.startActivity(intent);
            isOpen = true;
        }
    }

    public static void open(Context context, String orderId, List<PrintPropertyTypeObj> baseObjs) {
        if (!isOpen) {
            Intent intent = new Intent(context, MyOrderConfirmActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("baseObjs", (Serializable) baseObjs);
            context.startActivity(intent);
            isOpen = true;
        }
    }

    public static void open(Context context, String orderId, int original) {
        if (!isOpen) {
            Intent intent = new Intent(context, MyOrderConfirmActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("original", original);
            context.startActivity(intent);
            isOpen = true;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_confirm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderId = getIntent().getStringExtra("orderId");
        original = getIntent().getIntExtra("original", 0);
        baseObjs = (List<PrintPropertyTypeObj>) getIntent().getSerializableExtra("baseObjs");
        Log.i("-------->", "orderId:" + orderId);
        progressDialog = TFProgressDialog.getInstance("");

        setupHeaderAndFooter();

        mStateView.setOnRetryListener(() -> reqData(orderId));

        reqData(orderId);
    }

    private void setupHeaderAndFooter() {
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_my_order_confirm_header, null);

        mAddAddressLayout = ButterKnife.findById(headerView, R.id.book_delivery_address_add_rl);
        mAddressInfoLayout = ButterKnife.findById(headerView, R.id.book_delivery_address_info_rl);
        mReceiver = ButterKnife.findById(headerView, R.id.book_recevier_tv);
        mReceiverPhone = ButterKnife.findById(headerView, R.id.book_receiver_phone_tv);
        mReceiverAddress = ButterKnife.findById(headerView, R.id.book_receiver_address_tv);

        footerView = LayoutInflater.from(this).inflate(R.layout.layout_my_order_confirm_footer, null);

        rlPVCode = ButterKnife.findById(footerView, R.id.rl_pv_code);
        tvPVCodeDesc = ButterKnife.findById(footerView, R.id.tv_pv_code_desc);
        tvPVCodeCancel = ButterKnife.findById(footerView, R.id.tv_pv_code_cancel);
        ivArrow = ButterKnife.findById(footerView, R.id.ivArrow);

        llUsePointRoot = ButterKnife.findById(footerView, R.id.ll_use_point_root);
        llUsePointRoot.setVisibility(View.GONE);
        llUseCouponsRoot = ButterKnife.findById(footerView, R.id.ll_use_coupons_root);
        llUseCouponsRoot.setVisibility(View.GONE);
        llUseCouponCodesRoot = ButterKnife.findById(footerView, R.id.ll_use_coupon_codes_root);
        llUseCouponCodesRoot.setVisibility(View.GONE);

        mRvDispatch = ButterKnife.findById(headerView, R.id.rv_dispatch);
        mRvDispatchAdapter = new OrderDispatchAdapter(this);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(MyOrderConfirmActivity.this, LinearLayoutManager.HORIZONTAL);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.size_12));
        mRvDispatch.addItemDecoration(itemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvDispatch.setLayoutManager(layoutManager);
        mRvDispatch.setAdapter(mRvDispatchAdapter);
        mDispatchSelectLayout = ButterKnife.findById(headerView, R.id.ll_dispatch_select);

        mRlFullSiteCoupon = ButterKnife.findById(footerView, R.id.rl_full_site_coupon);//全站优惠信息
        mTvFullSiteCoupon = ButterKnife.findById(footerView, R.id.tv_full_site_coupon);
        mTvFullSiteCouponMoney = ButterKnife.findById(footerView, R.id.tv_full_site_coupon_money);
        mIsUsePoints = ButterKnife.findById(footerView, R.id.cb_use_point);
        mUsePointsLayout = ButterKnife.findById(footerView, R.id.ll_use_points_policy);
        mExChangePoints = ButterKnife.findById(footerView, R.id.et_exchange_points);
        mExChangedPints = ButterKnife.findById(footerView, R.id.tv_points_replace);
        mPointUse = ButterKnife.findById(footerView, R.id.tv_use_point_policy);
        mServiceItem = ButterKnife.findById(footerView, R.id.cb_agree_service);

        cbUseCoupons = ButterKnife.findById(footerView, R.id.cb_use_coupons);
        llUseCoupons = ButterKnife.findById(footerView, R.id.ll_use_coupons_policy);
        spinnerCoupons = ButterKnife.findById(footerView, R.id.spinner_use_coupon);
        cbUseCouponCodes = ButterKnife.findById(footerView, R.id.cb_use_coupon_codes);
        llUseCouponCodes = ButterKnife.findById(footerView, R.id.ll_use_coupon_codes_policy);
        etCouponCodes = ButterKnife.findById(footerView, R.id.et_use_coupon_codes);
        tvCouponCodes = ButterKnife.findById(footerView, R.id.tv_use_coupon_codes);
    }

    private void reqData(String orderId) {
        if (adapter == null || adapter.getCount() == 0) {
            mStateView.loading();
        }

        Subscription s = apiService.findOrderDetail(orderId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            mStateView.finish();
                            if (response.success()) {
                                List<PrintParamObj> valueList = response.getDispatchObject().getValueList();
                                for (int i = 0; i < valueList.size(); i++) {
                                    if (valueList.get(i).getValue().equals(response.getDispatch())) {
                                        dispatchPosition = i;
                                        break;
                                    }
                                }
                                mRvDispatch.scrollToPosition(dispatchPosition);
                                setupData(response);
                                List<MyOrderBookItem> items = response.getBookList();
                                if (items != null && items.size() > 0) {
                                    dataList.clear();
                                    dataList.addAll(items);

                                    adapter = new MyOrderConfirmAdapter(MyOrderConfirmActivity.this, dataList);
                                    mPullRefreshList.setLayoutManager(
                                            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                                    adapter.addHeader(headerView);
                                    adapter.addFooter(footerView);

                                    mPullRefreshList.setAdapter(adapter);
                                }
                                if (dataList == null || dataList.size() == 0) {
                                    mStateView.setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_MESSAGE));
                                    mStateView.setTitle(getResources().getString(R.string.no_list_data));
                                }
                            }
                        },
                        throwable -> {
                            mStateView.showException(throwable);
                        }
                );
        addSubscription(s);
    }

    private void setupData(MyOrderConfirmListResponse response) {
        fullSiteCouponObj = response.getFullSiteCoupon();
        setupFullSiteCoupon(response);

//        dispatchWay = response.getDispatch();
        dispatchList = response.getDispatchObject();

        if (dispatchList.getValueList() != null && dispatchList.getValueList().size() > 0) {
            String[] showArray = dispatchList.getValueList().get(0).getShow().split(",");
            expressPrice = Float.parseFloat(showArray[1]);
        }

        addressId = response.getAddressId() == 0 ? "" : response.getAddressId() + "";
        // 收货地址
        if (TextUtils.isEmpty(response.getAddress()) || TextUtils.isEmpty(addressId)) {
            mAddAddressLayout.setVisibility(View.VISIBLE);
            mAddressInfoLayout.setVisibility(View.GONE);
            mDispatchSelectLayout.setVisibility(View.GONE); // 选择快递方式
            mReceiverPhone.setText(response.getContactsPhone());
            mReceiverAddress.setText(response.getAddress());
            mReceiver.setText(response.getContacts());
            expressPrice = 0;
        } else {
            mAddAddressLayout.setVisibility(View.GONE);
            mAddressInfoLayout.setVisibility(View.VISIBLE);
            mDispatchSelectLayout.setVisibility(View.VISIBLE); // 选择快递方式
            mReceiverAddress.setText(response.getAddress());
            mReceiverPhone.setText(response.getContactsPhone());
            mReceiver.setText(response.getContacts());

            mRvDispatchAdapter.setDataList(response.getDispatchObject().getValueList());
            mRvDispatchAdapter.notifyDate(dispatchPosition);
        }

        userPoint = response.getPoints();
        exchangeRate = response.getExchangeRate();

//        bookPrice = 0;
//        for (MyOrderBookItem item : response.getBookList()) {
//            bookPrice += item.getPrice();
//        }
        bookPrice = response.getOrderPrice();

        // 台历优惠活动
        /*if (response.hasPromotion() && response.getPromotionFee() > 0) {
            mTvPromotionInfo.setText(String.format(getString(R.string.cart_promotion_fee),
                    "台历", response.getPromotionFee()));
            mTvPromotionInfo.setVisibility(View.VISIBLE);
        } else {
            mTvPromotionInfo.setVisibility(View.GONE);
        }*/

        mExChangePoints.addTextChangedListener(new EditTextWatcher());
        mIsUsePoints.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isUsePoint = isChecked;
            if (isChecked) {
                mUsePointsLayout.setVisibility(View.VISIBLE);
            } else {
                mUsePointsLayout.setVisibility(View.GONE);
            }
//                updateUsePoints(bookPrice);//取消印书券印书码与积分的关联
            updateTotalPrice();
        });

        cbUseCoupons.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                llUseCoupons.setVisibility(View.VISIBLE);
                cbUseCouponCodes.setChecked(false);
                if (couponObj == null) {
                    reqCouponList();
                }
                disableFullSiteCoupon(fullSiteCouponObj.getDisableDesc(false));
            } else {
                llUseCoupons.setVisibility(View.GONE);
                setupFullSiteCoupon(response);
            }
            updateTotalPrice();
        });
        cbUseCouponCodes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                llUseCouponCodes.setVisibility(View.VISIBLE);
                cbUseCoupons.setChecked(false);
                if (couponCodeObj != null) {
                    etCouponCodes.setText(couponCodeObj.getCouponCode());
                }
                disableFullSiteCoupon(fullSiteCouponObj.getDisableDesc(true));
            } else {
                llUseCouponCodes.setVisibility(View.GONE);
                setupFullSiteCoupon(response);
            }
            updateTotalPrice();
        });

        etCouponCodes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= COUPON_CODE_LENGTH) {
                    Utils.hideSoftInput(MyOrderConfirmActivity.this);
                    reqCouponCodes(etCouponCodes.getText().toString());
                }
            }
        });

        updateTotalPrice();
    }

    // 设置全站优惠信息
    private void setupFullSiteCoupon(PrintFullSiteCouponObj fullSiteCouponObj) {
        if (fullSiteCouponObj != null && fullSiteCouponObj.getCoupon() != 0) {
            if (fullSiteCouponObj.getCouponValue() > 0) {//满足全站优惠条件
                fullSiteCouponEnable = true;
                mTvFullSiteCoupon.setText(TextUtils.isEmpty(fullSiteCouponObj.getDiscountDesc()) ?
                        fullSiteCouponObj.getCouponDesc() :
                        fullSiteCouponObj.getCouponDesc() + "\n" + fullSiteCouponObj.getDiscountDesc());

                mTvFullSiteCouponMoney.setText(String.format(getString(R.string.full_site_coupon),
                        fullSiteCouponObj.getCouponValue()));
                mTvFullSiteCouponMoney.setVisibility(View.VISIBLE);
//                mRlFullSiteCoupon.setVisibility(View.VISIBLE);
                mRlFullSiteCoupon.setVisibility(View.GONE);
            } else {//不满足全站优惠条件，显示优惠条件
                fullSiteCouponEnable = false;
                mTvFullSiteCoupon.setText(TextUtils.isEmpty(fullSiteCouponObj.getDiscountDesc()) ?
                        "" : fullSiteCouponObj.getDiscountDesc());

                mTvFullSiteCouponMoney.setVisibility(View.GONE);
//                mRlFullSiteCoupon.setVisibility(View.VISIBLE);
                mRlFullSiteCoupon.setVisibility(View.GONE);
            }
        } else {
            fullSiteCouponEnable = false;
            mRlFullSiteCoupon.setVisibility(View.GONE);
        }
    }

    // 设置全站优惠信息
    private void setupFullSiteCoupon(MyOrderConfirmListResponse response) {
        String discountTitle = response.getDiscountTitle();
        float discountPrice = response.getDiscountPrice();
        if (!TextUtils.isEmpty(discountTitle) && discountPrice != 0) {

            mRlFullSiteCoupon.setVisibility(View.VISIBLE);
            mTvFullSiteCoupon.setText(discountTitle);
            mTvFullSiteCouponMoney.setText(String.format(getString(R.string.full_site_coupon), discountPrice));
        } else {
            mRlFullSiteCoupon.setVisibility(View.GONE);
        }
    }

    private void disableFullSiteCoupon(String cause) {
        fullSiteCouponEnable = false;
        if (fullSiteCouponObj != null) {
            if (fullSiteCouponObj.getCouponValue() > 0) {
                //满足全站优惠条件时使用了其他优惠则显示不能同时享受，不满足优惠条件时仍然继续显示优惠条件
                mTvFullSiteCoupon.setText(cause);

                mTvFullSiteCouponMoney.setVisibility(View.GONE);
//                mRlFullSiteCoupon.setVisibility(View.VISIBLE);
                mRlFullSiteCoupon.setVisibility(View.GONE);
            }
        } else {
            mRlFullSiteCoupon.setVisibility(View.GONE);
        }
    }

    private void setupSpinnerData(final List<CouponItem> couponList) {
//        if (couponList == null || couponList.size() == 0) {
//            Toast.makeText(this, "暂无印书券", Toast.LENGTH_SHORT).show();
//            return;
//        }
        final List<String> list = new ArrayList<>();
//        list.add("请选择印书券");
//        if (couponList != null || couponList.size() > 0) {
//            for (PrintCouponObj obj : couponList) {
//                list.add(obj.getParvalue() + "元");
//            }
//        }
        if (couponList == null || couponList.size() == 0) {
            list.add("您没有印书券啦");
        } else {
//            list.add("请选择印书券");
            list.add("不选择印书券");
            for (int i = 0; i < couponList.size(); i++) {
                if (couponList.get(i).canDiscount(bookPrice)) {
                    list.add(couponList.get(i).getDiscountString());
                } else {
                    couponList.remove(i);
                }
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.item_spinner_checked_text_print_coupon, list) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        convertView = LayoutInflater.from(MyOrderConfirmActivity.this)
                                .inflate(R.layout.item_spinner_dropdown_print_coupon, parent, false);
                        ((TextView) convertView).setText(list.get(position));
                        return convertView;
                    }
                };
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_print_coupon);
        spinnerCoupons.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position <= 0) {
                    couponObj = null;//第一个Item为不选择印书券
                } else {
                    couponObj = (couponList != null && couponList.size() > position - 1) ?
                            couponList.get(position - 1) : null;
                }
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 条数大于等于8时 设置Spinner Dropdown的高度
        if (list.size() >= 8) {
            setDropDownHeight(DeviceUtil.dpToPx(getResources(), 256), spinnerCoupons);//256=32*8
        }
        spinnerCoupons.setAdapter(adapter);
    }

    /**
     * 设置Spinner Dropdown的高度
     */
    private void setDropDownHeight(int pHeight, Spinner mSpinner) {
        try {
            Field field = Spinner.class.getDeclaredField("mPopup");
            field.setAccessible(true);
            ListPopupWindow popUp = (ListPopupWindow) field.get(mSpinner);
            popUp.setHeight(pHeight);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.book_delivery_address_info_rl:
                SelectReceiverAddActivity.open(this, addressId);
                break;
            case R.id.book_delivery_address_add_rl:
                SelectReceiverAddActivity.open(this, addressId);
                break;
            case R.id.btn_submit_order:
                if (mAddAddressLayout.getVisibility() == View.VISIBLE) {
                    Toast.makeText(this,
                            getString(R.string.please_add_delivery_address),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mServiceItem.isChecked()) {
                    Toast.makeText(this, getString(R.string.please_read_service), Toast.LENGTH_SHORT).show();
                    return;
                }
//                updateTotalPrice();
                try {
                    reqApplyOrder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.accept_service_tv:
                /*WebViewActivity.open(this, URLConstant.PAY_SERVICE_AGREEMENT, getString(R.string.terms_of_service), false);*/
                FragmentBridgeActivity.openWebViewFragment(this, URLConstant.PAY_SERVICE_AGREEMENT, "服务条款");
                break;
            case R.id.ll_dispatch:
                int p = (int) view.getTag(R.string.tag_index);
                dispatchPosition = p;
                float fee = (float) view.getTag(R.string.tag_ex);
                expressPrice = fee;
                setDispatchEnable(p);
                updateTotalPrice();
                mRvDispatchAdapter.notifyDate(p);
                break;
        }
    }

    private void setDispatchEnable(int position) {
        for (int i = 0; i < mRvDispatch.getLayoutManager().getChildCount(); i++) {
            LinearLayout view = (LinearLayout) mRvDispatch.getLayoutManager().getChildAt(i);
            for (int j = 0; j < view.getChildCount(); j++) {
                if (i == position) {
                    view.setBackgroundResource(R.drawable.shape_dash_orange_bg);
                    view.getChildAt(j).setSelected(true);
                } else {
                    view.setBackgroundResource(R.drawable.shape_grey_dash_border_bg);
                    view.getChildAt(j).setSelected(false);
                }
            }
        }
    }

    /**
     * 更新用户积分使用信息
     */
    private void updateUsePoints(float currentPrice) {
        currentPrice = currentPrice > 0 ? currentPrice : 0;
//        usePointMax = Math.round(currentPrice * exchangeRate);
        usePointMax = Math.round(currentPrice * exchangeRate * POINT_USE_RATIO);
//        if (isUsePoint) {
        usePointMax = usePointMax > userPoint ? userPoint : usePointMax;
//        } else {
//            usePointMax = 0;
//        }
        if (userPoint >= usePointMax) {
            mPointUse.setText(String.format(getString(R.string.my_points_use_policy), (float) userPoint, (float) usePointMax));
            mExChangePoints.setText(String.valueOf(usePointMax));
            mExChangedPints.setText(String.format(getString(R.string.points_exchanged), (float) usePointMax / exchangeRate));
        } else {
            mPointUse.setText(String.format(getString(R.string.my_points_use_policy), (float) userPoint, (float) userPoint));
            mExChangePoints.setText(String.valueOf(userPoint));
            mExChangedPints.setText(String.format(getString(R.string.points_exchanged), (float) userPoint / exchangeRate));
        }
    }

    private void reqExpressFee() {
        if (!TextUtils.isEmpty(addressId) && dataList != null) {
            progressDialog.setTvMessage(getString(R.string.loading));
            progressDialog.show(getSupportFragmentManager(), "");

            Subscription s = apiService.queryDispatchList(orderId, addressId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(
                            response -> {
                                progressDialog.dismiss();
                                if (response.success()) {
                                    dispatchList = response.getProperty("dispatch");

                                    // 判断是否为现场配送
                                    setupSpotDispacth(pvCodeObj != null, dispatchList);
                                    updateTotalPrice();
                                }
                            }
                            , throwable -> progressDialog.dismiss()
                    );
            addSubscription(s);
        }
    }

    // 更新快递数据
    private void updateDispatchData(PrintParamResponse dispatchList) {
        if (dispatchList != null) {
            mRvDispatchAdapter.setDataList(dispatchList.getValueList());
            mRvDispatchAdapter.notifyDate(dispatchPosition);
            String[] showArray = dispatchList.getValueList().get(dispatchPosition).getShow().split(",");
            expressPrice = Float.parseFloat(showArray[1]);
        }
    }

    // 获取普通快递DispatchWay
//    private int getCommonDispatchWay(PrintParamResponse dispatchObj) {
//        if (dispatchObj != null && dispatchObj.getValueList() != null && dispatchObj.getValueList().size() > 0) {
//            for (PrintParamObj obj : dispatchObj.getValueList()) {
//                if (!obj.getValue().equals("1")) {
//                    return Integer.valueOf(obj.getValue());
//                }
//            }
//        }
//        return 0;
//    }

//    private void showExpressFee(PrintParamResponse dispatchObj, String dispatchWay) {
//        if (dispatchObj != null && dispatchObj.getValueList() != null && dispatchObj.getValueList().size() > 0) {
//            for (PrintParamObj obj : dispatchObj.getValueList()) {
//                String[] array = obj.getShow().split(",");
//                float fee = Float.valueOf(array.length > 1 ? array[array.length - 1] : "0");
//                if (obj.getValue().equals("1")) {
//                    mDispatchSfFee.setTextAndEvent(String.format(getString(R.string.total_price), fee));
//                } else if (obj.getValue().equals(commonDispatchWay + "")) {
//                    mDispatchCommonFee.setTextAndEvent(String.format(getString(R.string.total_price), fee));
//                }
//                if (obj.getValue().equals(dispatchWay)) {
//                    expressPrice = fee;
//                }
//            }
//        }
//    }

    // 获取优惠CouponId
    private String getUseCouponId() {
        if (fullSiteCouponEnable && fullSiteCouponObj != null) {
            return fullSiteCouponObj.getCoupon() + "";
        } else if (couponObj != null && cbUseCoupons.isChecked()) {
            return couponObj.getId() + "";
        } else if (couponCodeObj != null && cbUseCouponCodes.isChecked()) {
            return couponCodeObj.getId() + "";
        } else if (pvCodeObj != null && !cbUseCoupons.isChecked() && !cbUseCouponCodes.isChecked()) {
            return pvCodeObj.getId() + "";
        }
        return "";
    }

    // 获取优惠CouponPersonType
    private String getUseCouponPersonType() {
        if (fullSiteCouponEnable && fullSiteCouponObj != null) {
            return fullSiteCouponObj.getPersonType() + "";
        } else if (couponObj != null && cbUseCoupons.isChecked()) {
            return couponObj.getPersonType() + "";
        } else if (couponCodeObj != null && cbUseCouponCodes.isChecked()) {
            return couponCodeObj.getPersonType() + "";
        } else if (pvCodeObj != null && !cbUseCoupons.isChecked() && !cbUseCouponCodes.isChecked()) {
            return pvCodeObj.getPersonType() + "";
        }
        return "";
    }

    // 获取优惠CouponType
    private String getUseCouponType() {
        if (fullSiteCouponEnable && fullSiteCouponObj != null) {
            return fullSiteCouponObj.getCouponType() + "";
        } else if (couponObj != null && cbUseCoupons.isChecked()) {
            return couponObj.getCouponType() + "";
        } else if (couponCodeObj != null && cbUseCouponCodes.isChecked()) {
            return couponCodeObj.getCouponType() + "";
        } else if (pvCodeObj != null && !cbUseCoupons.isChecked() && !cbUseCouponCodes.isChecked()) {
            return pvCodeObj.getCouponType() + "";
        }
        return "";
    }

    // 获取优惠金额
    private float getUseCouponsValue(float bookPrice) {
        if (fullSiteCouponEnable && fullSiteCouponObj != null) {
            return fullSiteCouponObj.getCouponValue();
        } else if (couponObj != null && cbUseCoupons.isChecked()) {
            return couponObj.getDiscountFloat(bookPrice);
        } else if (couponCodeObj != null && cbUseCouponCodes.isChecked()) {
            return couponCodeObj.getDiscountFloat(bookPrice);
        } else if (pvCodeObj != null && !cbUseCoupons.isChecked() && !cbUseCouponCodes.isChecked()) {
            return pvCodeObj.getDiscountFloat(bookPrice);
        }
        return 0;
    }

    private void updateTotalPrice() {
        float parvalue = getUseCouponsValue(bookPrice);
        updateUsePoints(bookPrice - parvalue);//优先使用印书券印书码 积分不可抵用快递费
        if (isUsePoint) {
//            orderPrice = bookPrice + expressPrice - parvalue - pointPrice;//积分可抵用快递费
            orderPrice = bookPrice - parvalue - pointPrice;//积分不可抵用快递费
        } else {
//            orderPrice = bookPrice + expressPrice - parvalue;//积分可抵用快递费
            orderPrice = bookPrice - parvalue;//积分不可抵用快递费
        }
        //积分不可抵用快递费
        orderPrice = (orderPrice > 0 ? orderPrice : 0) + expressPrice;
        mOrderAmount.setText(String.format(getString(R.string.total_price), orderPrice));
    }

    public void clickScanner(View view) {
        /*CaptureActivity.open(this, CaptureActivity.OPEN_TYPE_COUPON_CODE);*/
    }

    public void clickFullSiteCoupon(View view) {
//        if (!fullSiteCouponEnable) {
//            cbUseCoupons.setChecked(false);
//            cbUseCouponCodes.setChecked(false);
//            setupFullSiteCoupon(fullSiteCouponObj);
//            updateTotalPrice();
//        }
    }

    // 使用优惠码
    public void clickPVCode(View view) {
        /*PVCodeActivity.open4Result(this, REQUEST_CODE_SCAN_PV_CODE);*/
    }

    // 取消使用优惠码
    public void clickCancelPVCode(View view) {
        pvCodeObj = null;
        setPVCodeEnable(false);
        updateTotalPrice();
    }

    private void setPVCodeEnable(boolean enable) {
        rlPVCode.setVisibility(View.GONE);
        ivArrow.setVisibility(enable ? View.GONE : View.VISIBLE);
        tvPVCodeDesc.setVisibility(enable ? View.VISIBLE : View.GONE);
        tvPVCodeDesc.setText(enable && pvCodeObj != null ?
                "优惠 ¥" + pvCodeObj.getDiscountString() : "");
        tvPVCodeCancel.setVisibility(enable ? View.VISIBLE : View.GONE);
        rlPVCode.setClickable(!enable);

        llUsePointRoot.setEnabled(!enable);
        mIsUsePoints.setEnabled(!enable);
        mIsUsePoints.setChecked(false);
        llUseCouponsRoot.setEnabled(!enable);
        cbUseCoupons.setChecked(false);
        cbUseCoupons.setEnabled(!enable);
        llUseCouponCodesRoot.setEnabled(!enable);
        cbUseCouponCodes.setChecked(false);
        cbUseCouponCodes.setEnabled(!enable);

        setupSpotDispacth(enable, dispatchList);
    }

    //设置现场配送
    private void setupSpotDispacth(boolean enable, PrintParamResponse dispatchList) {
        if (enable && pvCodeObj != null && pvCodeObj.isSpotDispatch()) {
            // 设置现场配送
            PrintParamResponse spotDispatch = new PrintParamResponse();
            List<PrintParamObj> valueList = new ArrayList<>(1);
            PrintParamObj printParamObj = new PrintParamObj();
            printParamObj.setValue(pvCodeObj.getSpotDispatch() + "");
            printParamObj.setShow("现场配送,0.0");
            valueList.add(printParamObj);
            spotDispatch.setName("快递列表");
            spotDispatch.setKey("dispatch");

            valueList.addAll(dispatchList.getValueList());
            spotDispatch.setValueList(valueList);

            dispatchPosition = 0;//选择第一条Item
            updateDispatchData(spotDispatch);
        } else {
            dispatchPosition = 0;//选择第一条Item
            updateDispatchData(dispatchList);
        }
    }

    /**
     * 查询用户下可使用的印书券信息
     */
    private void reqCouponList() {
        /*progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        Subscription s = apiServiceV2.couponsList("1")// 1:查询可用的
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response == null)
                                return;
                            if (response.success()) {
                                setupSpinnerData(response.getDataList());
                            } else {
                                Toast.makeText(MyOrderConfirmActivity.this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> progressDialog.dismiss()
                );
        addSubscription(s);*/
    }

    /**
     * 印书券校验
     */
    private void reqCouponCodes(String couponCodes) {
        /*progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        Subscription s = apiServiceV2.checkCoupon(couponCodes)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if (response.success()) {
                        couponCodeObj = response.getCouponDto();
                        tvCouponCodes.setTextColor(getResources().getColor(R.color.text_color9));
                        tvCouponCodes.setText(couponCodeObj.getDiscountString());
                        updateTotalPrice();
                    } else {
                        couponCodeObj = null;
                        tvCouponCodes.setTextColor(getResources().getColor(R.color.bg27));
                        tvCouponCodes.setText(response.info);
//                            tvCouponCodes.setTextAndEvent("0元");
//                            Toast.makeText(MyOrderConfirmActivity.this, response.info, Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    progressDialog.dismiss();
                });
        addSubscription(s);*/
    }

    /**
     * 提交订单
     */
    private void reqApplyOrder() throws IOException {
//        int pointsExchange = 0;
//        if (isUsePoint) {
//            pointsExchange = Integer.valueOf(mExChangePoints.getText().toString());
//        }
        expressId = Integer.valueOf(mRvDispatchAdapter.getDataList().get(dispatchPosition).getValue());

        progressDialog.setTvMessage(getString(R.string.apply_order));
        progressDialog.show(getSupportFragmentManager(), "");

//        for(PrintPropertyTypeObj baseObj : baseObjs){
//            baseObj.setAddressId(Integer.valueOf(addressId));
//            baseObj.setExpressId(Integer.valueOf(mRvDispatchAdapter.getDataList().get(dispatchPosition).getValue()));
//        }

        Subscription s = apiService.addOrder(Integer.valueOf(addressId), LoganSquare.serialize(baseObjs, PrintPropertyTypeObj.class), expressId, orderId, TypeConstant.APP_ID)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    progressDialog.dismiss();
                    if (response.success()) {
//                        startPayment();
                        doPay();
                    } else {
                        Toast.makeText(MyOrderConfirmActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(MyOrderConfirmActivity.this, "服务器返回失败", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
        addSubscription(s);
    }

    /**
     * 支付
     */
    private void doPay() {
        progressDialog.setTvMessage(getString(R.string.begin_payoff));
        Log.i("------->", "orderPrice:" + orderPrice + "-->getPayTitle:" + getPayTitle());
//        if (orderPrice == 0) {//积分支付
//            reqPayByPoint();
//        } else {// 现金支付
        final SelectPayWayDialog dialog = new SelectPayWayDialog(original);
        dialog.setClickListener(new SelectPayWayDialog.ClickListener() {
            @Override
            public void okClick(int payType) {
                dialog.dismiss();
                progressDialog.show(getSupportFragmentManager(), "");
                switch (payType) {
                    // 支付宝
                    case 1:
                        orderPrice = 0.01f;//一分钱测试支付
//                            if (isUsePoint || !TextUtils.isEmpty(getUseCouponId())) {
//                                //4混合支付(支付宝)
//                                new AliPayNewUtil(MyOrderConfirmActivity.this, orderId, getPayTitle(), orderPrice, "4").pay();
//                            } else {
//                                //2支付宝支付
//                        new AliPayNewUtil(MyOrderConfirmActivity.this, orderId, getPayTitle(), orderPrice, "2").pay();
                        Subscription subscription = new AliPay().payV2(orderId, MyOrderConfirmActivity.this);
                        addSubscription((subscription));
//                            }
                        break;

                    // 微信
//                        case 2:
                            /*if (isUsePoint || !TextUtils.isEmpty(getUseCouponId())) {
                                new WxUtil(MyOrderConfirmActivity.this, orderId, FastData.getUserId(), "3").pay();
                            } else {
                                new WxUtil(MyOrderConfirmActivity.this, orderId, FastData.getUserId(), "1").pay();
                            }
                            break;*/

                    //翼支付
//                        case 3:
                           /* new EPayUtil(MyOrderConfirmActivity.this, orderId).pay();
                            break;*/
                }
            }

            @Override
            public void cancelClick() {
                dialog.dismiss();
                    /*OrderDetailCartActivity.open(MyOrderConfirmActivity.this, orderId, TypeConstant.STATUS_NOT_PAY);*/
                OrderDetailActivity.open(MyOrderConfirmActivity.this, orderId);
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");
//        }
    }

    private void reqPayByPoint() {
        /*progressDialog.show();

        Subscription s = apiServiceV2.cartOperOrder(orderId, "0")
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    Toast.makeText(MyOrderConfirmActivity.this, response.info, Toast.LENGTH_SHORT).show();
                    if (response.success()) {
                        mHandler.sendEmptyMessage(1);
                        EventBus.getDefault().post(new TimeChangeEvent(TimeChangeEvent.TYPE_USER_INFO));
                    }
                });
        addSubscription(s);*/
    }

    /**
     * 获取支付时显示的商品名称
     */
    private String getPayTitle() {
        if (dataList != null && dataList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (MyOrderBookItem item : dataList) {
                sb.append(item.getBookName() + ",");
            }
            return sb.length() > 1 ? sb.substring(0, sb.lastIndexOf(",")) : "";//去掉最后一个逗号
        }
        return "";
    }

    /**
     * 确认订单支付结果
     */
    private void reqConfirmOrder() {
        progressDialog.setTvMessage(getString(R.string.pay_result_confirm_begin));
        progressDialog.show(getSupportFragmentManager(), "");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Subscription s = apiService.findOrderDetail(orderId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
//                                    orderDetail = response;
                                orderStatus = response.getOrderStatus();
                                orderSummary = response.getSummary();
                                mHandler.sendEmptyMessage(2);
                            }
                        });
                addSubscription(s);
            }
        }, 0, 2 * 1000);
    }

    private void setupAddressLayout(AddressItem addressModule) {
        addressId = addressModule.getId();
        mReceiver.setText(addressModule.getContacts());
        mReceiverPhone.setText(addressModule.getContactsPhone());
        StringBuffer sb = new StringBuffer();
        sb.append(DistrictModel.query(addressModule.getProv()).getLocationName());
        sb.append(DistrictModel.query(addressModule.getCity()).getLocationName());
        sb.append(DistrictModel.query(addressModule.getArea()).getLocationName());
        sb.append(addressModule.getAddress());
        mReceiverAddress.setText(sb.toString());
    }

    @Subscribe
    public void onEvent(AddAddressFinishEvent event) {
        switch (event.type) {
            case AddAddressFinishEvent.TYPE_CHANGE:
            case AddAddressFinishEvent.TYPE_SELECT:
                mAddAddressLayout.setVisibility(View.GONE);
                mAddressInfoLayout.setVisibility(View.VISIBLE);
                mDispatchSelectLayout.setVisibility(View.VISIBLE);
                setupAddressLayout(event.addressModule);
                reqExpressFee();
                break;
            case AddAddressFinishEvent.TYPE_DELET:
                if (event.addressModule.getId().equals(addressId)) {
                    mAddAddressLayout.setVisibility(View.VISIBLE);
                    mAddressInfoLayout.setVisibility(View.GONE);
                }
                if (event.count <= 0) {
                    mDispatchSelectLayout.setVisibility(View.GONE);
                    expressPrice = 0;
                    updateTotalPrice();
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(PayResultEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (event.type != null && (event.type).equals(PayResultEvent.PayType.WX)) {
            if ((event.resultCode).equals("-1")) {
                Toast.makeText(this, getString(R.string.pay_fail), Toast.LENGTH_SHORT).show();
            } else if ((event.resultCode).equals("-2")) {
                Toast.makeText(this, getString(R.string.pay_cancel), Toast.LENGTH_SHORT).show();
            }
        }

        if (event.paySuccess()) {
            Toast.makeText(this, getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
            reqConfirmOrder();
        } else {
            // 支付不成功跳转至订单详情页
            /*OrderDetailCartActivity.open(MyOrderConfirmActivity.this, orderId, TypeConstant.STATUS_NOT_PAY);*/
            OrderDetailActivity.open(MyOrderConfirmActivity.this, orderId);
            finish();
        }
    }

    @Subscribe
    public void onEvent(OrderCancelEvent event) {
        finish();
    }

    @Subscribe
    public void onEvent(CartCouponCodeEvent obj) {
        if (obj != null && obj.couponCode != null) {
//            reqCouponCodes(obj.couponCode);
            etCouponCodes.setText(obj.couponCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SCAN_PV_CODE && data != null) {
            pvCodeObj = (CouponItem) data.getSerializableExtra("couponItem");
            if (pvCodeObj != null) {
                setPVCodeEnable(true);
                updateTotalPrice();
            }
            return;
        }

        //翼支付结果 -1：支付成功 0：取消支付 512：已受理
        /*if (data != null) {
            String resMsg = data.getStringExtra("result");
            if (resultCode == EPayUtil.E_PAY_RESULT_SUCCESS || resultCode == EPayUtil.E_PAY_RESULT_PROCESS) {
                reqConfirmOrder();
            } else {
                *//*OrderDetailCartActivity.open(MyOrderConfirmActivity.this, orderId, TypeConstant.STATUS_NOT_PAY);*//*
                OrderDetailActivity.open(MyOrderConfirmActivity.this,orderId);
                finish();
                Toast.makeText(MyOrderConfirmActivity.this, resMsg, Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        isOpen = false;
        super.onDestroy();
    }

    class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())) {
                float exchanged = Float.parseFloat(s.toString());
                mExChangedPints.setText(String.format(getString(R.string.points_exchanged), exchanged / exchangeRate));
                if (exchanged > usePointMax) {
                    mExChangedPints.setText(String.format(getString(R.string.points_exchanged), (float) usePointMax / exchangeRate));
                    if (usePointMax > userPoint) {
                        mExChangePoints.setText(String.valueOf(userPoint));
                    } else {
                        mExChangePoints.setText(String.valueOf(usePointMax));
                    }
                }
                // 使用积分
                if (isUsePoint) {
                    pointPrice = Float.parseFloat(mExChangePoints.getText().toString()) / exchangeRate;
//                    updateTotalPrice();// 更改总价

                    float parvalue = getUseCouponsValue(bookPrice);
                    orderPrice = bookPrice - parvalue - pointPrice;
                    //积分不可抵用快递费
                    orderPrice = (orderPrice > 0 ? orderPrice : 0) + expressPrice;
                    mOrderAmount.setText(String.format(getString(R.string.total_price), orderPrice));
                }
            } else {
                mExChangedPints.setText(String.format(getString(R.string.points_exchanged), 0.0));
                pointPrice = 0;
//                updateTotalPrice();// 更改总价

                float parvalue = getUseCouponsValue(bookPrice);
                orderPrice = bookPrice - parvalue - pointPrice;
                //积分不可抵用快递费
                orderPrice = (orderPrice > 0 ? orderPrice : 0) + expressPrice;
                mOrderAmount.setText(String.format(getString(R.string.total_price), orderPrice));
            }
            Selection.setSelection(s, s.length());
        }
    }
}