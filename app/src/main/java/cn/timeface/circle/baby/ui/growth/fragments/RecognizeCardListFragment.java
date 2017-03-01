package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.responses.EditBookResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CardPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.CardPreviewActivity;
import cn.timeface.circle.baby.ui.growth.adapters.RecognizeCardListAdapter;
import cn.timeface.circle.baby.ui.growth.events.CardEditEvent;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;
import rx.functions.Func1;

/**
 * 识图卡片fragment
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class RecognizeCardListFragment extends BasePresenterFragment implements CardPresentation.RecognizeCardView, View.OnClickListener, IEventBus {

    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.tv_empty_info)
    TextView tvEmptyInfo;
    @Bind(R.id.btn_create)
    Button btnCreate;
    @Bind(R.id.btn_ask_for_print)
    Button btnAskForPrint;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;

    RecognizeCardListAdapter cardListAdapter;
    CardPresenter cardPresenter;
    List<KnowledgeCardObj> selectCards = new ArrayList<>();
    int bookPage = 8;
    TFDialog tougueDialog;

    public static RecognizeCardListFragment newInstance() {
        return new RecognizeCardListFragment();
    }

    public RecognizeCardListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);
        cardPresenter = new CardPresenter(this);

        btnAskForPrint.setOnClickListener(this);
        btnAskForPrint.setText("申请印刷");
        tvTip.setText(" 每套选择" + bookPage + "张（也可以是" + bookPage + "的倍数）");

        rvBooks.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0,
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                getResources().getDimensionPixelOffset(R.dimen.size_72));
        return view;
    }

    @Override
    public void onResume() {
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
    public void onClick(View view) {
        KnowledgeCardObj knowledgeCardObj = (KnowledgeCardObj) view.getTag(R.string.tag_obj);
        switch (view.getId()) {
            //申请印刷
            case R.id.btn_ask_for_print:
                askPrint();
                break;

            //识图卡片预览
            case R.id.fl_root:

                CardPreviewActivity.open(getActivity(), knowledgeCardObj);
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
                    btnAskForPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
                } else {
                    btnAskForPrint.setText("申请印刷");
                }
                break;
        }
    }

    @Override
    public void setRecognizeCardData(List<KnowledgeCardObj> knowledgeCardObjs) {
        for (KnowledgeCardObj knowledgeCardObj : knowledgeCardObjs) {
            if (selectCards.contains(knowledgeCardObj)) {
                knowledgeCardObj.setSelect(1);
            }
        }

        if (cardListAdapter == null) {
            rvBooks.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            cardListAdapter = new RecognizeCardListAdapter(getActivity(), knowledgeCardObjs, this);
            rvBooks.setAdapter(cardListAdapter);
        } else {
            cardListAdapter.setListData(knowledgeCardObjs);
            cardListAdapter.notifyDataSetChanged();
        }

        if (cardListAdapter.getListData().isEmpty()) {
            tvTip.setVisibility(View.GONE);
            btnAskForPrint.setVisibility(View.GONE);

            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView();
        } else {
            llEmpty.setVisibility(View.GONE);

            tvTip.setVisibility(View.VISIBLE);
            btnAskForPrint.setVisibility(View.VISIBLE);
        }
    }

    private void setupEmptyView() {
        tvEmptyInfo.setText(FastData.getBabyName() + "的识图卡片为空哦，赶紧制作一张吧~");
        btnCreate.setText("立即制作");
        btnCreate.setOnClickListener(v -> CardPublishActivity.open(getContext()));
    }

    @Override
    public void setStateView(boolean loading) {
        if (loading) {
            tfStateView.setVisibility(View.VISIBLE);
            tfStateView.loading();
        } else {
            tfStateView.finish();
        }
    }

    @Override
    public void showError(String errMsg) {
        ToastUtil.showToast(errMsg);
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
                                FastData.getBabyName() + "的识图卡片",
                                BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD,
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
                                        (BasePresenterAppCompatActivity) getActivity(),
                                        BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD,
                                        BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD,//识图卡片没有openBookType，直接使用本平台BookType
                                        selectCards.size(),
                                        0,//识图卡片没有booksizeid，传值0
                                        response.getDataId(),
                                        selectCards.get(0).getMedia().getImgUrl(),
                                        FastData.getBabyName() + "的识图卡片",
                                        System.currentTimeMillis(),
                                        CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD).reqPrintStatus();
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
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
            tougueDialog.show(getChildFragmentManager(), "dialog");
            return false;
        } else {
            //bookPage 的倍数才可以申请印制
            if (selectCards.size() % bookPage != 0) {
                tougueDialog.setMessage(
                        "识图卡片需要"
                                + bookPage + "的倍数哦~");
                tougueDialog.show(getChildFragmentManager(), "dialog");
            }
            return selectCards.size() % bookPage == 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        }
    }

    @Subscribe
    public void publishRefreshEvent(PublishRefreshEvent refreshEvent){
        selectCards.addAll(refreshEvent.getDataList());
        cardPresenter.loadRecognizeCard();
    }
}
