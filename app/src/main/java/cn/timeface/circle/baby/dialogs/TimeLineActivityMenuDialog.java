package cn.timeface.circle.baby.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
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
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineEditActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ShareSdkUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
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

    private int allDetailsListPosition;

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


    public void share(TimeLineObj timelineobj) {
        this.timelineobj = timelineobj;
        if (timelineobj.getType() == 1) {
            tvDownload.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(timelineobj.getGrowthCricleName()) && timelineobj.getAuthor().getUserId().equals(FastData.getUserId())) {
            tvEdit.setVisibility(View.VISIBLE);
            tvDlete.setVisibility(View.VISIBLE);
        } else {
            tvEdit.setVisibility(View.GONE);
            tvDlete.setVisibility(View.GONE);
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

    public TimeLineActivityMenuDialog editeor(boolean isEditor) {
        if (tvEdit != null)
            tvEdit.setVisibility(isEditor ? View.VISIBLE : View.GONE);
        return this;
    }

    private void initListener() {
        tvEdit.setOnClickListener(v -> {
            dismiss();
            context.startActivity(new Intent(context, TimeLineEditActivity.class).putExtra("timelimeobj", timelineobj).putExtra("allDetailsListPosition", allDetailsListPosition));
        });
        tvDlete.setOnClickListener(v -> {
            dismiss();
            new AlertDialog.Builder(context)
                    .setTitle(R.string.do_you_delet)
                    .setNegativeButton(R.string.dialog_cancle, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton(R.string.dialog_submit, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ApiService apiService = ApiFactory.getApi().getApiService();
                    apiService.delTime(timelineobj.getTimeId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    EventBus.getDefault().post(new DeleteTimeLineEvent(timelineobj.getTimeId()));
                                    EventBus.getDefault().post(new HomeRefreshEvent(timelineobj.getTimeId()));
                                } else {
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
            String path = timelineobj.getMediaList().get(0).getVideoUrl();
            String fileName = path.substring(path.lastIndexOf("/"));
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(absolutePath + "/baby");
            if (!file.exists()) {
                file.mkdirs();
            }
            File file1 = new File(file, fileName);
            if (file1.exists()) {
                ToastUtil.showToast("已保存到baby文件夹下");
                return;
            }
            if (!Utils.isNetworkConnected(context)) {
                ToastUtil.showToast("网络异常");
                return;
            }
            ToastUtil.showToast("保存视频…");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ImageFactory.saveVideo(path, file1);
                    return;
                }
            }).start();
        });
        tvShare.setOnClickListener(v -> {
            dismiss();
            String imgUrl = "";
            String title = FastData.getBabyNickName() + "长大了";
            String content = FastData.getBabyNickName() + FastData.getBabyAge() + "了" + ",快来看看" + FastData.getBabyNickName() + "的新变化";
            String url = context.getString(R.string.share_url_time, timelineobj.getTimeId());
            switch (timelineobj.getType()) {
                case TypeConstants.PHOTO:
                    url = BuildConfig.API_URL + context.getString(R.string.share_url_time, timelineobj.getTimeId());
                    break;
                case TypeConstants.VIDEO:
                    url = BuildConfig.API_URL + context.getString(R.string.share_url_video, timelineobj.getTimeId());
                    break;
                case TypeConstants.DIARY:
                    url = BuildConfig.API_URL + context.getString(R.string.share_url_diary, timelineobj.getTimeId());
                    break;
                case TypeConstants.CARD:
                    url = BuildConfig.API_URL + context.getString(R.string.share_url_generalmap, timelineobj.getTimeId());
                    break;

            }
//            if (timelineobj.getMediaList().size() > 0 && !TextUtils.isEmpty(timelineobj.getMediaList().get(0).getImgUrl())) {
//                imgUrl = timelineobj.getMediaList().get(0).getImgUrl();
//            } else {
//                imgUrl = FastData.getBabyAvatar();
//            }
            new ShareDialog(context).share(title, content, ShareSdkUtil.getImgStrByResource(getContext(), R.drawable.ic_laucher_quadrate), url);
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public int getAllDetailsListPosition() {
        return allDetailsListPosition;
    }

    public TimeLineActivityMenuDialog setAllDetailsListPosition(int allDetailsListPosition) {
        this.allDetailsListPosition = allDetailsListPosition;
        return this;
    }
}
