package cn.timeface.circle.baby.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.adapters.MessageAdapter;
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class MessageFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private MessageAdapter adapter;

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle("消息");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new MessageAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        return view;
    }

    private void reqData() {
        apiService.queryMsgList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(msgListResponse -> {
                    if(msgListResponse.success()){
                        setDataList(msgListResponse.getDataList());
                    }
                }, error -> {
                    Log.e(TAG, "queryMsgList:");
                    error.printStackTrace();
                });

    }

    private void setDataList(List<Msg> dataList) {
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
                switch (msg.getType()) {
                    case 0://系统消息，跳转系统消息列表
                        FragmentBridgeActivity.open(getActivity(), "SystemMessageFragment");
                        break;
                    case 1://赞
                    case 2://发布动态
                    case 3://评论，跳转动态详情
                        if(msg.getTimeInfo().getTimeId()==0){
                            ToastUtil.showToast("时光已删除");
                        }else{
                            TimeLineDetailActivity.open(getActivity(), msg.getTimeInfo());
                        }
                        break;
                    case 4://新成员加入，跳转亲友圈
                        FragmentBridgeActivity.open(getActivity(), "FamilyMemberFragment");
                        break;

                }
                apiService.read(msg.getId(),0)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if(response.success()){
                                reqData();
                            }
                        }, error -> {
                            Log.e(TAG, "read:");
                            error.printStackTrace();
                        });
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_msg,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.read){
            apiService.read(0, 1)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        ToastUtil.showToast(response.getInfo());
                        if (response.success()) {
                            adapter.setAllRead();
                            adapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new UnreadMsgEvent());
                        }
                    }, error -> {
                        Log.e(TAG, "read:");
                    });

        }else if(item.getItemId() == R.id.del){
            new AlertDialog.Builder(getContext())
                    .setTitle("确定删除全部消息?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apiService.delMsg(0, 0)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                ToastUtil.showToast(response.getInfo());
                                if (response.success()) {
                                    adapter.getListData().clear();
                                    adapter.notifyDataSetChanged();
                                }
                            }, error -> {
                                Log.e(TAG, "delMsg:");
                            });
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
