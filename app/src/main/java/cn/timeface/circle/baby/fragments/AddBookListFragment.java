package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.AddBookListAdapter;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.managers.listeners.OnItemClickListener;
import cn.timeface.circle.baby.support.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.TFStateView;

public class AddBookListFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;

    public AddBookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addbooklist, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle("为" + FastData.getBabyNickName() + "定制");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tfStateView.setOnRetryListener(() -> reqData());
        tfStateView.loading();
        reqData();
        return view;
    }

    private void reqData() {
        apiService.getBabyBookWorksTypeList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(bookTypeListResponse -> {
                    tfStateView.finish();
                    if (bookTypeListResponse.success()) {
                        setDataList(bookTypeListResponse.getDataList());
                    }
                }, error -> {
                    tfStateView.showException(error);
                    Log.e(TAG, "getBabyBookWorksTypeList:");
                });

    }

    private void setDataList(List<BookTypeListObj> dataList) {
        AddBookListAdapter adapter = new AddBookListAdapter(getContext(), dataList);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void clickItem(Object o) {
                BookTypeListObj obj = (BookTypeListObj) o;
                FragmentBridgeActivity.openAddBookFragment(getContext(), obj);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
