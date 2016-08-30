package cn.timeface.open.views;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.R;
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
                                String tag = "sticker" + System.nanoTime();
                                StickerView stickerView = new StickerView(getContext(), tfoBookElementModel.isRight() ? rightContent.getContentId() : leftContent.getContentId(), tfoBookElementModel, tag);
                                LayoutParams lp = new LayoutParams((int) (tfoBookElementModel.getElementWidth() + half_btn_size * 2), (int) (tfoBookElementModel.getElementHeight() + half_btn_size * 2));
                                lp.leftMargin = (int) tfoBookElementModel.getElementLeft() - half_btn_size;
                                lp.topMargin = (int) tfoBookElementModel.getElementTop() - half_btn_size;
                                if (tfoBookElementModel.isRight()) {
                                    lp.leftMargin += bookWidth;
                                }
                                stickerView.setLayoutParams(lp);
                                stickerView.setDeleteListener(delStickerListener);
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

    public OnClickListener delStickerListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (StickerView sv : stickerViews) {
                if (sv.getTag(R.string.tag_global).equals(v.getTag(R.string.tag_global))) {
                    EditDoubleContentView.this.removeView(sv);
                    stickerViews.remove(sv);
                    return;
                }
            }
        }
    };
}
