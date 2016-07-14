package cn.timeface.circle.baby.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CartPrintPropertyGvAdapter;
import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.objs.BasePrintProperty;
import cn.timeface.circle.baby.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.events.CartAddClickEvent;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.CartPropertyChangeEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.DeviceUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.NoScrollGridView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * @author YW.SUN
 * @from 2015/5/21
 * @TODO
 */
public class CartPrintPropertyDialog extends DialogFragment implements IEventBus {
    public final static int TYPE_ADD_CART = 0;
    public final static int TYPE_BUY_NOW = 1;
    public final static int TYPE_NORMAL = 2;
    public final static int REQUEST_CODE_MINETIME = 0;
    public final static int REQUEST_CODE_WECHAT = 1;
    public final static int REQUEST_CODE_QQ = 2;
    public final static int REQUEST_CODE_POD = 3;
    public final static int REQUEST_CODE_SPLIT = 4;
    @Bind(R.id.book_print_number_minus_ib)
    ImageButton mBookPrintNumberMinusIb;
    @Bind(R.id.book_print_number_et)
    EditText mBookPrintNumberEt;
    @Bind(R.id.book_print_number_plus_ib)
    ImageButton mBookPrintNumberPlusIb;
    @Bind(R.id.iv_book_cover)
    RatioImageView mIvBookCover;
    @Bind(R.id.fl_cover)
    FrameLayout mFlCover;
    @Bind(R.id.rl_cover)
    RelativeLayout mRlCover;
    @Bind(R.id.gv_book_size)
    NoScrollGridView mGvBookSize;
    @Bind(R.id.gv_print_color)
    NoScrollGridView mGvPrintColor;
    @Bind(R.id.gv_pack)
    NoScrollGridView mGvPack;
    @Bind(R.id.gv_paper)
    NoScrollGridView mGvPaper;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.iv_book_tag)
    ImageView mIvBookTag;
    @Bind(R.id.tv_pack_label)
    TextView mTvPack;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.btn_add_to_cart)
    Button btnAddCart;
    @Bind(R.id.btn_buy_now)
    Button btnBuyNow;
    @Bind(R.id.btn_ok)
    Button btnOk;
    List<PrintParamObj> sizeList = new ArrayList<>();
    List<PrintParamObj> colorList = new ArrayList<>();
    List<PrintParamObj> packList = new ArrayList<>();
    List<PrintParamObj> paperList = new ArrayList<>();
    DismissListener dismissListener;
    private PrintCartItem cartItem;
    private PrintPropertyPriceObj propertyObj;
    private List<PrintParamResponse> paramList;
    private CartPrintPropertyGvAdapter sizeAdapter;
    private CartPrintPropertyGvAdapter colorAdapter;
    private CartPrintPropertyGvAdapter packAdapter;
    private CartPrintPropertyGvAdapter paperAdapter;
    private float bookPrice;
    private String bookId;
    private String bookType;
    private TFProgressDialog tfProgressDialog;
    private int requestCode;
    private String printId = "";
    private int printCode;
    private String bookCover;
    private boolean isQuery = false;
    private int original = 0;
    private int pageNum;
    private String bookName;
    private String createTime;

    public static CartPrintPropertyDialog getInstance(PrintCartItem printCartItem,
                                                      PrintPropertyPriceObj obj,
                                                      List<PrintParamResponse> responseList,
                                                      String bookId,
                                                      String bookType,
                                                      int requestCode,
                                                      int printCode,
                                                      String bookCover,
                                                      int original,
                                                      int pageNum,
                                                      String bookName,
                                                      String createTime) {
        CartPrintPropertyDialog dialog = new CartPrintPropertyDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cart_item", printCartItem);
        bundle.putSerializable("print_property", obj);
        bundle.putParcelableArrayList("param_response", (ArrayList<? extends Parcelable>) responseList);
        bundle.putString("book_id", bookId);
        bundle.putString("book_type", bookType);
        bundle.putInt("request_code", requestCode);
        bundle.putInt("print_code", printCode);
        bundle.putString("book_cover", bookCover);
        bundle.putInt("original", original);
        bundle.putInt("pageNum", pageNum);
        bundle.putString("bookName", bookName);
        bundle.putString("createTime", createTime);
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tfProgressDialog = new TFProgressDialog(getActivity());
        cartItem = (PrintCartItem) getArguments().getSerializable("cart_item");
        propertyObj = (PrintPropertyPriceObj) getArguments().getSerializable("print_property");
        bookId = getArguments().getString("book_id");
        bookType = getArguments().getString("book_type");
        requestCode = getArguments().getInt("request_code", REQUEST_CODE_MINETIME);
        printCode = getArguments().getInt("print_code", TypeConstant.PRINT_CODE_NORMAL);
        bookCover = getArguments().getString("book_cover");
        original = getArguments().getInt("original", 0);
        pageNum = getArguments().getInt("pageNum", 0);
        bookName = getArguments().getString("bookName");
        createTime = getArguments().getString("createTime");
        if (cartItem == null) {
            paramList = getArguments().getParcelableArrayList("param_response");
        } else {
            paramList = cartItem.getParamList();
        }
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_dialog_cart_print_property, null, false);
        ButterKnife.bind(this, view);

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        setupLayout();
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = DeviceUtil.getScreenHeight(getActivity()) - DeviceUtil.dpToPx(getResources(), 160);
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        dialog.onWindowAttributesChanged(wl);
        queryBookPrice();
        if(Integer.parseInt(bookType) == TypeConstant.BOOK_TYPE_CIRCLE){
            mTvPack.setVisibility(View.GONE);
        } else {
            mTvPack.setVisibility(View.VISIBLE);
        }

        dialog.setOnKeyListener((dialog1, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                if (dismissListener != null) {
                    dismissListener.dismiss();
                }
                return true;
            }
            return false;
        });
        return dialog;
    }

    private void setupLayout() {
        mBookPrintNumberEt.addTextChangedListener(new EditTextWatcher(mBookPrintNumberEt, 99, false));
        Glide.with(getContext())
                .load(bookCover)
                .placeholder(R.drawable.book_default_bg)
                .error(R.drawable.book_default_bg)
                .into(mIvBookCover);
        for (PrintParamResponse paramResponse : paramList) {
            if (PrintParamResponse.KEY_SIZE.equals(paramResponse.getKey())) {
                sizeList = paramResponse.getValueList();
            } else if (PrintParamResponse.KEY_COLOR.equals(paramResponse.getKey())) {
                colorList = paramResponse.getValueList();
            } else if (PrintParamResponse.KEY_PACK.equals(paramResponse.getKey())) {
                packList = paramResponse.getValueList();
            } else if (PrintParamResponse.KEY_PAPER.equals(paramResponse.getKey())) {
                paperList = paramResponse.getValueList();
            }
        }

        //remove 法式精装
        PrintParamObj packObj = null;
        for (PrintParamObj obj : packList) {
            if ("法式精装".equals(obj.getShow())) {
                packObj = obj;
            }
        }
        packList.remove(packObj);

        if (cartItem == null) {
            mIvBookTag.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.VISIBLE);
            btnBuyNow.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.GONE);
        } else {
            btnAddCart.setVisibility(View.GONE);
            btnBuyNow.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            switch (cartItem.getBookType()) {
                case TypeConstant.BOOK_TYPE_QQ:
                    mIvBookTag.setImageResource(R.drawable.ic_time_book_tag_qq);
                    break;

                case TypeConstant.BOOK_TYPE_WECHAT:
                    mIvBookTag.setImageResource(R.drawable.ic_time_book_tag_wechat);
                    break;

                default:
                    mIvBookTag.setVisibility(View.GONE);
                    break;

            }
        }

        if (propertyObj == null) {
            sizeList.get(0).setIsSelect(true);
            colorList.get(0).setIsSelect(true);
            packList.get(0).setIsSelect(true);
            if(paperList.size() > 0) paperList.get(0).setIsSelect(true);

            for (PrintParamObj obj : packList) {
                if (printCode == TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK) {
                    mTvPack.setText(getString(R.string.cart_print_code_limit_soft_pack_2));
                    if (obj.getShow().equals("平装")) {
                        obj.setIsActive(true);
                    } else {
                        obj.setIsActive(false);
                    }
                }
            }
        } else {
            mBookPrintNumberEt.setText(String.valueOf(propertyObj.getNum()));
            for (PrintParamObj obj : sizeList) {
                if (obj.getValue().equals(propertyObj.getSize())) {
                    obj.setIsSelect(true);
                }
            }

            for (PrintParamObj obj : colorList) {
                if (obj.getValue().equals(String.valueOf(propertyObj.getColor()))) {
                    obj.setIsSelect(true);
                }
            }

            for (PrintParamObj obj : packList) {
                if (obj.getValue().equals(String.valueOf(propertyObj.getPack()))) {
                    obj.setIsSelect(true);
                }

                //12-90页只可以软装
                if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK) {
                    mTvPack.setText(getString(R.string.cart_print_code_limit_soft_pack_2));
                    if (obj.getShow().equals("平装")) {
                        obj.setIsActive(true);
                    } else {
                        obj.setIsActive(false);
                    }
                }
            }

            for (PrintParamObj obj : paperList) {
                if (obj.getValue().equals(String.valueOf(propertyObj.getPaper()))) {
                    obj.setIsSelect(true);
                }
            }

        }

        sizeAdapter = new CartPrintPropertyGvAdapter(getActivity(), sizeList, PrintParamResponse.KEY_SIZE);
        colorAdapter = new CartPrintPropertyGvAdapter(getActivity(), colorList, PrintParamResponse.KEY_COLOR);
        packAdapter = new CartPrintPropertyGvAdapter(getActivity(), packList, PrintParamResponse.KEY_PACK);
        paperAdapter = new CartPrintPropertyGvAdapter(getActivity(), paperList, PrintParamResponse.KEY_PAPER);
        mGvBookSize.setAdapter(sizeAdapter);
        mGvPrintColor.setAdapter(colorAdapter);
        mGvPack.setAdapter(packAdapter);
        mGvPaper.setAdapter(paperAdapter);
    }

    @OnClick({
            R.id.book_print_number_plus_ib,
            R.id.book_print_number_minus_ib,
            R.id.iv_close,
            R.id.btn_add_to_cart,
            R.id.btn_buy_now,
            R.id.btn_ok})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.book_print_number_plus_ib:
                int num = Integer.parseInt(mBookPrintNumberEt.getText().toString());
                if (num < 99) {
                    mBookPrintNumberEt.setText(String.valueOf(num + 1));
                    num += 1;
                }
                mTvPrice.setText(getString(R.string.total_price, bookPrice * num));
                break;

            case R.id.book_print_number_minus_ib:
                int numm = Integer.parseInt(mBookPrintNumberEt.getText().toString());
                if (numm > 1) {
                    mBookPrintNumberEt.setText(String.valueOf(numm - 1));
                    numm -= 1;
                }
                mTvPrice.setText(getString(R.string.total_price, bookPrice * numm));
                break;

            case R.id.iv_close:
                this.dismiss();
                break;

            case R.id.btn_ok:
                List<BasePrintProperty> printProperties = null;
                try {
                    printProperties = LoganSquare.parseList( getParams(0).get("printList"), BasePrintProperty.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(printProperties != null){
                    EventBus.getDefault().post(new CartPropertyChangeEvent(printProperties.get(0), bookPrice));
                }
                this.dismiss();
                break;
            case R.id.btn_add_to_cart:
                if (cartItem != null) {
                    for (PrintPropertyPriceObj obj : cartItem.getPrintList()) {
                        if (obj.getSize().equals(getSizeSelect()) &&
                                obj.getPack() == Integer.parseInt(getPackSelect()) &&
                                obj.getColor() == Integer.parseInt(getColorSelect()) &&
                                obj.getPaper() == Integer.parseInt(getPaperSelect()) &&
                                obj.getNum() >= 99) {
                            Toast.makeText(getActivity(), "印刷数量不可超过上限！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                addToCart();
                break;

            case R.id.btn_buy_now:
                try {
                    doAddOrder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private String getSizeSelect() {
        for (PrintParamObj obj : sizeList) {
            if (obj.isSelect()) {
                return obj.getValue();
            }
        }

        return sizeList.get(0).getValue();
    }

    private String getColorSelect() {
        for (PrintParamObj obj : colorList) {
            if (obj.isSelect()) {
                return obj.getValue();
            }
        }

        return colorList.get(0).getValue();
    }

    private String getPackSelect() {
        for (PrintParamObj obj : packList) {
            if (obj.isSelect()) {
                return obj.getValue();
            }
        }

        return packList.get(0).getValue();
    }

    private String getPaperSelect() {
        for (PrintParamObj obj : paperList) {
            if (obj.isSelect()) {
                return obj.getValue();
            }
        }

        return paperList.get(0).getValue();
    }

    private void queryBookPrice() {
        isQuery = true;
        mProgressBar.setVisibility(View.VISIBLE);
        btnAddCart.setBackgroundResource(R.drawable.shape_grey_btn_bg);
        btnBuyNow.setBackgroundResource(R.drawable.shape_grey_btn_bg);
        btnOk.setBackgroundResource(R.drawable.shape_grey_btn_bg);
        btnAddCart.setClickable(false);
        btnBuyNow.setClickable(false);
        btnOk.setClickable(false);

        Subscription s = BaseAppCompatActivity.apiService.queryBookPrice(getParams(1))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            isQuery = false;
                            mProgressBar.setVisibility(View.GONE);
                            btnAddCart.setBackgroundResource(R.drawable.selector_common_blue);
                            btnBuyNow.setBackgroundResource(R.drawable.selector_common_yellow);
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnAddCart.setClickable(true);
                            btnBuyNow.setClickable(true);
                            btnOk.setClickable(true);
                            if (response.success() && isAdded()) {
                                bookPrice = response.getPrice();
                                mTvPrice.setText(getString(R.string.total_price,
                                        response.getPrice() * Integer.parseInt(mBookPrintNumberEt.getText().toString())));
                            }
                        },
                        throwable -> {
                            btnAddCart.setBackgroundResource(R.drawable.selector_common_blue);
                            btnBuyNow.setBackgroundResource(R.drawable.selector_common_yellow);
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnAddCart.setClickable(true);
                            btnBuyNow.setClickable(true);
                            btnOk.setClickable(true);
                        }
                );
        ((BaseAppCompatActivity) getActivity()).addSubscription(s);
    }

    private void addToCart() {
        tfProgressDialog.show();
        HashMap<String, String> params = getParams(0);
        tfProgressDialog.show();

        Subscription s = BaseAppCompatActivity.apiService.addCartItem(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            tfProgressDialog.dismiss();
                            if (response.success()) {
                                printId = response.getPrintIds().get(0);

                                CartPrintPropertyDialog.this.dismiss();
                                //修改印刷属性
                                if (propertyObj != null) {
                                    propertyObj.setPrintId(response.getPrintIds().get(0));
//                                    propertyObj.setData(params.get(PrintParamResponse.KEY_SIZE),
//                                            Integer.parseInt(params.get(PrintParamResponse.KEY_COLOR)),
//                                            Integer.parseInt(params.get(PrintParamResponse.KEY_PACK)),
//                                            Integer.parseInt(params.get(PrintParamResponse.KEY_PAPER)),
//                                            Integer.parseInt(params.get("num")),
//                                            bookPrice);
                                    EventBus.getDefault().post(new CartPropertyChangeEvent(propertyObj));
                                    //新加到印刷车
                                } else {
                                    EventBus.getDefault().post(new CartAddClickEvent());
                                    Toast.makeText(getActivity(), "恭喜，已添加到印刷车", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), response.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        , throwable -> {
                            tfProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        ((BaseAppCompatActivity) getActivity()).addSubscription(s);
    }

    private void doAddOrder() throws IOException {
        List<PrintPropertyTypeObj> baseObjs = new ArrayList<>();
        PrintPropertyTypeObj baseObj = new PrintPropertyTypeObj();
        for (PrintParamObj obj : sizeList) {
            if (obj.isSelect()) {
                baseObj.setSize(obj.getValue());
            }
        }

        for (PrintParamObj obj : colorList) {
            if (obj.isSelect()) {
                baseObj.setColor(Integer.parseInt(obj.getValue()));
            }
        }

        for (PrintParamObj obj : packList) {
            if (obj.isSelect()) {
                baseObj.setPack(Integer.parseInt(obj.getValue()));
            }
        }

        for (PrintParamObj obj : paperList) {
            if (obj.isSelect()) {
                baseObj.setPaper(Integer.parseInt(obj.getValue()));
            }
        }
        baseObj.setBookId(bookId);
        baseObj.setBookType(Integer.parseInt(bookType));
        baseObj.setPrintId(printId);
        baseObj.setNum(Integer.parseInt(mBookPrintNumberEt.getText().toString()));
        baseObj.setPageNum(pageNum);
        baseObj.setAddressId(0);
        baseObj.setBookCover(bookCover);
        baseObj.setBookName(URLEncoder.encode(bookName));
        baseObj.setCreateTime(Long.valueOf(createTime));
        baseObj.setExpressId(1);
        baseObjs.add(baseObj);

        tfProgressDialog.show();

        btnOk.setBackgroundResource(R.drawable.shape_grey_btn_bg);
        btnOk.setClickable(false);
        Subscription s = BaseAppCompatActivity.apiService.addOrderNew(original + 1 + "", LoganSquare.serialize(baseObjs, PrintPropertyTypeObj.class))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnOk.setClickable(true);
                            tfProgressDialog.dismiss();
                            EventBus.getDefault()
                                    .post(new CartBuyNowEvent(response, requestCode, original));
                        }
                        , throwable -> {
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnOk.setClickable(true);
                            tfProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        ((BaseAppCompatActivity) getActivity()).addSubscription(s);
    }

    /**
     * 获取印刷参数
     * @param type 0默认是返回object形式，1是返回string形式的参数
     * @return
     */
    private HashMap<String, String> getParams(int type) {
        HashMap<String, String> params = new HashMap<>();
        BasePrintProperty printProperty = new BasePrintProperty();
        List<BasePrintProperty> printProperties = new ArrayList<>();
        if (cartItem == null) {
            params.put("bookId", bookId);
            params.put("bookType", bookType);
            params.put("pageNum",String.valueOf(pageNum));
            params.put("bookCover",bookCover);
            params.put("bookName",URLEncoder.encode(bookName));
            params.put("createTime",createTime);


        } else {
            params.put("bookId", cartItem.getBookId());
            params.put("bookType", String.valueOf(cartItem.getBookType()));
            params.put("pageNum",String.valueOf(cartItem.getTotalPage()));
            params.put("bookCover",cartItem.getCoverImage());
            params.put("bookName",URLEncoder.encode(cartItem.getTitle()));
            params.put("createTime",cartItem.getDate());
        }
        printProperty.setNum(Integer.parseInt(mBookPrintNumberEt.getText().toString()));
        if (propertyObj != null) {
            printProperty.setPrintId(propertyObj.getPrintId());
        }

        //尺寸
        for (PrintParamObj paramObj : sizeList) {
            if (paramObj.isSelect()) {
                printProperty.setSize(paramObj.getValue());
            }
        }

        //颜色
        for (PrintParamObj paramObj : colorList) {
            if (paramObj.isSelect()) {
                printProperty.setColor(Integer.parseInt(paramObj.getValue()));
            }
        }

        //包装方式
        for (PrintParamObj paramObj : packList) {
            if (paramObj.isSelect()) {
                printProperty.setPack(Integer.parseInt(paramObj.getValue()));
            }
        }

        //纸张
        for (PrintParamObj paramObj : paperList) {
            if (paramObj.isSelect()) {
                printProperty.setPaper(Integer.parseInt(paramObj.getValue()));
            }
        }

        printProperties.add(printProperty);

        //已拆分
        if (printCode == TypeConstant.PRINT_CODE_LIMIT_MORE_SPLIT) {
            params.put("child", "1");
        }

        try {
            if(type == 1){
                if (propertyObj != null) {
                    params.put("printId", propertyObj.getPrintId());
                }
                params.put(PrintParamResponse.KEY_SIZE, printProperty.getSize());
                params.put(PrintParamResponse.KEY_COLOR, String.valueOf(printProperty.getColor()));
                params.put(PrintParamResponse.KEY_PACK, String.valueOf(printProperty.getPack()));
                params.put(PrintParamResponse.KEY_PAPER, String.valueOf(printProperty.getPaper()));
            } else {
                params.put("printList", LoganSquare.serialize(printProperties, BasePrintProperty.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

    @Subscribe
    public void onEvent(CartItemClickEvent event) {
        if (isQuery) {
            return;
        }
        if (event instanceof CartItemClickEvent) {
            String key = (String) event.view.getTag(R.string.tag_ex);
            PrintParamObj paramObj = (PrintParamObj) event.view.getTag(R.string.tag_obj);
            switch (key) {
                case PrintParamResponse.KEY_SIZE:
                    for (PrintParamObj obj : sizeList) {
                        if(obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()){
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);
                    sizeAdapter.notifyDataSetChanged();
                    break;

                case PrintParamResponse.KEY_COLOR:
                    for (PrintParamObj obj : colorList) {
                        if(obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()){
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);
                    colorAdapter.notifyDataSetChanged();
                    break;

                case PrintParamResponse.KEY_PACK:
                    if (!paramObj.isActive()) {
                        return;
                    }
                    for (PrintParamObj obj : packList) {
                        if(obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()){
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);
                    packAdapter.notifyDataSetChanged();
                    break;

                case PrintParamResponse.KEY_PAPER:
                    int index = paperList.indexOf(paramObj);
                    for (PrintParamObj obj : paperList) {
                        if (obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()) {
                            return;
                        }
                        obj.setIsSelect(false);
                    }

                    //圈子时光书
                    if (Integer.parseInt(bookType) == TypeConstant.BOOK_TYPE_CIRCLE) {
                        for (PrintParamObj printParamObj : packList) {
                            int packIndex = packList.indexOf(printParamObj);
                            //铜版纸 只能平装
                            if (index == 0) {
                                if (packIndex == 0) {
                                    printParamObj.setIsActive(true);
                                    printParamObj.setIsSelect(true);
                                } else {
                                    printParamObj.setIsSelect(false);
                                    printParamObj.setIsActive(false);
                                }

                                //卡纸 不能平装
                            } else if (index == 1) {
                                if (packIndex == 0) {
                                    printParamObj.setIsActive(false);
                                    printParamObj.setIsActive(false);
                                } else {
                                    if(packIndex == 1){
                                        printParamObj.setIsSelect(true);
                                    } else {
                                        printParamObj.setIsSelect(false);
                                    }
                                    printParamObj.setIsActive(true);
                                }
                            }
                            packAdapter.notifyDataSetChanged();
                        }
                    }

                    paramObj.setIsSelect(true);
                    paperAdapter.notifyDataSetChanged();
                    break;
            }
            queryBookPrice();
        }
    }

    public interface DismissListener {
        void dismiss();
    }

    class EditTextWatcher implements TextWatcher {
        protected EditText editText;
        private int maxNum;
        private boolean isChange;

        public EditTextWatcher(EditText editText, int max, boolean isChange) {
            this.editText = editText;
            this.maxNum = max;
            this.isChange = isChange;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isChange) {
                processNumEdt(s);
            } else {
                if (!TextUtils.isEmpty(s.toString())) {
                    float exchanged = Float.parseFloat(s.toString());
                    Selection.setSelection(s, s.length());
                }

            }
        }

        private void processNumEdt(Editable s) {
            String str = s.toString();
            if (TextUtils.isEmpty(s) || Integer.parseInt(str) == 0) {
                s.replace(0, s.length(), "1");
                mBookPrintNumberPlusIb.setBackgroundResource(R.drawable.shape_red_border_bg);
                mBookPrintNumberPlusIb.setImageResource(R.drawable.ic_plus_press);
                mBookPrintNumberPlusIb.setEnabled(true);
            } else if (Integer.parseInt(str) == 1) {
                mBookPrintNumberMinusIb.setBackgroundResource(
                        R.drawable.shape_grey_border_bg);
                mBookPrintNumberMinusIb.setImageResource(R.drawable.ic_minus_default);
                mBookPrintNumberMinusIb.setEnabled(false);
            } else {
                int num = Integer.parseInt(str);
                if (num > maxNum) {
                    s.replace(0, s.length(), "99");
                }

                if (num >= maxNum) {
                    mBookPrintNumberPlusIb.setBackgroundResource(
                            R.drawable.shape_grey_border_bg);
                    mBookPrintNumberPlusIb.setImageResource(R.drawable.ic_plus_default);
                    mBookPrintNumberPlusIb.setEnabled(false);
                } else {
                    mBookPrintNumberPlusIb.setBackgroundResource(
                            R.drawable.shape_red_border_bg);
                    mBookPrintNumberPlusIb.setImageResource(R.drawable.ic_plus_press);
                    mBookPrintNumberPlusIb.setEnabled(true);
                }
                mBookPrintNumberMinusIb.setBackgroundResource(R.drawable.shape_red_border_bg);
                mBookPrintNumberMinusIb.setImageResource(R.drawable.ic_minus_press);
                mBookPrintNumberMinusIb.setEnabled(true);
            }
            Selection.setSelection(s, s.length());
            mTvPrice.setText(getString(R.string.total_price, bookPrice * (Integer.parseInt(mBookPrintNumberEt.getText().toString()))));
        }
    }
}
