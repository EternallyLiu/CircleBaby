package cn.timeface.circle.baby.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wechat.photopicker.PickerPhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.AddBookListAdapter;
import cn.timeface.circle.baby.adapters.BookSizeListAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.CardBookSizeObj;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.managers.listeners.OnItemClickListener;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class BookSizeListFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private List<ImageInfoListObj> dataList;
    private String bookSizeId;


    public BookSizeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = getArguments().getParcelableArrayList("dataList");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addbooklist, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("选择尺寸");
        reqData();
        return view;
    }

    private void reqData() {
        apiService.getSubBabyBookWorksTypeList(2)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(cardBookSizeResponse -> {
                    if (cardBookSizeResponse.success()) {
                        setDataList(cardBookSizeResponse.getDataList());
                    }
                }, error -> {
                    Log.e(TAG, "getBabyBookWorksTypeList:");
                });

    }

    private void setDataList(List<CardBookSizeObj> list) {
        BookSizeListAdapter adapter = new BookSizeListAdapter(getContext(), list);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(bookSizeId ->{
            this.bookSizeId = bookSizeId+"";
            startPhotoPick(dataList);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void startPhotoPick(List<ImageInfoListObj> dataList) {
        Intent intent = new Intent(getActivity(), PickerPhotoActivity2.class);
        intent.putExtra("bookType",2);
        intent.putExtra("bookSizeId",bookSizeId);
        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
        startActivity(intent);
        getActivity().finish();
    }

}
