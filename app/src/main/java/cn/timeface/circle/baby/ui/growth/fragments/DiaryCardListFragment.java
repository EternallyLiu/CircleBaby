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
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.constants.CountlyEventHelper;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.DiaryPublishEvent;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;
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
import cn.timeface.circle.baby.ui.growth.adapters.DiaryCardListAdapter;
import cn.timeface.circle.baby.ui.growth.events.CardEditEvent;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;
import rx.functions.Func1;

/**
 * 日记卡片列表fragment
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class DiaryCardListFragment extends BasePresenterFragment implements CardPresentation.DiaryCardView, View.OnClickListener, IEventBus {

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

    DiaryCardListAdapter diaryCardListAdapter;
    CardPresenter cardPresenter;
    boolean canSelect;//控制展示和选择卡片开关
    String coverTitle;
    int bookPage;
    int bookSizeId;
    List<CardObj> selectCards = new ArrayList<>();
    TFDialog tougueDialog;

    public static DiaryCardListFragment newInstance() {
        return newInstance(false, "", 0, 0);
    }

    public static DiaryCardListFragment newInstance(boolean canSelect, String coverTitle, int bookPage, int bookSizeId) {
        DiaryCardListFragment fragment = new DiaryCardListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("can_select", canSelect);
        bundle.putString("cover_title", coverTitle);
        bundle.putInt("book_page", bookPage);
        bundle.putInt("book_size_id", bookSizeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);

        canSelect = getArguments().getBoolean("can_select", false);
        coverTitle = getArguments().getString("cover_title");
        bookPage = getArguments().getInt("book_page", 0);
        bookSizeId = getArguments().getInt("book_size_id", 0);
        cardPresenter = new CardPresenter(this);
        cardPresenter.loadDiaryCard();

        btnAskForPrint.setOnClickListener(this);
        rvBooks.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                0,
                getResources().getDimensionPixelOffset(R.dimen.size_16),
                getResources().getDimensionPixelOffset(R.dimen.size_72));
        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_ask_for_print) {
            //选择印刷规格
            if (!canSelect) {
                FragmentBridgeActivity.openBookSizeListFragment(getActivity());

                //申请印刷
            } else {
                Observable.just(selectCards)
                        .filter(cardObjs -> checkPrintInfo())
                        .flatMap(new Func1<List<CardObj>, Observable<EditBookResponse>>() {
                            @Override
                            public Observable<EditBookResponse> call(List<CardObj> cardObjs) {
                                StringBuffer sb = new StringBuffer("{\"dataList\":[");
                                int index = 0;
                                for (CardObj cardObj : selectCards) {
                                    index++;
                                    sb.append(cardObj.getCardId());
                                    if (index < selectCards.size()) {
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
                                        BookModel.BOOK_TYPE_DIARY_CARD,
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
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        );
            }
        } else if (view.getId() == R.id.iv_select) {
            CardObj cardObj = (CardObj) view.getTag(R.string.tag_obj);
            int index = (int) view.getTag(R.string.tag_index);
            cardObj.setSelect(cardObj.select() ? 0 : 1);
            if (cardObj.select()) {
                if (!selectCards.contains(cardObj)) selectCards.add(cardObj);
            } else {
                if (selectCards.contains(cardObj)) selectCards.remove(cardObj);
            }
            diaryCardListAdapter.notifyItemChanged(index);

            if (selectCards.size() > 0) {
                btnAskForPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
            } else {
                btnAskForPrint.setText("申请印刷");
            }
        }

    }

    private boolean checkPrintInfo() {
        if (tougueDialog == null) {
            tougueDialog = TFDialog.getInstance();
            tougueDialog.setPositiveButton("确定", v -> tougueDialog.dismiss());
        }

        if (selectCards.size() < bookPage) {
            tougueDialog.setMessage(
                    "印制"
                            + coverTitle + "日记卡片需要"
                            + bookPage + "张，只选了" + selectCards.size()
                            + "张还少" + (bookPage - selectCards.size() + "张。"));
            tougueDialog.show(getChildFragmentManager(), "dialog");
            return false;
        } else {
            //bookPage 的倍数才可以申请印制
            if (selectCards.size() % bookPage != 0) {
                tougueDialog.setMessage(
                        "印制"
                                + coverTitle + "日记卡片需要"
                                + bookPage + "的倍数哦~");
                tougueDialog.show(getChildFragmentManager(), "dialog");
            }
            return selectCards.size() % bookPage == 0;
        }
    }

    @Override
    public void setDiaryCardData(List<DiaryCardObj> diaryCardObjs) {
        if (diaryCardListAdapter == null) {
            rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 2));
            diaryCardListAdapter = new DiaryCardListAdapter(getContext(), diaryCardObjs, canSelect, this);
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
            btnAskForPrint.setVisibility(View.GONE);

            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView();
        } else {
            llEmpty.setVisibility(View.GONE);

            btnAskForPrint.setVisibility(View.VISIBLE);
            //just 展示列表
            if (!canSelect) {
                btnAskForPrint.setText("选择印刷规格");
                tvTip.setVisibility(View.GONE);
                //可以选择卡片申请印刷
            } else {
                btnAskForPrint.setText("申请印刷");
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(coverTitle + " 每套选择" + bookPage + "张（也可以是" + bookPage + "的倍数）");
            }
        }
    }

    private void setupEmptyView() {
        tvEmptyInfo.setText(FastData.getBabyNickName() + "的日记卡片为空哦，赶紧制作一张吧~");
        btnCreate.setText("立即制作");
        btnCreate.setOnClickListener(v -> DiaryPublishActivity.open(getContext()));
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

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_DIARY_CARD) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(getActivity(), event.response.getOrderId(), event.baseObjs);
            } else {
                Toast.makeText(getActivity(), event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe
    public void onEvent(DiaryPublishEvent event) {
        if (cardPresenter != null) {
            selectCards.add(event.getDiaryCardObj());
            cardPresenter.loadDiaryCard();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                btnAskForPrint.setText("（已选" + selectCards.size() + "张）申请印刷");
            } else {
                btnAskForPrint.setText("申请印刷");
            }
        }
    }
}
