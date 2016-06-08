package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.RelationshipAdapter;
import cn.timeface.circle.baby.api.models.objs.Relationship;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class RelationshipActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_relationship)
    EditText etRelationship;
    @Bind(R.id.tv_create)
    TextView tvCreate;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private RelationshipAdapter adapter;
    private List<Relationship> dataList;

    public static void open(Context context) {
        Intent intent = new Intent(context, RelationshipActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);
        ButterKnife.bind(this);
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

                apiService.addRelationship(URLEncoder.encode(relationship))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(relationIdResponse -> {
                            if (relationIdResponse.success()) {
                                Relationship relation = new Relationship(relationship, relationIdResponse.getRelationId());
                                Intent intent = new Intent();
                                intent.putExtra("relationName",relation.getRelationName());
                                intent.putExtra("relationId",relation.getRelationId());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(this, relationIdResponse.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            Log.e(TAG, "addRelationship:", throwable);
                        });
                break;
            case R.id.tv_relationship:
                Relationship relation = (Relationship) v.getTag(R.string.tag_ex);
                Intent intent = new Intent();
                intent.putExtra("relationName",relation.getRelationName());
                intent.putExtra("relationId",relation.getRelationId());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    public void setDataList(List<Relationship> dataList) {
        this.dataList = dataList;
        adapter.getListData().clear();
        adapter.getListData().addAll(dataList);
        adapter.notifyDataSetChanged();
    }
}
