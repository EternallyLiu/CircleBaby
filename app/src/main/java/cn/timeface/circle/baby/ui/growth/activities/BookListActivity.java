package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 书作品列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookListActivity extends ProductionListActivity{

    public static void open(Context context, int bookType){
        Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra("book_type", bookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTip.setVisibility(View.GONE);
    }
}
