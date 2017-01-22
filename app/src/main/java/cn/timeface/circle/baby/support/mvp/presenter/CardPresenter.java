package cn.timeface.circle.baby.support.mvp.presenter;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.CardModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;

/**
 * 卡片presenter
 * author : YW.SUN Created on 2017/1/22
 * email : sunyw10@gmail.com
 */
public class CardPresenter extends BasePresenter<CardPresentation.View, CardPresentation.Model> implements CardPresentation.Presenter {

    CardModel cardModel;

    public CardPresenter(CardPresentation.View view) {
        cardModel = new CardModel();

    }

    @Override
    public void create() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void edit() {

    }

    @Override
    public void delete() {

    }
}
