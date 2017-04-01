package cn.timeface.circle.baby.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CreateCalendarDialog;
import cn.timeface.circle.baby.support.api.models.responses.ProductionIntroListResponse;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServeHomeWorksActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerTimesActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectSeverAlbumsActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;

/**
 * @author wxw
 * @from 2017/2/21
 * 产品介绍页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class ProductionIntroActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.tv_create)
    TextView tvCreate;

    private int bookType;

    public static void open(Context context, int bookType) {
        Intent intent = new Intent(context, ProductionIntroActivity.class);
        intent.putExtra("book_type", bookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_intro);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bookType = getIntent().getIntExtra("book_type", 0);

        setupWebView();
        reqProductionIntro();
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
    }

    private void reqProductionIntro() {
        apiService.queryProductionIntro(bookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                setupData(response);
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "queryProductionIntro:", throwable);
                        }
                );
    }

    private void setupData(ProductionIntroListResponse response) {
        if (!TextUtils.isEmpty(response.getHtml())) {
            webView.loadUrl(response.getHtml());
        }
    }

    public void clickCreate(View v) {
        switch (bookType) {
            //精装照片书
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                addSubscription(
                        apiService.getDefaultTheme(bookType)
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(
                                        response -> {
                                            if (response.success()) {
                                                SelectServerPhotoActivity.open(this, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, response.getId(), "", "", FastData.getBabyId());
                                            }
                                        },
                                        throwable -> {
                                            Log.e(TAG, throwable.getLocalizedMessage());
                                        }
                                ));
                break;
            //成长纪念册
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                SelectServerTimeActivity.open(
                        this,
                        bookType,
                        TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                        "",
                        "",
                        FastData.getBabyId(),
                        FastData.getUserName(),
                        FastData.getBabyNickName() + "的成长纪念册");
                break;
            //绘画集
            case BookModel.BOOK_TYPE_PAINTING:
                SelectServerPhotoActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING, "", "", FastData.getBabyId());
                break;
            //成长语录
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                SelectServerTimeActivity.open(
                        this,
                        bookType,
                        TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS,
                        "",
                        "",
                        FastData.getBabyId(),
                        FastData.getUserName(),
                        FastData.getBabyNickName() + "的成长语录");
                break;
            //识图卡片
            case BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD:
//                RecognizeCardCreateActivity.open(this);
                CardPublishActivity.open(this);
                break;
            //日记卡片
            case BookModel.BOOK_TYPE_DIARY_CARD:
                DiaryPublishActivity.open(this);
                break;
            //台历
            case BookModel.BOOK_TYPE_CALENDAR:
                CreateCalendarDialog createCalendarDialog = CreateCalendarDialog.newInstance();
                createCalendarDialog.setCancelable(true);
                createCalendarDialog.show(getSupportFragmentManager(), "CreateCalendarDialog");
                break;

            //家校纪念册
            case BookModel.CIRCLE_BOOK_TYPE_FAMILY_SCHOOL:
                CircleSelectServeHomeWorksActivity.open(this, String.valueOf(FastData.getCircleId()));
                break;

            //圈时光书
            case BookModel.CIRCLE_BOOK_TYPE_TIME:
                CircleSelectServerTimesActivity.open(
                        this, BookModel.CIRCLE_BOOK_TYPE_TIME, 
                        TypeConstants.OPEN_BOOK_TYPE_CIRCLE_TIME_BOOK,
                        "", "", String.valueOf(FastData.getCircleId()));
                break;

            //圈照片书
            case BookModel.CIRCLE_BOOK_TYPE_PHOTO:
                CircleSelectSeverAlbumsActivity.open(
                        this, String.valueOf(FastData.getCircleId()),
                        BookModel.CIRCLE_BOOK_TYPE_PHOTO, "", 0, "");
                break;
        }
    }

}
