package cn.timeface.open.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.util.List;

import cn.timeface.open.BuildConfig;
import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.managers.interfaces.IPODResult;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.BookPodView;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class PODActivity extends BaseAppCompatActivity implements IPODResult {

    Toolbar toolbar;
    float pageScale = 1.f;
    int bookType = 23;

    final int EDIT_REQUEST_CODE = 100;
    private BookPodView bookPodView;
    TFOPublishObj publishObj;
    String bookId;

    public static void open(Context context, String bookId, int bookType, TFOPublishObj publishObj) {
        Intent intent = new Intent(context, PODActivity.class);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("publish_obj", publishObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);
        {
            this.bookType = getIntent().getIntExtra("book_type", 23);
            this.bookId = getIntent().getStringExtra("book_id");
            this.publishObj = getIntent().getParcelableExtra("publish_obj");
        }
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        bookPodView = (BookPodView) findViewById(R.id.bookPodView);

        setSupportActionBar(toolbar);

        reqPod(bookId, bookType, TextUtils.isEmpty(bookId) ? 1 : 0, new Gson().toJson(publishObj));
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
        bookPodView.setupPodDate(getSupportFragmentManager(), data);
        if (BuildConfig.DEBUG) {
            bookPodView.scrollBookPageIndex(3);
        }
    }

    public void clickBack(View view) {
        finish();
    }

    public void clickEdit(View view) {
        List<TFOBookContentModel> currentPage = bookPodView.getCurrentPage();
        if (currentPage.size() == 1) {
            EditActivity.open4result(this, EDIT_REQUEST_CODE, pageScale, currentPage.get(0));
        } else {
            EditActivity.open4result(this, EDIT_REQUEST_CODE, pageScale, currentPage.get(0), currentPage.get(1), bookPodView.currentPageIsCover());
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
