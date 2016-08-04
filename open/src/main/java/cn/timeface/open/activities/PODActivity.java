package cn.timeface.open.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.timeface.open.BuildConfig;
import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.BookPodView;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class PODActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    float pageScale = 1.f;
    public int bookType = 23;

    final int EDIT_REQUEST_CODE = 100;
    private BookPodView bookPodView;
    List<TFOPublishObj> publishObjs;
    String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);
        {
            this.bookType = getIntent().getIntExtra("book_type", 23);
            this.bookId = getIntent().getStringExtra("book_id");
            this.publishObjs = getIntent().getParcelableArrayListExtra("publish_objs");
        }
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        bookPodView = (BookPodView) findViewById(R.id.bookPodView);

        setSupportActionBar(toolbar);
//        String publishObjs = loadJSONFromAsset();

        reqPod(bookId, bookType, TextUtils.isEmpty(bookId) ? 1 : 0, new Gson().toJson(publishObjs));
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("content_list_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void reqPod(final String bookId, int bookType, int rebuild, String contentList) {
        apiService
                .getPOD(bookId, bookType, rebuild, contentList)
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
                                   }
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
        if (BuildConfig.DEBUG) {
            bookPodView.scrollBookPageIndex(3);
        }
    }

    public void clickBack(View view) {
        finish();
    }

    public void clickEdit(View view) {
        List<TFOBookContentModel> currentPage = bookPodView.getCurrentPage();
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

                String bookId = data.getStringExtra("book_id");

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
}
