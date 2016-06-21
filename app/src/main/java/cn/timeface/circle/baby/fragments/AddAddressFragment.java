package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.api.models.objs.BookObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.ToastUtil;

public class AddAddressFragment extends BaseFragment implements View.OnClickListener {


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
    private BookObj bookObj;
    private MineBookObj mineBookObj;

    public AddAddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookObj = getArguments().getParcelable("BookObj");
        mineBookObj = getArguments().getParcelable("MineBookObj");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addaddress, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
                if (TextUtils.isEmpty(phone) || phone.length() < 11) {
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

//                apiService.addAddress(URLEncoder.encode(detailaddress),)


                break;
        }
    }
}
