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
public class SingleContentView extends FrameLayout {
    TFOBookContentModel contentModel;

    public SingleContentView(Context context, TFOBookContentModel contentModel) {
        super(context);
        this.contentModel = contentModel;

        setupViews();
    }

    private void setupViews() {

        Observable.from(contentModel.getElementList())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<TFOBookElementModel>() {
                            @Override
                            public void call(TFOBookElementModel elementModel) {
                                View view = elementModel.getView(getContext());
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
