package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.LittleWindow;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookAdapter extends BaseRecyclerAdapter<MineBookObj> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;
    private FragmentManager supportFragmentManager;

    public MineBookAdapter(Context mContext, List<MineBookObj> listData) {
        super(mContext, listData);

    }

    public MineBookAdapter(Context mContext, ArrayList<MineBookObj> listData, FragmentManager supportFragmentManager) {
        super(mContext, listData);
        this.supportFragmentManager = supportFragmentManager;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_minebook, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        MineBookObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.supportFragmentManager = supportFragmentManager;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getBookCover(), holder.ivBook);
        holder.tvTitle.setText(obj.getBookName());
//        holder.tvPagecount.setText(obj.getPageCount());
        holder.tvCreattime.setText(DateUtil.getYear2(obj.getUpdateTime()));

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
        @Bind(R.id.iv_book)
        ImageView ivBook;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_pagecount)
        TextView tvPagecount;
        @Bind(R.id.tv_creattime)
        TextView tvCreattime;
        @Bind(R.id.tv_edit)
        TextView tvEdit;
        @Bind(R.id.tv_print)
        TextView tvPrint;
        @Bind(R.id.ll_menu)
        LinearLayout llMenu;

        Context context;
        FragmentManager supportFragmentManager;
        View.OnClickListener onClickListener = null;
        MineBookObj obj;
        private LittleWindow littleWindow;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
            llMenu.setOnClickListener(this);
            tvEdit.setOnClickListener(this);
            tvPrint.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_menu:
                    llMenu.getChildAt(0).setPressed(true);
                    llMenu.getChildAt(1).setPressed(true);
                    llMenu.getChildAt(2).setPressed(true);
                    if (littleWindow == null) {
                        littleWindow = new LittleWindow(context);
                    }
                    littleWindow.setContentData(obj);
                    littleWindow.setOnClickItemListener((id, bookObj) -> {
                        switch (id) {
                            case R.id.share:
                                //分享
                                break;
                            case R.id.del:
                                //删除
                                break;
                        }
                    });
                    littleWindow.show(v);
                    break;
                case R.id.tv_edit:
                    break;
                case R.id.tv_print:
                    BaseAppCompatActivity.apiService.printStatus(obj.getBookType(), obj.getPageNum(), obj.getBookSizeId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(printStatusResponse -> {
                                obj.setPrintCode(printStatusResponse.getPrintCode());
                                queryParamList();
                            }, error -> {
                                Log.e("MineBookAdapter", "printStatus:");
                            });
                    break;
            }
        }

        private void queryParamList() {
            BaseAppCompatActivity.apiService.queryParamList(obj.getBookType(), obj.getPageNum())
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(paramListResponse -> {
                        CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(null,
                                null,
                                paramListResponse.getDataList(),
                                obj.getBookId(),
                                String.valueOf(obj.getBookType()),
                                CartPrintPropertyDialog.REQUEST_CODE_MINETIME,
                                obj.getPrintCode(),
                                obj.getBookCover(),
                                TypeConstant.FROM_PHONE,
                                obj.getPageNum(),
                                obj.getBookName(),
                                String.valueOf(obj.getUpdateTime()));
                        dialog.show(supportFragmentManager, "dialog");

                    }, error -> {
                        Log.e("MineBookAdapter", "queryParamList:");
                    });
        }

    }
}
