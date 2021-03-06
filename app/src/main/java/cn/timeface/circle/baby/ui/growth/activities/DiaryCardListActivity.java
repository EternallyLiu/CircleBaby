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
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.constants.CountlyEventHelper;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.events.DiaryPublishEvent;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.responses.EditBookResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CardPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.DiaryCardListAdapter;
import cn.timeface.circle.baby.ui.growth.events.CardEditEvent;
import rx.Observable;
import rx.functions.Func1;

/**
 * 日记卡片列表（纯展示/可以选择卡片印刷）
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class DiaryCardListActivity extends ProductionListActivity implements CardPresentation.DiaryCardView, View.OnClickListener, IEventBus {
    DiaryCardListAdapter diaryCardListAdapter;
    CardPresenter cardPresenter;
    boolean canSelect;//控制展示和选择卡片开关
    String coverTitle;
    int bookPage;
    int bookSizeId;
    List<CardObj> selectCards = new ArrayList<>();
    TFDialog tougueDialog;

    public static void open(Context context){
        open(context, false, "", 0, 0);
    }

    public static void open(Context context, boolean canSelect, String coverTitle, int bookPage, int bookSizeId){
        Intent intent = new Intent(context, DiaryCardListActivity.class);
        intent.putExtra("can_select", canSelect);
        intent.putExtra("cover_title", coverTitle);
        intent.putExtra("book_page", bookPage);
        intent.putExtra("book_size_id", bookSizeId);
        intent.putExtra("book_type", BookModel.BOOK_TYPE_DIARY_CARD);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canSelect = getIntent().getBooleanExtra("can_select", false);
        coverTitle = getIntent().getStringExtra("cover_title");
        bookPage = getIntent().getIntExtra("book_page", 0);
        bookSizeId = getIntent().getIntExtra("book_size_id", 0);
        cardPresenter = new CardPresenter(this);
        cardPresenter.loadDiaryCard();

        RxView.clicks(btnAskPrint)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(
                        aVoid -> onClick(btnAskPrint),
                        throwable -> Log.e(TAG, throwable.getLocalizedMessage())
                );
        getSupportActionBar().setTitle(FastData.getBabyNickName() + "的日记卡片");
        rvBooks.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0,
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                getResources().getDimensionPixelOffset(R.dimen.size_72));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            DiaryPublishActivity.open(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateClick() {
        DiaryPublishActivity.open(this);
    }

    @Override
    public void setDiaryCardData(List<DiaryCardObj> diaryCardObjs) {
        if(diaryCardListAdapter == null){
            rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
            diaryCardListAdapter = new DiaryCardListAdapter(this, diaryCardObjs, canSelect, this);
            rvBooks.setAdapter(diaryCardListAdapter);
        } else {
            for(DiaryCardObj diaryCardObj : diaryCardObjs){
                diaryCardObj.setSelect(selectCards.contains(diaryCardObj) ? 1 : 0);
            }
            diaryCardListAdapter.setListData(diaryCardObjs);
            diaryCardListAdapter.notifyDataSetChanged();
        }

        if (diaryCardListAdapter.getListData().isEmpty()) {
            tvTip.setVisibility(View.GONE);
            btnAskPrint.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyViewContent(false);
        } else {
            llEmpty.setVisibility(View.GONE);
            btnAskPrint.setVisibility(View.VISIBLE);
            //just 展示列表
            if (!canSelect) {
                btnAskPrint.setText("选择印刷规格");
                tvTip.setVisibility(View.GONE);
                //可以选择卡片申请印刷
            } else {
                btnAskPrint.setText("申请印刷");
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(coverTitle + " 每套选择" + bookPage + "张（也可以是" + bookPage + "的倍数）");
            }
        }
    }

    @Override
    public void setStateView(boolean loading) {
        if(loading){
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

    private boolean checkPrintInfo(){
        if(tougueDialog == null){
            tougueDialog = TFDialog.getInstance();
            tougueDialog.setPositiveButton("确定", v -> tougueDialog.dismiss());
        }

        if(selectCards.size() < bookPage){
            tougueDialog.setMessage(
                    "印制"
                            + coverTitle + "日记卡片需要"
                            + bookPage + "张，只选了" + selectCards.size()
                            + "张还少" + (bookPage - selectCards.size() + "张。"));
            tougueDialog.show(getSupportFragmentManager(), "dialog");
            return false;
        } else {
            //bookPage 的倍数才可以申请印制
            if(selectCards.size() % bookPage != 0){
                tougueDialog.setMessage(
                        "印制"
                                + coverTitle + "日记卡片需要"
                                + bookPage + "的倍数哦~");
                tougueDialog.show(getSupportFragmentManager(), "dialog");
            }
            return selectCards.size() % bookPage == 0;
        }
    }

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_ask_for_print) {
            //选择印刷规格
            if (!canSelect) {
                FragmentBridgeActivity.openBookSizeListFragment(this);

            //申请印刷
            } else {
                Observable.just(selectCards)
                        .filter(cardObjs -> checkPrintInfo())
                        .flatMap(new Func1<List<CardObj>, Observable<EditBookResponse>>() {
                            @Override
                            public Observable<EditBookResponse> call(List<CardObj> cardObjs) {
                                StringBuffer sb = new StringBuffer("{\"dataList\":[");
                                int index = 0;
                                for(CardObj cardObj : selectCards){
                                    index++;
                                    sb.append(cardObj.getCardId());
                                    if(index < selectCards.size()){
                                        sb.append(",");
                                    } else {
                                        sb.append("],\"bookSizeId\":")
                                                .append(bookSizeId)
                                                .append("}");
                                    }
                                }

                                return apiService.saveProduction(
                                        FastData.getBabyId(),
                                        FastData.getUserInfo().getNickName(),
                                        selectCards.get(0).getMedia().getImgUrl(),
                                        "",
                                        FastData.getBabyNickName() + "的日记卡片",
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
                                                DiaryCardListActivity.this,
                                                BookModel.BOOK_TYPE_DIARY_CARD,
                                                BookModel.BOOK_TYPE_DIARY_CARD,//日记卡片没有openBookType，直接使用本平台BookType
                                                selectCards.size(),
                                                bookSizeId,
                                                response.getDataId(),
                                                selectCards.get(0).getMedia().getImgUrl(),
                                                FastData.getBabyNickName() + "的日记卡片",
                                                System.currentTimeMillis(),
                                                CartPrintPropertyDialog.REQUEST_CODE_DIARY_CARD).reqPrintStatus();
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
        } else if(view.getId() == R.id.iv_select){
            CardObj cardObj = (CardObj) view.getTag(R.string.tag_obj);
            int index = (int) view.getTag(R.string.tag_index);
            cardObj.setSelect(cardObj.select() ? 0 : 1);
            if(cardObj.select()){
                if(!selectCards.contains(cardObj)) selectCards.add(cardObj);
            } else {
                if(selectCards.contains(cardObj)) selectCards.remove(cardObj);
            }
            diaryCardListAdapter.notifyItemChanged(index);

            if(selectCards.size() > 0){
                btnAskPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
            } else {
                btnAskPrint.setText("申请印刷");
            }
        }else if(view.getId() == R.id.fl_root && canSelect){
            CardObj cardObj = (CardObj) view.getTag(R.string.tag_obj);
            CardPreviewActivity.open(this, cardObj, 1);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Iterator iterator = selectCards.iterator();
        //去掉没选择的
        while (iterator.hasNext()) {
            DiaryCardObj diaryCardObj = (DiaryCardObj) iterator.next();
            if (!diaryCardObj.select()) {
                iterator.remove();
            }
        }
        cardPresenter.loadDiaryCard();
    }

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_DIARY_CARD) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(this, event.response.getOrderId(),event.baseObjs);
            } else {
                Toast.makeText(this, event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe
    public void onEvent(DiaryPublishEvent event) {
        if (cardPresenter != null) {
            selectCards.add(event.getDiaryCardObj());
            btnAskPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
            cardPresenter.loadDiaryCard();
        }
    }

    @Subscribe
    public void CardSelectEvent(CardEditEvent event) {
        if (diaryCardListAdapter != null && !diaryCardListAdapter.getListData().isEmpty()) {
            for (DiaryCardObj cardObj : diaryCardListAdapter.getListData()) {
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
}
