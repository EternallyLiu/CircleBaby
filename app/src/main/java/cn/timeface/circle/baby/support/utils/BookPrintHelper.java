package cn.timeface.circle.baby.support.utils;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.support.api.models.responses.PrintStatusResponse;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * @author YW.SUN
 * @from 2016/3/11
 * @TODO
 */
public class BookPrintHelper {
    BasePresenterAppCompatActivity context;
    TFProgressDialog progressDialog;
    TFDialog tougueDialog;
    ApiService apiService;
    int bookType;
    int openBookType;
    int pageNum;
    int bookSizeId;
    String bookId;
    String bookCover;
    String bookName;
    long updateTime;
    int reqCode;

    public BookPrintHelper(
            BasePresenterAppCompatActivity context,
            int bookType,
            int openBookType,
            int pageNum,
            int bookSizeId,
            String bookId,
            String bookCover,
            String bookName,
            long updateDate,
            int requestCode) {
        this.context = context;
        this.bookType = bookType;
        this.openBookType = openBookType;
        this.pageNum = pageNum;
        this.bookSizeId = bookSizeId;
        this.bookId = bookId;
        this.bookCover = bookCover;
        this.bookName = bookName;
        this.updateTime = updateDate;
        this.reqCode = requestCode;
        apiService = ApiFactory.getApi().getApiService();
        progressDialog = new TFProgressDialog();
        tougueDialog = TFDialog.getInstance();
        tougueDialog.setPositiveButton("确定", v -> {
            tougueDialog.dismiss();
        });
    }

    /**
     * 获取该书的印刷状态
     */
    public void reqPrintStatus() {
        progressDialog.show(context.getSupportFragmentManager(), "dialog");
        Subscription s = apiService.printStatus(bookType, pageNum, bookSizeId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
                                doPrintStatus(response);
                            } else {
                                Toast.makeText(context, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            progressDialog.dismiss();
                            Toast.makeText(context, "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        context.addSubscription(s);
    }

    /**
     * 处理该书的印刷状态
     *
     * @param response
     */
    public void doPrintStatus(PrintStatusResponse response) {
        switch (response.getPrintCode()) {
            case TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE:
                progressDialog.dismiss();
                tougueDialog.setMessage(context.getString(R.string.cart_print_code_limit_had_delete));
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_LIMIT_LESS:
                progressDialog.dismiss();
                tougueDialog.setMessage("少于20页，不可印刷");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");

                break;

            case TypeConstant.PRINT_CODE_LIMIT_MORE:
                progressDialog.dismiss();
                tougueDialog.setMessage("大于60页，不可印刷");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_LIMIT_8806:
                progressDialog.dismiss();
                tougueDialog.setMessage("印制3寸日记卡片需18张");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_LIMIT_8807:
                progressDialog.dismiss();
                tougueDialog.setMessage("印制4寸日记卡片需15张");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_LIMIT_8808:
                progressDialog.dismiss();
                tougueDialog.setMessage("印制5寸日记卡片需9张");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_LIMIT_8809:
                progressDialog.dismiss();
                tougueDialog.setMessage("印制识图卡片需8张");
                tougueDialog.show(context.getSupportFragmentManager(), "dialog");
                break;

            case TypeConstant.PRINT_CODE_NORMAL:
                showCartDialog(TypeConstant.PRINT_CODE_NORMAL);
                break;
        }
    }

    /**
     * 加入购物车
     */
    private void showCartDialog(final int printCode) {
        Log.d("-------->", "-------->showCartDialog openBookType:" + openBookType);

        Subscription s = apiService.queryParamList(openBookType, pageNum)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
                                progressDialog.dismiss();
                                List<PrintParamObj> valueList1 = new ArrayList<PrintParamObj>();
                                List<PrintParamResponse> dataList = response.getDataList();
                                for (PrintParamResponse printParamResponse : dataList){
                                    if(printParamResponse.getKey().equals("size")){
                                        List<PrintParamObj> valueList = printParamResponse.getValueList();
                                        for(PrintParamObj printParamObj : valueList){
                                            if(printParamObj.getValue().equals(bookSizeId + "")){
                                                valueList1.add(printParamObj);
                                            }
                                        }
                                        if(valueList1.size()>0){
                                            printParamResponse.setValueList(valueList1);
                                        }
                                    }
                                }

                                CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(null,
                                        null,
                                        dataList,
                                        bookId,
                                        String.valueOf(bookType),
                                        reqCode,
                                        printCode,
                                        bookCover,
                                        TypeConstant.FROM_PHONE,
                                        pageNum,
                                        bookName,
                                        String.valueOf(updateTime),context);
                                dialog.show(context.getSupportFragmentManager(), "dialog");

                            } else {
                                Toast.makeText(context, response.info, Toast.LENGTH_SHORT).show();
                            }
                        }
                        , throwable -> {
                            progressDialog.dismiss();
                            Toast.makeText(context, "服务器返回失败", Toast.LENGTH_SHORT).show();
                        }
                );
        context.addSubscription(s);
    }
}
