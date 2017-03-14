package cn.timeface.circle.baby.ui.growth.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.ProductionIntroActivity;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.views.IntroView;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 作品列表页面
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public abstract class ProductionListActivity extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.btn_create)
    Button btnCreate;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.btn_ask_for_print)
    Button btnAskPrint;
    @Bind(R.id.tv_empty_info)
    TextView tvEmptyInfo;
    @Bind(R.id.intro_view)
    IntroView introView;

    protected int bookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.bookType = getIntent().getIntExtra("book_type", 0);
        rvBooks.getItemAnimator().setChangeDuration(0);//fix notify item shine problem

        introView.setIntroText("一分钟了解" + BookModel.getGrowthBookName(bookType));
        introView.setOnClickTextListener(v -> ProductionIntroActivity.open(this, bookType));
        if (FastData.showProductionIntro()) {
            FastData.setProductionIntro(false);
            introView.startShakeAnimation();
        }

        btnCreate.setOnClickListener(v -> onCreateClick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mine_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void setupEmptyViewContent(boolean hasPic) {
        String babyName = FastData.getBabyNickName();
        switch (bookType) {
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                tvEmptyInfo.setText(hasPic ? babyName + "的成长纪念册为空哦，赶紧制作一本属于" + babyName + "的成长纪念册吧~"
                        : babyName + "的成长纪念册为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_DIARY_CARD:
                tvEmptyInfo.setText(babyName + "的日记卡片为空哦，赶紧制作一张吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_RECOGNIZE_PHOTO_CARD:
                tvEmptyInfo.setText(babyName + "的识图卡片为空哦，赶紧制作一张吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_CALENDAR:
                tvEmptyInfo.setText(babyName + "的台历为空哦，赶紧制作一本属于" + babyName + "的台历吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                tvEmptyInfo.setText(hasPic ? babyName + "的照片书为空哦，赶紧制作一本属于" + babyName + "的照片书吧~"
                        : babyName + "的照片书为空哦，赶紧上传照片，制作照片书吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                tvEmptyInfo.setText(hasPic ? babyName + "的成长语录为空哦，赶紧制作一本属于" + babyName + "的成长语录吧~"
                        : babyName + "的成长语录为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_NOTEBOOK:
                tvEmptyInfo.setText(babyName + "的记事本为空哦，赶紧制作一本属于" + babyName + "的记事本吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_PAINTING:
                tvEmptyInfo.setText(hasPic ? babyName + "的绘画集为空哦，赶紧制作一本属于" + babyName + "的绘画集吧~"
                        : babyName + "的绘画集为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
        }
    }

    /**
     * 数据为空时点击创建按钮
     */
    public abstract void onCreateClick();
}
