package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.objs.BookObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookAdapter extends BaseRecyclerAdapter<MineBookObj> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public MineBookAdapter(Context mContext, List<MineBookObj> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_minebook, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        MineBookObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getCoverImage(), holder.ivBook);
        holder.tvTitle.setText(obj.getTitle());
//        holder.tvPagecount.setText(obj.getPageCount());
        holder.tvCreattime.setText(DateUtil.getYear2(obj.getCreateTime()));

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
        View.OnClickListener onClickListener = null;
        MineBookObj obj;
        private EditText tvCount;
        private TextView tvSize16;
        private TextView tvSize32;
        private TextView tvColor;
        private TextView tvBw;
        private TextView tvPaper1;
        private TextView tvPaper2;
        private TextView tvBind1;
        private TextView tvBind2;
        private TextView tvBind3;
        private BookObj bookObj;

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

                    View view = View.inflate(context, R.layout.view_popwindow, null);
                    TextView tvShare = (TextView) view.findViewById(R.id.tv_share);
                    TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
                    tvShare.setOnClickListener(this);
                    tvDelete.setOnClickListener(this);
                    PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    popupWindow.showAtLocation(itemView, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAsDropDown(llMenu);

                    break;
                case R.id.tv_edit:
                    PopupWindow pw = new PopupWindow(initView(obj), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    pw.setBackgroundDrawable(new BitmapDrawable());
                    pw.setOutsideTouchable(true);
                    pw.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.tv_print:
//                    AlertDialog dialog = new AlertDialog.Builder(context).setView(initView(obj)).create();
//                    Window window = dialog.getWindow();
//                    WindowManager m = window.getWindowManager();
//                    Display d = m.getDefaultDisplay();
//                    WindowManager.LayoutParams p = window.getAttributes();
//                    p.width = d.getWidth();
//                    window.setAttributes(p);
//                    window.setGravity(Gravity.BOTTOM);
//                    window.setWindowAnimations(R.style.bottom_dialog_animation);
//                    dialog.show();
                    PrintCartItem printCartItem = new PrintCartItem();
                    printCartItem.setAuthorName(obj.getAuthor());
                    printCartItem.setCoverImage(obj.getCoverImage());
                    printCartItem.setTitle(obj.getTitle());
                    printCartItem.setBookId(obj.getBookId());
                    printCartItem.setBookType(obj.getType());
                    printCartItem.setTotalPage(obj.getPageCount());
                    printCartItem.setDate(DateUtil.formatDate("yyyy-MM-dd", obj.getCreateTime()));


                    CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(printCartItem,
                            null,
                            null,
                            printCartItem.getBookId(),
                            String.valueOf(printCartItem.getBookType()),
                            CartPrintPropertyDialog.REQUEST_CODE_MINETIME,
                            printCartItem.getPrintCode(),
                            printCartItem.getCoverImage(),
                            TypeConstant.FROM_PHONE);
//                    dialog.show(getSupportFragmentManager(), "dialog");


                    break;
                case R.id.tv_share:

                    break;
                case R.id.tv_delete:

                    break;


                case R.id.iv_close:
                    break;
                case R.id.book_print_number_minus_ib:
                    Integer count = Integer.valueOf(tvCount.getText().toString());
                    if (count > 1) {
                        count--;
                        tvCount.setText(count + "");
                        bookObj.setNum(count);
                    }
                    break;
                case R.id.book_print_number_plus_ib:
                    Integer c = Integer.valueOf(tvCount.getText().toString());
                    if (c < 99) {
                        c++;
                        tvCount.setText(c + "");
                        bookObj.setNum(c);
                    }
                    break;
                case R.id.tv_size16:
                    tvSize16.setSelected(true);
                    tvSize32.setSelected(false);
                    bookObj.setSize("16开");
                    break;
                case R.id.tv_size32:
                    tvSize16.setSelected(false);
                    tvSize32.setSelected(true);
                    bookObj.setSize("32开");
                    break;
                case R.id.tv_color:
                    tvColor.setSelected(true);
                    tvBw.setSelected(false);
                    bookObj.setColor("彩色");
                    break;
                case R.id.tv_bw:
                    tvColor.setSelected(false);
                    tvBw.setSelected(true);
                    bookObj.setColor("黑白");
                    break;
                case R.id.tv_paper1:
                    tvPaper1.setSelected(true);
                    tvPaper2.setSelected(false);
                    bookObj.setPaper("特种纸");
                    break;
                case R.id.tv_paper2:
                    tvPaper1.setSelected(false);
                    tvPaper2.setSelected(true);
                    bookObj.setPaper("铜版纸");
                    break;
                case R.id.tv_bind1:
                    tvBind1.setSelected(true);
                    tvBind2.setSelected(false);
                    tvBind3.setSelected(false);
                    bookObj.setPack("平装");
                    break;
                case R.id.tv_bind2:
                    tvBind1.setSelected(false);
                    tvBind2.setSelected(true);
                    tvBind3.setSelected(false);
                    bookObj.setPack("法式精装");
                    break;
                case R.id.tv_bind3:
                    tvBind1.setSelected(false);
                    tvBind2.setSelected(false);
                    tvBind3.setSelected(true);
                    bookObj.setPack("豪华精装");
                    break;
                case R.id.btn_add_to_cart:

                    break;
                case R.id.btn_buy_now:
                    FragmentBridgeActivity.openEnsureOrderFragment(context, bookObj, obj);
                    break;


            }
        }

        public View initView(MineBookObj obj) {
            View view = View.inflate(context, R.layout.view_apply_print, null);
            RatioImageView ivBook = (RatioImageView) view.findViewById(R.id.iv_book_cover);
            TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
            ImageButton ivDel = (ImageButton) view.findViewById(R.id.book_print_number_minus_ib);
            tvCount = (EditText) view.findViewById(R.id.et_count);
            ImageButton ivAdd = (ImageButton) view.findViewById(R.id.book_print_number_plus_ib);
            ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
            tvSize16 = (TextView) view.findViewById(R.id.tv_size16);
            tvSize32 = (TextView) view.findViewById(R.id.tv_size32);
            tvColor = (TextView) view.findViewById(R.id.tv_color);
            tvBw = (TextView) view.findViewById(R.id.tv_bw);
            tvPaper1 = (TextView) view.findViewById(R.id.tv_paper1);
            tvPaper2 = (TextView) view.findViewById(R.id.tv_paper2);
            tvBind1 = (TextView) view.findViewById(R.id.tv_bind1);
            tvBind2 = (TextView) view.findViewById(R.id.tv_bind2);
            tvBind3 = (TextView) view.findViewById(R.id.tv_bind3);
            Button tvIncar = (Button) view.findViewById(R.id.btn_add_to_cart);
            Button tvBuy = (Button) view.findViewById(R.id.btn_buy_now);
            TextView tvNotify = (TextView) view.findViewById(R.id.tv_pack_label);

            tvCount.clearFocus();
            GlideUtil.displayImage(obj.getCoverImage(), ivBook);
            tvSize16.setSelected(true);
            tvColor.setSelected(true);
            tvPaper1.setSelected(true);
            tvBind1.setSelected(true);
            if (obj.getPageCount() < 90) {
                tvNotify.setVisibility(View.VISIBLE);
                tvBind2.setEnabled(false);
                tvBind3.setEnabled(false);
            }
            bookObj = new BookObj();
            bookObj.setNum(1);
            bookObj.setSize("16开");
            bookObj.setColor("彩色");
            bookObj.setPaper("特种纸");
            bookObj.setPack("平装");


            ivClose.setOnClickListener(this);
            ivDel.setOnClickListener(this);
            ivAdd.setOnClickListener(this);
            tvSize16.setOnClickListener(this);
            tvSize32.setOnClickListener(this);
            tvColor.setOnClickListener(this);
            tvBw.setOnClickListener(this);
            tvPaper1.setOnClickListener(this);
            tvPaper2.setOnClickListener(this);
            tvBind1.setOnClickListener(this);
            tvBind2.setOnClickListener(this);
            tvBind3.setOnClickListener(this);
            tvIncar.setOnClickListener(this);
            tvBuy.setOnClickListener(this);

            return view;
        }
    }
}
