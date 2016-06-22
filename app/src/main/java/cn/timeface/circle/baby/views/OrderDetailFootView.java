package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.responses.MyOrderConfirmListResponse;

/**
 * Created by zhsheng on 2016/6/22.
 */
public class OrderDetailFootView extends LinearLayout {
    @Bind(R.id.tv_total_count)
    TextView tvTotalCount;
    @Bind(R.id.tv_exchange_point)
    TextView tvExchangePoint;
    @Bind(R.id.ll_exchange_point)
    LinearLayout llExchangePoint;
    @Bind(R.id.tv_coupon_label)
    TextView tvCouponLabel;
    @Bind(R.id.tv_coupon)
    TextView tvCoupon;
    @Bind(R.id.ll_coupon)
    LinearLayout llCoupon;
    @Bind(R.id.tv_pv_code)
    TextView tvPvCode;
    @Bind(R.id.ll_pv_code)
    LinearLayout llPvCode;
    @Bind(R.id.tv_express_fee)
    TextView tvExpressFee;
    @Bind(R.id.order_total_price_lb)
    TextView orderTotalPriceLb;
    @Bind(R.id.order_total_price_tv)
    TextView orderTotalPriceTv;
    @Bind(R.id.order_action_cancel_btn)
    Button orderActionCancelBtn;
    @Bind(R.id.order_action_btn)
    Button orderActionBtn;
    @Bind(R.id.rl_bottom)
    RelativeLayout rlBottom;

    public OrderDetailFootView(Context context) {
        super(context);
        initView();
    }

    public OrderDetailFootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OrderDetailFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OrderDetailFootView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View footView = LayoutInflater.from(getContext()).inflate(R.layout.activity_cart_detail_foot, this, false);
        ButterKnife.bind(this, footView);

    }

    public void setupViewData(MyOrderConfirmListResponse listResponse) {

    }
}
