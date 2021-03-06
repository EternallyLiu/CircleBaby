package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.RelationshipAdapter;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.Relationship;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.beans.BabyChanged;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class ConfirmRelationActivity extends BaseAppCompatActivity implements View.OnClickListener, DeleteDialog.SubmitListener, DeleteDialog.CloseListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_relationship)
    EditText etRelationship;
    @Bind(R.id.tv_create)
    TextView tvCreate;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private RelationshipAdapter adapter;
    private DeleteDialog dialog;
    private String code;
    private TFProgressDialog tfprogress;

    public static void open(Context context) {
        Intent intent = new Intent(context, ConfirmRelationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);
        ButterKnife.bind(this);
        code = getIntent().getStringExtra("code");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvCreate.setOnClickListener(this);
        adapter = new RelationshipAdapter(this, new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(adapter);
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.TRANSPARENT).sizeResId(R.dimen.view_space_small).build());

        reqData();

    }

    private void reqData() {
        apiService.queryBabyFamilyTypeInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(relationshipResponse -> {
                    setDataList(relationshipResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getRelationshipList:", throwable);
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create:
                String relationship = etRelationship.getText().toString().trim();
                if (TextUtils.isEmpty(relationship)) {
                    Toast.makeText(this, "请输入与宝宝的关系", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (relationship.length() > 20) {
                    Toast.makeText(this, "与宝宝的关系不能超过20个字符，请修改", Toast.LENGTH_SHORT).show();
                    return;
                }
                attention(relationship);
//                apiService.addRelationship(URLEncoder.encode(relationship))
//                        .compose(SchedulersCompat.applyIoSchedulers())
//                        .subscribe(relationIdResponse -> {
//                            if (relationIdResponse.success()) {
//                                attention(relationship);
//                            } else {
//                                Toast.makeText(this, relationIdResponse.getInfo(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, throwable -> {
//                            Log.e(TAG, "addRelationship:", throwable);
//                        });
                break;
            case R.id.tv_relationship:
                Relationship relation = (Relationship) v.getTag(R.string.tag_ex);

                attention(relation.getRelationName());
                break;
        }
    }

    public void setDataList(List<Relationship> dataList) {
        adapter.getListData().clear();
        adapter.getListData().addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    public void attention(String name) {
        showProgress();
        apiService.babyAttention("", URLEncoder.encode(name))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(userLoginResponse -> {
                    if (userLoginResponse.success()) {
                        if (userLoginResponse.getErrorCode() != 0) {
                            ToastUtil.showToast(userLoginResponse.getInfo());
                        } else {
                            babyChanged = new BabyChanged(userLoginResponse.getUserInfo());
                            queryFamily(userLoginResponse.getUserInfo().getRelationName(), userLoginResponse.getUserInfo().getBabyObj());
                            FastData.setUserInfo(userLoginResponse.getUserInfo());
//                            EventBus.getDefault().post(new ConfirmRelationEvent());
//                            finish();
                        }
                    } else {
                        ToastUtil.showToast(userLoginResponse.getInfo());
                    }
                }, error -> {
                    Toast.makeText(this, "邀请码校验失败", Toast.LENGTH_SHORT).show();
                });
    }


    private BabyChanged babyChanged = null;

    private void queryFamily(String relativeName, BabyObj babyObj) {
        apiService.queryBabyFamilyLoginInfoList().compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(familyListResponse -> {
                    if (familyListResponse.success()) {
                        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(String.format("欢迎 %s 加入！", relativeName)).append("\n");
                        builder.append("你将和");
                        int beginIndex = builder.length();
                        for (int i = 0; i < familyListResponse.getDataList().size(); i++) {
                            if (i != 0 && i < familyListResponse.getDataList().size() - 1)
                                builder.append("、");
                            if (!FastData.getUserId().equals(familyListResponse.getDataList().get(i).getUserInfo().getUserId())) {
                                builder.append(familyListResponse.getDataList().get(i).getUserInfo().getRelationName());
                            }
                        }
                        int endIndex = builder.length();
                        builder.append("一起来记录").append("\n");
                        builder.append(String.format("见证 %s 的成长~", babyObj.getNickName()));
                        String content = builder.toString();
                        builder.setSpan(SpannableUtils.getTextColor(this,R.color.sea_buckthorn), content.indexOf(relativeName), content.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(styleSpan, content.indexOf(relativeName), content.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(SpannableUtils.getTextColor(this,R.color.sea_buckthorn), content.indexOf(babyObj.getNickName()), content.indexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(styleSpan, content.indexOf(babyObj.getNickName()), content.indexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(SpannableUtils.getTextColor(this,R.color.sea_buckthorn), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        babyChanged.setBuilder(builder);
                        hideProgress();
                        submit();
                    }
                },error->{
                    hideProgress();
                });
    }

    private void hideProgress() {
        if (tfprogress != null && !tfprogress.isHidden())
            tfprogress.dismiss();
    }

    private void showProgress() {
        if (tfprogress == null) {
            tfprogress = TFProgressDialog.getInstance("正在加载……");
        }
        if (tfprogress.isHidden())
            tfprogress.show(getSupportFragmentManager(), "");
    }

    @Override
    public void close() {
        EventBus.getDefault().post(new ConfirmRelationEvent());
        finish();
    }

    @Override
    public void submit() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        EventBus.getDefault().post(babyChanged);
        EventBus.getDefault().post(new ConfirmRelationEvent());
        finish();
    }
}
