package cn.timeface.circle.baby.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.events.MilestoneRefreshEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * Created by lidonglin on 2016/6/25.
 */
public class MilestoneMenuDialog extends BaseDialog implements View.OnClickListener {
    private Context context;
    private MilestoneTimeObj obj;
    private RelativeLayout rlEdit;
    private RelativeLayout rlDelete;
    private TextView tvDownload;
    private RelativeLayout rlShare;
    private RelativeLayout rlCancel;
    private AlertDialog alertDialog;

    public MilestoneMenuDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        this.context = context;
        init();
    }

    public MilestoneMenuDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    public void share(MilestoneTimeObj obj){
        this.obj = obj;
        this.show();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_timedetail_menu, null);
        this.setContentView(mView);
        rlEdit = ButterKnife.findById(mView, R.id.rl_add_content);
        rlDelete = ButterKnife.findById(mView, R.id.rl_delete);
        tvDownload = ButterKnife.findById(mView, R.id.tv_download);
        rlShare = ButterKnife.findById(mView, R.id.rl_share);
        rlCancel = ButterKnife.findById(mView, R.id.rl_cancel);

        initListener();

        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    private void initListener() {
        rlEdit.setVisibility(View.GONE);
        tvDownload.setVisibility(View.GONE);
        rlShare.setVisibility(View.GONE);
        rlDelete.setVisibility(View.VISIBLE);
        rlCancel.setVisibility(View.VISIBLE);

        rlDelete.setOnClickListener(v -> {
            dismiss();
//            alertDialog = new AlertDialog.Builder(getContext()).setView(initDialogView()).show();
//            alertDialog.setCanceledOnTouchOutside(false);
            new AlertDialog.Builder(getContext()).setMessage("确定删除里程碑 "+obj.getMilestone()+" 吗？")
                    .setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApiFactory.getApi().getApiService().delMilestone(obj.getMilestoneId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    EventBus.getDefault().post(new MilestoneRefreshEvent());
                                }else{
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> {
                                Log.e("MilestoneMenuDialog", "delMilestone:");
                                error.printStackTrace();
                            });
                }
            }).show();
        });
        rlCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private View initDialogView() {
        View view = View.inflate(getContext(),R.layout.view_dialog,null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        tvMsg.setText("你确定删除里程碑 "+obj.getMilestone()+" 吗？");
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                alertDialog.dismiss();

                ApiFactory.getApi().getApiService().delMilestone(obj.getMilestoneId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                EventBus.getDefault().post(new MilestoneRefreshEvent());
                            }else{
                                ToastUtil.showToast(response.getInfo());
                            }
                        }, error -> {
                            Log.e("MilestoneMenuDialog", "delMilestone:");
                            error.printStackTrace();
                        });
                break;
            case R.id.btn_cancel:
                alertDialog.dismiss();
                break;
        }
    }
}
