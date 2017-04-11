package com.wechat.photopicker.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.dialog.CircleBabyDialog;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleMediaEvent;
import cn.timeface.circle.baby.ui.guides.GuideHelper;
import cn.timeface.circle.baby.ui.guides.GuideUtils;
import cn.timeface.circle.baby.ui.images.TagAddFragment;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.images.views.ImageActionDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
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
public class BigImageFragment extends BaseFragment implements ImageActionDialog.ClickCallBack, View.OnClickListener, DeleteDialog.SubmitListener, PhotoPagerAdapter.ImageClickListener, CircleBabyDialog.CircleBabyCallBack {

    public static int CIRCLE_MEDIA_IMAGE_EDITOR = 1009;
    public static int CIRCLE_MEDIA_IMAGE_NONE = 1010;
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
    @Bind(R.id.tv_relate_baby)
    TextView tvRelateBaby;
    @Bind(R.id.tv_babys)
    TextView tvBabys;
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
    private ImageActionDialog dialog;

    /**
     * @param context
     * @param mediaObj
     * @param type     BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR/CIRCLE_MEDIA_IMAGE_NONE
     * @param download
     * @param delete
     */
    public static void open(Context context, MediaObj mediaObj, int type, boolean download, boolean delete) {
        ArrayList<MediaObj> list = new ArrayList<>(0);
        list.add(mediaObj);
        FragmentBridgeActivity.openBigimageFragment(context, 0, list, MediaObj.getUrls(list), 0, type, download, delete);
    }

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
            mPhotoPagerAdapter.setMediaObjs(mMedias);
            mPhotoPagerAdapter.setClickListener(this);
        }
        mViewPager.setAdapter(mPhotoPagerAdapter);
        mViewPager.setCurrentItem(mCurrenItem);
        initTips();
        initLikeCount();

        tag.setOnClickListener(this);
        love.setOnClickListener(this);
        initListener();
        showGuide();
        if (mMedias != null && mMedias.size() > 0)
            llBotton.setVisibility(View.VISIBLE);
        else llBotton.setVisibility(View.GONE);
        tvBabys.setVisibility(View.GONE);
        if (type == CIRCLE_MEDIA_IMAGE_NONE) llBotton.setVisibility(View.GONE);
        if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
            tvRelateBaby.setVisibility(View.VISIBLE);
            initCircleBaby();
        }
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
            if (mediaObj.getId() <= 0) {
                if (mediaObj.getTips().contains(currentTip))
                    mediaObj.getTips().remove(currentTip);
                initTips();
                deletePostion = -1;
                currentTip = null;
                EventBus.getDefault().post(new CircleMediaEvent((CircleMediaObj) mediaObj));
            } else
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
                            } else ToastUtil.showToast(getActivity(), response.getInfo());

                        }, throwable -> {
                        }));
        } else {
            if (mediaObj.getId() <= 0) {
                if (mediaObj.getTips().contains(currentTip))
                    mediaObj.getTips().remove(currentTip);
                initTips();
                deletePostion = -1;
                currentTip = null;
                if (allDetailsListPosition >= -1)
                    EventBus.getDefault().post(new MediaUpdateEvent(allDetailsListPosition, mediaObj));
                else
                    EventBus.getDefault().post(new MediaUpdateEvent(mediaObj, deletePostion));
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
                            } else ToastUtil.showToast(getActivity(), response.getInfo());

                        }, throwable -> {
                        }));

        }
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
        if (delete && download) {
            inflater.inflate(R.menu.menu_timeline_detail, menu);
        } else {
            inflater.inflate(R.menu.menu_download, menu);
            menu.findItem(R.id.action_download).setTitle(download ? R.string.download : R.string.delete_name);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                click(null, download ? 3 : 2);
                break;
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
        if (tfProgressDialog == null)
            tfProgressDialog = TFProgressDialog.getInstance("");
        tfProgressDialog.setTvMessage("保存图片中…");
        if (tfProgressDialog.isHidden())
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
                if (this.type == CIRCLE_MEDIA_IMAGE_EDITOR || this.type == CIRCLE_MEDIA_IMAGE_NONE) {
                    EventBus.getDefault().post(new CircleMediaEvent(1, (CircleMediaObj) mMedias.get(currentItem)));
                } else
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
                                LogUtil.showLog("event post==" + mediaObj.getId());
                                EventBus.getDefault().post(new CircleMediaEvent((CircleMediaObj) mediaObj));
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
            if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
                EventBus.getDefault().post(new CircleMediaEvent((CircleMediaObj) mediaObj));
            } else if (allDetailsListPosition >= 0) {
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


    @Override
    public void submit() {
        deleteTip();
        if (deleteDialog != null && deleteDialog.isShowing()) {
            deleteDialog.dismiss();
        }
    }

    private void initCircleBaby() {
        if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
            tvBabys.setVisibility(View.GONE);
            addSubscription(Observable.defer(() -> Observable.just(mMedias.get(mViewPager.getCurrentItem())))
                    .map(mediaObj -> (CircleMediaObj) mediaObj)
                    .flatMap(circleMediaObj -> Observable.from(circleMediaObj.getRelateBabys()))
                    .filter(relateBabyObj -> relateBabyObj != null)
                    .toList().compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(relateBabyObjs -> {
                        if (relateBabyObjs != null && relateBabyObjs.size() > 0) {
                            tvBabys.setVisibility(View.VISIBLE);
                            StringBuilder builder = new StringBuilder("已关联 ");
                            for (int i = 0; i < relateBabyObjs.size(); i++) {
                                if (i < 15)
                                    builder.append("@").append(relateBabyObjs.get(i).getBabyName()).append("  ");
                                else builder.append(" ").append("…………");
                            }
                            tvBabys.setText(builder);
                        }
                    }, throwable -> LogUtil.showError(throwable)));
        }
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
        if (event.getMediaObj() != null && event.getType() == 0) {
            int currentIndex = mViewPager.getCurrentItem();
            mMedias.get(currentIndex).setTips(event.getMediaObj().getTips());
            initTips();
        }
    }

    @Override
    public void imageClcik() {
        if (toolbar != null)
            toolbar.setVisibility(toolbar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        if (llBotton != null) {
            llBotton.setVisibility(llBotton.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
        if (type == CIRCLE_MEDIA_IMAGE_NONE) llBotton.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_relate_baby)
    public void onClick() {
        if (type == CIRCLE_MEDIA_IMAGE_EDITOR) {
            if (circleBabyDialog == null) {
                circleBabyDialog = new CircleBabyDialog(getActivity(), (CircleMediaObj) mMedias.get(mViewPager.getCurrentItem()));
                circleBabyDialog.setCircleBabyCallBack(this);
            } else
                circleBabyDialog.setMediaObj((CircleMediaObj) mMedias.get(mViewPager.getCurrentItem()));
            if (!circleBabyDialog.isShowing()) {
                circleBabyDialog.show();
            }
        }
    }

    @Override
    public void circleResult(List<GetCircleAllBabyObj> babys, long mediaId) {
        if (babys == null || babys.size() <= 0) return;
        if (mediaId > 0) {
            if (tfProgressDialog == null)
                tfProgressDialog = TFProgressDialog.getInstance("");
            tfProgressDialog.setTvMessage("正在执行此操作~");
            tfProgressDialog.show(getChildFragmentManager(), "");
            addSubscription(apiService.circleAtBaby(Uri.encode(JSONUtils.parse2JSONString(babys)), FastData.getCircleId(), mediaId)
                    .compose(SchedulersCompat.applyIoSchedulers()).doOnNext(circleMediaResponse -> tfProgressDialog.dismiss()).
                            subscribe(circleMediaResponse -> {
                                if (circleMediaResponse.success()) {
                                    int currentIndex = mViewPager.getCurrentItem();
                                    ((CircleMediaObj) mMedias.get(currentIndex)).setRelateBabys(circleMediaResponse.getCircleMedia().getRelateBabys());
                                    EventBus.getDefault().post(new CircleMediaEvent(((CircleMediaObj) mMedias.get(currentIndex))));
                                    initCircleBaby();
                                } else
                                    ToastUtil.showToast(getActivity(), circleMediaResponse.getInfo());
                            }, throwable -> {
                                if (!tfProgressDialog.isHidden())
                                    tfProgressDialog.dismiss();
                                LogUtil.showError(throwable);
                            }));
        } else {
            int currentPostion = mViewPager.getCurrentItem();
            CircleMediaObj mediaObj = (CircleMediaObj) mMedias.get(currentPostion);
            mediaObj.getRelateBabys().clear();
            mediaObj.getRelateBabys().addAll(babys);
            tvBabys.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder("已关联 ");
            for (int i = 0; i < mediaObj.getRelateBabys().size(); i++) {
                if (i < 15)
                    builder.append("@").append(mediaObj.getRelateBabys().get(i).getBabyName()).append("  ");
                else builder.append(" ").append("…………");
            }
            tvBabys.setText(builder);
            EventBus.getDefault().post(new CircleMediaEvent(mediaObj));
        }
    }

    /**
     * 标签导航
     *
     * @return
     */
    private GuideHelper.TipData getTagTipData() {
        if (!GuideUtils.checkVersion(getClass().getSimpleName())) {
            return null;
        }
        if (inflate == null) inflate = LayoutInflater.from(getActivity());
        View view = inflate.inflate(R.layout.guide_bigimage_tag_tip, null);
        view.findViewById(R.id.next).setOnClickListener(v -> guideHelper.nextPage());
        GuideHelper.TipData tipData = new GuideHelper.TipData(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, tag);
        tipData.setLocation(Gravity.TOP | Gravity.CENTER_HORIZONTAL, DeviceUtil.dpToPx(getResources(), 80), -DeviceUtil.dpToPx(getResources(), 5));
        return tipData;
    }

    /**
     * 添加喜欢导航
     *
     * @return
     */
    private GuideHelper.TipData getLikeTipData() {
        if (!GuideUtils.checkVersion(getClass().getSimpleName() + "_like")) {
            return null;
        }
        if (inflate == null) inflate = LayoutInflater.from(getActivity());
        View view = inflate.inflate(R.layout.guide_bigimage_like_tip, null);
        view.findViewById(R.id.next).setOnClickListener(v -> guideHelper.nextPage());
        ImageView cut = (ImageView) view.findViewById(R.id.cut);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cut.getLayoutParams();
        params.leftMargin = DeviceUtil.dpToPx(getResources(), type == CIRCLE_MEDIA_IMAGE_EDITOR ? 130 : 180);

        GuideHelper.TipData tipData = new GuideHelper.TipData(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, love);
        tipData.setLocation(Gravity.TOP | Gravity.CENTER, type == CIRCLE_MEDIA_IMAGE_EDITOR ? -DeviceUtil.dpToPx(getResources(), 160) : DeviceUtil.dpToPx(getResources(), 0), -DeviceUtil.dpToPx(getResources(), 5));
        return tipData;
    }

    /**
     * 添加圈宝宝导航
     *
     * @return
     */
    private GuideHelper.TipData getCircleTipData() {
        if (type != CIRCLE_MEDIA_IMAGE_EDITOR) return null;
        if (!GuideUtils.checkVersion(getClass().getSimpleName() + "_circle")) {
            return null;
        }
        if (inflate == null) inflate = LayoutInflater.from(getActivity());
        View view = inflate.inflate(R.layout.guide_bigimage_circle_tip, null);
        view.findViewById(R.id.next).setOnClickListener(v -> guideHelper.nextPage());
        GuideHelper.TipData tipData = new GuideHelper.TipData(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, tvRelateBaby);
        tipData.setLocation(Gravity.TOP | Gravity.LEFT, DeviceUtil.dpToPx(getResources(), 30), -DeviceUtil.dpToPx(getResources(), 5));
        return tipData;
    }

    private void initGuideHelper(List<GuideHelper.TipData> list) {
        if (guideHelper == null)
            guideHelper = new GuideHelper(getActivity());
        for (GuideHelper.TipData tipData : list)
            guideHelper.addPage(false, tipData);
    }

    private void showGuide() {
        if (type == CIRCLE_MEDIA_IMAGE_NONE) return;
        Observable.defer(() -> Observable.just(getTagTipData(), getLikeTipData(), getCircleTipData())).filter(tipData -> tipData != null)
                .toList().filter(tipDatas -> tipDatas != null && tipDatas.size() > 0).doOnNext(tipDatas -> initGuideHelper(tipDatas))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(list -> guideHelper.show(false), throwable -> LogUtil.showError(throwable));

    }
}
