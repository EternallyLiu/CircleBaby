package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.SystemMessageAdapter;
import cn.timeface.circle.baby.api.models.objs.SystemMsg;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class SystemMessageFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_delete)
    TextView tvDelete;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private SystemMessageAdapter adapter;

    public SystemMessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle.setText("系统消息");


        adapter = new SystemMessageAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        tvDelete.setOnClickListener(this);

        return view;
    }

    private void reqData() {
        apiService.querySystemMsgList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(systemMsgListResponse -> {

                    setDataList(systemMsgListResponse.getDataList());

                }, throwable -> {
                    Log.e(TAG, "queryBabyFamilyList:");
                });

    }

    private void setDataList(List<SystemMsg> dataList) {
        adapter.setListData(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_message:
                SystemMsg msg = (SystemMsg) v.getTag(R.string.tag_ex);
                if (msg.getContent().contains("订单详情")) {
                    //跳转订单详情

                }
                break;
        }
    }
}
