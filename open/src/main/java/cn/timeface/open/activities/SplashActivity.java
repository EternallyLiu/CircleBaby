package cn.timeface.open.activities;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.response.BookList;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import rx.functions.Action1;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends BaseAppCompatActivity {

    final String APP_ID = "123456789";
    final String APP_SECRET = "987654321";
    private Object bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        getBookList();
    }

    public void getBookList() {
        apiService.bookList(10, 1)
                .compose(SchedulersCompat.<BaseResponse<BookList>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<BookList>>() {
                    @Override
                    public void call(BaseResponse<BookList> bookListBaseResponse) {
                        BookList data = bookListBaseResponse.getData();
                        Log.d(TAG, "call: " + data);
                        List<TFOBookModel> book_list = data.getBook_list();
                        TFOBookModel tfoBookModel = book_list.get(0);

                        openPod(tfoBookModel);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "call: " + throwable);

                    }
                });

    }

    private void openPod(final TFOBookModel tfoBookModel) {
        apiService.getPOD(tfoBookModel.getBookId(), tfoBookModel.getBookType(), 0, "")
                .compose(SchedulersCompat.<BaseResponse<TFOBookModel>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<TFOBookModel>>() {
                    @Override
                    public void call(BaseResponse<TFOBookModel> tfoBookModelBaseResponse) {
                        TFOBookModel tfoBookModel1 = tfoBookModelBaseResponse.getData();
                        List<TFOBookContentModel> contentList = tfoBookModel1.getContentList();
                        EditActivity.open4result(SplashActivity.this, 100, 1, contentList.get(0));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}
