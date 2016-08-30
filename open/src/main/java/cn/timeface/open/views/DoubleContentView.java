package cn.timeface.open.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * author: rayboot  Created on 16/4/25.
 * email : sy0725work@gmail.com
 */
public class DoubleContentView extends FrameLayout {
    TFOBookContentModel leftContent;
    TFOBookContentModel rightContent;
    int bookWidth = 0;

    public DoubleContentView(Context context, int bookW, TFOBookContentModel left, TFOBookContentModel right) {
        super(context);
        this.leftContent = left;
        this.rightContent = right;
        this.bookWidth = bookW;

        setupViews();
    }

    protected void setupViews() {
        Observable.merge(Observable.from(leftContent.getElementList()), Observable.from(rightContent.getElementList()))
                .subscribeOn(Schedulers.io())
                .toSortedList(new Func2<TFOBookElementModel, TFOBookElementModel, Integer>() {
                    @Override
                    public Integer call(TFOBookElementModel elementModel, TFOBookElementModel elementModel2) {
                        return elementModel.getElementDepth() > elementModel2.getElementDepth() ? 1 : -1;
                    }
                })
                .flatMap(new Func1<List<TFOBookElementModel>, Observable<TFOBookElementModel>>() {
                    @Override
                    public Observable<TFOBookElementModel> call(List<TFOBookElementModel> tfoBookElementModels) {
                        return Observable.from(tfoBookElementModels);
                    }
                })
                .filter(new Func1<TFOBookElementModel, Boolean>() {
                    @Override
                    public Boolean call(TFOBookElementModel tfoBookElementModel) {
                        return tfoBookElementModel.getElementAssist() == TFOBookElementModel.ELEMENT_ASSIST_DEFAULT;
                    }
                })
                .filter(new Func1<TFOBookElementModel, Boolean>() {
                    @Override
                    public Boolean call(TFOBookElementModel tfoBookElementModel) {
                        return tfoBookElementModel.getElementDeleted() == 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<TFOBookElementModel>() {
                            @Override
                            public void call(TFOBookElementModel tfoBookElementModel) {
                                View view = tfoBookElementModel.getView(getContext());
                                if (tfoBookElementModel.isRight()) {
                                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                                    lp.leftMargin += bookWidth;
                                    view.setLayoutParams(lp);
                                }
                                addView(view);
                            }
                        }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
    }
}
