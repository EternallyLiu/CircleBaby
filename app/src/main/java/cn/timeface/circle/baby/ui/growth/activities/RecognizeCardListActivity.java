package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.constants.CountlyEventHelper;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.responses.EditBookResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CardPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.RecognizeCardListAdapter;
import cn.timeface.circle.baby.ui.growth.events.CardEditEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 识图卡片列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class RecognizeCardListActivity extends ProductionListActivity implements CardPresentation.RecognizeCardView, View.OnClickListener, IEventBus {
    RecognizeCardListAdapter cardListAdapter;
    CardPresenter cardPresenter;
    List<KnowledgeCardObj> selectCards = new ArrayList<>();
    int bookPage = 8;
    TFDialog tougueDialog;

    public static void open(Context context) {
        Intent intent = new Intent(context, RecognizeCardListActivity.class);
        intent.putExtra("book_type", BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(FastData.getBabyNickName() + "识图卡片");
        cardPresenter = new CardPresenter(this);
        RxView.clicks(btnAskPrint)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(
                        aVoid -> onClick(btnAskPrint),
                        throwable -> Log.e(TAG, throwable.getLocalizedMessage())
                );
        btnAskPrint.setText("申请印刷");
        tvTip.setText(" 每套选择" + bookPage + "张（也可以是" + bookPage + "的倍数）");

        rvBooks.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0,
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                getResources().getDimensionPixelOffset(R.dimen.size_72));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            CardPublishActivity.open(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateClick() {
        CardPublishActivity.open(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Iterator iterator = selectCards.iterator();
        //去掉没选择的
        while (iterator.hasNext()) {
            KnowledgeCardObj knowledgeCardObj = (KnowledgeCardObj) iterator.next();
            if (!knowledgeCardObj.select()) {
                iterator.remove();
            }
        }
        cardPresenter.loadRecognizeCard();
    }

    @Override
    public void setStateView(boolean loading) {
        if (loading) {
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
        } else {
            stateView.finish();
        }
    }

    @Override
    public void showError(String errMsg) {
        showToast(errMsg);
    }

    @Override
    public void setRecognizeCardData(List<KnowledgeCardObj> knowledgeCardObjs) {
        for (KnowledgeCardObj knowledgeCardObj : knowledgeCardObjs) {
            if (selectCards.contains(knowledgeCardObj)) {
                knowledgeCardObj.setSelect(1);
            }
        }

        if (cardListAdapter == null) {
            rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
            cardListAdapter = new RecognizeCardListAdapter(this, knowledgeCardObjs, this);
            rvBooks.setAdapter(cardListAdapter);
        } else {
            cardListAdapter.setListData(knowledgeCardObjs);
            cardListAdapter.notifyDataSetChanged();
        }

        if (cardListAdapter.getListData().isEmpty()) {
            tvTip.setVisibility(View.GONE);
            btnAskPrint.setVisibility(View.GONE);

            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyViewContent(false);
        } else {
            llEmpty.setVisibility(View.GONE);

            tvTip.setVisibility(View.VISIBLE);
            btnAskPrint.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkPrintInfo() {
        if (tougueDialog == null) {
            tougueDialog = TFDialog.getInstance();
            tougueDialog.setPositiveButton("确定", v -> tougueDialog.dismiss());
        }

        if (selectCards.size() < bookPage) {
            tougueDialog.setMessage(
                    "识图卡片需要"
                            + bookPage + "张，只选了" + selectCards.size()
                            + "张还少" + (bookPage - selectCards.size() + "张。"));
            tougueDialog.show(getSupportFragmentManager(), "dialog");
            return false;
        } else {
            //bookPage 的倍数才可以申请印制
            if (selectCards.size() % bookPage != 0) {
                tougueDialog.setMessage(
                        "识图卡片需要"
                                + bookPage + "的倍数哦~");
                tougueDialog.show(getSupportFragmentManager(), "dialog");
            }
            return selectCards.size() % bookPage == 0;
        }
    }

    private void askPrint() {
        //申请印刷
        Observable.just(selectCards)
                .filter(cardObjs -> checkPrintInfo())
                .flatMap(new Func1<List<KnowledgeCardObj>, Observable<EditBookResponse>>() {
                    @Override
                    public Observable<EditBookResponse> call(List<KnowledgeCardObj> cardObjs) {
                        StringBuffer sb = new StringBuffer("{\"dataList\":[");
                        int index = 0;
                        for (KnowledgeCardObj knowledgeCardObj : selectCards) {
                            index++;
                            sb.append(knowledgeCardObj.getCardId());
                            if (index < selectCards.size()) {
                                sb.append(",");
                            } else {
                                sb.append("]}");
                            }
                        }

                        return apiService.saveProduction(
                                FastData.getBabyId(),
                                FastData.getUserInfo().getNickName(),
                                selectCards.get(0).getMedia().getImgUrl(),
                                "",
                                FastData.getBabyNickName() + "的识图卡片",
                                bookType,
                                "",
                                sb.toString(),
                                "2",
                                0,
                                bookPage
                        );
                    }
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                new BookPrintHelper(
                                        this,
                                        BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD,
                                        BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD,//识图卡片没有openBookType，直接使用本平台BookType
                                        selectCards.size(),
                                        0,//识图卡片没有booksizeid，传值0
                                        response.getDataId(),
                                        selectCards.get(0).getMedia().getImgUrl(),
                                        FastData.getBabyNickName() + "的识图卡片",
                                        System.currentTimeMillis(),
                                        CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD).reqPrintStatus();
                                CountlyEventHelper.getInstance().printEvent(FastData.getUserId(), response.getDataId());
                            } else {
                                showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    @Override
    public void onClick(View view) {
        KnowledgeCardObj knowledgeCardObj = (KnowledgeCardObj) view.getTag(R.string.tag_obj);
        switch (view.getId()) {
            //申请印刷
            case R.id.btn_ask_for_print:
                askPrint();
                break;

            //识图卡片预览
            case R.id.fl_root:

                CardPreviewActivity.open(this, knowledgeCardObj);
                break;

            //选择卡片
            case R.id.iv_select:
                int index = (int) view.getTag(R.string.tag_index);
                knowledgeCardObj.setSelect(knowledgeCardObj.select() ? 0 : 1);
                if (knowledgeCardObj.select()) {
                    if (!selectCards.contains(knowledgeCardObj)) selectCards.add(knowledgeCardObj);
                } else {
                    if (selectCards.contains(knowledgeCardObj))
                        selectCards.remove(knowledgeCardObj);
                }
                cardListAdapter.notifyItemChanged(index);

                if (selectCards.size() > 0) {
                    btnAskPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
                } else {
                    btnAskPrint.setText("申请印刷");
                }
                break;
        }
    }

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(this, event.response.getOrderId(), event.baseObjs);
            } else {
                Toast.makeText(this, event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe
    public void CardSelectEvent(CardEditEvent event) {
        if (cardListAdapter != null && !cardListAdapter.getListData().isEmpty()) {
            for (KnowledgeCardObj cardObj : cardListAdapter.getListData()) {
                if (cardObj.getCardId() == event.getCardId()) {
                    cardObj.setSelect(event.getSelect());

                    if (cardObj.select() && !selectCards.contains(cardObj)) {
                        selectCards.add(cardObj);
                    }

                    if (!cardObj.select() && selectCards.contains(cardObj)) {
                        selectCards.remove(cardObj);
                    }
                }
            }
            if (selectCards.size() > 0) {
                btnAskPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
            } else {
                btnAskPrint.setText("申请印刷");
            }
        }
    }

    @Subscribe
    public void publishRefreshEvent(PublishRefreshEvent refreshEvent){
        selectCards.addAll(refreshEvent.getDataList());
        cardPresenter.loadRecognizeCard();
    }
}
