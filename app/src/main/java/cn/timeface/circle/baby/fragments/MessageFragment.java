package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.MessageAdapter;
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class MessageFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_read)
    TextView tvRead;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private MessageAdapter adapter;

    public MessageFragment() {
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


        adapter = new MessageAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        tvRead.setOnClickListener(this);

        return view;
    }

    private void reqData() {
        apiService.queryMsgList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(msgListResponse -> {

                    setDataList(msgListResponse.getDataList());

                }, throwable -> {
                    Log.e(TAG, "queryBabyFamilyList:");
                });

    }

    private void setDataList(List<Msg> dataList) {
        adapter.getListData().clear();
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
                Msg msg = (Msg) v.getTag(R.string.tag_ex);
                switch (msg.getType()){
                    case 0://系统消息，跳转系统消息列表
                        FragmentBridgeActivity.open(getActivity(), "SystemMessageFragment");
                        break;
                    case 1://赞
                    case 2://发布动态
                    case 3://评论，跳转动态详情
                        break;
                    case 4://新成员加入，跳转亲友圈
                        FragmentBridgeActivity.open(getActivity(), "FamilyMemberFragment");
                        break;

                }
                break;
        }
    }
}
