package cn.timeface.open.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import cn.timeface.open.BuildConfig;
import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.adapters.PODViewPagerAdapter;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import cn.timeface.open.views.viewpager.transforms.StackTransformer;
import okio.Okio;
import rx.functions.Action1;
import rx.functions.Func1;

public class PODActivity extends BaseAppCompatActivity {

    Toolbar toolbar;
    ViewPager vpBook;
    PODViewPagerAdapter adapter;
    FrameLayout flContainer;
    TFOBookModel data;
    float pageScale = 1.f;
    int bookType = 23;

    final int EDIT_REQUEST_CODE = 100;

    public static void open(Context context) {
        Intent intent = new Intent(context, PODActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.vpBook = (ViewPager) findViewById(R.id.vpBook);
        this.flContainer = (FrameLayout) findViewById(R.id.fl_container);

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
        vpBook.setPageTransformer(true, new StackTransformer());
    }

    private void reqPod(String bookId, int bookType, String rebuild, String contentList) {
        apiService
                .getPOD(bookId, bookType, rebuild, contentList)
                .map(new Func1<BaseResponse<TFOBookModel>, TFOBookModel>() {
                    @Override
                    public TFOBookModel call(BaseResponse<TFOBookModel> podResponse) {
                        Point screenInfo = new Point(flContainer.getWidth(), flContainer.getHeight());
                        //获取缩放比例
                        {
                            if (screenInfo.y > screenInfo.x) {
                                //竖屏
                                pageScale = screenInfo.x / (float) podResponse.getData().getBookWidth();
                            } else {
                                //横屏
                                float pageW = podResponse.getData().getBookWidth() * 2;
                                float pageH = podResponse.getData().getBookHeight();

                                pageScale = screenInfo.y / pageH;
                                if (pageW * pageScale > screenInfo.x) {
                                    //按照高度拉伸,如果拉伸后宽度超出屏幕
                                    pageScale = screenInfo.x / pageW;
                                }
                            }
                        }
                        podResponse.getData().setPageScale(pageScale);
                        for (TFOBookContentModel cm : podResponse.getData().getContentList()) {
                            if (cm.getPageType() == TFOBookContentModel.PAGE_RIGHT) {
                                for (TFOBookElementModel em : cm.getElementList()) {
                                    em.setRight(true);
                                }
                            }
                        }
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
        this.data = data;
//        if (adapter == null) {
        adapter = new PODViewPagerAdapter(getSupportFragmentManager(), data, new Point(flContainer.getWidth(), flContainer.getHeight()));
        vpBook.setAdapter(adapter);

        if (BuildConfig.DEBUG) {
            vpBook.setCurrentItem(3);
//                clickEdit(null);
        }
        return;
//        }
    }

    public void clickBack(View view) {
        finish();
    }

    public void clickEdit(View view) {
        int[] index = adapter.getContentIndex(vpBook.getCurrentItem());
        if (index.length == 1) {
            EditActivity.open4result(this, EDIT_REQUEST_CODE, pageScale, data.getContentList().get(index[0]));
        } else {
            int leftIndex = index[0];
            int rightIndex = index[1];
            boolean isCover = false;
            {
                //左页为封底,右页为封面
                if (leftIndex < 0) {
                    leftIndex = data.getContentList().size() - 1;
                    isCover = true;
                } else if (leftIndex == data.getContentList().size() - 1) {
                    rightIndex = 0;
                    isCover = true;
                }
            }

            EditActivity.open4result(this, EDIT_REQUEST_CODE, pageScale, data.getContentList().get(leftIndex), data.getContentList().get(rightIndex), isCover);
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
        if (vpBook.getCurrentItem() > 0) {
            vpBook.setCurrentItem(vpBook.getCurrentItem() - 1);
        }
    }

    public void clickNext(View view) {
        if (vpBook.getCurrentItem() < vpBook.getAdapter().getCount()) {
            vpBook.setCurrentItem(vpBook.getCurrentItem() + 1);
        }
    }
}
