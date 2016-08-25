package cn.timeface.open.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.constants.Constant;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.BookPodView;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class PODActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    SeekBar seekBar;
    float pageScale = 1.f;
    public int bookType = 23;

    final int EDIT_REQUEST_CODE = 100;
    private BookPodView bookPodView;
    List<TFOPublishObj> publishObjs;
    Map<String, String> params = new HashMap<>();
    String bookId;
    int curIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);
        int rebuild;
        {
            this.bookType = getIntent().getIntExtra("book_type", 23);
            this.bookId = getIntent().getStringExtra("book_id");
            this.publishObjs = getIntent().getParcelableArrayListExtra(Constant.PUBLISH_OBJS);
            rebuild = getIntent().getIntExtra(Constant.REBUILD_BOOK, -1);
            List<String> paramKeys = getIntent().getStringArrayListExtra(Constant.POD_KEYS);
            List<String> paramValues = getIntent().getStringArrayListExtra(Constant.POD_VALUES);
            if (paramKeys != null && paramKeys.size() > 0) {
                for (int i = 0; i < paramKeys.size(); i++) {
                    params.put(paramKeys.get(i), paramValues.get(i));
                }
            }
        }
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        bookPodView = (BookPodView) findViewById(R.id.bookPodView);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);

        setSupportActionBar(toolbar);
        reqPod(bookId, bookType, rebuild == -1 ? (TextUtils.isEmpty(bookId) ? 1 : 0) : rebuild, new Gson().toJson(publishObjs));
    }

    private void reqPod(final String bookId, int bookType, int rebuild, String contentList) {
        apiService
                .getPOD(bookId, bookType, rebuild, contentList, params)
                .map(new Func1<BaseResponse<TFOBookModel>, TFOBookModel>() {
                    @Override
                    public TFOBookModel call(BaseResponse<TFOBookModel> podResponse) {
                        return podResponse.getData();
                    }
                })
                .compose(SchedulersCompat.<TFOBookModel>applyIoSchedulers())
                .subscribe(new Action1<TFOBookModel>() {
                               @Override
                               public void call(TFOBookModel podResponse) {
                                   Log.i(TAG, "call: 111111 reqPod + " + new Gson().toJson(podResponse));
                                   BookModelCache.getInstance().setBookModel(podResponse);
                                   setData(podResponse);
                                   if (TextUtils.isEmpty(bookId)) {
                                       createBookInfo(podResponse);
                                   } else {
                                       editBookInfo(podResponse);
                                   }
                                   setupSeekBar();
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
    }

    private void setData(TFOBookModel data) {
        bookPodView.setupPodData(getSupportFragmentManager(), data);
        pageScale = bookPodView.getPageScale();
        bookPodView.setCurrentIndex(curIndex);
    }

    private void setupSeekBar() {
        seekBar.setMax(bookPodView.getPageCount() - 1);
        bookPodView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                seekBar.setProgress(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bookPodView.setCurrentIndex(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void clickBack(View view) {
        finish();
    }

    public void clickEdit(View view) {
        List<TFOBookContentModel> currentPage = bookPodView.getCurrentPageData();
        TFOBookContentModel bookContentModel = currentPage.get(0);
        // 重置了页面的缩放比例
        //bookContentModel.resetPageScale(pageScale);
        if (currentPage.size() == 1) {
            EditActivity.open4result(this, EDIT_REQUEST_CODE, bookContentModel, bookContentModel.getContentType() == TFOBookContentModel.CONTENT_TYPE_FENG1);
        } else {
            TFOBookContentModel rightModel = currentPage.get(1);
            // rightModel.resetPageScale(pageScale);
            EditActivity.open4result(this, EDIT_REQUEST_CODE, bookContentModel, rightModel, bookPodView.currentPageIsCover());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    return;
                }

                TFOBookContentModel leftModel = data.getParcelableExtra("left_model");
                TFOBookContentModel rightModel = data.getParcelableExtra("right_model");
//                if (leftModel != null) leftModel.setPageScale(pageScale);
//                if (rightModel != null) rightModel.setPageScale(pageScale);

                bookId = data.getStringExtra("book_id");
                reqPod(bookId, bookType, 0, "");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickShare(View view) {
    }

    public void clickPre(View view) {
        bookPodView.clickPre();
    }

    public void clickNext(View view) {
        bookPodView.clickNext();
    }

    public abstract void createBookInfo(TFOBookModel bookModel);

    public abstract void editBookInfo(TFOBookModel bookModel);

    @Override
    protected void onPause() {
        curIndex = bookPodView.getCurrentIndex();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bookPodView != null) {
            bookPodView.clearOnPageChangeListeners();
        }
        super.onDestroy();
    }
}
