package cn.timeface.circle.baby.views;

import android.content.Context;
import android.text.TextUtils;
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

import org.greenrobot.eventbus.EventBus;

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
import cn.timeface.circle.baby.events.DeleteDynamicEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import cn.timeface.common.utils.ShareSdkUtil;

/**
 * Created by yusen on 2015/2/4.
 */
public class ShareDialog extends BaseDialog {
    private Context context;
    private String title;
    private String content;
    private String imgUrl;
    private String imgSinaUrl;
    private String url;
    private CustomerLogo[] logos;
    private TableLayout shareTable;
    private View shareToSina;
    private View shareToWxCircle;
    private View shareToWx;
    private View shareToQQ;
    private View shareToQzone;
    private View divider;
    private TableRow cusRow;
    private View cancleBtn;
    private View deleteBtn;
    private View divide;
    private int id;
    public static final int PLANT_QQ = 1;
    public static final int PLANT_QZONG = 2;
    public static final int PLANT_WEIBO = 3;
    public static final int PLANT_WEIXIN = 4;
    public static final int PLANT_WEIXINFRIEND = 5;

    public ShareDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        this.context = context;
        init();
    }

    public ShareDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    public void share(String title, String content, String imgUrl, String url, CustomerLogo... logos) {
        share(title, content, imgUrl, imgUrl, url, logos);
        System.out.println("share_url ========== "+url);
    }

    public void share(String title, String content, String imgUrl, String imgSinaUrl, String url, CustomerLogo... logos) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.imgSinaUrl = TextUtils.isEmpty(imgSinaUrl) ? imgUrl : imgSinaUrl;
        this.url = url;
        this.logos = logos;
        if (logos == null || logos.length <= 0) {
            divider.setVisibility(View.GONE);
            cusRow.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            cusRow.setVisibility(View.VISIBLE);
            for (CustomerLogo logo : logos) {
                if (logo != null) {
                    cusRow.addView(getView(logo));
                }
            }
        }
        this.show();
    }

    public void share(boolean del,int id,String title, String content, String imgUrl, String imgSinaUrl, String url, CustomerLogo... logos) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.imgSinaUrl = TextUtils.isEmpty(imgSinaUrl) ? imgUrl : imgSinaUrl;
        this.url = url;
        this.logos = logos;
        if (logos == null || logos.length <= 0) {
            divider.setVisibility(View.GONE);
            cusRow.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            cusRow.setVisibility(View.VISIBLE);
            for (CustomerLogo logo : logos) {
                if (logo != null) {
                    cusRow.addView(getView(logo));
                }
            }
        }
        this.id = id;
        if(del)
        {
            divide.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
        this.show();
    }

    public void shareNoDialog(String title, String content, String imgUrl, String imgSinaUrl, String url, CustomerLogo... logos) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.imgSinaUrl = TextUtils.isEmpty(imgSinaUrl) ? imgUrl : imgSinaUrl;
        this.url = url;
        this.logos = logos;
        if (logos == null || logos.length <= 0) {
            divider.setVisibility(View.GONE);
            cusRow.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            cusRow.setVisibility(View.VISIBLE);
            for (CustomerLogo logo : logos) {
                if (logo != null) {
                    cusRow.addView(getView(logo));
                }
            }
        }
    }

    public void shareToPlant(int type) {
        switch (type) {
            case PLANT_QQ:
                shareToQQ(ShareSDK.getPlatform(QQ.NAME));
                break;
            case PLANT_QZONG:
                shareToQQ(ShareSDK.getPlatform(QZone.NAME));
                break;
            case PLANT_WEIBO:
                shareToSina();
                break;
            case PLANT_WEIXIN:
                shareToWx(ShareSDK.getPlatform(Wechat.NAME));
                break;
            case PLANT_WEIXINFRIEND:
                shareToWx(ShareSDK.getPlatform(WechatMoments.NAME));
                break;

        }
    }

    public void shareToQQ(String title, String content, String imgUrl, String imgSinaUrl, String url, CustomerLogo... logos) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.imgSinaUrl = TextUtils.isEmpty(imgSinaUrl) ? imgUrl : imgSinaUrl;
        this.url = url;
        this.logos = logos;
        if (logos == null || logos.length <= 0) {
            divider.setVisibility(View.GONE);
            cusRow.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            cusRow.setVisibility(View.VISIBLE);
            for (CustomerLogo logo : logos) {
                if (logo != null) {
                    cusRow.addView(getView(logo));
                }
            }
        }
        shareToQQ(ShareSDK.getPlatform(QQ.NAME));
    }

    public void shareToWx(String title, String content, String imgUrl, String imgSinaUrl, String url, CustomerLogo... logos) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.imgSinaUrl = TextUtils.isEmpty(imgSinaUrl) ? imgUrl : imgSinaUrl;
        this.url = url;
        this.logos = logos;
        if (logos == null || logos.length <= 0) {
            divider.setVisibility(View.GONE);
            cusRow.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            cusRow.setVisibility(View.VISIBLE);
            for (CustomerLogo logo : logos) {
                if (logo != null) {
                    cusRow.addView(getView(logo));
                }
            }
        }
        shareToWx(ShareSDK.getPlatform(Wechat.NAME));
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share, null);
        this.setContentView(mView);
        shareTable = ButterKnife.findById(mView, R.id.share_table);
        shareTable.setStretchAllColumns(true);
        shareToSina = ButterKnife.findById(mView, R.id.share_to_sina);
        shareToWx = ButterKnife.findById(mView, R.id.share_to_wx);
        shareToWxCircle = ButterKnife.findById(mView, R.id.share_to_wxcircle);
        shareToQQ = ButterKnife.findById(mView, R.id.share_to_qq);
        shareToQzone = ButterKnife.findById(mView, R.id.share_to_qzone);
        divider = ButterKnife.findById(mView, R.id.share_divider);
        cusRow = ButterKnife.findById(mView, R.id.share_cus_row);
        cancleBtn = ButterKnife.findById(mView, R.id.share_cancle);
        deleteBtn = ButterKnife.findById(mView, R.id.share_delete);
        divide = ButterKnife.findById(mView, R.id.divide);
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
        shareToSina.setOnClickListener(v -> {
            dismiss();
            shareToSina();
        });
        shareToWx.setOnClickListener(v -> {
            dismiss();
            shareToWx(ShareSDK.getPlatform(Wechat.NAME));
        });
        shareToWxCircle.setOnClickListener(v -> {
            dismiss();
            shareToWx(ShareSDK.getPlatform(WechatMoments.NAME));
        });
        shareToQQ.setOnClickListener(v -> {
            dismiss();
            shareToQQ(ShareSDK.getPlatform(QQ.NAME));
        });
        shareToQzone.setOnClickListener(v -> {
            dismiss();
            shareToQQ(ShareSDK.getPlatform(QZone.NAME));
        });
        cancleBtn.setOnClickListener(v -> {
            dismiss();
        });

        deleteBtn.setOnClickListener(v -> {
            dismiss();
            EventBus.getDefault().post(new DeleteDynamicEvent(id));
        });
    }

    private View getView(final CustomerLogo logo) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_customer_item, null);
        ImageView img = ButterKnife.findById(view, R.id.share_item_img);
        TextView text = ButterKnife.findById(view, R.id.share_item_title);
        img.setImageBitmap(logo.enableLogo);
        text.setText(logo.label);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                logo.listener.onClick(v);
            }
        });
        return view;
    }

    private void shareToSina() {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(title);
        oks.setText(title + " 快点此预览本书>>" + url + ShareSdkUtil.FROM);//微博分享内容：文案+链接+From
        if (!TextUtils.isEmpty(url)) {
            oks.setUrl(getFullUrl(url, "weibo"));
        }

        if (!TextUtils.isEmpty(imgSinaUrl)) {
            if (imgSinaUrl.startsWith("http")) {
                oks.setImageUrl(imgSinaUrl);
            } else {
                oks.setImagePath(imgSinaUrl);
            }
        }
        oks.setSilent(true);
        oks.setPlatform(SinaWeibo.NAME);
        oks.show(context);
    }

    private void shareToWx(Platform platform) {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(title);
        oks.setText(content);
        if (!TextUtils.isEmpty(url)) {
            oks.setUrl(getFullUrl(url, "wechat"));
        }
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = ShareSdkUtil.getImgStrByResource(context, R.mipmap.ic_launcher);
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                oks.setImageUrl(imgUrl);
            } else {
                oks.setImagePath(imgUrl);
            }
        }
        oks.setSilent(true);
        oks.setPlatform(platform.getName());
        oks.show(context);
    }

    private void shareToQQ(Platform platform) {
        OnekeyShare oks = new OnekeyShare();
//        oks.setNotification(context.getResources().getIdentifier("ic_launcher", "drawable", context.getPackageName()), context.getString(context.getResources().getIdentifier("app_name", "string", context.getPackageName())));
        oks.setTitle(title);
//        oks.setText(content.substring(0, content.indexOf("http")));
        oks.setText(content);
        if (!TextUtils.isEmpty(url)) {
            oks.setTitleUrl(getFullUrl(url, "qq"));
        }
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = ShareSdkUtil.getImgStrByResource(context, R.mipmap.ic_launcher);
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                oks.setImageUrl(imgUrl);
            } else {
                oks.setImagePath(imgUrl);
            }
        }
        oks.setSilent(true);
        oks.setPlatform(platform.getName());
        oks.show(context);
    }

    public String getFullUrl(String url, String platform) {
        if (url.contains("?")) {
            url += "&";
        } else {
            url += "?";
        }
        return url + "tf_c=android&tf_t=" + platform + "&uid=" + FastData.getUserId() + "&tf_time=" + (System.currentTimeMillis() / 1000);
    }
}
