package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import rx.Observable;

/**
 * 卡片presentation
 * author : YW.SUN Created on 2017/1/20
 * email : sunyw10@gmail.com
 */
public interface CardPresentation {

    interface Model {

        Observable<KnowledgeCardListResponse> recognizeCardList();

        Observable<DiaryCardListResponse> diaryCardList();

    }

    interface View {

        void setStateView(boolean loading);

        void setCardData(List<CardObj> cardObjs);

        void showError(String errMsg);

    }

    interface Presenter {

        void create();

        void loadData();

        void edit();

        void delete();

    }
}
