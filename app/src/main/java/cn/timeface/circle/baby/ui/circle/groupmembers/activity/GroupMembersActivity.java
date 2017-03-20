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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.ui.circle.groupmembers.adapter.GroupMemberAdapter;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.section.GroupMemberSection;

public class GroupMembersActivity extends BaseAppCompatActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    GroupMemberAdapter adapter;
    boolean isExpand = true;
    List<CircleUserInfo> circleUserInfoList;

    public static void open(Context context) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
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

        setUpView();
        reqContent();
        circleUserInfoList = new ArrayList<>();
        CircleUserInfo circleUserInfo = new CircleUserInfo("http://img1.timeface.cn/uploads/avator/default.png", 15, "CC", 45, 1);
        circleUserInfoList.add(circleUserInfo);
        for (int i = 0; i <18 ; i++) {
            CircleUserInfo circleUserInfo1 = new CircleUserInfo("http://img1.timeface.cn/uploads/avator/default.png", 15, "CC", 45, 2);
            circleUserInfoList.add(circleUserInfo1);
        }
        ArrayList<GroupMemberSection> content = getContent();
        adapter.setNewData(content);
    }

    private void reqContent() {

    }
    private  ArrayList<GroupMemberSection> getContent() {
        ArrayList<GroupMemberSection> menuSections = new ArrayList<>();
        //1&老师&4   1是类型  2是title  3是个数
        menuSections.add(new GroupMemberSection(true, "1&老师&4"));
        for (int i = 0; i < 4; i++) {
            CircleUserInfo circleUserInfo = new CircleUserInfo("http://img1.timeface.cn/uploads/avator/default.png", 15, "CC", 45, 2);
            MenemberInfo menemberInfo = new MenemberInfo("http://img1.timeface.cn/uploads/avator/default.png", circleUserInfo);
            menuSections.add(new GroupMemberSection(menemberInfo));
        }
        menuSections.add(new GroupMemberSection(true, "2&圈子成员&10"));
        List<CircleUserInfo> circleMember = getCircleMember();
        for (int i = 0; i < circleMember.size(); i++) {
            CircleUserInfo circleUserInfo = new CircleUserInfo("http://img1.timeface.cn/uploads/avator/default.png", 15, "CC", 45, 2);
            MenemberInfo menemberInfo = new MenemberInfo("http://img1.timeface.cn/uploads/avator/default.png", circleMember.get(i));
            menuSections.add(new GroupMemberSection(menemberInfo));
        }
        menuSections.add(new GroupMemberSection(true, "3&入圈申请&5"));
        for (int i = 0; i < 5; i++) {
            CircleUserInfo circleUserInfo = new CircleUserInfo("http://img1.timeface.cn/uploads/avator/default.png", 15, "CC", 45, 2);
            MenemberInfo menemberInfo = new MenemberInfo("http://img1.timeface.cn/uploads/avator/default.png", circleUserInfo);
            menuSections.add(new GroupMemberSection(menemberInfo));
        }
        return menuSections;
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
                CircleUserInfo circleUserInfo = groupMemberSection.t.getCircleUserInfo();
                switch (circleUserInfo.getCircleUserType()) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 4:
                        InviteActivity.open(GroupMembersActivity.this);
                        break;
                }
                ToastUtil.showToast(GroupMembersActivity.this, "成功");
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

    private List<CircleUserInfo> getCircleMember() {
        List<CircleUserInfo> circleUserInfos = new ArrayList<>();
        if (isExpand) {
            for(int i=0;i<9;i++) {
                circleUserInfos.add(circleUserInfoList.get(i));
            }
        } else {
            circleUserInfos.addAll(circleUserInfoList);
        }
        circleUserInfos.add(new CircleUserInfo("","新增成员",4));
        return circleUserInfos;
    }
}
