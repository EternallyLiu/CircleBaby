package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CartAdapter;
import cn.timeface.circle.baby.adapters.CartPrintPropertyAdapter;
import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.PrintPropertyModel;
import cn.timeface.circle.baby.api.models.objs.BasePrintProperty;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.api.models.responses.PrintCartListResponse;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.CartPropertyChangeEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Subscription;


public class CartActivity extends BaseAppCompatActivity implements IEventBus {
    @Bind(R.id.tb_toolbar)
    Toolbar mToolBar;
    @Bind(R.id.ptr_layout)
    RelativeLayout mPtrLayout;
    @Bind(R.id.pull_refresh_list)
    RecyclerView mPullRefreshList;
    @Bind(R.id.ll_cart_empty)
    LinearLayout mLlEmpty;
    @Bind(R.id.stateView)
    TFStateView mStateView;
    @Bind(R.id.tv_total_balance)
    TextView mTvTotalPrice;
    @Bind(R.id.iv_select_all)
    ImageView mIvSelectAll;
    @Bind(R.id.foot_cart_list)
    RelativeLayout mFoot;
    @Bind(R.id.tv_promotion_info)
    TextView tvPromotionInfo;
    List<PrintCartItem> dataList;
    private CartAdapter mAdapter;
    private CartPrintPropertyAdapter mPropertyAdapter;
    private TFProgressDialog progressDialog;
    private String bookId;
    private String bookType;
    private PrintCartListResponse cartResponse;

    public static void open(Context context) {
        context.startActivity(new Intent(context, CartActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dataList = new ArrayList<>();
        progressDialog = new TFProgressDialog(this);
        mAdapter = new CartAdapter(CartActivity.this, dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mPullRefreshList.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#f2f2f2"));
        itemDecoration.setPaint(paint);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.view_space_medium));
        mPullRefreshList.addItemDecoration(itemDecoration);

        mPullRefreshList.setAdapter(mAdapter);
        mStateView.setOnRetryListener(() -> requestData());
    }

    @Override
    protected void onResume() {
        requestData();
        super.onResume();
    }

//    public void clickDetail(View view){
//        if(!TextUtils.isEmpty(cartResponse.getPromotionUrl())){
//            WebViewActivity.open(this, cartResponse.getPromotionUrl(), "活动详情", true);
//        }
//    }

    /**
     * click 去印刷
     *
     * @param view
     */
    public void clickPrint(View view) {
//        Intent intent = new Intent(CartActivity.this, MineTimeBookActivity.class);
//        intent.putExtra("userId", FastData.getUserId());
//        intent.putExtra("book_type", bookType);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        FragmentBridgeActivity.open(this, "MineBookFragment");
    }

    /**
     * click删除
     *
     * @param view
     */
    public void clickDeleteItem(View view) {
        progressDialog.show();

        final int index = (int) view.getTag(R.string.tag_index);
        final int pIndex = (int) view.getTag(R.string.tag_ex);
        final PrintPropertyPriceObj obj = dataList.get(pIndex).getPrintList().get(index);

//        Subscription s = apiService.deleteProperty(obj.getPrintId())
//                .compose(SchedulersCompat.applyIoSchedulers())
//                .subscribe(
//                        response -> {
//                            progressDialog.dismiss();
//                            if (response.success()) {
//
//                                dataList.get(pIndex).getPrintList().remove(obj);
//                                PrintPropertyModel.deleteById(obj.getPrintId());
//
//                                //数据全部删除后处理
//                                if (dataList.get(pIndex).getPrintList().size() <= 0) {
//                                    dataList.remove(dataList.get(pIndex));
//                                    mAdapter.notifyDataDelete();
//                                }
//
//                                if (dataList.size() <= 0) {
//                                    mLlEmpty.setVisibility(View.VISIBLE);
//                                    mPtrLayout.setVisibility(View.GONE);
//                                    mFoot.setVisibility(View.GONE);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                                calcTotalPrice();//删除一本后更新总价格
//                            }
//                        }
//                        , throwable -> {
//                            progressDialog.dismiss();
//                        });
//
//        addSubscription(s);

    }

    public void clickPlus(View view) {
        int num = (int) view.getTag(R.string.tag_ex);
        PrintPropertyPriceObj propertyObj = (PrintPropertyPriceObj) view.getTag(R.string.tag_obj);
        if (num < 99) {
            num += 1;
            propertyObj.setNum(num);
        }
        mAdapter.notifyDataSetChanged();
        calcTotalPrice();
    }

    public void clickMinus(View view) {
        int num = (int) view.getTag(R.string.tag_ex);
        PrintPropertyPriceObj propertyObj = (PrintPropertyPriceObj) view.getTag(R.string.tag_obj);
        if (num > 1) {
            num -= 1;
            propertyObj.setNum(num);
        }
        mAdapter.notifyDataSetChanged();
        calcTotalPrice();
    }

    /**
     * click 再印一本
     *
     * @param view
     */
    public void onPrintMoreClick(View view) {
        PrintCartItem item = (PrintCartItem) view.getTag(R.string.tag_obj);
        CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(item,
                null,
                null,
                item.getBookId(),
                String.valueOf(item.getBookType()),
                CartPrintPropertyDialog.REQUEST_CODE_MINETIME,
                item.getPrintCode(),
                item.getCoverImage(),
                TypeConstant.FROM_PHONE);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * click 更改属性
     *
     * @param view
     */
    public void clickChangeProperty(View view) {
        PrintCartItem cartItem = (PrintCartItem) view.getTag(R.string.tag_ex);
        //已经删除
        if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS) {
            Toast.makeText(this, getPrintInfo(cartItem.getPrintCode()), Toast.LENGTH_SHORT).show();
            return;
        }
        PrintPropertyPriceObj obj = (PrintPropertyPriceObj) view.getTag(R.string.tag_obj);
        CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(
                cartItem,
                obj,
                null,
                cartItem.getBookId(),
                String.valueOf(cartItem.getBookType()),
                CartPrintPropertyDialog.REQUEST_CODE_MINETIME,
                cartItem.getPrintCode(),
                cartItem.getCoverImage(),
                TypeConstant.FROM_PHONE);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    public void requestData(){
        mokoData();
        mAdapter.setListData(dataList);
//        for (PrintCartItem item : dataList) {
//            item.checkAllSelect();
//        }
//        calcTotalPrice();
//        mIvSelectAll.setSelected(checkSelectAll());
        if (dataList.size() <= 0) {
            mLlEmpty.setVisibility(View.VISIBLE);
            mPtrLayout.setVisibility(View.GONE);
            mFoot.setVisibility(View.GONE);
        } else {
            mLlEmpty.setVisibility(View.GONE);
            mPtrLayout.setVisibility(View.VISIBLE);
            mFoot.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 请求印刷车数据
     */
    public void requestDat() {

        mStateView.loading();
        Subscription s = apiService.getCartList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    mStateView.finish();
                    if (response.success()) {
                        cartResponse = response;
                        dataList = response.getDataList();
                        mAdapter.setListData(dataList);
                        for (PrintCartItem item : dataList) {
                            item.checkAllSelect();
                        }
                        calcTotalPrice();
                        mIvSelectAll.setSelected(checkSelectAll());
                        if (dataList.size() <= 0) {
                            mLlEmpty.setVisibility(View.VISIBLE);
                            mPtrLayout.setVisibility(View.GONE);
                            mFoot.setVisibility(View.GONE);
                        } else {
                            mLlEmpty.setVisibility(View.GONE);
                            mPtrLayout.setVisibility(View.VISIBLE);
                            mFoot.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CartActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    mStateView.showException(throwable);
                });
        addSubscription(s);
    }

    /**
     * 处理上次编辑的选中状态
     *
     * @param dataList
     * @return
     */
    private List<PrintCartItem> doSelectState(List<PrintCartItem> dataList) {
        List<PrintPropertyModel> dbList = PrintPropertyModel.queryAll();
        if (dbList == null || dbList.size() <= 0) {
            return dataList;
        } else {
            //暂时没想到更好的办法 不得已这样循环
            for (PrintCartItem cartItem : dataList) {
                for (PrintPropertyPriceObj obj : cartItem.getPrintList()) {
                    for (PrintPropertyModel propertyModel : dbList) {
                        if (obj.getPrintId().equals(propertyModel.getPrintId())) {
                            obj.setIsSelect(propertyModel.isSelect());
                        }
                    }
                }
            }

            //处理是否全选
            for (PrintCartItem cartItem : dataList) {
                cartItem.checkAllSelect();
            }
        }

        return dataList;
    }

    /**
     * 计算总价
     */
    private void calcTotalPrice() {
        float totalPrice = 0;
        float calendarPrice = 0;
        for (PrintCartItem item : dataList) {
            int bookType = item.getBookType();
            for (PrintPropertyPriceObj obj : item.getPrintList()) {
                if (obj.isSelect()) {
                    if (bookType == TypeConstant.BOOK_TYPE_DESK_CALENDAR) {
                        calendarPrice += obj.getPrice() * obj.getNum();
                    }
                    totalPrice += obj.getPrice() * obj.getNum();
                }
            }
        }
        mTvTotalPrice.setText(Html.fromHtml(getString(R.string.cart_total_settlement,
                cartResponse.hasPromotion() && calendarPrice > 0 &&
                        totalPrice - calendarPrice >= cartResponse.getPromotionTerm() ?
                        totalPrice - cartResponse.getPromotionFee() : totalPrice)));

        //是否显示优惠信息
        if (cartResponse.hasPromotion()) {
            tvPromotionInfo.setVisibility(View.VISIBLE);
            String text = cartResponse.getPromotionInfo() +
                    (TextUtils.isEmpty(cartResponse.getPromotionUrl()) ? "" : "<font color = '#069bf2'>查看活动详情>></font>");
            tvPromotionInfo.setText(Html.fromHtml(text));
            if (totalPrice - calendarPrice >= cartResponse.getPromotionTerm() && calendarPrice > 0) {
                tvPromotionInfo.setVisibility(View.VISIBLE);
                tvPromotionInfo.setText(String.format(
                        getString(R.string.cart_promotion_fee),
                        "台历",
                        cartResponse.getPromotionFee()));
            } else {
                tvPromotionInfo.setVisibility(View.GONE);
            }

        } else {
            tvPromotionInfo.setVisibility(View.GONE);
            tvPromotionInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 添加到订单
     *
     * @param view
     */
    public void clickAddOrder(View view) {
        String params = "";
        List<PrintPropertyTypeObj> baseObjs = new ArrayList<>();
        for (PrintCartItem item : dataList) {
            for (PrintPropertyPriceObj obj : item.getPrintList()) {
                if (obj.isSelect()) {
                    PrintPropertyTypeObj baseObj = new PrintPropertyTypeObj();
                    baseObj.setBookId(item.getBookId());
                    baseObj.setBookType(item.getBookType());
                    baseObj.setPrintId(obj.getPrintId());
                    baseObj.setSize(obj.getSize());
                    baseObj.setPack(obj.getPack());
                    baseObj.setPaper(obj.getPaper());
                    baseObj.setNum(obj.getNum());
                    baseObj.setColor(obj.getColor());
                    baseObjs.add(baseObj);
                }
            }
        }
        if (baseObjs.size() <= 0) {
            Toast.makeText(this, "请选择您要打印的书！", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        try {
            params = LoganSquare.serialize(baseObjs, PrintPropertyTypeObj.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Subscription s = apiService.addOrderNew(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
                                //从数据库中删除
                                for (PrintCartItem item : dataList) {
                                    for (PrintPropertyPriceObj obj : item.getPrintList()) {
                                        if (obj.isSelect()) {
                                            PrintPropertyModel.deleteById(obj.getPrintId());
                                        }
                                    }
                                }
//                                MyOrderConfirmActivity.open(CartActivity.this, response.getOrderId());
                            } else {
                                Toast.makeText(CartActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        , throwable -> {
                            progressDialog.dismiss();
                        });
        addSubscription(s);
    }

    /**
     * 点击属性
     *
     * @param view
     */
    public void clickProperty(View view) {
        PrintPropertyPriceObj obj = (PrintPropertyPriceObj) view.getTag(R.string.tag_obj);
        PrintCartItem item = (PrintCartItem) view.getTag(R.string.tag_ex);
        if (item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS) {
            Toast.makeText(this, getPrintInfo(item.getPrintCode()), Toast.LENGTH_SHORT).show();
            return;
        }
        obj.setIsSelect(obj.isSelect() ? false : true);
        item.checkAllSelect();
        mIvSelectAll.setSelected(false);
        mAdapter.notifyDataSetChanged();
        calcTotalPrice();
        mIvSelectAll.setSelected(checkSelectAll());

        //选择状态保存到数据库
        PrintPropertyModel.saveUpdate(obj);
    }

    /**
     * Item选中
     *
     * @param view
     */
    public void clickItem(View view) {
        PrintCartItem item = (PrintCartItem) view.getTag(R.string.tag_obj);
        if (item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS) {
            Toast.makeText(this, getPrintInfo(item.getPrintCode()), Toast.LENGTH_SHORT).show();
            return;
        }
        item.setIsSelect(item.isSelect() ? false : true);
        mIvSelectAll.setSelected(false);
        mAdapter.notifyDataSetChanged();
        calcTotalPrice();
        mIvSelectAll.setSelected(checkSelectAll());

        //选择状态保存到数据库
        for (PrintPropertyPriceObj obj : item.getPrintList()) {
            PrintPropertyModel.saveUpdate(obj);
        }
    }

    /**
     * click 编辑
     *
     * @param view
     */
    public void clickEdit(View view) {
        PrintCartItem cartItem = (PrintCartItem) view.getTag(R.string.tag_obj);
        //已经删除
        if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS) {
            Toast.makeText(this, getPrintInfo(cartItem.getPrintCode()), Toast.LENGTH_SHORT).show();
            return;
        }
        int position = (int) view.getTag(R.string.tag_index);
        CartPrintPropertyAdapter propertyAdapter = mAdapter.getPropertyAdapter(position);

        //完成操作
        if (propertyAdapter.propertyState == CartPrintPropertyAdapter.PROPERTY_STATE_EDIT) {
            final List<PrintPropertyPriceObj> dataList = propertyAdapter.getDataList();
            HashMap<String, String> params = new HashMap<>();
            List<BasePrintProperty> printProperties = new ArrayList<>();

            for (PrintPropertyPriceObj propertyPriceObj : dataList) {
                BasePrintProperty property = new BasePrintProperty();
                property.setPrintId(propertyPriceObj.getPrintId());
                property.setData(propertyPriceObj.getSize(),
                        propertyPriceObj.getColor(),
                        propertyPriceObj.getPack(),
                        propertyPriceObj.getPaper(),
                        propertyPriceObj.getNum());
                printProperties.add(property);
            }
            try {
                params.put("bookId", mAdapter.getItem(position).getBookId());
                params.put("bookType", String.valueOf(mAdapter.getItem(position).getBookType()));
                params.put("printList", LoganSquare.serialize(printProperties, BasePrintProperty.class));
                addToCart(params, propertyAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            changeEditState(propertyAdapter, view);
        }
    }

    private void addToCart(HashMap<String, String> params, CartPrintPropertyAdapter propertyAdapter) {
        progressDialog.show();
        Subscription s = BaseAppCompatActivity.apiService.addCartItem(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (!response.success()) {
                                propertyAdapter.setPropertyState(CartPrintPropertyAdapter.PROPERTY_STATE_EDIT);
                                Toast.makeText(CartActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                            } else {
                                requestData();
                                propertyAdapter.setPropertyState(CartPrintPropertyAdapter.PROPERTY_STATE_NOMAL);
                                propertyAdapter.notifyDataSetChanged();
                            }

                        }
                        , throwable -> {
                            propertyAdapter.setPropertyState(CartPrintPropertyAdapter.PROPERTY_STATE_EDIT);
                            progressDialog.dismiss();
                            Toast.makeText(CartActivity.this, "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        addSubscription(s);
    }

    private void changeEditState(CartPrintPropertyAdapter propertyAdapter, View view) {
        //更改显示样式
        propertyAdapter.setPropertyState(
                propertyAdapter.propertyState == CartPrintPropertyAdapter.PROPERTY_STATE_NOMAL ?
                        CartPrintPropertyAdapter.PROPERTY_STATE_EDIT :
                        CartPrintPropertyAdapter.PROPERTY_STATE_NOMAL);

        //更改 编辑text
        if (propertyAdapter.propertyState == CartPrintPropertyAdapter.PROPERTY_STATE_EDIT) {
            ((TextView) view).setText(getString(R.string.finish));
        } else if (propertyAdapter.propertyState == CartPrintPropertyAdapter.PROPERTY_STATE_NOMAL) {
            ((TextView) view).setText(getString(R.string.edit));
        } else {
            ((TextView) view).setText(getString(R.string.edit));
        }
    }

    private boolean checkSelectAll() {
        boolean isSelectAll = true;
        for (PrintCartItem item : dataList) {
            if (!item.isSelect() || (item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS
                    || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                    || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE)) {
                isSelectAll = false;
                break;
            }
        }
        return isSelectAll;
    }

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    public void clickSelectAll(View view) {
        boolean selectAll = true;
        for (PrintCartItem item : dataList) {
            if (item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                    || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                    || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS) {
                selectAll = false;
            }
            item.setIsSelect(view.isSelected() ? false : true);
        }
        view.setSelected(selectAll ? (view.isSelected() ? false : true) : false);

        mAdapter.notifyDataSetChanged();
        calcTotalPrice();
    }

//    public void clickToPod(View view) {
//        PrintCartItem item = (PrintCartItem) view.getTag(R.string.tag_obj);
//        if (item.getChildNum() > 0) {
//            SplitPrintActivity.open(this,
//                    Utils.changeBookType2PodType(item.getBookType()),
//                    item.getBookId(),
//                    String.valueOf(item.getBookType()),
//                    item.getCoverImage() == null ? "" : item.getCoverImage(),
//                    item.getTitle() == null ? "" : item.getTitle(),
//                    "0",
//                    FastData.getUserName(),
//                    String.valueOf(item.getTotalPage()),
//                    TypeConstant.FROM_PHONE, 1);
//        } else {
//            if(item.getBookType() == TypeConstant.BOOK_TYPE_DESK_CALENDAR){
//                CalendarPodActivity.open(this, item.getBookId(), false);
//            } else {
//                PodActivity.open(this, item.getBookId(),
//                        Utils.changeBookType2PodType(item.getBookType()), 1);
//            }
//        }
//    }

    private String getPrintInfo(int printCode) {
        String printInfo = "";
        switch (printCode) {
            case TypeConstant.PRINT_CODE_LIMIT_LESS:
                printInfo = getString(R.string.cart_print_code_limit_less_2);
                break;

            case TypeConstant.PRINT_CODE_LIMIT_MORE:
                printInfo = getString(R.string.cart_print_code_limit_more_2);
                break;

            case TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE:
                printInfo = getString(R.string.cart_print_code_limit_had_delete);
                break;
        }

        return printInfo;
    }

    @Subscribe
    public void onEvent(CartPropertyChangeEvent event) {
        BasePrintProperty propertyObj = event.printPropertyObj;
        for (PrintCartItem cartItem : dataList) {
            for (PrintPropertyPriceObj obj : cartItem.getPrintList()) {
                if (obj.getPrintId().equals(propertyObj.getPrintId())) {
                    obj.setPrice(event.price);
                    obj.setData(propertyObj.getSize(),
                            propertyObj.getColor(),
                            propertyObj.getPack(),
                            propertyObj.getPaper(),
                            propertyObj.getNum());
                    mAdapter.notifyDataSetChanged();
                    calcTotalPrice();
                    break;
                }
            }
        }
    }

    @Override
    public void onEvent(Object event) {

    }

    private void mokoData() {
        PrintCartItem printCartItem = new PrintCartItem();
        printCartItem.setBookId("1");
        printCartItem.setBookType(1);
        printCartItem.setTitle("时光书");
        printCartItem.setTagName("识图卡片");
        printCartItem.setCoverImage("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1402/27/c4/31612517_1393474458218_mthumb.jpg");
        printCartItem.setTotalPage(50);
        printCartItem.setAuthorName("啦啦啦");
        dataList.add(printCartItem);
        dataList.add(printCartItem);
        dataList.add(printCartItem);
    }
}
