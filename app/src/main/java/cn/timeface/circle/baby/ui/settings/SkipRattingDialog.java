package cn.timeface.circle.baby.ui.settings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.PermissionUtils;

/**
 * author : wangshuai Created on 2017/2/21
 * email : wangs1992321@gmail.com
 * 提示评论
 */
public class SkipRattingDialog extends DeleteDialog implements DeleteDialog.SubmitListener{
    public SkipRattingDialog(Context context) {
        super(context);
        initMessage();
    }

    public SkipRattingDialog(Context context, int theme) {
        super(context, theme);
        initMessage();
    }

    public SkipRattingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initMessage();
    }

    public static SkipRattingDialog getInstance(Context context){
        return new SkipRattingDialog(context);
    }

    private void initMessage(){
        String name= FastData.getBabyObj().getNickName();
        SpannableStringBuilder builder=new SpannableStringBuilder().append(String.format("成长印记又陪\n%s\n",name))
                .append("度过了一段美好的时光，\n").append(String.format("让我们和 %s 一起成长，\n",name))
                .append("变得越来越棒吧！");
        String content=builder.toString();
        LogUtil.showLog("content:"+content);
        String[] split = content.split("\n");
        ForegroundColorSpan colorSpan=new ForegroundColorSpan(Color.parseColor("#F59650"));
        StyleSpan styleSpan=new StyleSpan(Typeface.BOLD);
        ForegroundColorSpan colorSpan1=new ForegroundColorSpan(Color.parseColor("#F59650"));
        StyleSpan styleSpan1=new StyleSpan(Typeface.BOLD);

        int beginIndex=content.indexOf(name);
        int endIndex=content.lastIndexOf(name);
        builder.setSpan(colorSpan1,beginIndex,beginIndex+name.length()+1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(colorSpan,endIndex,endIndex+name.length()+1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(styleSpan1,beginIndex,beginIndex+name.length()+1,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(styleSpan,endIndex,endIndex+name.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        setMessageGravity(Gravity.CENTER);
        setMessage(builder);
        setCancelTip("去提意见");
        setSubmitTip("五星好评");
        showClose(true);
        setSubmitListener(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                submit();
                dismiss();
                break;
            case R.id.submit:
                if (getSubmitListener() != null) {
                    getSubmitListener().submit();
                    dismiss();
                }
                break;
            case R.id.close:
                dismiss();
                break;
        }
    }

    @Override
    public void submit() {
        PermissionUtils.skipRatting(getContext());
    }
}
