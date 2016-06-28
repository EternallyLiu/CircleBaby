package cn.timeface.circle.baby.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.CustomerLogo;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.activities.TimeLineEditActivity;
import cn.timeface.circle.baby.api.Api;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.events.DeleteDynamicEvent;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ImageFactory;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.ShareSdkUtil;
import cn.timeface.common.utils.TimeFaceUtilInit;
import de.greenrobot.event.EventBus;

/**
 * Created by lidonglin on 2016/6/25.
 */
public class TimeLineActivityMenuDialog extends BaseDialog {
    private Context context;
    private TimeLineObj timelineobj;
    private TextView tvEdit;
    private TextView tvDlete;
    private TextView tvDownload;
    private TextView tvShare;
    private TextView tvCancel;

    public TimeLineActivityMenuDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        this.context = context;
        init();
    }

    public TimeLineActivityMenuDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    public void share(TimeLineObj timelineobj){
        this.timelineobj = timelineobj;
        if(timelineobj.getType()==1){
            tvDownload.setVisibility(View.VISIBLE);
        }
        this.show();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_timedetail_menu, null);
        this.setContentView(mView);
        tvEdit = ButterKnife.findById(mView, R.id.tv_edit);
        tvDlete = ButterKnife.findById(mView, R.id.tv_delete);
        tvDownload = ButterKnife.findById(mView, R.id.tv_download);
        tvShare = ButterKnife.findById(mView, R.id.tv_share);
        tvCancel = ButterKnife.findById(mView, R.id.tv_cancel);

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
        tvEdit.setOnClickListener(v -> {
            dismiss();
            context.startActivity(new Intent(context,TimeLineEditActivity.class).putExtra("timelimeobj",timelineobj));
        });
        tvDlete.setOnClickListener(v -> {
            dismiss();
            new AlertDialog.Builder(context)
                    .setTitle("确定删除这条记录吗?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApiService apiService = ApiFactory.getApi().getApiService();
                    apiService.delTime(timelineobj.getTimeId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    EventBus.getDefault().post(new DeleteTimeLineEvent());
                                }else{
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> {
                                Log.e("TimeLineMenuDialog", "delTime:");
                            });
                }
            }).show();
        });
        tvDownload.setOnClickListener(v -> {
            dismiss();
            ImageFactory.saveVideo(timelineobj.getMediaList().get(0).getVideoUrl());
        });
        tvShare.setOnClickListener(v -> {
            dismiss();
            String baseUrl = "http://h5.stg1.v5time.net/hobbyDetail?";
            String url = baseUrl + "userId=" + FastData.getUserId() + "&deviceId=" + new DeviceUuidFactory(
                    TimeFaceUtilInit.getContext()).getDeviceId() + "&recordId=" + timelineobj.getTimeId();
            new ShareDialog(context).share(false, timelineobj.getTimeId(), "宝宝时光，让家庭充满和谐，让教育充满温馨。", "宝宝时光，让家庭充满和谐，让教育充满温馨。",
                    ShareSdkUtil.getImgStrByResource(context, R.mipmap.ic_launcher),
                    ShareSdkUtil.getImgStrByResource(context, R.drawable.setting_sina_share_img),
                    url);
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

}
