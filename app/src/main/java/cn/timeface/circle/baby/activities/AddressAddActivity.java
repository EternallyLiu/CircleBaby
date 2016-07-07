package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wbtech.ums.UmsAgent;
import com.wbtech.ums.common.UmsConstants;


import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.AddressItem;
import cn.timeface.circle.baby.api.models.DistrictModel;
import cn.timeface.circle.baby.api.models.responses.AddAddressResponse;
import cn.timeface.circle.baby.events.AddAddressFinishEvent;
import cn.timeface.circle.baby.utils.Utils;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.CheckedUtil;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @author SUN
 * @from 2014/12/4
 * @TODO
 */
public class AddressAddActivity extends BaseAppCompatActivity {

    public static final int SELECT_REGION_REQUEST_CODE = 101;
    @Bind(R.id.add_region_tv)
    TextView mRegion;
    @Bind(R.id.receiver_name_et)
    EditText mReceiverName;
    @Bind(R.id.receiver_phone_et)
    EditText mReceiverPhone;
    @Bind(R.id.address_detail_et)
    EditText mAddDetail;
    @Bind(R.id.stateView)
    TFStateView mStateView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private String provId = null;
    private String cityId = null;
    private String districtId = null;
    private int type; //修改和添加
    private AddressItem module;
    private TFProgressDialog progressDialog;

    public static void open(Context context, AddressItem addAddressResponse, int type) {
        Intent intent = new Intent(context, AddressAddActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("address", addAddressResponse);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.type = getIntent().getIntExtra("type", AddAddressFinishEvent.TYPE_ADD);
        this.module = (AddressItem) getIntent().getSerializableExtra("address");

        progressDialog = new TFProgressDialog(this);

        switch (type) {
            case AddAddressFinishEvent.TYPE_ADD:
                getSupportActionBar().setTitle("添加收货地址");
                break;

            case AddAddressFinishEvent.TYPE_CHANGE:
                getSupportActionBar().setTitle("修改收货地址");
                setupView();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_edit_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_complete:
                reqSaveAdd();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupView() {
        mReceiverName.setText(module.getContacts());
        mReceiverPhone.setText(module.getContactsPhone());
        StringBuffer sb = new StringBuffer();
        sb.append(DistrictModel.query(module.getProv()).locationName).append(" ");
        sb.append(DistrictModel.query(module.getCity()).locationName).append(" ");
        sb.append(DistrictModel.query(module.getArea()).locationName);
        mRegion.setText(sb.toString().trim());
        mAddDetail.setText(module.getAddress());
        provId = module.getProv();
        cityId = module.getCity();
        districtId = module.getArea();

    }

    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.add_region_rl:
                Intent intent = new Intent(this, SelectRegionActivity.class);
                startActivityForResult(intent, SELECT_REGION_REQUEST_CODE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SELECT_REGION_REQUEST_CODE:
                String str = data.getStringExtra("data");
                mRegion.setText(str.trim());
                sortRegion(str);
                break;
        }
    }

    public void reqSaveAdd() {
        Utils.hideSoftInput(this);
        HashMap<String, String> params = new HashMap<>();
        if (module != null && !TextUtils.isEmpty(module.getId())) {
            params.put("id", module.getId());
        } else {
            params.put("id", "");
        }

        // 收货人姓名
        if (TextUtils.isEmpty(mReceiverName.getText().toString())) {
            Toast.makeText(this, getString(R.string.please_input_receiver_name), Toast.LENGTH_SHORT).show();
            return;
        } else {
            params.put("contacts", Uri.encode(mReceiverName.getText().toString()));
        }

        // 收货人手机号码
        if (TextUtils.isEmpty(mReceiverPhone.getText().toString())) {
            Toast.makeText(this, getString(R.string.please_input_receiver_phone), Toast.LENGTH_SHORT).show();
            return;
        } else if (!CheckedUtil.isMobileNum(mReceiverPhone.getText().toString())) {
            Toast.makeText(this, getString(R.string.err_user_phoneNo_format), Toast.LENGTH_SHORT).show();
            return;
        } else {
            params.put("contactsPhone", mReceiverPhone.getText().toString());
        }

        // 收货人详细地址
        if (TextUtils.isEmpty(mAddDetail.getText().toString())) {
            Toast.makeText(this, getString(R.string.please_input_receiver_add_detail), Toast.LENGTH_SHORT).show();
            return;
        } else {
            params.put("address", Uri.encode(mAddDetail.getText().toString()));
        }

        // 行政区域
        if (TextUtils.isEmpty(provId)) {
            Toast.makeText(this, getString(R.string.please_input_receiver_region), Toast.LENGTH_SHORT).show();
            return;
        } else {
            params.put("prov", provId);
            if (!TextUtils.isEmpty(cityId)) params.put("city", cityId);
            if (!TextUtils.isEmpty(districtId)) params.put("area", districtId);
        }
        progressDialog.show();


        Subscription s = apiService.changeAdd(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<AddAddressResponse>() {
                    @Override
                    public void call(AddAddressResponse response) {
                        progressDialog.dismiss();
                        if (response.success()) {
                            AddressItem addressModule = new AddressItem();
                            addressModule.setAddress(mAddDetail.getText().toString());
                            addressModule.setContacts(mReceiverName.getText().toString());
                            addressModule.setContactsPhone(mReceiverPhone.getText().toString());
                            addressModule.setArea(districtId);
                            addressModule.setCity(cityId);
                            addressModule.setProv(provId);
                            addressModule.setId(String.valueOf(response.getId()));
                            finish();
                            AddAddressFinishEvent event = new AddAddressFinishEvent(addressModule, type);
                            EventBus.getDefault().post(event);
                        }
                    }
                });
        addSubscription(s);
    }

    /**
     * 分类地区id
     *
     * @param regionStr
     */
    private void sortRegion(String regionStr) {
        regionStr = regionStr.trim();
        String[] regionArray = regionStr.split(" ");

        if (regionArray.length == 3) {
            cityId = DistrictModel.queryByName(regionArray[1]).locationId;
            districtId = DistrictModel.queryByName(regionArray[2]).locationId;
        } else if (regionArray.length == 2) {
            cityId = DistrictModel.queryByName(regionArray[1]).locationId;
            districtId = "";
        } else if (regionArray.length == 1) {
            cityId = "";
            districtId = "";
        }
        provId = DistrictModel.queryByName(regionArray[0]).locationId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmsAgent.onResume(this, UmsConstants.MODULE_PERSONAL_CENTER + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmsAgent.onPause(this);
    }
}