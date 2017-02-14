package cn.timeface.circle.baby.ui.growth.events;

/**
 * 卡片编辑event
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class CardEditEvent {
    long cardId;
    int select;

    public CardEditEvent(long cardId, int select) {
        this.cardId = cardId;
        this.select = select;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }
}
