package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.AddressItem;
import cn.timeface.circle.baby.api.models.objs.AddressObj;
import cn.timeface.circle.baby.api.models.objs.BookObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.events.AddressEvent;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class AddressListAdapter extends BaseRecyclerAdapter<AddressItem> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;
    private Activity activity;

    public AddressListAdapter(Context mContext, List<AddressItem> listData) {
        super(mContext, listData);
        this.activity = (Activity) mContext;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_address, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        AddressItem obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        holder.activity = activity;
        holder.tvName.setText(obj.getContacts());
        holder.tvPhone.setText(obj.getContactsPhone());
        holder.tvAddress.setText(obj.getAddress());
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_phone)
        TextView tvPhone;
        @Bind(R.id.iv_select)
        ImageView ivSelect;

        Context context;
        Activity activity;
        View.OnClickListener onClickListener = null;
        AddressItem obj;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ivSelect.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new AddressEvent(obj));
            activity.finish();
        }

    }
}
