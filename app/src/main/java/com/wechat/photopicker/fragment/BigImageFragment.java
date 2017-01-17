package com.wechat.photopicker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wechat.photopicker.adapter.PhotoPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.images.TagAddFragment;
import cn.timeface.circle.baby.ui.images.beans.TipObj;
import cn.timeface.circle.baby.ui.images.views.ImageActionDialog;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_PHOTO_PATHS;
import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_SELECTOR_POSITION;

/**
 * Created by yellowstart on 15/12/15.
 */
public class BigImageFragment extends BaseFragment implements ImageActionDialog.ClickCallBack ,View.OnClickListener{

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
    RelativeLayout tag;
    @Bind(R.id.love)
    RelativeLayout love;
    @Bind(R.id.ll_botton)
    LinearLayout llBotton;
    private List<String> mPaths;

    private ArrayList<MediaObj> mMedias;
    private Bundle mBundle;
    private PhotoPagerAdapter mPhotoPagerAdapter;
    private int mCurrenItem;
    private boolean download;
    private boolean delete;
    private MenuItem save;
    private TFProgressDialog tfProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mPaths = intent.getStringArrayListExtra(KEY_PHOTO_PATHS);
        mMedias = intent.getParcelableArrayListExtra("mediaList");
        mCurrenItem = intent.getIntExtra(KEY_SELECTOR_POSITION, 0);
        download = intent.getBooleanExtra("download", false);
        delete = intent.getBooleanExtra("delete", false);
        setHasOptionsMenu(true);
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
        }
        mViewPager.setAdapter(mPhotoPagerAdapter);
        mViewPager.setCurrentItem(mCurrenItem);
        if (mMedias != null && mMedias.size() > 0)
            llBotton.setVisibility(View.VISIBLE);
        else llBotton.setVisibility(View.GONE);

        tag.setOnClickListener(this);
        love.setOnClickListener(this);
        initListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

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
        bundle.putLong("mediaId", mediaObj.getId());
        FragmentBridgeActivity.open(getActivity(), TagAddFragment.class.getSimpleName(), bundle);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_download:
//                tvDownload.setEnabled(false);
//                saveImage();
//                break;
//            case R.id.tv_delete:
//                tvDelete.setEnabled(false);
//                int currentItem = mViewPager.getCurrentItem();
//                String path = mPaths.get(currentItem);
//                EventBus.getDefault().post(new TimeEditPhotoDeleteEvent(currentItem, path));
//                getActivity().finish();
//                break;
//        }
//    }

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

    private List<TipObj> tips;

    private void reqData() {
        addSubscription(apiService.getTips("", "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {

                    if (response.success()) {
                    }

                }, throwable -> {
                }));
    }

    @Override
    public void click(View view, int type) {
        switch (type) {
            case 2:

                break;
            case 3:
                saveImage();
//                addTag();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tag:
                addTag();
                break;
            case R.id.love:

                break;
        }
    }
}
