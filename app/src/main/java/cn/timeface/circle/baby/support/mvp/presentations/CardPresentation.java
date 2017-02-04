package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
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

    interface View extends BasePresenterView {

        void setStateView(boolean loading);

        void showError(String errMsg);

    }

    interface RecognizeCardView extends View {
        void setRecognizeCardData(List<KnowledgeCardObj> knowledgeCardObjs);
    }

    interface DiaryCardView extends View {
        void setDiaryCardData(List<DiaryCardObj> diaryCardObjs);
    }

    interface Presenter{

        void create();

        void loadData(int bookType);

        void edit();

        void delete();

        void loadRecognizeCard();

        void loadDiaryCard();

    }
}
