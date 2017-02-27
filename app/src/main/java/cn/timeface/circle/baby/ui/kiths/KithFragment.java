package cn.timeface.circle.baby.ui.kiths;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timeface.refreshload.headfoot.impl.DefaultLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.kiths.adapters.KithsAdapter;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.views.DividerItemDecoration;

/**
 * 亲友团
 * author : wangshuai Created on 2017/1/19
 * email : wangs1992321@gmail.com
 */
public class KithFragment extends BaseFragment implements BaseAdapter.OnItemClickLister, EmptyDataView.EmptyCallBack, BaseAdapter.LoadDataFinish {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.pull_refresh_list)
    SwipeRefreshLayout pullRefreshList;
    @Bind(R.id.empty)
    EmptyDataView emptyDataView;

    private KithsAdapter adapter = null;
    private ArrayList<String> relationNames;
    private DefaultLoadMoreView loadMoreView;
    private TFPTRRecyclerViewHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kith, container, false);

        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        title.setText(String.format("%s的一家", FastData.getBabyObj().getNickName()));
        adapter = new KithsAdapter(getActivity());
        adapter.setLoadDataFinish(this);
        emptyDataView.setErrorDrawable(R.drawable.net_empty);
        emptyDataView.setErrorRetryText("重新加载");
        emptyDataView.setErrorText("对不起！没有加载到数据！");
        emptyDataView.setEmptyCallBack(this);
        adapter.setItemClickLister(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(recyclerView);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation(), R.color.divider_color);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
//        pullRefreshList.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
//        pullRefreshList.setDistanceToTriggerSync(300);
//        pullRefreshList.setSize(SwipeRefreshLayout.DEFAULT);
//        pullRefreshList.setOnRefreshListener(this);
        helper = new TFPTRRecyclerViewHelper(getActivity(), recyclerView, pullRefreshList);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        reqData();
                    }

                    @Override
                    public void onTFPullUpToRefresh(View refreshView) {

                    }

                    @Override
                    public void onScrollUp(int firstVisibleItem) {
                    }

                    @Override
                    public void onScrollDown(int firstVisibleItem) {
                    }
                });
        reqData();

        return view;
    }

    private List<FamilyMemberInfo> filterData(List<FamilyMemberInfo> list) {
        if (relationNames == null) {
            relationNames = new ArrayList<>();
        }
        if (relationNames.size() > 0)
            relationNames.clear();
        relationNames.add("爸爸");
        relationNames.add("妈妈");
        relationNames.add("爷爷");
        relationNames.add("奶奶");
        relationNames.add("外公");
        relationNames.add("外婆");
        relationNames.add("其他成员");
        for (FamilyMemberInfo info : list) {
            String relativeName = info.getUserInfo().getRelationName();
            if (relationNames.contains(relativeName))
                relationNames.remove(relativeName);
        }
        for (String s : relationNames) {
            list.add(new FamilyMemberInfo(new UserObj(s)));
        }
        return list;
    }

    private void reqData() {
        apiService.queryBabyFamilyLoginInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(familyListResponse -> {
                    if (familyListResponse.success()) {
                        adapter.addList(true, filterData(familyListResponse.getDataList()));
                    } else
                        empty();
                    helper.finishTFPTRRefresh();
                }, throwable -> {
                    helper.finishTFPTRRefresh();
                    empty();
                });
    }

    private void empty() {
        if (adapter.getRealItemSize() <= 0) {
            emptyDataView.setVisibility(View.VISIBLE);
            pullRefreshList.setVisibility(View.GONE);
        } else {
            emptyDataView.setVisibility(View.GONE);
            pullRefreshList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_invite, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.invite) {
            FragmentBridgeActivity.openInviteFragment(getActivity(), "");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        FamilyMemberInfo item = adapter.getItem(position);
        if (item.getUserInfo() != null && !TextUtils.isEmpty(item.getUserInfo().getUserId()))
            FragmentBridgeActivity.openFamilyMemberInfoFragment(getActivity(), item.getUserInfo());
    }

    @Override
    public void retry() {
        reqData();
    }

    @Override
    public void loadfinish(int cpde) {
        empty();
    }
}
