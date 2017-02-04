package cn.timeface.circle.baby.support.mvp.presenter;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.CardModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

/**
 * 卡片presenter
 * author : YW.SUN Created on 2017/1/22
 * email : sunyw10@gmail.com
 */
public class CardPresenter extends BasePresenter<CardPresentation.View, CardModel> implements CardPresentation.Presenter {

    CardModel cardModel;

    public CardPresenter(CardPresentation.View view) {
        cardModel = new CardModel();
        setup(view, cardModel);
    }

    @Override
    public void create() {

    }

    @Override
    public void loadData(int bookType) {}

    @Override
    public void edit() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void loadRecognizeCard() {
        view.setStateView(true);
        view.addSubscription(
                cardModel.recognizeCardList()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if(view instanceof CardPresentation.RecognizeCardView){
                                        ((CardPresentation.RecognizeCardView) view).setRecognizeCardData(response.getDataList());
                                    }
                                    view.setStateView(false);
                                },
                                throwable -> {
                                    view.showError("数据获取失败");
                                }
                        )
        );
    }

    @Override
    public void loadDiaryCard() {
        view.setStateView(true);
        view.addSubscription(
                cardModel.diaryCardList()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if(view instanceof CardPresentation.DiaryCardView){
                                        ((CardPresentation.DiaryCardView) view).setDiaryCardData(response.getDataList());
                                    }
                                    view.setStateView(false);
                                },
                                throwable -> {
                                    view.showError("数据获取失败");
                                }
                        )
        );
    }
}
