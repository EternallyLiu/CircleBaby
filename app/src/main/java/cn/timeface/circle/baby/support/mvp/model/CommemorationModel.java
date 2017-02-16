package cn.timeface.circle.baby.support.mvp.model;

import android.util.Log;

import java.util.Map;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.response.DateResponse;
import rx.Observable;

/**
 * Created by JieGuo on 16/9/29.
 */

public class CommemorationModel extends BasePresenterModel implements
        CalendarPresentation.CommemorationPresentation.Model {

    CommemorationDataManger dataManger = CommemorationDataManger.getInstance();

    @Override
    public Observable<BaseResponse> add(CalendarPresentation.CommemorationParamsBuilder builder) {
        return Observable.defer(() -> {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.info = "ok";
            Map<String, String> data = builder.build();
            DateObj dateObj = new DateObj(builder);
            try {
                dataManger.add(data.get("month"), dateObj);
            } catch (Exception e) {
                return Observable.error(e);
            }
            baseResponse.status = 1;
            return Observable.just(baseResponse);
        });
    }

    @Override
    public Observable<DateResponse> get(String id) {
        return Observable.empty();
    }

    @Override
    public Observable<DateResponse> list(String calendarId, String year, String month) {
        return Observable.defer(() -> {
            return Observable.just(dataManger.list(month));
        });
    }

    @Override
    public Observable<BaseResponse> update(
            CalendarPresentation.CommemorationParamsBuilder builder, String oldDay) {
        return Observable.defer(() -> {
            BaseResponse response = new BaseResponse();
            try {
                DateObj obj = new DateObj(builder);
                dataManger.update(obj, oldDay);
                response.status = 1;
            } catch (Throwable throwable) {
                Log.e("update", "error", throwable);
                response.status = 0;
                return Observable.error(throwable);
            }
            return Observable.just(response);
        });
    }

    @Override
    public Observable<BaseResponse> delete(DateObj obj) {
        return Observable.defer(()->{
            BaseResponse response = new BaseResponse();
            response.status = 1;
            dataManger.delete(obj);
            return Observable.just(response);
        });
    }
}
