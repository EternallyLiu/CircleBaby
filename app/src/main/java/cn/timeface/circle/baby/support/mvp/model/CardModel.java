package cn.timeface.circle.baby.support.mvp.model;

import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.CardPresentation;
import rx.Observable;

/**
 * 卡片model
 * author : YW.SUN Created on 2017/1/20
 * email : sunyw10@gmail.com
 */
public class CardModel extends BasePresenterModel implements CardPresentation.Model {

    @Override
    public Observable<KnowledgeCardListResponse> recognizeCardList() {
        return null;
    }

    @Override
    public Observable<DiaryCardListResponse> diaryCardList() {
        return null;
    }
}
