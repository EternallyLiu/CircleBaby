package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectRegionActivity;
import cn.timeface.circle.baby.api.models.DistrictModel;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class AddAddressFragment extends BaseFragment implements View.OnClickListener {
    public static final int SELECT_REGION_REQUEST_CODE = 101;


    @Bind(R.id.tv_save)
    TextView tvSave;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.rl_address)
    RelativeLayout rlAddress;
    @Bind(R.id.et_adress)
    EditText etAdress;
    private String cityId;
    private String districtId;
    private String provId;

    public AddAddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addaddress, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvSave.setOnClickListener(this);
        rlAddress.setOnClickListener(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_address:
                Intent intent = new Intent(getActivity(), SelectRegionActivity.class);
                startActivityForResult(intent, SELECT_REGION_REQUEST_CODE);
                break;
            case R.id.tv_save:
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String detailaddress = etAdress.getText().toString();
                String address = tvAddress.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    ToastUtil.showToast("请填写收件人姓名");
                    return;
                }
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    ToastUtil.showToast("填写正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(detailaddress)) {
                    ToastUtil.showToast("请填写详细地址");
                    return;
                }
                if (address.length() <= 3) {
                    ToastUtil.showToast("请选择地址");
                    return;
                }

                apiService.addAddress(URLEncoder.encode(address + detailaddress), districtId, cityId, URLEncoder.encode(name), URLEncoder.encode(phone), "", provId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(addAddressResponse -> {
                            if (addAddressResponse.success()) {
                                getActivity().finish();
                            } else {
                                ToastUtil.showToast(addAddressResponse.getInfo());
                            }
                        }, error -> {
                            Log.e(TAG, "timeline:");
                        });


                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SELECT_REGION_REQUEST_CODE:
                String str = data.getStringExtra("data");
                tvAddress.setText(processRegionTxt(str.trim()));
                if (!TextUtils.isEmpty(str)) {
                    sortRegion(str);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 分类地区id
     */

    private void sortRegion(String regionStr) {
        regionStr = regionStr.trim();
        String[] regionArray = regionStr.split(" ");

        if (regionArray.length == 3) {
            cityId = DistrictModel.queryByName(regionArray[1]).getLocationId();
            districtId = DistrictModel.queryByName(regionArray[2]).getLocationId();
        } else if (regionArray.length == 2) {
            cityId = DistrictModel.queryByName(regionArray[1]).getLocationId();
            districtId = "";
        } else if (regionArray.length == 1) {
            cityId = "";
            districtId = "";
        }
        provId = DistrictModel.queryByName(regionArray[0]).getLocationId();
    }

    /**
     * 处理返回地区字段
     */
    private String processRegionTxt(String regionTxt) {
        if (regionTxt.startsWith("北京")
                || regionTxt.startsWith("上海")
                || regionTxt.startsWith("天津")
                || regionTxt.startsWith("重庆")
                || regionTxt.startsWith("香港")
                || regionTxt.startsWith("澳门")) {
            int index = regionTxt.indexOf(" ");
            regionTxt = regionTxt.substring(index);
        }

        return regionTxt;
    }

}
