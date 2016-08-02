package cn.timeface.circle.baby.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.wechat.photopicker.PickerPhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class AddBookFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.fl_ad)
    FrameLayout flAd;
    @Bind(R.id.tv_booktitle)
    TextView tvBooktitle;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.iv_image)
    ImageView ivImage;
    @Bind(R.id.iv_creatbook)
    ImageView ivCreatbook;
    private BookTypeListObj bookTypeListObj;
    private List<MediaObj> imgList;
    private ConvenientBanner banner;

    public AddBookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookTypeListObj = getArguments().getParcelable("BookTypeListObj");
        imgList = bookTypeListObj.getImgList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addbook, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle(bookTypeListObj.getCoverTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initData();
        return view;
    }

    private void initData() {
        tvBooktitle.setText(bookTypeListObj.getCoverTitle());
        tvPrice.setText("¥"+bookTypeListObj.getPrice()+"元/套起");
        GlideUtil.displayImage(bookTypeListObj.getDetail().getImgUrl(),ivImage);
        ivCreatbook.setOnClickListener(this);

        banner = new ConvenientBanner(getActivity(), true);
        banner.startTurning(3000);
        banner.setMinimumHeight((int) (Remember.getInt("width", 0)*1.8));
        flAd.addView(banner);

        banner.setPages(() -> new NetworkImageHolderView(), imgList)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_creatbook:
                apiService.queryImageInfoList("", bookTypeListObj.getType())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                List<MediaObj> mediaObjs = new ArrayList<>();
                                List<ImageInfoListObj> dataList = response.getDataList();
//                                if (dataList.size() > 0) {
                                    for (ImageInfoListObj obj : dataList) {
                                        List<MediaObj> mediaList = obj.getMediaList();
                                        int timeId = obj.getTimeId();
//                                        mediaObjs.addAll(mediaList);
                                        for (MediaObj mediaObj : mediaList) {
                                            mediaObj.setTimeId(timeId);
                                        }
                                    }
                                    if (bookTypeListObj.getType() == 2) {
                                        //日记卡片书，进入选择size界面
                                        FragmentBridgeActivity.openBookSizeListFragment(getContext(), dataList);
                                    } else if (bookTypeListObj.getType() == 5) {
                                        //照片书，进入选择主题界面
                                        Intent intent = new Intent(getActivity(), SelectThemeActivity.class);
                                        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
                                        startActivity(intent);
                                    } else {
                                        startPhotoPick(dataList);
                                    }
//                                } else {
//                                    ToastUtil.showToast("没有此类图片");
//                                }
                                getActivity().finish();
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        }, error -> {
                            Log.e(TAG, "queryImageInfoList:");
                        });
                break;
        }
    }

    private void startPhotoPick(List<ImageInfoListObj> dataList) {
        Intent intent = new Intent(getActivity(), PickerPhotoActivity2.class);
        intent.putExtra("bookType", bookTypeListObj.getType());
        intent.putExtra("bookPage", bookTypeListObj.getBookPage());
        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
        startActivity(intent);
    }

    class NetworkImageHolderView implements Holder<MediaObj> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, MediaObj data) {
            Glide.with(getActivity()).load(data.getImgUrl()).into(imageView);
        }
    }
}
