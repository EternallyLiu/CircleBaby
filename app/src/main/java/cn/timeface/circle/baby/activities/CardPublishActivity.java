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
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.events.RecognizeCardCreateEvent;
import cn.timeface.circle.baby.views.TFStateView;
import rx.functions.Action1;

/**
 * 制作卡片
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class CardPublishActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus {

    protected final int PHOTO_COUNT_MAX = 1;
    public final int PICTURE = 0;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.vp)
    ViewPager vp;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    private List<ImgObj> selImages = new ArrayList<>();
    private List<KnowledgeCardObj> dataList = new ArrayList<>();
    private MyAdapter adapter;

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
//        tfStateView.setOnRetryListener(() -> reqData());

        tvAdd.setOnClickListener(this);
        setData();
    }

//    @Override
//    protected void onResume() {
//        reqData();
//        super.onResume();
//    }

//    private void reqData() {
//        tfStateView.loading();
//        apiService.recognizeCardList()
//                .compose(SchedulersCompat.applyIoSchedulers())
//                .subscribe(cardListResponse -> {
//                    if (tfStateView != null)
//                        tfStateView.finish();
//                    dataList = cardListResponse.getDataList();
//                    adapter = null;
//                    adapter = new MyAdapter(dataList);
//                    adapter.setOnClickListener(CardPublishActivity.this);
//                    vp.setAdapter(adapter);
//                    if (dataList.size() > 1) {
//                        vp.setCurrentItem(dataList.size() - 1);
//                    }
//                    tvAdd.setEnabled(true);
//                }, throwable -> {
//                    tfStateView.showException(throwable);
//                    Log.e(TAG, "getComposedCardList:", throwable);
//                });
//    }

    private void setData() {
        if(adapter == null){
            adapter = new MyAdapter(dataList);
            adapter.setOnClickListener(CardPublishActivity.this);
            vp.setAdapter(adapter);
        } else {
            adapter.setDataList(dataList);
            adapter.notifyDataSetChanged();
        }
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
                    if (selImages != null && selImages.size() > 0) {
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
            case R.id.tv_add:
                if (dataList.size() > 0) {
                    vp.setCurrentItem(dataList.size());
                }
                break;
            case R.id.iv_deletecard:
                Integer position = (Integer) v.getTag(R.string.tag_ex);
                CardObj cardObj = dataList.get(position);
                dataList.remove(cardObj);
                adapter = null;
                adapter = new MyAdapter(dataList);
                adapter.setOnClickListener(this);
                vp.setAdapter(adapter);
                if (position < dataList.size()) {
                    vp.setCurrentItem(position);
                } else {
                    if (dataList.size() > 1) {
                        vp.setCurrentItem(dataList.size() - 1);
                    } else {
                        vp.setCurrentItem(0);
                    }
                }
                apiService.delCard("[" + cardObj.getCardId() + "]")
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                        }, throwable -> {
                            Log.e(TAG, "delCard:");
                        });
                break;
        }

    }

    public class MyAdapter extends PagerAdapter implements View.OnClickListener {
        List<KnowledgeCardObj> list;
        View.OnClickListener listener;

        public MyAdapter(List<KnowledgeCardObj> list) {
            this.list = list;
        }

        public void setOnClickListener(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return list.size() + 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if (position < list.size()) {
                view = getLayoutInflater().inflate(R.layout.view_card, container, false);
                RatioImageView ivCard = (RatioImageView) view.findViewById(R.id.iv_cover);
                ImageView ivDeletecard = (ImageView) view.findViewById(R.id.iv_deletecard);
                int measuredWidth = ivDeletecard.getMeasuredWidth();
                ivDeletecard.setTranslationX(measuredWidth / 2);
                ivDeletecard.setTranslationY(-measuredWidth / 2);
                if(list.get(position).getMedia() != null)GlideUtil.displayImage(list.get(position).getMedia().getImgUrl(), ivCard);
                ivDeletecard.setTag(R.string.tag_ex, position);
                ivDeletecard.setOnClickListener(this);
            } else {
                view = getLayoutInflater().inflate(R.layout.view_addcard, container, false);
                ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
                ivAdd.setOnClickListener(v -> selectImages());
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setDataList(List<KnowledgeCardObj> list) {
            this.list = list;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if(dataList.size() > 0){
                TFDialog tfDialog = TFDialog.getInstance();
                tfDialog.setTitle("提示");
                tfDialog.setMessage("离开后编辑内容会丢失，您确定这么做吗？");
                tfDialog.setNegativeButton("取消", view -> tfDialog.dismiss());

                tfDialog.setPositiveButton("确定", view -> {
                    StringBuffer sb = new StringBuffer("[");
                    //删除卡片
                    for(KnowledgeCardObj knowledgeCardObj : dataList){
                        sb.append(knowledgeCardObj.getCardId())
                                .append(",");
                    }
                    sb.substring(0, sb.lastIndexOf(","));
                    apiService.delCard(sb.append("]").toString())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if(response.success()){
                                            finish();
                                        }
                                    }
                                    , throwable -> {
                                        Log.e(TAG, "delCard:");
                                    });
                });
                tfDialog.show(getSupportFragmentManager(), "");

                return true;
            }

        } else if (item.getItemId() == R.id.complete) {
            if (dataList.size() == 0) {
                ToastUtil.showToast("先制作一张识图卡片吧~");
                return true;
            }
            for(KnowledgeCardObj knowledgeCardObj : dataList){
                knowledgeCardObj.setSelect(1);
            }
            EventBus.getDefault().post(new PublishRefreshEvent(dataList));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void recognizeCardCreateEvent(RecognizeCardCreateEvent cardCreateEvent){
        dataList.add(cardCreateEvent.getKnowledgeCardObj());
        setData();
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        selImages = null;
        dataList = null;
        super.onDestroy();
    }
}
