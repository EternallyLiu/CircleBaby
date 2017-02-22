package cn.timeface.circle.baby.ui.babyInfo.beans;

import android.text.SpannableStringBuilder;

/**
 * author : wangshuai Created on 2017/2/3
 * email : wangs1992321@gmail.com
 */
public class BabyAttentionEvent {

    private SpannableStringBuilder builder = null;
    private int type = 0;

    public BabyAttentionEvent(SpannableStringBuilder builder, int type) {
        this.builder = builder;
        this.type = type;
    }

    public BabyAttentionEvent(int type) {
        this.type = type;
    }

    public BabyAttentionEvent(SpannableStringBuilder builder) {
        this.builder = builder;
        this.type = 1;
    }

    public BabyAttentionEvent() {
    }

    public SpannableStringBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(SpannableStringBuilder builder) {
        this.builder = builder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
