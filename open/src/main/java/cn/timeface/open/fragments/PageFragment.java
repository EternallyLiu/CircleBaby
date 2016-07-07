package cn.timeface.open.fragments;


import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.fragments.base.BaseFragment;
import cn.timeface.open.utils.BookModelCache;
import cn.timeface.open.views.PageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends BaseFragment {
    TFOBookModel bookModel;
    TFOBookContentModel rightModel;
    TFOBookContentModel leftModel;
    Point screenInfo = new Point();

    public static PageFragment newInstance(Point rootWH, TFOBookContentModel contentModel) {
        return newInstance(rootWH, null, contentModel);
    }

    public static PageFragment newInstance(Point rootWH, TFOBookContentModel leftModel, TFOBookContentModel rightModel) {
        PageFragment pageFragment = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("root_view_info", rootWH);
        bundle.putParcelable("right_model", rightModel);
        bundle.putParcelable("left_model", leftModel);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rightModel = getArguments().getParcelable("right_model");
            leftModel = getArguments().getParcelable("left_model");
            screenInfo = getArguments().getParcelable("root_view_info");
        }
        this.bookModel = BookModelCache.getInstance().getBookModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //为毛要套一层framelayout??因为不套的话pageview设置背景颜色会超出范围
        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        PageView pageView = new PageView(getActivity(), leftModel, rightModel, false);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageView.getLayoutParams();
        lp.gravity = Gravity.CENTER;

        frameLayout.addView(pageView, lp);
        return frameLayout;
    }

}
