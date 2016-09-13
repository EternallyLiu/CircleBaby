package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class CardPublishActivity extends BaseAppCompatActivity implements View.OnClickListener {


    protected final int PHOTO_COUNT_MAX = 1;


    public final int PICTURE = 0;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.vp)
    ViewPager vp;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.ll_title)
    LinearLayout llTitle;
    @Bind(R.id.sv)
    ScrollView sv;
    private List<ImgObj> selImages = new ArrayList<>();
    private List<View> mViews;
    private List<MediaObj> dataList;
    private MyAdapter adapter;
    private List<View> newViews;
    private boolean showGuide;
    public static void open(Context context) {
        Intent intent = new Intent(context, CardPublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardlist);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAdd.setOnClickListener(this);
        llTitle.setOnClickListener(this);
        tvAdd.setEnabled(false);
//        reqData();
//        selectImages();
    }

    @Override
    protected void onResume() {
        reqData();
        super.onResume();
    }

    private void reqData() {
        apiService.getComposedCardList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(cardListResponse -> {
                    dataList = cardListResponse.getDataList();
                    mViews = new ArrayList<>();
                    if (dataList.size() > 0) {
                        for (MediaObj media : dataList) {
                            View card = getCard(media);
                            mViews.add(card);
                        }
                    } else {
                        if (!isAddCard()) {
                            View addCard = getAddCard();
                            mViews.add(addCard);
                        }
                    }
                    vp.setAdapter(new MyAdapter(mViews));
                    if (mViews.size() > 1) {
                        vp.setCurrentItem(mViews.size() - 1);
                    }
                    tvAdd.setEnabled(true);
                });

    }

    private void selectImages() {
        selImages.clear();
        SelectPhotoActivity.openForResult(this, selImages, PHOTO_COUNT_MAX, PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (selImages.size() > 0) {
                        //跳转识图卡片预览界面
                        FragmentBridgeActivity.openCardPreviewFragment(this, selImages.get(0));
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_deletecard:
                int currentItem = vp.getCurrentItem();
                System.out.println("currentItem=======" + currentItem);
                newViews = new ArrayList<>();
                for (int i = 0; i < mViews.size(); i++) {
                    if (i != currentItem) {
                        newViews.add(mViews.get(i));
                    }
                }
//                mViews.remove(currentItem);
                if (newViews.size() == 0) {
                    newViews.add(getAddCard());
                }
                System.out.println("mViews.size()=======" + mViews.size());
                vp.setAdapter(new MyAdapter(newViews));
                mViews = newViews;
                apiService.delCard(dataList.get(currentItem).getId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                dataList.remove(currentItem);
                            }
                        }, throwable -> {
                            Log.e(TAG, "delCard:");
                        });
                break;
            case R.id.iv_add:
                selectImages();
                break;
            case R.id.tv_add:
                if (!isAddCard()) {
                    View addCard = getAddCard();
                    mViews.add(addCard);
                    vp.setAdapter(new MyAdapter(mViews));
                    vp.setCurrentItem(mViews.size() - 1);
                }
                break;
            case R.id.ll_title:
                if (showGuide) {
                    sv.setVisibility(View.GONE);
                    showGuide = false;
                    ivIcon.setImageResource(R.drawable.down);
                }else{
                    sv.setVisibility(View.VISIBLE);
                    showGuide = true;
                    ivIcon.setImageResource(R.drawable.up);
                }
                break;

        }

    }

    public View getCard(MediaObj media) {
        View view = getLayoutInflater().inflate(R.layout.view_card, null);
        RatioImageView ivCard = (RatioImageView) view.findViewById(R.id.iv_cover);
        ImageView ivDeletecard = (ImageView) view.findViewById(R.id.iv_deletecard);
        int measuredWidth = ivDeletecard.getMeasuredWidth();
        ivDeletecard.setTranslationX(measuredWidth / 2);
        ivDeletecard.setTranslationY(-measuredWidth / 2);
        GlideUtil.displayImage(media.getImgUrl(), ivCard);
        ivDeletecard.setOnClickListener(this);
        return view;
    }

    public View getAddCard() {
        View view = getLayoutInflater().inflate(R.layout.view_addcard, null);
        ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(this);
        return view;
    }

    public boolean isAddCard() {
        boolean b = false;
        if (mViews != null) {
            b = mViews.size() > dataList.size();
        }
        return b;
    }

    public class MyAdapter extends PagerAdapter {
        List<View> list;

        public MyAdapter(List<View> list) {
            this.list = list;
            System.out.println("MyAdapter.list.size========" + this.list.size());
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            System.out.println("destroyItem.position========" + position);
            container.removeView(list.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position), 0);
            return list.get(position);
        }

        public void setDataList(List<View> list) {
            this.list = list;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.complete) {
            if (dataList.size() == 0) {
                ToastUtil.showToast("先制作一张识图卡片吧~");
                return true;
            }
            EventBus.getDefault().post(new PublishRefreshEvent(dataList));
            PublishActivity.open(this, dataList);
//            EventBus.getDefault().post(new CardEvent(dataList));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
