package cn.timeface.circle.baby.ui.kiths;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.kiths.adapters.KithsAdapter;
import cn.timeface.circle.baby.ui.timelines.views.SuperSwipeRefreshLayout;

/**
 * 亲友团
 * author : wangshuai Created on 2017/1/19
 * email : wangs1992321@gmail.com
 */
public class KithFragment extends BaseFragment {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private KithsAdapter adapter = null;
    private ArrayList<String> relationNames;

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
        title.setText("亲友团");
        adapter = new KithsAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        reqData();
        return view;
    }

    private List<FamilyMemberInfo> filterData(List<FamilyMemberInfo> list) {
        if (relationNames == null) {
            relationNames = new ArrayList<>();
        }
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
                    if (familyListResponse.success())
                        adapter.addList(true, filterData(familyListResponse.getDataList()));
                }, throwable -> {
                });
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
        if(item.getItemId() == R.id.invite){
            FragmentBridgeActivity.openInviteFragment(getActivity(), "");
        }
        return super.onOptionsItemSelected(item);
    }
}
