package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.UpdateMemberDetailEvent;
import cn.timeface.circle.baby.events.UpdateMemberEvent;
import cn.timeface.circle.baby.events.UpdateNameEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.adapter.GroupMemberAdapter;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.responses.MemberListResponse;
import cn.timeface.circle.baby.ui.circle.groupmembers.section.GroupMemberSection;
import rx.Subscription;
import rx.functions.Action1;

public class GroupMembersActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    GroupMemberAdapter adapter;
    boolean isExpand = true;
    List<MenemberInfo> normalUserInfoList;
    List<MenemberInfo> teacherUserInfoList;
    List<MenemberInfo> appliUserInfoList;
    GrowthCircleObj circleObj;
    MenemberInfo menemberInfo;

    public static void open(Context context, GrowthCircleObj circleObj) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra("circleObj", circleObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("群成员管理");
        circleObj = getIntent().getParcelableExtra("circleObj");

        setUpView();
        reqContent();
        normalUserInfoList = new ArrayList<>();
        teacherUserInfoList = new ArrayList<>();
        appliUserInfoList = new ArrayList<>();

    }

    private void reqContent() {
        Subscription subscribe = apiService.memberList(circleObj.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<MemberListResponse>() {
                    @Override
                    public void call(MemberListResponse memberListResponse) {
                        if (memberListResponse.success()) {
                            normalUserInfoList = memberListResponse.getNormals();
                            appliUserInfoList = memberListResponse.getApplicants();
                            teacherUserInfoList = memberListResponse.getTeachers();
                            ArrayList<GroupMemberSection> content = getContent();
                            adapter.setNewData(content);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private ArrayList<GroupMemberSection> getContent() {
        ArrayList<GroupMemberSection> menuSections = new ArrayList<>();
        //1&老师&4   1是类型  2是title  3是个数
        if (teacherUserInfoList.size() > 0) {
            menuSections.add(new GroupMemberSection(true, "1&老师&" + teacherUserInfoList.size()));
            for(int i=0;i<teacherUserInfoList.size();i++) {
                if (teacherUserInfoList.get(i).getUserInfo().getCircleUserId() == FastData.getCircleUserInfo().getCircleUserId()) {
                    this.menemberInfo = teacherUserInfoList.get(i);
                    EventBus.getDefault().post(new UpdateMemberDetailEvent(menemberInfo));
                }
            }

        }
        for (int i = 0; i < teacherUserInfoList.size(); i++) {
            menuSections.add(new GroupMemberSection(teacherUserInfoList.get(i)));
        }
        menuSections.add(new GroupMemberSection(true, "2&圈子成员&" + normalUserInfoList.size()));
        List<MenemberInfo> circleMember = getCircleMember();
        for (int i = 0; i < circleMember.size(); i++) {
            menuSections.add(new GroupMemberSection(circleMember.get(i)));
            if (circleMember.get(i).getUserInfo().getCircleUserId() == FastData.getCircleUserInfo().getCircleUserId()) {
                this.menemberInfo = circleMember.get(i);
                EventBus.getDefault().post(new UpdateMemberDetailEvent(menemberInfo));
            }
        }
        if (appliUserInfoList.size() > 0) {
            menuSections.add(new GroupMemberSection(true, "3&入圈申请&" + appliUserInfoList.size()));
        }
        for (int i = 0; i < appliUserInfoList.size(); i++) {
            appliUserInfoList.get(i).getUserInfo().setCircleUserType(5);
            menuSections.add(new GroupMemberSection(appliUserInfoList.get(i)));
        }
        return menuSections;
    }

    private List<MenemberInfo> getCircleMember() {
        List<MenemberInfo> circleUserInfos = new ArrayList<>();
        if (isExpand) {
            if (normalUserInfoList.size() > 9) {
                for (int i = 0; i < 9; i++) {
                    circleUserInfos.add(normalUserInfoList.get(i));
                }
            } else {
                circleUserInfos.addAll(normalUserInfoList);
            }
        } else {
            circleUserInfos.addAll(normalUserInfoList);
        }
        CircleUserInfo circleUserInfo = new CircleUserInfo("", "新增成员", 4);
        circleUserInfos.add(new MenemberInfo(new CircleBabyBriefObj(), "", circleUserInfo));
        return circleUserInfos;
    }

    private void setUpView() {
        rvContent.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        ArrayList<GroupMemberSection> menuSections = new ArrayList<>();

        adapter = new GroupMemberAdapter(R.layout.view_home_content, R.layout.layout_main_menu_header, menuSections);
        rvContent.setNestedScrollingEnabled(false);
        rvContent.setAdapter(adapter);

        rvContent.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                GroupMemberSection groupMemberSection = (GroupMemberSection) baseQuickAdapter.getItem(i);
                MenemberInfo t = groupMemberSection.t;
                if (t != null) {
                    CircleUserInfo circleUserInfo = groupMemberSection.t.getUserInfo();
                    switch (circleUserInfo.getCircleUserType()) {
                        case 4:
                            InviteActivity.open(GroupMembersActivity.this, circleObj);
                            break;
                        default:
                            CheckMemberDetailActivity.open(GroupMembersActivity.this, t);
                    }
                }
            }
        });
        rvContent.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if (isExpand) {
                    adapter.setIsExpand(true);
                    isExpand = false;
                    ArrayList<GroupMemberSection> content = getContent();
                    adapter.setNewData(content);
                } else {
                    adapter.setIsExpand(false);
                    isExpand = true;
                    ArrayList<GroupMemberSection> content = getContent();
                    adapter.setNewData(content);
                }
            }
        });
    }

    @Subscribe
    public void onEvent(UpdateMemberEvent event) {
        if (normalUserInfoList.size() > 0) {
            normalUserInfoList.clear();
        }
        if (teacherUserInfoList.size() > 0) {
            teacherUserInfoList.clear();
        }
        if (appliUserInfoList.size() > 0) {
            appliUserInfoList.clear();
        }
        reqContent();
    }

    @Subscribe
    public void onEvent(UpdateNameEvent event) {
        reqContent();
    }
}
