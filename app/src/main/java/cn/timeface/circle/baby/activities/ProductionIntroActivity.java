package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.responses.ProductionIntroListResponse;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.RecognizeCardCreateActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;

/**
 * @author wxw
 * @from 2017/2/21
 * 产品介绍页面
 */
public class ProductionIntroActivity extends BaseAppCompatActivity {

    private static final String ENCODING_UTF_8 = "UTF-8";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.view_banner)
    ConvenientBanner viewBanner;
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
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDefaultTextEncodingName(ENCODING_UTF_8);
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
        if (response.getDataList() != null && response.getDataList().size() > 0) {
            setupBanner(response.getDataList());
        }
        if (!TextUtils.isEmpty(response.getHtml())) {
            webView.loadData(response.getHtml(), "text/html", ENCODING_UTF_8);
        }
    }

    private void setupBanner(List<MediaObj> dataList) {
        viewBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, dataList) //设置需要切换的View
                .startTurning(3000)//设置自动切换（同时设置了切换时间间隔）
                .setPointViewVisible(true)    //设置指示器是否可见
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        viewBanner.setCanLoop(true);
        viewBanner.setManualPageable(true); //设置手动影响（设置了该项无法手动切换）
        viewBanner.setcurrentitem(0);
    }

    public void clickCreate(View v) {
        switch (bookType) {
            //精装照片书
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                SelectThemeActivity.open(this);
                break;
            //成长纪念册
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK);
                break;
            //绘画集
            case BookModel.BOOK_TYPE_PAINTING:
                SelectServerPhotoActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING);
                break;
            //成长语录
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS);
                break;
            //识图卡片
            case BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD:
                RecognizeCardCreateActivity.open(this);
                break;
            //日记卡片
            case BookModel.BOOK_TYPE_DIARY_CARD:
                DiaryPublishActivity.open(this);
                break;
            //台历
            case BookModel.BOOK_TYPE_CALENDAR:

                break;
        }
    }

    public class NetworkImageHolderView implements Holder<MediaObj> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new RatioImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, MediaObj data) {
            Glide.with(context)
                    .load(data.getImgUrl())
                    .centerCrop()
                    .into(imageView);
        }
    }
}
