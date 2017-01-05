package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.OrderState;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.common.utils.DateUtil;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class OrderDetailHeaderView extends LinearLayout {
    @Bind(R.id.order_status_tv)
    TextView orderStatusTv;
    @Bind(R.id.count_down_tv)
    CountdownChronometer countDownTv;
    @Bind(R.id.order_no_tv)
    TextView orderNoTv;
    @Bind(R.id.order_apply_date_tv)
    TextView orderApplyDateTv;
    @Bind(R.id.order_sumary_tv)
    TextView orderSumaryTv;
    @Bind(R.id.order_sumary_tv1)
    TextView orderSumaryTv1;
    @Bind(R.id.order_sumary_ll)
    LinearLayout orderSumaryLl;
    @Bind(R.id.express_order_no_tv)
    TextView expressOrderNoTv;
    @Bind(R.id.express_order_no_ll)
    LinearLayout expressOrderNoLl;
    @Bind(R.id.logistics_info_tv)
    TextView logisticsInfoTv;
    @Bind(R.id.logistics_info_date_tv)
    TextView logisticsInfoDateTv;
    @Bind(R.id.logistics_info_ll)
    LinearLayout logisticsInfoLl;
    @Bind(R.id.order_detail_header_ll)
    LinearLayout orderDetailHeaderLl;
    @Bind(R.id.order_reciver_name_tv)
    TextView orderReciverNameTv;
    @Bind(R.id.receicer_phone_tv)
    TextView receicerPhoneTv;
    @Bind(R.id.receiver_address_tv)
    TextView receiverAddressTv;

    public OrderDetailHeaderView(Context context) {
        super(context);
        initView();
    }

    public OrderDetailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OrderDetailHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OrderDetailHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }


    private void initView() {
        setOrientation(VERTICAL);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_order_header, this, false);
        ButterKnife.bind(this, headerView);
        addView(headerView);
/*        TextView textView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(getResources().getColor(R.color.bg7));
        int pad = DeviceUtil.dpToPx(getResources(), 12f);
        textView.setPadding(pad, pad, pad, pad);
        addView(textView, layoutParams);*/
    }

    public void setupViewData(MyOrderConfirmListResponse response) {
        // 配送中 || 已送达|| // 商家优惠码现场配送不显示运单号、物流信息
        boolean express = TextUtils.isEmpty(response.getExpressOrder()) || response.getOrderStatus() == 3 || response.getOrderStatus() == 5;
        expressOrderNoLl.setVisibility(express ? View.GONE : View.VISIBLE);
        logisticsInfoLl.setVisibility(express ? View.GONE : View.VISIBLE);

        orderStatusTv.setText(OrderState.processStatus(response.getOrderStatus()));
        orderNoTv.setText(response.getOrderId());
        orderApplyDateTv.setText(DateUtil.formatDate("yyyy-MM-dd", response.getOrderTime() * 1000));
        logisticsInfoTv.setText(response.getLastExpress());
        expressOrderNoTv.setText(response.getExpressOrder());
        orderSumaryTv.setText(response.getSummary());
        orderReciverNameTv.setText(response.getContacts());
        receicerPhoneTv.setText(response.getContactsPhone());
        receiverAddressTv.setText(response.getAddress());

        // 待确认(未支付)
        if (response.getOrderStatus() == TypeConstant.STATUS_NOT_PAY) {
            countDownTv.setVisibility(VISIBLE);
            countDownTv.setBase(1000 * 60 * 60 * 24 * 7 + response.getOrderTime() * 1000);
            countDownTv.setCustomChronoFormat("%1$d天%2$02d时%3$02d分%4$02d秒");
            countDownTv.setFormat("将于%s后关闭");
            countDownTv.start();
        }

    }

}
