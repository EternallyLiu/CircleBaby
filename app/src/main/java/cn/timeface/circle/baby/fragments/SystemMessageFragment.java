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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.OrderDetailActivity;
import cn.timeface.circle.baby.adapters.SystemMessageAdapter;
import cn.timeface.circle.baby.api.models.objs.SystemMsg;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class SystemMessageFragment extends BaseFragment implements View.OnClickListener {

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
            actionBar.setTitle("系统消息");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new SystemMessageAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

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
                    OrderDetailActivity.open(getContext(),msg.getDataId()+"");
                }
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
                            adapter.notifyDataSetChanged();
                        }
                    }, error -> {
                        Log.e(TAG, "read:");
                    });

        }else if(item.getItemId() == R.id.del){
            new AlertDialog.Builder(getContext())
                    .setTitle("确定删除全部系统消息?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apiService.delMsg(0, 1)
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
