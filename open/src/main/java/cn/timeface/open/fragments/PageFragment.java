package cn.timeface.open.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.timeface.open.api.OpenApiFactory;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.fragments.base.BaseFragment;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.views.PageView;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseFragment {
    FrameLayout mainFrameLayout;
    TFOBookModel bookModel;
    TFOBookContentModel rightModel;
    TFOBookContentModel leftModel;
    Point screenInfo = new Point();

    public static PageFragment newInstance(Point rootWH, TFOBookContentModel contentModel) {
        return newInstance(rootWH, null, contentModel);
    }

    public static PageFragment newInstance(Point rootWH, TFOBookContentModel leftModel, TFOBookContentModel rightModel) {
        PageFragment pageFragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("root_view_info", rootWH);
        bundle.putParcelable("right_model", rightModel);
        bundle.putParcelable("left_model", leftModel);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rightModel = getArguments().getParcelable("right_model");
            leftModel = getArguments().getParcelable("left_model");
            screenInfo = getArguments().getParcelable("root_view_info");
        }
        this.bookModel = BookModelCache.getInstance().getBookModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //为毛要套一层framelayout??因为不套的话pageview设置背景颜色会超出范围
        mainFrameLayout = new FrameLayout(getActivity());
        mainFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setupViews();
        return mainFrameLayout;
    }

    private void setupViews() {
        Subscription s =
                Observable.merge(Observable.from(leftModel.getElementList()), Observable.from(rightModel.getElementList()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .filter(new Func1<TFOBookElementModel, Boolean>() {
                            @Override
                            public Boolean call(TFOBookElementModel elementModel) {
                                if (TextUtils.isEmpty(elementModel.getElementMaskImage())) {
                                    return false;
                                }
                                if (elementModel.getMaskFile(getActivity()).exists()) {
                                    return false;
                                }
                                return true;
                            }
                        })
                        .flatMap(new Func1<TFOBookElementModel, Observable<ResponseBody>>() {
                            @Override
                            public Observable<ResponseBody> call(TFOBookElementModel elementModel) {
                                return OpenApiFactory.getOpenApi().getApiService().getImageStream(elementModel.getElementMaskImage());
                            }
                        }, new Func2<TFOBookElementModel, ResponseBody, File>() {
                            @Override
                            public File call(TFOBookElementModel elementModel, ResponseBody body) {

                                File file = elementModel.getMaskFile(getActivity());
                                Bitmap bitmap = BitmapFactory.decodeStream(body.byteStream());

                                try {
                                    file.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return file;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                PageView pageView = new PageView(getActivity(), leftModel, rightModel, false);
                                pageView.setBackgroundColor(Color.BLACK);
                                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageView.getLayoutParams();
                                lp.gravity = Gravity.CENTER;
                                mainFrameLayout.addView(pageView, lp);
                            }
                        })
                        .subscribe(
                                new Action1<File>() {
                                    @Override
                                    public void call(File file) {
                                        Log.i(TAG, "call: " + file.getAbsolutePath());
                                    }
                                }
                                , new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Log.e(TAG, "call: ", throwable);
                                    }
                                }
                        );

    }

}
