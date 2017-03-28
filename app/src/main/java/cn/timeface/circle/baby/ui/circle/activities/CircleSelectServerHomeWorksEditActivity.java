package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectSchoolTaskFragment;

/**
 * 圈家校纪念册编辑页面
 * author : sunyanwei Created on 17-3-28
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerHomeWorksEditActivity extends CircleSelectServeHomeWorksActivity {

    public static void openEdit(Context context, String circleId, String bookId, String openBookId){
        Intent intent = new Intent(context, CircleSelectServerHomeWorksEditActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        edit = true;
        super.onCreate(savedInstanceState);
        bookId = getIntent().getStringExtra("book_id");
        openBookId = getIntent().getStringExtra("open_book_id");
        tvContentType.setText("编辑");
        tvContentType.setCompoundDrawables(null, null, null, null);

        if (selectSchoolTaskFragment == null) {
            selectSchoolTaskFragment = CircleSelectSchoolTaskFragment.newInstance(circleId, allSelHomeWorks);
        }
        showContent(selectSchoolTaskFragment);
    }
}
