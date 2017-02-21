package cn.timeface.circle.baby.support.mvp.presenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.CommemorationModel;
import cn.timeface.circle.baby.support.mvp.presentations.BasePresenterView;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.response.DateResponse;
import rx.functions.Action1;

/**
 * 纪念日Presenter
 * Created by JieGuo on 16/9/29.
 */

public class CommemorationPresenter extends BasePresenter<BasePresenterView, CommemorationModel>
        implements CalendarPresentation.CommemorationPresentation.Presenter {

    public CommemorationPresenter(CalendarPresentation.CommemorationPresentation.View view) {
        setup(view, new CommemorationModel());
    }

    @Override
    public void add(CalendarPresentation.CommemorationParamsBuilder builder,
                    Action1<BaseResponse> onLoad, Action1<Throwable> onError) {
        model.add(builder)
                .delay(3, TimeUnit.SECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(onLoad, onError);
    }

    @Override
    public void get(String id, Action1<DateResponse> onLoad, Action1<Throwable> onError) {

    }

    @Override
    public void list(String calendarId, String year, String month,
                     Action1<DateResponse> onLoad, Action1<Throwable> onError) {
        view.addSubscription(
                model.list(calendarId, year, month)
                        .compose(SchedulersCompat.applyComputationSchedulers())
                        .subscribe(onLoad, onError)
        );
    }

    @Override
    public void update(CalendarPresentation.CommemorationParamsBuilder builder,
                       String oldDay,
                       Action1<BaseResponse> onLoad,
                       Action1<Throwable> onError) {
        model.update(builder, oldDay)
                .delay(3, TimeUnit.SECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(onLoad, onError);
    }

    @Override
    public void delete(DateObj obj, Action1<BaseResponse> onLoad, Action1<Throwable> onError) {
        model.delete(obj)
                .delay(3, TimeUnit.SECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(onLoad, onError);
    }

    @Override
    public int getMaxDateOfMonth(int year, int month) {
        if (month < 1 || month > 12) {
            return 30;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    @Override
    public List<Long> getAllDayInMonth(int year, int month) {
        List<Long> data = new ArrayList<>();
        int max = getMaxDateOfMonth(year, month);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < max; i++) {
            int day = i + 1;
            //String key = year + "年" + month + "月" + (i + 1) + "日";
            calendar.set(Calendar.DATE, day);
            data.add(calendar.getTimeInMillis());
        }
        return data;
    }
}
