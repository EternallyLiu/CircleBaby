package com.wechat.photopicker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wechat.photopicker.adapter.PhotoPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.RelateBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.dialog.CircleBabyDialog;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleMediaEvent;
import cn.timeface.circle.baby.ui.guides.GuideHelper;
import cn.timeface.circle.baby.ui.guides.GuideUtils;
import cn.timeface.circle.baby.ui.images.TagAddFragment;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.images.views.FlowLayout;
import cn.timeface.circle.baby.ui.images.views.ImageActionDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.DeviceUtil;
import rx.Observable;

import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_PHOTO_PATHS;
import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_SELECTOR_POSITION;

/**
 * Created by yellowstart on 15/12/15.
 */
public class BigImageFragment extends BaseFragment implements ImageActionDialog.ClickCallBack, View.OnClickListener, DeleteDialog.SubmitListener, PhotoPagerAdapter.ImageClickListener {

    public static int CIRCLE_MEDIA_IMAGE_EDITOR = 1009;

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.vp_big_image)
    ViewPager mViewPager;
    @Bind(R.id.tv_download)
    TextView tvDownload;
    @Bind(R.id.tv_delete)
    TextView tvDelete;
    @Bind(R.id.tag)
    LinearLayout tag;
    @Bind(R.id.love)
    LinearLayout love;
    @Bind(R.id.ll_botton)
    LinearLayout llBotton;
    @Bind(R.id.ll_tag_list)
    LinearLayout llTagList;
    @Bind(R.id.tv_tag_add)
    TextView tvTagAdd;
    @Bind(R.id.tv_like_count)
    TextView tvLikeCount;
    @Bind(R.id.iv_tag_add)
    FlipImageView ivTagAdd;
    @Bind(R.id.iv_image_like)
    FlipImageView ivImageLike;
    @Bind(R.id.fl_baby_list)
    FlowLayout flBabyList;
    @Bind(R.id.tv_relate_baby)
    TextView tvRelateBaby;
    private List<String> mPaths;

    private ArrayList<MediaObj> mMedias;
    private Bundle mBundle;
    private PhotoPagerAdapter mPhotoPagerAdapter;
    private int mCurrenItem;
    private boolean download;
    private boolean delete;
    private MenuItem save;
    private TFProgressDialog tfProgressDialog;

    private int type = 0;//0默认时光轴跳转  1009、成长圈跳转，需要圈人

    private int allDetailsListPosition;
    private CircleBabyDialog circleBabyDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        allDetailsListPosition = intent.getIntExtra("allDetailsListPosition", -1);
        mPaths = intent.getStringArrayListExtra(KEY_PHOTO_PATHS);
        mMedias = intent.getParcelableArrayListExtra("mediaList");
        mCurrenItem = intent.getIntExtra(KEY_SELECTOR_POSITION, 0);
        download = intent.getBooleanExtra("download", false);
        delete = intent.getBooleanExtra("delete", false);
        type = intent.getIntExtra("type", 0);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpage_big_image_show, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (mPaths.size() > 0 && mPaths != null) {
            mPhotoPagerAdapter = new PhotoPagerAdapter(getActivity(), mPaths);
            mPhotoPagerAdapter.setClickListener(this);
        }
        mViewPager.setAdapter(mPhotoPagerAdapter);
        mViewPager.setCurrentItem(mCurrenItem);
        initTips();
        initLikeCount();
        if (mMedias != null && mMedias.size() > 0)
            llBotton.setVisibility(View.VISIBLE);
        else llBotton.setVisibility(View.GONE);

        tag.setOnClickListener(this);
        love.setOnClickListener(this);
        initListener();
        showGuide();
        initCircleBaby();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private LayoutInflater inflate = null;

    private void initTips() {
        if (llTagList.getChildCount() > 0)
            llTagList.removeAllViews();
        int currentPosition = mViewPager.getCurrentItem();
        MediaObj media = null;
        if (mMedias != null && mMedias.size() > currentPosition)
            media = mMedias.get(currentPosition);
        if (media != null && media.getTips() != null && media.getTips().size() > 0) {
            llTagList.setVisibility(View.VISIBLE);
            if (inflate == null) {
                inflate = LayoutInflater.from(getActivity());
            }
            for (MediaTipObj tip : media.getTips()) {
                View view = inflate.inflate(R.layout.tag_list_item, llTagList, false);
                TextView tipName = (TextView) view.findViewById(R.id.name);
                view.setTag(R.id.tag_list_item, tip);
                view.setTag(R.id.tag_delete, currentPosition);
                tipName.setText(tip.getTipName());
                view.setOnLongClickListener(longClick);
                view.setLongClickable(true);
                llTagList.addView(view);
            }
        } else {
            llTagList.setVisibility(View.GONE);
        }
        ivTagAdd.changeStatus(R.drawable.tag_click_added);
    }

    private int deletePostion = -1;

    private MediaTipObj currentTip = null;

    private void deleteTip() {
        if (deletePostion < 0 || currentTip == null) {
            return;
        }
        MediaObj mediaObj = mMedias.get(deletePostion);
        if (!mediaObj.getTips().contains(currentTip))
            return;
        if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
            addSubscription(apiService.deleteCircleLabel(mediaObj.getId(), currentTip.getTipId())
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        if (response.success()) {
                            if (mediaObj.getTips().contains(currentTip))
                                mediaObj.getTips().remove(currentTip);
                            initTips();
                            deletePostion = -1;
                            currentTip = null;
                            EventBus.getDefault().post(new CircleMediaEvent((CircleMediaObj) mediaObj));
                        }else ToastUtil.showToast(getActivity(), response.getInfo());

                    }, throwable -> {
                    }));
        } else
            addSubscription(apiService.deleteLabel(mediaObj.getId() + "", currentTip.getTipId() + "")
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        if (response.success()) {
                            if (mediaObj.getTips().contains(currentTip))
                                mediaObj.getTips().remove(currentTip);
                            initTips();
                            deletePostion = -1;
                            currentTip = null;
                            if (allDetailsListPosition >= -1)
                                EventBus.getDefault().post(new MediaUpdateEvent(allDetailsListPosition, mediaObj));
                            else
                                EventBus.getDefault().post(new MediaUpdateEvent(mediaObj, deletePostion));
                        }else ToastUtil.showToast(getActivity(), response.getInfo());

                    }, throwable -> {
                    }));
    }

    private DeleteDialog deleteDialog = null;

    private void showDeleteDialog() {
        if (deleteDialog == null)
            deleteDialog = new DeleteDialog(getActivity());
        deleteDialog.setTitle("提示");
        deleteDialog.setMessage("您确定删除该标签么？");
        deleteDialog.setSubmitListener(this);
        deleteDialog.show();
    }

    private View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.tag_list_item:
                    currentTip = (MediaTipObj) v.getTag(R.id.tag_list_item);
                    deletePostion = (int) v.getTag(R.id.tag_delete);
                    if (currentTip != null)
                        showDeleteDialog();
                    break;
            }
            return false;
        }
    };

    private void initListener() {
        tvTitle.setText(mCurrenItem + 1 + "/" + mPaths.size());
//        tvDownload.setOnClickListener(this);
//        tvDelete.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position + 1;
                tvTitle.setText(position + "/" + mPaths.size());
                initTips();
                initLikeCount();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvDownload.setVisibility(View.GONE);
        tvDelete.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (delete || download)
            inflater.inflate(R.menu.menu_timeline_detail, menu);
//        save = menu.findItem(R.id.save);
//        if (delete) {
//            save.setTitle("删除");
//        } else {
//            if (!download) {
//                save.setTitle("");
//            }
//        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private ImageActionDialog dialog = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.save) {
//            if (delete) {
//                //删除
//                save.setEnabled(false);
//                int currentItem = mViewPager.getCurrentItem();
//                String path = mPaths.get(currentItem);
//                EventBus.getDefault().post(new TimeEditPhotoDeleteEvent(currentItem, path));
//                getActivity().finish();
//            } else if (download) {
//                //保存图片到本地
////                save.setEnabled(false);
////                saveImage();
//
//                addTag();
//            }
//
//        }
        switch (item.getItemId()) {
            case R.id.action_more:
                if (dialog == null) {
                    dialog = new ImageActionDialog(getActivity());
                    dialog.setClickListener(this);
                    dialog.isShared(false);
                    dialog.isDownload(download);
                    dialog.isDelete(delete);
                    dialog.isEdit(false);
                }
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTag() {
        int currentPosition = mViewPager.getCurrentItem();
        MediaObj mediaObj = mMedias.get(currentPosition);

        Bundle bundle = new Bundle();
        bundle.putParcelable("media", mediaObj);
        bundle.putInt("type", type);
        FragmentBridgeActivity.open(getActivity(), TagAddFragment.class.getSimpleName(), bundle);
    }

//    @Override

    public void saveImage() {
        int currentItem = mViewPager.getCurrentItem();
        String path = mPaths.get(currentItem);

        String fileName = path.substring(path.lastIndexOf("/"));
        String s = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(s + "/baby");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file, fileName);
        if (file1.exists()) {
            Toast.makeText(getContext(), "已保存到baby文件夹下", Toast.LENGTH_SHORT).show();
//            save.setEnabled(true);
            return;
        }
        if (!Utils.isNetworkConnected(getContext())) {
            ToastUtil.showToast("网络异常");
            return;
        }
//        ToastUtil.showToast("开始保存图片…");
        tfProgressDialog = TFProgressDialog.getInstance("");
        tfProgressDialog.setTvMessage("保存图片中…");
        tfProgressDialog.show(getChildFragmentManager(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageFactory.saveImage(path, file1);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tfProgressDialog.dismiss();
                        Toast.makeText(getContext(), "已保存到baby文件夹下", Toast.LENGTH_SHORT).show();
//                        save.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private List<MediaTipObj> tips;

    @Override
    public void click(View view, int type) {
        switch (type) {
            case 2:
                int currentItem = mViewPager.getCurrentItem();
                EventBus.getDefault().post(new TimeEditPhotoDeleteEvent(mMedias.get(currentItem), allDetailsListPosition, currentItem));
                getActivity().finish();
                break;
            case 3:
                saveImage();
//                addTag();
                break;
        }
    }


    private void initLikeCount() {
        int currentPosition = mViewPager.getCurrentItem();
        LogUtil.showLog(mMedias == null ? "null" : mMedias.size() + "----" + currentPosition);
        MediaObj mediaObj = mMedias.get(currentPosition);
        tvLikeCount.setText("+ " + mediaObj.getFavoritecount());
        if (mediaObj.getFavoritecount() > 0)
            tvLikeCount.setTextColor(getResources().getColor(R.color.sea_buckthorn));
        else tvLikeCount.setTextColor(getResources().getColor(R.color.aluminum));
        ivImageLike.changeStatus(mediaObj.getIsFavorite() == 1 ? R.drawable.image_liked : R.drawable.image_like);
    }


    private void addLike() {
        int currentPosition = mViewPager.getCurrentItem();
        MediaObj mediaObj = mMedias.get(currentPosition);
        if (mediaObj != null && mediaObj.getId() > 0) {
            if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
                addSubscription(apiService.addCircleMediaLike(mediaObj.getIsFavorite() == 1 ? 0 : 1, mediaObj.getId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                mediaObj.setFavoritecount(response.getFavoritecount());
                                mediaObj.setIsFavorite(mediaObj.getIsFavorite() == 1 ? 0 : 1);
                                tvLikeCount.setText("+ " + response.getFavoritecount());
                                ivImageLike.changeStatus(mediaObj.getIsFavorite() == 1 ? R.drawable.image_liked : R.drawable.image_like);
                                EventBus.getDefault().register(new CircleMediaEvent((CircleMediaObj) mediaObj));
                            }

                        }, throwable -> {
                        }));
            } else
                addSubscription(apiService.addLabelLike(mediaObj.getIsFavorite() == 1 ? "0" : "1", mediaObj.getId() + "")
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                mediaObj.setFavoritecount(response.getFavoritecount());
                                mediaObj.setIsFavorite(mediaObj.getIsFavorite() == 1 ? 0 : 1);
                                tvLikeCount.setText("+ " + response.getFavoritecount());
                                ivImageLike.changeStatus(mediaObj.getIsFavorite() == 1 ? R.drawable.image_liked : R.drawable.image_like);
                                if (allDetailsListPosition >= 0) {
                                    EventBus.getDefault().post(new MediaUpdateEvent(allDetailsListPosition, mediaObj));
                                } else {
                                    EventBus.getDefault().post(new MediaUpdateEvent(mediaObj, currentPosition));
                                }
                            }

                        }, throwable -> {
                        }));
        } else if (mediaObj != null) {
            mediaObj.setIsFavorite(mediaObj.getIsFavorite() == 1 ? 0 : 1);
            ivImageLike.changeStatus(mediaObj.getIsFavorite() == 1 ? R.drawable.image_liked : R.drawable.image_like);
            mediaObj.setFavoritecount(mediaObj.getIsFavorite() == 1 ? mediaObj.getFavoritecount() + 1 : mediaObj.getFavoritecount() - 1);
            tvLikeCount.setText("+ " + mediaObj.getFavoritecount());
            if (allDetailsListPosition >= 0) {
                EventBus.getDefault().post(new MediaUpdateEvent(allDetailsListPosition, mediaObj));
            } else
                EventBus.getDefault().post(new MediaUpdateEvent(mediaObj, currentPosition));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tag:
                addTag();
                break;
            case R.id.love:
                addLike();
                break;
        }
    }

    private GuideHelper guideHelper = null;

    private GuideHelper.TipData getTagTipData() {
        if (inflate == null) inflate = LayoutInflater.from(getActivity());
        View view = inflate.inflate(R.layout.guide_bigimage_tag_tip, null);
        view.findViewById(R.id.next).setOnClickListener(v -> guideHelper.nextPage());
        GuideHelper.TipData tipData = new GuideHelper.TipData(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, tag);
        tipData.setLocation(Gravity.TOP | Gravity.CENTER_HORIZONTAL, DeviceUtil.dpToPx(getResources(), 80), -DeviceUtil.dpToPx(getResources(), 5));
        return tipData;
    }

    private GuideHelper.TipData getLikeTipData() {
        if (inflate == null) inflate = LayoutInflater.from(getActivity());
        View view = inflate.inflate(R.layout.guide_bigimage_like_tip, null);
        view.findViewById(R.id.next).setOnClickListener(v -> guideHelper.nextPage());
        GuideHelper.TipData tipData = new GuideHelper.TipData(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, love);
        tipData.setLocation(Gravity.TOP | Gravity.CENTER_HORIZONTAL, DeviceUtil.dpToPx(getResources(), 80), -DeviceUtil.dpToPx(getResources(), 5));
        return tipData;
    }

    private void initGuideHelper(List<GuideHelper.TipData> list) {
        if (guideHelper == null)
            guideHelper = new GuideHelper(getActivity());
        for (GuideHelper.TipData tipData : list)
            guideHelper.addPage(false, tipData);
    }

    private void showGuide() {
        if (!GuideUtils.checkVersion(getClass().getSimpleName())) {
            return;
        }
        Observable.defer(() -> Observable.just(getTagTipData(), getLikeTipData())).filter(tipData -> tipData != null)
                .toList().doOnNext(tipDatas -> initGuideHelper(tipDatas))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(list -> guideHelper.show(false), throwable -> LogUtil.showError(throwable));

    }

    @Override
    public void submit() {
        deleteTip();
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        }
    }

    private TextView initBabyName(RelateBabyObj babyObj) {
        TextView textView = (TextView) inflate.inflate(R.layout.circle_relate_baby, flBabyList, false);
        textView.setText(String.format("@%s", babyObj.getBabyName()));
        textView.setTag(R.id.recycler_item_click_tag, babyObj);
        return textView;
    }

    private void initCircleBaby() {
        if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
            flBabyList.removeAllViews();
            addSubscription(Observable.defer(() -> Observable.just(mMedias.get(mViewPager.getCurrentItem())))
                    .map(mediaObj -> (CircleMediaObj) mediaObj)
                    .flatMap(circleMediaObj -> Observable.from(circleMediaObj.getRelateBabys()))
                    .filter(relateBabyObj -> relateBabyObj != null)
                    .map(relateBabyObj -> initBabyName(relateBabyObj))
                    .toList()
                    .compose(SchedulersCompat.applyIoSchedulers()).doOnNext(textViews -> flBabyList.removeAllViews())
                    .flatMap(textViews -> Observable.from(textViews))
                    .subscribe(textView -> flBabyList.addView(textView), throwable -> LogUtil.showError(throwable)));
        } else tvRelateBaby.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(MediaObj mediaObj) {
        int currentPosition = mViewPager.getCurrentItem();
        mMedias.get(currentPosition).setTips(mediaObj.getTips());
        initTips();
        if (allDetailsListPosition >= 0)
            EventBus.getDefault().post(new MediaUpdateEvent(allDetailsListPosition, mediaObj));
        else
            EventBus.getDefault().post(new MediaUpdateEvent(mediaObj, currentPosition));
    }


    @Subscribe
    public void onEvent(CircleMediaEvent event) {
        LogUtil.showLog("event  " + event.getType());
        if (event.getMediaObj() != null && event.getType() == 0) {
            int currentIndex = mViewPager.getCurrentItem();
            mMedias.get(currentIndex).setTips(event.getMediaObj().getTips());
            initTips();
        }
    }

    @Override
    public void imageClcik() {
        if (llBotton != null)
            llBotton.setVisibility(llBotton.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.tv_relate_baby)
    public void onClick() {
//        if (circleBabyDialog == null)
//            circleBabyDialog = new CircleBabyDialog(getActivity());
    }
}
