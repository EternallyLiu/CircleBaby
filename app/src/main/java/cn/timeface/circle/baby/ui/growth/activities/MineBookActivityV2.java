package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.CreateCalendarDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.activities.AddCircleBookActivity;
import cn.timeface.circle.baby.ui.growth.adapters.MineBookAdapterV2;
import cn.timeface.circle.baby.ui.growth.fragments.BookListFragment;
import cn.timeface.circle.baby.ui.growth.fragments.CircleListFragment;
import cn.timeface.circle.baby.ui.growth.fragments.DiaryCardListFragment;
import cn.timeface.circle.baby.ui.growth.fragments.RecognizeCardListFragment;

public class MineBookActivityV2 extends BasePresenterAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    public static void open(Context context) {
        Intent intent = new Intent(context, MineBookActivityV2.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_book_v2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的作品");
        setupViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mine_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            switch (viewPager.getCurrentItem()) {
                case 0: //精装照片书
                    addSubscription(
                            apiService.getDefaultTheme(BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK)
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
                case 1: //成长纪念册
                    SelectServerTimeActivity.open(
                            this,
                            BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                            TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                            "",
                            "",
                            FastData.getBabyId(),
                            FastData.getUserName(),
                            FastData.getBabyNickName() + "的成长纪念册");
                    break;
                case 2: //绘画集
                    SelectServerPhotoActivity.open(this, BookModel.BOOK_TYPE_PAINTING,
                            TypeConstants.OPEN_BOOK_TYPE_PAINTING, "", "", FastData.getBabyId());
                    break;
                case 3: //成长语录
                    SelectServerTimeActivity.open(
                            this,
                            BookModel.BOOK_TYPE_GROWTH_QUOTATIONS,
                            TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS,
                            "",
                            "",
                            FastData.getBabyId(),
                            FastData.getUserName(),
                            FastData.getBabyNickName() + "的成长语录");
                    break;
                case 4: //识图卡片
                    CardPublishActivity.open(this);
                    break;
                case 5: //日记卡片
                    DiaryPublishActivity.open(this);
                    break;
                case 6://台历
                    CreateCalendarDialog createCalendarDialog = CreateCalendarDialog.newInstance();
                    createCalendarDialog.setCancelable(true);
                    createCalendarDialog.show(getSupportFragmentManager(), "CreateCalendarDialog");
                    break;
                case 7://成长圈书
                    AddCircleBookActivity.open(this);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        MineBookAdapterV2 adapter = new MineBookAdapterV2(getSupportFragmentManager());
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK), "精装照片书");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK), "成长纪念册");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_PAINTING), "绘画集");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_GROWTH_QUOTATIONS), "成长语录");
        adapter.addFragment(RecognizeCardListFragment.newInstance(), "识图卡片");
        adapter.addFragment(DiaryCardListFragment.newInstance(), "日记卡片");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_CALENDAR), "台历");
        adapter.addFragment(CircleListFragment.newInstance(BookModel.CIRCLE_BOOK_TYPE_TIME), "成长圈书");
        viewPager.setOffscreenPageLimit(8);
        viewPager.setAdapter(adapter);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                (event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_DIARY_CARD
                        || event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD
                        || event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_BOOK_LIST)) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(this, event.response.getOrderId(), event.baseObjs);
            } else {
                Toast.makeText(this, event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
