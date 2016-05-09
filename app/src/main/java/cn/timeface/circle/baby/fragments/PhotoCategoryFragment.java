package cn.timeface.circle.baby.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.PhotoCategoryAdapter;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.mediastore.MediaStoreBucket;
import cn.timeface.circle.baby.views.DividerItemDecoration;

/**
 * @author YW.SUN
 * @from 2016/3/23
 * @TODO
 */
public class PhotoCategoryFragment extends BaseFragment {

    @Bind(R.id.rv_bucket_list)
    RecyclerView rvBuckets;

    List<MediaStoreBucket> buckets;
    PhotoCategoryAdapter adapter;

    public static PhotoCategoryFragment newInstance(List<MediaStoreBucket> buckets) {
        PhotoCategoryFragment fragment = new PhotoCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("buckets", (ArrayList<? extends Parcelable>) buckets);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_category, container, false);
        ButterKnife.bind(this, view);
        this.buckets = getArguments().getParcelableArrayList("buckets");
        setupView();
        return view;
    }

    private void setupView(){
        adapter = new PhotoCategoryAdapter(getActivity(), buckets);
        rvBuckets.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBuckets.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#f2f2f2"));
        itemDecoration.setPaint(paint);
        rvBuckets.addItemDecoration(itemDecoration);
    }

    public void clickAlbumSelect(View view){
        adapter.notifyDataSetChanged();
    }
}
