package cn.timeface.open.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import cn.timeface.open.BuildConfig;
import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.BookPodView;
import okio.Okio;
import rx.functions.Action1;
import rx.functions.Func1;

public class PODActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    float pageScale = 1.f;
    int bookType = 23;

    final int EDIT_REQUEST_CODE = 100;
    private BookPodView bookPodView;

    public static void open(Context context) {
        Intent intent = new Intent(context, PODActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        bookPodView = (BookPodView) findViewById(R.id.bookPodView);

        setSupportActionBar(toolbar);

        try {
            InputStream json = getAssets().open("content_list_data.txt");
            String source = Okio.buffer(Okio.source(json)).readString(Charset.forName("UTF-8"));
            reqPod("", bookType, "1", source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reqPod(String bookId, int bookType, String rebuild, String contentList) {
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

                reqPod(bookId, bookType, "0", "");


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
}
