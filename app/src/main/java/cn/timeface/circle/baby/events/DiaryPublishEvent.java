package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class DiaryPublishEvent {
    private DiaryCardObj diaryCardObj;

    public DiaryPublishEvent(DiaryCardObj diaryCardObj) {
        this.diaryCardObj = diaryCardObj;
    }

    public DiaryCardObj getDiaryCardObj() {
        return diaryCardObj;
    }

    public void setDiaryCardObj(DiaryCardObj diaryCardObj) {
        this.diaryCardObj = diaryCardObj;
    }
}
