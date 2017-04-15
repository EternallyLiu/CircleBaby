package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
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
        reqData();
    }

    private void reqData() {
        addSubscription(
                apiService.queryBookHomeworks(bookId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        List<CircleHomeworkExObj> dataList = new ArrayList<>();
                                        for (CircleHomeworkObj homeworkObj : response.getDataList()) {
                                            CircleHomeworkExObj homeworkExObj = new CircleHomeworkExObj();
                                            homeworkExObj.setHomework(homeworkObj);
                                            dataList.add(homeworkExObj);
                                        }
                                        this.allSelHomeWorks = dataList;
                                        tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(allSelHomeWorks.size()))));
                                    }
                                    if (selectSchoolTaskFragment == null) {
                                        selectSchoolTaskFragment = CircleSelectSchoolTaskFragment.newInstance(circleId, allSelHomeWorks);
                                    }
                                    showContent(selectSchoolTaskFragment);
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }
}
