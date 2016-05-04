package com.wechat.photopicker.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wechat.photopicker.adapter.PhotoPagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.utils.ImageFactory;

import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_PHOTO_PATHS;
import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_SELECTOR_POSITION;

/**
 * Created by yellowstart on 15/12/15.
 */
public class BigImageFragment extends Fragment {

    private ViewPager mViewPager;
    private List<String> mPaths;

    private Bundle mBundle;
    private PhotoPagerAdapter mPhotoPagerAdapter;
    private int mCurrenItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mPaths = intent.getStringArrayListExtra(KEY_PHOTO_PATHS);
        mCurrenItem = intent.getIntExtra(KEY_SELECTOR_POSITION, 0);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpage_big_image_show,container,false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_big_image);
        if (mPaths.size() > 0 && mPaths != null){
            mPhotoPagerAdapter = new PhotoPagerAdapter(getActivity(),mPaths);
        }
        mViewPager.setAdapter(mPhotoPagerAdapter);
        mViewPager.setCurrentItem(mCurrenItem);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_bigimage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            //保存图片到本地
            int currentItem = mViewPager.getCurrentItem();
            String path = mPaths.get(currentItem);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = new URL(path).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        ImageFactory.saveImage(bitmap);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        return super.onOptionsItemSelected(item);
    }

}
