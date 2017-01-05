package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.api.models.AddressItem;
import cn.timeface.circle.baby.api.models.DistrictModel;

public class SelectAddressAdapter extends BaseListAdapter<AddressItem> {
    private String addressId;

    public SelectAddressAdapter(Context context, List<AddressItem> listData) {
        super(context, listData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_select_address, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AddressItem item = listData.get(position);

        viewHolder.mReceiverName.setText(item.getContacts());
        viewHolder.mReceiverPhone.setText(item.getContactsPhone());
        StringBuffer sb = new StringBuffer();
        sb.append(DistrictModel.query(item.getProv()) == null ? "" : DistrictModel.query(item.getProv()).getLocationName());
        sb.append(DistrictModel.query(item.getCity()) == null ? "" : DistrictModel.query(item.getCity()).getLocationName());
        sb.append(DistrictModel.query(item.getArea()) == null ? "" : DistrictModel.query(item.getArea()).getLocationName());
        sb.append(item.getAddress());
        viewHolder.mReceiverAddress.setText(sb.toString());

        if (item.getId().equals(addressId)) {
            convertView.setBackgroundResource(R.color.main_blue);
            viewHolder.mSelectedIv.setVisibility(View.VISIBLE);
            viewHolder.mReceiverAddress.setTextColor(0xffffffff);
            viewHolder.mReceiverName.setTextColor(0xffffffff);
            viewHolder.mReceiverPhone.setTextColor(0xffffffff);
        }

        return convertView;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    static class ViewHolder {
        @Bind(R.id.receiver_name_tv)
        TextView mReceiverName;
        @Bind(R.id.receiver_phone_tv)
        TextView mReceiverPhone;
        @Bind(R.id.receiver_address_tv)
        TextView mReceiverAddress;
        @Bind(R.id.ic_selected_iv)
        ImageView mSelectedIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
