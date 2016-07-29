package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.wbtech.ums.UmsAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.views.AbsoluteLayout.ImageLayout;

/**
 * Created by lidonglin on 16/7/29.
 */
public class GuideActivity extends BaseAppCompatActivity {

    @Bind(R.id.fl_guide)
    FrameLayout flGuide;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuideActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        List<Integer> imgs = new ArrayList<>();
        imgs.add(R.drawable.guide_1);
        imgs.add(R.drawable.guide_2);
        imgs.add(R.drawable.guide_3);
        imgs.add(R.drawable.guide_4);
        imgs.add(R.drawable.guide_5);

        ConvenientBanner banner = new ConvenientBanner<>(this,true);
        banner.startTurning(3000);
        banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        flGuide.addView(banner);
        banner.setPages(() -> new NetworkImageHolderView(), imgs)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        banner.setcurrentitem(0);
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position == imgs.size()-1){
                    //跳转登录界面
                    LoginActivity.open(GuideActivity.this);
                    finish();
                }
            }
        });
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == imgs.size()-1){
                    banner.setCanLoop(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class NetworkImageHolderView implements Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

}
