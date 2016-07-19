package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.timeface.refreshload.PullRefreshLoadRecyclerView;
import com.timeface.refreshload.headfoot.LoadMoreView;
import com.timeface.refreshload.headfoot.RefreshView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URLEncoder;
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
import cn.timeface.circle.baby.api.models.objs.BasePrintProperty;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.CartPropertyChangeEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import rx.Subscription;


public class CartActivity extends BaseAppCompatActivity implements IEventBus {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    @Bind(R.id.stateView)
    TFStateView mStateView;
    @Bind(R.id.tv_total_balance)
    TextView mTvTotalPrice;
    @Bind(R.id.iv_select_all)
    ImageView mIvSelectAll;
    @Bind(R.id.foot_cart_list)
    RelativeLayout mFoot;
    List<PrintCartItem> dataList = new ArrayList<>();
    @Bind(R.id.rlRecyclerView)
    PullRefreshLoadRecyclerView rlRecyclerView;
    private CartAdapter mAdapter;
    private LoadingDialog progressDialog;
    public int currentPage = 1;

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
        setupView();
        reqData(1, true);
    }

    private void setupView() {
        RecyclerView recyclerView = rlRecyclerView.getRecyclerView();
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.bg7);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.view_space_normal));
        recyclerView.addItemDecoration(itemDecoration);
//        dataList = new ArrayList<>();
        progressDialog = LoadingDialog.getInstance();
//        mAdapter = new CartAdapter(CartActivity.this, dataList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rlRecyclerView.setAdapter(mAdapter);
        rlRecyclerView.setLoadRefreshListener(new PullRefreshLoadRecyclerView.LoadRefreshListener() {
            @Override
            public void onRefresh(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, RefreshView refreshView) {
                reqData(1, true);
            }

            @Override
            public void onLoadMore(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, LoadMoreView loadMoreView) {
                reqData(currentPage, false);
            }
        });
        mStateView.setOnRetryListener(() -> reqData(1, true));
        mStateView.loading();
    }

    /**
     * click 去印刷
     *
     * @param view
     */
    public void clickPrint(View view) {
        MineBookActivity.open(this);
        finish();
    }

    /**
     * click删除
     *
     * @param view
     */
    public void clickDeleteItem(View view) {
        progressDialog.show(getSupportFragmentManager(), "");

        final int index = (int) view.getTag(R.string.tag_index);
        final int pIndex = (int) view.getTag(R.string.tag_ex);
        final PrintPropertyPriceObj obj = dataList.get(pIndex).getPrintList().get(index);

        Subscription s = apiService.delCartitem(obj.getPrintId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {

                                dataList.get(pIndex).getPrintList().remove(obj);

                                //数据全部删除后处理
                                if (dataList.get(pIndex).getPrintList().size() <= 0) {
                                    dataList.remove(dataList.get(pIndex));
                                    mAdapter.notifyDataDelete();
                                }

                                if (dataList.size() == 0) {
                                    showNoDataView(true);
                                }
                                mAdapter.notifyDataSetChanged();
                                calcTotalPrice();//删除一本后更新总价格
                            }
                        }
                        , throwable -> {
                            progressDialog.dismiss();
                        });

        addSubscription(s);

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
                TypeConstant.FROM_PHONE,
                cartItem.getTotalPage(),
                cartItem.getTitle(),
                cartItem.getDate());
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * 请求印刷车数据
     */
    public void reqData(int page, boolean refresh) {
        apiService.getCartList(20 + "", page + "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> rlRecyclerView.complete())
                .subscribe(printCartListResponse -> {
                    mStateView.finish();
                    if (printCartListResponse.success()) {
                        currentPage += 1;
                        if (refresh) {
                            dataList.clear();
                        }
                        dataList.addAll(printCartListResponse.getDataList());
                        if (mAdapter == null) {
                            mAdapter = new CartAdapter(CartActivity.this, dataList);
                            rlRecyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        for (PrintCartItem item : dataList) {
                            item.checkAllSelect();
                        }
                        calcTotalPrice();
                        mIvSelectAll.setSelected(checkSelectAll());
                        if (dataList.size() == 0) {
                            showNoDataView(true);
                        } else {
                            showNoDataView(false);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CartActivity.this, printCartListResponse.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    mStateView.showException(throwable);
                    throwable.printStackTrace();
                });
    }

    /**
     * 计算总价
     */
    private void calcTotalPrice() {
        float totalPrice = 0;
        for (PrintCartItem item : dataList) {
            for (PrintPropertyPriceObj obj : item.getPrintList()) {
                if (obj.isSelect()) {
                    totalPrice += obj.getPrice() * obj.getNum();
                }
            }
        }
        mTvTotalPrice.setText(Html.fromHtml(getString(R.string.cart_total_settlement, totalPrice)));

    }

    /**
     * 添加到订单
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
                    baseObj.setPageNum(item.getTotalPage());
                    baseObj.setBookCover(item.getCoverImage());
                    baseObj.setAddressId(0);
                    baseObj.setBookName(URLEncoder.encode(item.getTitle()));
                    baseObj.setCreateTime(Long.valueOf(item.getDate()));
                    baseObj.setExpressId(1);
                    baseObjs.add(baseObj);
                }
            }
        }
        if (baseObjs.size() <= 0) {
            Toast.makeText(this, "请选择您要打印的书！", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show(getSupportFragmentManager(), "");
        try {
            params = LoganSquare.serialize(baseObjs, PrintPropertyTypeObj.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Subscription s = apiService.addOrder("", params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
//                                OrderDetailActivity.open(CartActivity.this, response.getOrderId());
                                MyOrderConfirmActivity.open(CartActivity.this, response.getOrderId(),baseObjs);
                            } else {
                                Toast.makeText(CartActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        , throwable -> {
                            Log.d(TAG, "clickAddOrder: " + throwable.getMessage());
                            progressDialog.dismiss();
                        });
        addSubscription(s);
    }

    /**
     * 点击属性
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
    }

    /**
     * Item选中
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
    }

    /**
     * click 编辑
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
                params.put("pageNum", mAdapter.getItem(position).getTotalPage()+"");
                params.put("createTime", mAdapter.getItem(position).getDate());
                params.put("bookName",URLEncoder.encode(mAdapter.getItem(position).getTitle()));
                params.put("bookCover", mAdapter.getItem(position).getCoverImage());
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
        progressDialog.show(getSupportFragmentManager(), "");
        Subscription s = apiService.addCartItem(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (!response.success()) {
                                propertyAdapter.setPropertyState(CartPrintPropertyAdapter.PROPERTY_STATE_EDIT);
                                Toast.makeText(CartActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                            } else {
                                reqData(1, true);
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

    //全选
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

    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        rlRecyclerView.setVisibility(showNoData ? View.GONE : View.VISIBLE);
        mFoot.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }

}
