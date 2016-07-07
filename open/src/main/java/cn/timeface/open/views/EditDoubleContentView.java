package cn.timeface.open.views;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.utils.DeviceUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * author: rayboot  Created on 16/6/14.
 * email : sy0725work@gmail.com
 */
public class EditDoubleContentView extends DoubleContentView {
    int half_btn_size;
    boolean cover = false;

    List<StickerView> stickerViews = new ArrayList<>();

    public EditDoubleContentView(Context context, int bookW, TFOBookContentModel left, TFOBookContentModel right, boolean cover) {
        super(context, bookW, left, right);
        this.half_btn_size = DeviceUtil.dpToPx(getResources(), StickerView.BUTTON_SIZE_DP) / 2;
        this.cover = cover;
    }

    @Override
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<TFOBookElementModel>() {
                            @Override
                            public void call(TFOBookElementModel tfoBookElementModel) {
                                StickerView stickerView = new StickerView(getContext(), tfoBookElementModel.isRight() ? rightContent.getContentId() : leftContent.getContentId(), tfoBookElementModel);
                                LayoutParams lp = new LayoutParams((int) (tfoBookElementModel.getElementWidth() + half_btn_size * 2), (int) (tfoBookElementModel.getElementHeight() + half_btn_size * 2));
                                lp.leftMargin = (int) tfoBookElementModel.getElementLeft() - half_btn_size;
                                lp.topMargin = (int) tfoBookElementModel.getElementTop() - half_btn_size;
                                if (tfoBookElementModel.isRight()) {
                                    lp.leftMargin += bookWidth;
                                }
                                stickerView.setLayoutParams(lp);
                                stickerView.showControlItems(false);
                                if (cover) {
                                    stickerView.canMove(false);
                                }
                                addView(stickerView);
                                stickerViews.add(stickerView);
                            }
                        }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
    }

    public List<StickerView> getStickerViews() {
        return stickerViews;
    }
}
