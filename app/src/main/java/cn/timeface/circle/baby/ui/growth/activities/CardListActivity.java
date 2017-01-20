package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.timeface.circle.baby.ui.growth.adapters.CardListAdapter;

/**
 * 卡片列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class CardListActivity extends ProductionListActivity {
    CardListAdapter cardListAdapter;

    public static void open(Context context, int bookType) {
        Intent intent = new Intent(context, CardListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTip.setVisibility(View.VISIBLE);
    }
}



