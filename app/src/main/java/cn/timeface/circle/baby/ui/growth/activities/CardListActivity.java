package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;

/**
 * 卡片列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class CardListActivity extends ProductionListActivity {

    public static void open(Context context, int bookType) {
        Intent intent = new Intent(context, CardListActivity.class);
        context.startActivity(intent);
    }

}
