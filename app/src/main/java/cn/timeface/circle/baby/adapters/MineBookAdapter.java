package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioFrameLayout;
import com.wechat.photopicker.PickerPhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeBookPickerPhotoActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.managers.listeners.OnClickListener;
import cn.timeface.circle.baby.managers.listeners.OnItemClickListener;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.LittleWindow;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookAdapter extends BaseRecyclerAdapter<MineBookObj> {

    private View.OnClickListener onClickListener;
    private FragmentManager supportFragmentManager;
    private static OnClickListener clickListener;
    private OnItemClickListener<MineBookObj> onItemClickListener;

    public MineBookAdapter(Context mContext, List<MineBookObj> listData) {
        super(mContext, listData);

    }

    public MineBookAdapter(Context mContext, ArrayList<MineBookObj> listData, FragmentManager supportFragmentManager) {
        super(mContext, listData);
        this.supportFragmentManager = supportFragmentManager;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_minebook, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        MineBookObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.supportFragmentManager = supportFragmentManager;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getBookCover(), holder.ivBook);
        holder.tvTitle.setText(obj.getBookName());
        holder.tvPageNum.setText(obj.getPageNum()+"");
        if(obj.getBookType()==TypeConstant.BOOK_TYPE_DIARY){
            holder.llType.setVisibility(View.VISIBLE);
            holder.tvType.setText(obj.getCoverTitle());
        }
        holder.tvCreattime.setText(DateUtil.getYear2(obj.getUpdateTime()));
        if (obj.getBookType() != 2) {
            holder.ivBookbg.setImageResource(R.drawable.book_front_mask2);
        }else{
            holder.ivBookbg.setImageResource(R.drawable.book_front_mask);
        }
        holder.flBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.clickItem(obj);
                }
            }
        });

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
        @Bind(R.id.iv_bookbg)
        ImageView ivBookbg;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_pagenum)
        TextView tvPageNum;
        @Bind(R.id.tv_creattime)
        TextView tvCreattime;
        @Bind(R.id.tv_edit)
        TextView tvEdit;
        @Bind(R.id.tv_print)
        TextView tvPrint;
        @Bind(R.id.ll_menu)
        LinearLayout llMenu;
        @Bind(R.id.ll_type)
        LinearLayout llType;
        @Bind(R.id.tv_type)
        TextView tvType;
        @Bind(R.id.fl_book_cover)
        RatioFrameLayout flBookCover;


        Context context;
        FragmentManager supportFragmentManager;
        View.OnClickListener onClickListener = null;
        MineBookObj obj;
        private LittleWindow littleWindow;
        private String notify = "";

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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
//                            case R.id.share:
//                                //分享
//                                String baseUrl = "http://h5.stg1.v5time.net/hobbyDetail?";
//                                String url = baseUrl + "userId=" + FastData.getUserId() + "&deviceId=" + new DeviceUuidFactory(
//                                        TimeFaceUtilInit.getContext()).getDeviceId();
//                                new ShareDialog(context).share("宝宝时光，让家庭充满和谐，让教育充满温馨。", "宝宝时光，让家庭充满和谐，让教育充满温馨。",
//                                        ShareSdkUtil.getImgStrByResource(context, R.mipmap.ic_launcher),
//                                        url);
//                                break;
                            case R.id.del:
                                //删除
                                if (clickListener != null) {
                                    clickListener.click(obj);
                                }
                                break;
                        }
                    });
                    littleWindow.show(v);
                    break;
                case R.id.tv_edit:
                    BaseAppCompatActivity.apiService.queryImageInfoList(obj.getBookId(), obj.getBookType())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    List<ImageInfoListObj> dataList = response.getDataList();
                                    if(obj.getBookType()==5){
                                        //编辑照片书
                                        startTimeBookPhotoPick(dataList);
                                    }else{
                                        //编辑日记书和识图卡片书
                                        startPhotoPick(dataList);
                                    }

                                }
                            }, error -> {
                                Log.e("MineBookAdapter", "queryImageInfoList:");
                            });

                    break;
                case R.id.tv_print:
                    BaseAppCompatActivity.apiService.printStatus(obj.getBookType(), obj.getPageNum(), obj.getBookSizeId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(printStatusResponse -> {
                                int printCode = printStatusResponse.getPrintCode();
                                switch (printCode) {
                                    case TypeConstant.PRINT_CODE_LIMIT_LESS:
                                        notify = "少于20页，不可印刷";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_MORE:
                                        notify = "大于60页，不可印刷";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE:
                                        notify = "该时光书已被删除，不可印刷";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_8806:
                                        notify = "3寸日记卡片需18张";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_8807:
                                        notify = "4寸日记卡片需15张";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_8808:
                                        notify = "5寸日记卡片需9张";
                                        break;
                                    case TypeConstant.PRINT_CODE_LIMIT_8809:
                                        notify = "识图卡片需8张";
                                        break;
                                }
                                if (!TextUtils.isEmpty(notify)) {
                                    ToastUtil.showToast(notify);
                                    notify = "";
                                    return;
                                }
                                obj.setPrintCode(printCode);
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
                        if (paramListResponse.success()) {
                            List<PrintParamObj> valueList1 = new ArrayList<PrintParamObj>();
                            List<PrintParamResponse> dataList = paramListResponse.getDataList();
                            for (PrintParamResponse printParamResponse : dataList){
                                if(printParamResponse.getKey().equals("size")){
                                    List<PrintParamObj> valueList = printParamResponse.getValueList();
                                    for(PrintParamObj printParamObj : valueList){
                                        if(printParamObj.getValue().equals(obj.getBookSizeId()+"")){
                                            valueList1.add(printParamObj);
                                        }
                                    }
                                    if(valueList1.size()>0){
                                        printParamResponse.setValueList(valueList1);
                                    }
                                }
                            }



                            CartPrintPropertyDialog dialog = CartPrintPropertyDialog.getInstance(null,
                                    null,
                                    dataList,
                                    obj.getBookId(),
                                    String.valueOf(obj.getBookType()),
                                    CartPrintPropertyDialog.REQUEST_CODE_MINETIME,
                                    obj.getPrintCode(),
                                    obj.getBookCover(),
                                    TypeConstant.FROM_PHONE,
                                    obj.getPageNum(),
                                    obj.getBookName(),
                                    String.valueOf(obj.getUpdateTime()),context);
                            dialog.show(supportFragmentManager, "dialog");
                        }
                    }, error -> {
                        Log.e("MineBookAdapter", "queryParamList:");
                        error.printStackTrace();
                    });
        }

        private void startPhotoPick(List<ImageInfoListObj> dataList) {
            Intent intent = new Intent(context, PickerPhotoActivity2.class);
            intent.putExtra("bookType", obj.getBookType());
            intent.putExtra("bookId",obj.getBookId());
            intent.putExtra("openBookId",obj.getOpenBookId());
            intent.putExtra("openBookType",obj.getOpenBookType());
            intent.putExtra("coverTitle",obj.getCoverTitle());
            intent.putExtra("bookPage",obj.getBookPage());
            intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
            context.startActivity(intent);
        }

        private void startTimeBookPhotoPick(List<ImageInfoListObj> dataList) {
            Intent intent = new Intent(context, TimeBookPickerPhotoActivity.class);
            intent.putExtra("bookType", obj.getBookType());
            intent.putExtra("bookId",obj.getBookId());
            intent.putExtra("openBookId",obj.getOpenBookId());
            intent.putExtra("openBookType",obj.getOpenBookType());
            intent.putExtra("coverTitle",obj.getCoverTitle());
            intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
            context.startActivity(intent);
        }

    }

    public void delBook(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<MineBookObj> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
