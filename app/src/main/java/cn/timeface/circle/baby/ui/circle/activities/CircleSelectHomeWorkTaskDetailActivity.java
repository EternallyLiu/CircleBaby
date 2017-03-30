package cn.timeface.circle.baby.ui.circle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeWorkExWrapperObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskDetailObj;

/**
 * 圈作品选择作业列表详情页面(从作业列表跳进来)
 * author : sunyanwei Created on 17-3-23
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectHomeWorkTaskDetailActivity extends CircleSelectHomeWorkDetailActivity {

    long taskId;

    public void open(Context context, long taskId){
        Intent intent= new Intent(context, CircleSelectHomeWorkTaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        context.startActivity(intent);
    }

    public static void open4Result(Context context, int reqCode, long taskId, String circleId, long babyId, List<CircleHomeworkExObj> allSelHomeWorks){
        Intent intent= new Intent(context, CircleSelectHomeWorkTaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        intent.putParcelableArrayListExtra("all_sel_home_works", (ArrayList<? extends Parcelable>) allSelHomeWorks);
        ((Activity) context).startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getIntent().getLongExtra("task_id", 0);
        getSupportActionBar().setTitle("作业详情");
        reqData();
    }

    private void reqData() {
        stateView.loading();
        addSubscription(
                apiService.teacherHomeworkDetal(taskId, 1, Integer.MAX_VALUE)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        CircleSchoolTaskDetailObj schoolTaskDetailObj = response.getSchoolTaskDetailObj();

                                        List<CircleHomeWorkExWrapperObj> homeWorkExWrapperObjList = new ArrayList<>();
                                        CircleHomeWorkExWrapperObj homeWorkExWrapperObj = new CircleHomeWorkExWrapperObj();
                                        homeWorkExWrapperObj.setDate(schoolTaskDetailObj.getTitle());

                                        List<CircleHomeworkExObj> homeworkExObjList = new ArrayList<>();
                                        for(CircleHomeworkObj circleHomeworkObj : schoolTaskDetailObj.getHomeworkList()){
                                            CircleHomeworkExObj homeworkExObj = new CircleHomeworkExObj();
                                            homeworkExObj.setHomework(circleHomeworkObj);
                                            homeworkExObj.setSchoolTaskName(schoolTaskDetailObj.getTitle());
                                            homeworkExObjList.add(homeworkExObj);
                                        }
                                        homeWorkExWrapperObj.setHomeworkList(homeworkExObjList);
                                        homeWorkExWrapperObjList.add(homeWorkExWrapperObj);
                                        setData(homeWorkExWrapperObjList, String.valueOf(taskId));

                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }
}
