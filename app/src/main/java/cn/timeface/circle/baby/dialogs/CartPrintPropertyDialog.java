package cn.timeface.circle.baby.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CartActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CartPrintPropertyGvAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.events.CartAddClickEvent;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.CartPropertyChangeEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.PrintCartItem;
import cn.timeface.circle.baby.support.api.models.base.BasePrintProperty;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.NoScrollGridView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * @author YW.SUN
 * @from 2015/5/21
 * @TODO
 */
public class CartPrintPropertyDialog extends DialogFragment implements IEventBus {

    public final static int BOOK_MAX_COUNT = 99;
    public final static int CALENDAR_MAX_COUNT = 999;

    public final static int TYPE_ADD_CART = 0;
    public final static int TYPE_BUY_NOW = 1;
    public final static int TYPE_NORMAL = 2;
    public final static int REQUEST_CODE_MINETIME = 0;
    public final static int REQUEST_CODE_WECHAT = 1;
    public final static int REQUEST_CODE_QQ = 2;
    public final static int REQUEST_CODE_POD = 3;
    public final static int REQUEST_CODE_SPLIT = 4;
    public final static int REQUEST_CODE_DIARY_CARD = 5;
    public final static int REQUEST_CODE_RECOGNIZE_CARD = 6;
    public final static int REQUEST_CODE_BOOK_LIST = 7;

    List<PrintParamObj> sizeList = new ArrayList<>();
    List<PrintParamObj> colorList = new ArrayList<>();
    List<PrintParamObj> packList = new ArrayList<>();
    List<PrintParamObj> paperList = new ArrayList<>();
    DismissListener dismissListener;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.iv_close)
    ImageView ivClose;
    @Bind(R.id.book_print_number_minus_ib)
    ImageButton mBookPrintNumberMinusIb;
    @Bind(R.id.book_print_number_et)
    EditText mBookPrintNumberEt;
    @Bind(R.id.book_print_number_plus_ib)
    ImageButton mBookPrintNumberPlusIb;
    @Bind(R.id.iv_book_cover)
    ImageView ivBookCover;
    @Bind(R.id.rl_book_cover)
    RelativeLayout rlBookCover;
    @Bind(R.id.iv_book_tag)
    ImageView mIvBookTag;
    @Bind(R.id.rl_cover)
    RelativeLayout mRlCover;
    @Bind(R.id.gv_book_size)
    NoScrollGridView mGvBookSize;
    @Bind(R.id.gv_print_color)
    NoScrollGridView mGvPrintColor;
    @Bind(R.id.gv_paper)
    NoScrollGridView mGvPaper;
    @Bind(R.id.tv_pack_label)
    TextView mTvPack;
    @Bind(R.id.gv_pack)
    NoScrollGridView mGvPack;
    @Bind(R.id.btn_add_to_cart)
    Button btnAddCart;
    @Bind(R.id.btn_buy_now)
    Button btnBuyNow;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.ll_btn_ok)
    LinearLayout llBtnOk;
    @Bind(R.id.ll_content)
    LinearLayout llContent;
    @Bind(R.id.tv_max_amount)
    TextView tvMaxAmount;
    @Bind(R.id.iv_front_mask)
    ImageView ivMask;

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
    private int openBookType;
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
    private Context context;
    private int limit = BOOK_MAX_COUNT;

    private final ApiService apiService = ApiFactory.getApi().getApiService();

    public static CartPrintPropertyDialog getInstance(PrintCartItem printCartItem,
                                                      PrintPropertyPriceObj obj,
                                                      List<PrintParamResponse> responseList,
                                                      String bookId,
                                                      String bookType,
                                                      int openBookType,
                                                      int requestCode,
                                                      int printCode,
                                                      String bookCover,
                                                      int original,
                                                      int pageNum,
                                                      String bookName,
                                                      String createTime, Context context) {
        CartPrintPropertyDialog dialog = new CartPrintPropertyDialog(context);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cart_item", printCartItem);
        bundle.putSerializable("print_property", obj);
        bundle.putParcelableArrayList("param_response", (ArrayList<? extends Parcelable>) responseList);
        bundle.putString("book_id", bookId);
        bundle.putString("book_type", bookType);
        bundle.putInt("open_book_type", openBookType);
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

    public CartPrintPropertyDialog() {
    }

    public CartPrintPropertyDialog(Context context) {
        this.context = context;
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
        ButterKnife.unbind(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tfProgressDialog = TFProgressDialog.getInstance("");
        cartItem = (PrintCartItem) getArguments().getSerializable("cart_item");
        propertyObj = (PrintPropertyPriceObj) getArguments().getSerializable("print_property");
        bookId = getArguments().getString("book_id");
        bookType = getArguments().getString("book_type");
        openBookType = getArguments().getInt("open_book_type", -1);
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
//        if(Integer.parseInt(bookType) == TypeConstant.BOOK_TYPE_CIRCLE){
//            mTvPack.setVisibility(View.GONE);
//        } else {
//        mTvPack.setVisibility(View.VISIBLE);
//        }

        /*for(PrintParamObj obj : packList){
                if (pageNum >= 12 && pageNum <= 20) {
                    mTvPack.setText("(照片书12-20页，只支持平装)");
                    if(obj.getShow().equals("平装")){
                        obj.setActive(true);
                    }else{
                        obj.setActive(false);
                    }
                } else if (pageNum > 20 && pageNum <= 60) {
                    mTvPack.setText("");
                    obj.setActive(true);
                } else if (pageNum > 60) {
                    mTvPack.setText("(照片书大于页，只支持平装)");
                    if(obj.getShow().equals("平装")){
                        obj.setActive(true);
                    }else{
                        obj.setActive(false);
                    }
                }
        }*/

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
        int imageWidth = DeviceUtil.dpToPx(getContext().getResources(), 120);
        switch (Integer.valueOf(bookType)) {
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK://精装照片书
            case BookModel.BOOK_TYPE_PAINTING://绘画集
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS://成长语录
            case BookModel.BOOK_TYPE_CALENDAR://台历
                imageWidth = DeviceUtil.dpToPx(getContext().getResources(), 120);
                break;
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK://成长纪念册
                imageWidth = DeviceUtil.dpToPx(getContext().getResources(), 100);
                break;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                imageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivBookCover.setLayoutParams(lp);
        ivBookCover.setMaxWidth(imageWidth);

        if (TextUtils.equals(bookType, String.valueOf(BookModel.BOOK_TYPE_CALENDAR))) {
            limit = CALENDAR_MAX_COUNT;
            llContent.setVisibility(View.GONE);
            ivMask.setVisibility(View.GONE);

            Glide.with(getContext())
                    .load(openBookType == CalendarModel.BOOK_TYPE_CALENDAR_HORIZONTAL
                            ? R.drawable.bg_calendar_horizontal : R.drawable.bg_calendar_vertical)
                    .into(ivBookCover);
        } else {
            limit = BOOK_MAX_COUNT;
            llContent.setVisibility(View.VISIBLE);
            ivMask.setVisibility(View.VISIBLE);

            GlideUtil.displayImage(bookCover, ivBookCover);
        }

        tvMaxAmount.setText(String.format(Locale.CHINESE, "(上限%s本)", limit));

        mBookPrintNumberEt.addTextChangedListener(new EditTextWatcher(mBookPrintNumberEt, limit, false));

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
//            switch (cartItem.getBookType()) {
//                case TypeConstant.BOOK_TYPE_QQ:
//                    mIvBookTag.setImageResource(R.drawable.ic_time_book_tag_qq);
//                    break;
//
//                case TypeConstant.BOOK_TYPE_WECHAT:
//                    mIvBookTag.setImageResource(R.drawable.ic_time_book_tag_wechat);
//                    break;
//
//                default:
            mIvBookTag.setVisibility(View.GONE);
//                    break;
//
//            }
        }

        if (propertyObj == null) {
            packList.get(0).setIsSelect(true);

            // 选中装订方式中对应的可选颜色
            selectAvailableList(colorList, packList.get(0).getColorList());

            // 选中装订方式中对应的可选纸张类别
            selectAvailableList(paperList, packList.get(0).getPaperList());

            // 选中装订方式中对应的可选尺寸
            selectAvailableList(sizeList, packList.get(0).getSizeList());
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
                        obj.setActive(true);
                    } else {
                        obj.setActive(false);
                    }
                }
            }

            for (PrintParamObj obj : paperList) {
                if (obj.getValue().equals(String.valueOf(propertyObj.getPaper()))) {
                    obj.setIsSelect(true);
                }
            }

        }

//        if (propertyObj == null) {
//            sizeList.get(0).setIsSelect(true);
//            colorList.get(0).setIsSelect(true);
//            packList.get(0).setIsSelect(true);
//            if (paperList.size() > 0) paperList.get(0).setIsSelect(true);
//
//            for (PrintParamObj obj : packList) {
//                if (printCode == TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK) {
//                    mTvPack.setText(getString(R.string.cart_print_code_limit_soft_pack_2));
//                    if (obj.getShow().equals("平装")) {
//                        obj.setActive(true);
//                    } else {
//                        obj.setActive(false);
//                    }
//                }
//            }
//        } else {
//            mBookPrintNumberEt.setText(String.valueOf(propertyObj.getNum()));
//            for (PrintParamObj obj : sizeList) {
//                if (obj.getValue().equals(propertyObj.getSize())) {
//                    obj.setIsSelect(true);
//                }
//            }
//
//            for (PrintParamObj obj : colorList) {
//                if (obj.getValue().equals(String.valueOf(propertyObj.getColor()))) {
//                    obj.setIsSelect(true);
//                }
//            }
//
//            for (PrintParamObj obj : packList) {
//                if (obj.getValue().equals(String.valueOf(propertyObj.getPack()))) {
//                    obj.setIsSelect(true);
//                }
//
//                //12-90页只可以软装
//                if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK) {
//                    mTvPack.setText(getString(R.string.cart_print_code_limit_soft_pack_2));
//                    if (obj.getShow().equals("平装")) {
//                        obj.setActive(true);
//                    } else {
//                        obj.setActive(false);
//                    }
//                }
//            }
//
//            for (PrintParamObj obj : paperList) {
//                if (obj.getValue().equals(String.valueOf(propertyObj.getPaper()))) {
//                    obj.setIsSelect(true);
//                }
//            }
//
//        }

        sizeAdapter = new CartPrintPropertyGvAdapter(getActivity(), sizeList, PrintParamResponse.KEY_SIZE);
        colorAdapter = new CartPrintPropertyGvAdapter(getActivity(), colorList, PrintParamResponse.KEY_COLOR);
        packAdapter = new CartPrintPropertyGvAdapter(getActivity(), packList, PrintParamResponse.KEY_PACK);
        paperAdapter = new CartPrintPropertyGvAdapter(getActivity(), paperList, PrintParamResponse.KEY_PAPER);
        mGvBookSize.setAdapter(sizeAdapter);
        mGvPrintColor.setAdapter(colorAdapter);
        mGvPack.setAdapter(packAdapter);
        mGvPaper.setAdapter(paperAdapter);
    }

    private void selectAvailableList(List<PrintParamObj> dataList, List<String> selectableList) {
        if (selectableList != null && selectableList.size() > 0) {
            for (PrintParamObj obj : dataList) {
                if (obj.getValue().equals(selectableList.get(0))) {
                    obj.setIsSelect(true);
                    break;
                }
            }
        } else {
            dataList.get(0).setIsSelect(true); // 默认选中第一条
        }
    }

    private void selectAvailableList2(List<PrintParamObj> dataList, List<String> selectableList) {
        if (selectableList != null && selectableList.size() > 0) {
            PrintParamObj selectedObj = null;
            for (PrintParamObj obj : dataList) {
                if (obj.getValue().equals(selectableList.get(0))) {
                    selectedObj = obj;
                }
                obj.setIsSelect(false);
            }
            if (selectedObj != null) {
                selectedObj.setIsSelect(true);
            }
        }
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
                if (num < limit) {
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
                    printProperties = LoganSquare.parseList(getParams(0).get("printList"), BasePrintProperty.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (printProperties != null) {
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
                                obj.getNum() >= limit) {
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

        Subscription s = apiService.queryBookPrice(getParams(1))
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
                            isQuery = false;
                            Log.e(CartPrintPropertyDialog.class.getSimpleName(), throwable.getLocalizedMessage());
                            Toast.makeText(getActivity(), "价格查询失败", Toast.LENGTH_SHORT).show();
                        }
                );
        if (getActivity() instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) getActivity()).addSubscription(s);
        } else if (getActivity() instanceof BasePresenterAppCompatActivity) {
            ((BasePresenterAppCompatActivity) getActivity()).addSubscription(s);
        }
    }

    private void addToCart() {
        tfProgressDialog.show(getChildFragmentManager(), "");
        HashMap<String, String> params = getParams(0);

        Subscription s = apiService.addCartItem(params)
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
//                                    Toast.makeText(getActivity(), "恭喜，已添加到印刷车", Toast.LENGTH_LONG).show();
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("已添加到印刷车")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).setPositiveButton("去查看", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            CartActivity.open(context);
                                        }
                                    }).show();
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
        if (getActivity() instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) getActivity()).addSubscription(s);
        } else if (getActivity() instanceof BasePresenterAppCompatActivity) {
            ((BasePresenterAppCompatActivity) getActivity()).addSubscription(s);
        }
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
        baseObj.setCreateTime(Long.valueOf(createTime) / 1000);
        baseObj.setExpressId(1);
        baseObjs.add(baseObj);

        tfProgressDialog.show(getChildFragmentManager(), "");

        btnOk.setBackgroundResource(R.drawable.shape_grey_btn_bg);
        btnOk.setClickable(false);
        Subscription s = apiService.addOrder(LoganSquare.serialize(baseObjs, PrintPropertyTypeObj.class), TypeConstant.APP_ID)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnOk.setClickable(true);
                            tfProgressDialog.dismiss();
                            EventBus.getDefault()
                                    .post(new CartBuyNowEvent(response, requestCode, original, baseObjs));
                        }
                        , throwable -> {
                            btnOk.setBackgroundResource(R.drawable.selector_blue_btn_bg);
                            btnOk.setClickable(true);
                            tfProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        if (getActivity() instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) getActivity()).addSubscription(s);
        } else if (getActivity() instanceof BasePresenterAppCompatActivity) {
            ((BasePresenterAppCompatActivity) getActivity()).addSubscription(s);
        }
    }

    /**
     * 获取印刷参数
     *
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
            params.put("pageNum", String.valueOf(pageNum));
            params.put("bookCover", bookCover);
            params.put("bookName", URLEncoder.encode(bookName));
            params.put("createTime", createTime);


        } else {
            params.put("bookId", cartItem.getBookId());
            params.put("bookType", String.valueOf(cartItem.getBookType()));
            params.put("pageNum", String.valueOf(cartItem.getTotalPage()));
            params.put("bookCover", cartItem.getCoverImage());
            params.put("bookName", URLEncoder.encode(cartItem.getTitle()));
            params.put("createTime", cartItem.getDate());
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
            if (type == 1) {
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
                        if (obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()) {
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);

                    // 选中尺寸中对应的可选颜色
                    selectAvailableList2(colorList, paramObj.getColorList());
                    // 选中尺寸中对应的可选纸张类别
                    selectAvailableList2(paperList, paramObj.getPaperList());
                    // 选中尺寸中对应的可选装订方式
                    selectAvailableList2(packList, paramObj.getPackList());

                    break;

                case PrintParamResponse.KEY_COLOR:
                    for (PrintParamObj obj : colorList) {
                        if (obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()) {
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);
//                    colorAdapter.notifyDataSetChanged();

                    // 选中颜色中对应的可选尺寸
                    selectAvailableList2(sizeList, paramObj.getSizeList());
                    // 选中颜色中对应的可选纸张类别
                    selectAvailableList2(paperList, paramObj.getPaperList());
                    // 选中颜色中对应的可选装订方式
                    selectAvailableList2(packList, paramObj.getPackList());

                    break;

                case PrintParamResponse.KEY_PACK:
                    if (!paramObj.isActive()) {
                        return;
                    }
                    for (PrintParamObj obj : packList) {
                        if (obj.getValue() == paramObj.getValue() &&
                                obj.isSelect() && paramObj.isSelect()) {
                            return;
                        }
                        obj.setIsSelect(false);
                    }
                    paramObj.setIsSelect(true);
//                    packAdapter.notifyDataSetChanged();

                    // 选中装订方式中对应的可选颜色
                    selectAvailableList2(colorList, paramObj.getColorList());
                    // 选中装订方式中对应的可选尺寸
                    selectAvailableList2(sizeList, paramObj.getSizeList());
                    // 选中装订方式中对应的可选纸张类别
                    selectAvailableList2(paperList, paramObj.getPaperList());

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
                    /*if (Integer.parseInt(bookType) == TypeConstant.BOOK_TYPE_CIRCLE) {
                        for (PrintParamObj printParamObj : packList) {
                            int packIndex = packList.indexOf(printParamObj);
                            //铜版纸 只能平装
                            if (index == 0) {
                                if (packIndex == 0) {
                                    printParamObj.setActive(true);
                                    printParamObj.setIsSelect(true);
                                } else {
                                    printParamObj.setIsSelect(false);
                                    printParamObj.setActive(false);
                                }

                                //卡纸 不能平装
                            } else if (index == 1) {
                                if (packIndex == 0) {
                                    printParamObj.setActive(false);
                                    printParamObj.setActive(false);
                                } else {
                                    if(packIndex == 1){
                                        printParamObj.setIsSelect(true);
                                    } else {
                                        printParamObj.setIsSelect(false);
                                    }
                                    printParamObj.setActive(true);
                                }
                            }
                            packAdapter.notifyDataSetChanged();
                        }
                    }*/

                    paramObj.setIsSelect(true);
//                    paperAdapter.notifyDataSetChanged();

                    // 选中纸张类别中对应的可选颜色
                    selectAvailableList2(colorList, paramObj.getColorList());
                    // 选中纸张类别中对应的可选尺寸
                    selectAvailableList2(sizeList, paramObj.getSizeList());
                    // 选中纸张类别中对应的可选装订方式
                    selectAvailableList2(packList, paramObj.getPackList());

                    break;
            }

            sizeAdapter.notifyDataSetChanged();
            colorAdapter.notifyDataSetChanged();
            paperAdapter.notifyDataSetChanged();
            packAdapter.notifyDataSetChanged();

            queryBookPrice();
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        ButterKnife.bind(this, rootView);
//        return rootView;
//    }

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
                    s.replace(0, s.length(), String.valueOf(limit));
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
