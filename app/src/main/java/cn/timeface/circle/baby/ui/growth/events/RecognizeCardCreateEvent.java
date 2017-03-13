package cn.timeface.circle.baby.ui.growth.events;

import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;

/**
 * 识图卡片创建成功event
 * author : YW.SUN Created on 2017/2/23
 * email : sunyw10@gmail.com
 */
public class RecognizeCardCreateEvent {
    private KnowledgeCardObj knowledgeCardObj;

    public RecognizeCardCreateEvent(KnowledgeCardObj knowledgeCardObj) {
        this.knowledgeCardObj = knowledgeCardObj;
    }

    public KnowledgeCardObj getKnowledgeCardObj() {
        return knowledgeCardObj;
    }

    public void setKnowledgeCardObj(KnowledgeCardObj knowledgeCardObj) {
        this.knowledgeCardObj = knowledgeCardObj;
    }
}
