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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineEditActivity;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ImageFactory;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.Utils;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * Created by lidonglin on 2016/6/25.
 */
public class TimeLineActivityMenuDialog extends BaseDialog {
    private Context context;
    private TimeLineObj timelineobj;
    private RelativeLayout tvEdit;
    private RelativeLayout tvDlete;
    private TextView tvDownload;
    private RelativeLayout tvShare;
    private RelativeLayout tvCancel;

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
        if(timelineobj.getAuthor().getUserId().equals(FastData.getUserId())){
            tvEdit.setVisibility(View.VISIBLE);
            tvDlete.setVisibility(View.VISIBLE);
        }
        this.show();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_timedetail_menu, null);
        this.setContentView(mView);
        tvEdit = ButterKnife.findById(mView, R.id.rl_add_content);
        tvDlete = ButterKnife.findById(mView, R.id.rl_delete);
        tvDownload = ButterKnife.findById(mView, R.id.tv_download);
        tvShare = ButterKnife.findById(mView, R.id.rl_share);
        tvCancel = ButterKnife.findById(mView, R.id.rl_cancel);

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
                                    EventBus.getDefault().post(new HomeRefreshEvent());
                                }else{
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> {
                                Log.e("TimeLineMenuDialog", "delTime:");
                                error.printStackTrace();
                            });
                }
            }).show();
        });
        tvDownload.setOnClickListener(v -> {
            dismiss();
//            ImageFactory.saveVideo(timelineobj.getMediaList().get(0).getVideoUrl());
//            ToastUtil.showToast("下载视频到baby文件夹下…");
            String path = timelineobj.getMediaList().get(0).getVideoUrl();
            String fileName = path.substring(path.lastIndexOf("/"));
            File file = new File("/mnt/sdcard/baby");
            if(!file.exists()){
                file.mkdirs();
            }
            File file1 = new File(file, fileName);
            if(file1.exists()){
                ToastUtil.showToast("已保存到baby文件夹下");
                return;
            }
            if(!Utils.isNetworkConnected(context)){
                ToastUtil.showToast("网络异常");
                return;
            }
            ToastUtil.showToast("保存视频…");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ImageFactory.saveVideo(path,file1);
                    return;
                }
            }).start();
        });
        tvShare.setOnClickListener(v -> {
            dismiss();
            String imgUrl = "";
            String title = FastData.getBabyName() + "长大了";
            String content = FastData.getBabyName() + FastData.getBabyAge() + "了" + ",快来看看" + FastData.getBabyName() + "的新变化";
            String url = context.getString(R.string.share_url_time,timelineobj.getTimeId());
            switch (timelineobj.getType()){
                case TypeConstants.PHOTO:
                    url = context.getString(R.string.share_url_time,timelineobj.getTimeId());
                    break;
                case TypeConstants.VIDEO:
                    url = context.getString(R.string.share_url_time,timelineobj.getTimeId());
                    break;
                case TypeConstants.DIARY:
                    url = context.getString(R.string.share_url_diary,timelineobj.getTimeId());
                    break;
                case TypeConstants.CARD:
                    url = context.getString(R.string.share_url_generalmap,timelineobj.getTimeId());
                    break;

            }
            if(timelineobj.getMediaList().size()>0 && !TextUtils.isEmpty(timelineobj.getMediaList().get(0).getImgUrl())){
                imgUrl = timelineobj.getMediaList().get(0).getImgUrl();
            }else{
                imgUrl = FastData.getBabyAvatar();
            }
            new ShareDialog(context).share(title, content, imgUrl,url);
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

}
