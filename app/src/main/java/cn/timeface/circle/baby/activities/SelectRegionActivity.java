package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.RegionAdapter;
import cn.timeface.circle.baby.api.models.DistrictModel;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014-4-16下午2:02:23
 * @TODO 选择地区(省)
 */
public class SelectRegionActivity extends BaseAppCompatActivity {
    // ListView
    @Bind(R.id.region_list)
    ListView mRegionListView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private StringBuffer sbRegion;
    private List<DistrictModel> subRegionList = new ArrayList<>();
    private RegionAdapter mregionAdapter;

    //初期值 顶级
    private long parentid = -1;

    public static void open(Context context) {
        Intent intent = new Intent(context, SelectRegionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mregionAdapter = new RegionAdapter(DistrictModel.queryDicts(), this);
        sbRegion = new StringBuffer();
        mRegionListView.setAdapter(mregionAdapter);
        mRegionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                // 被点击 item Model
                DistrictModel model = ((DistrictModel) mregionAdapter.getItem(position));
                subRegionList = DistrictModel.queryDictsByParentId(model.getLocationId());
                sbRegion.append(" ");
                sbRegion.append(((DistrictModel) mregionAdapter.getItem(position)).getLocationName());
                if (subRegionList != null && subRegionList.size() > 0) {
                    SelectRegionActivity.this.parentid = Long.parseLong(model.getLocationPid());
                    mregionAdapter = new RegionAdapter(subRegionList, SelectRegionActivity.this);
                    mRegionListView.setAdapter(mregionAdapter);
                } else {
                    // back();
                    SelectRegionActivity.this.parentid = -1;
                    if (!TextUtils.isEmpty(sbRegion.toString())) {
                        Intent data = new Intent();
                        data.putExtra("data", sbRegion.toString());
//                        setResult(EditMineDataActivity.SELECT_REGION_RESULT_CODE, data);
                        setResult(Activity.RESULT_OK, data);
                    }
                    finish();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回
        if (item.getItemId() == android.R.id.home) {
            if (parentid != -1) {
                // 顶级 （省）
                if (parentid == 0) {
                    mregionAdapter = new RegionAdapter(DistrictModel.queryDicts(), this);
                    mRegionListView.setAdapter(mregionAdapter);
                    parentid = -1;
                }
                // 非顶级
                else {
                    subRegionList = DistrictModel.queryDictsByParentId(String.valueOf(parentid));
                    parentid = Long.parseLong(DistrictModel.query(String.valueOf(parentid)).getLocationPid());
                    mregionAdapter = new RegionAdapter(subRegionList, this);
                    mRegionListView.setAdapter(mregionAdapter);
                }
                sbRegion.delete(sbRegion.lastIndexOf(" "), sbRegion.length());
            } else {
//                getActivity().onBackPressed();
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
