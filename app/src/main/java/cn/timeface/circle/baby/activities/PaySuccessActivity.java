package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.PopBottomDialog;
import cn.timeface.circle.baby.utils.FastData;

/**
 * @author SUN
 * @from 2014/12/23
 * @TODO 支付成功
 */
public class PaySuccessActivity extends BaseAppCompatActivity {

    @Bind(R.id.order_summary_tv)
    TextView mOrderSumary;

    @Bind(R.id.payresult_title)
    TextView mtvOrderTitle;
    @Bind(R.id.payresult_price)
    TextView mtvOrderPrice;
    @Bind(R.id.payresult_orderId)
    TextView mtvOrderId;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String orderId;
    private float orderPrice;
    private int orderStatus;
    private String orderSummary;

    public static void open(Context context, String orderId, float orderPrice, int orderStatus, String orderSummary) {
        Intent intent = new Intent(context, PaySuccessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("order_id", orderId);
        intent.putExtra("order_price", orderPrice);
        intent.putExtra("order_status", orderStatus);
        intent.putExtra("order_summary", orderSummary);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.orderId = getIntent().getStringExtra("order_id");
        this.orderPrice = getIntent().getFloatExtra("order_price", 0);
        this.orderStatus = getIntent().getIntExtra("order_status", 0);
        this.orderSummary = getIntent().getStringExtra("order_summary");

        mtvOrderId.setText(orderId);
        mtvOrderPrice.setText(String.format(getString(R.string.total_price), orderPrice));
        mOrderSumary.setText(orderSummary);
    }

    @OnClick({R.id.payresult_toorder, R.id.payresult_tobookshelf})
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.payresult_toorder:
                /*OrderDetailCartActivity.open(this, orderId, orderStatus);*/
                OrderDetailActivity.open(this,orderId);
                finish();
                break;
            case R.id.payresult_tobookshelf:
                /*MineTimeBookActivity.open(this, FastData.getUserId(), FastData.getUserName());*/
                MineBookActivity.open(this);
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_activity_pay_success, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_phone:
//
//                PopBottomDialog dialog =
//                        PopBottomDialog.newInstance("时光流影客服中心竭诚为您服务",
//                                "拨打电话 400-102-1099", "取消");
//                dialog.setClickListener(new PopBottomDialog.ClickListener() {
//
//                    @Override
//                    public void onItem3Click() {
//
//                    }
//
//                    @Override
//                    public void onItem2Click() {
//                        //调用系统的拨号服务实现电话拨打功能
//                        String phone_number = "4001021099";
//
//                        phone_number = phone_number.trim();//删除字符串首部和尾部的空格
//
//                        if (phone_number != null && !phone_number.equals("")) {
//                            //调用系统的拨号服务实现电话拨打功能
//                            //封装一个拨打电话的intent，并且将电话号码包装成一个Uri对象传入
//                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
//                            startActivity(intent);//内部类
//                        }
//
//                    }
//
//                    @Override
//                    public void onItem1Click() {
//
//                    }
//                });
//                dialog.show(getSupportFragmentManager(), "dialog");
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}