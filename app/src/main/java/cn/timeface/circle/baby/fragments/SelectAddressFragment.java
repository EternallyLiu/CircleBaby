package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.AddressListAdapter;
import cn.timeface.circle.baby.adapters.MineBookAdapter;
import cn.timeface.circle.baby.api.models.AddressItem;
import cn.timeface.circle.baby.api.models.objs.AddressObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class SelectAddressFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    List<MineBookObj> bookList = new ArrayList<>();

    public SelectAddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minebook, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText("确认收货地址");
        ivAdd.setOnClickListener(this);
//        reqData();
        return view;
    }

    @Override
    public void onResume() {
        reqData();
        super.onResume();
    }

    private void reqData() {
        apiService.getAddressList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(addressListResponse -> {
                    setDataList(addressListResponse.getDataList());
                }, error -> {
                    Log.e(TAG, "timeline:");
                });
    }

    private void setDataList(List<AddressItem> bookList) {
//        List<AddressObj> addressObjs = mokeData(bookList);
        AddressListAdapter adapter = new AddressListAdapter(getActivity(), bookList);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(adapter);
        

    }

    private List<AddressObj> mokeData(List<AddressObj> bookList) {
        AddressObj addressObj = new AddressObj();
        for (int i = 0; i < 5; i++) {
            bookList.add(addressObj);
        }
        return bookList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                FragmentBridgeActivity.open(getContext(), "AddAddressFragment");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
