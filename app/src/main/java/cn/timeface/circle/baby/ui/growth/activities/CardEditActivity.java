package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.timeface.circle.baby.support.api.models.objs.CardObj;

/**
 * 卡片编辑页面
 * author : YW.SUN Created on 2017/2/8
 * email : sunyw10@gmail.com
 */
public class CardEditActivity extends CardPreviewActivity {

    public static void open(Context context, CardObj cardObj){
        Intent intent = new Intent(context, CardEditActivity.class);
        intent.putExtra("card", cardObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        tvPinyin.setVisibility(View.VISIBLE);
        etTitle.setVisibility(View.VISIBLE);
    }
}
