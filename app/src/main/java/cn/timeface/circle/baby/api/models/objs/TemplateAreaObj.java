package cn.timeface.circle.baby.api.models.objs;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.googlecode.mp4parser.util.Math;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.base.BaseObj;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by lidonglin on 2016/5/27.
 */
public class TemplateAreaObj extends BaseObj {
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TEXT_TYPE_TITLE = 0;
    public static final int TEXT_TYPE_CONTENT = 1;
    public static final int TEXT_ALIGN_LEFT = 0;
    public static final int TEXT_ALIGN_RIGHT = 1;
    public static final int TEXT_ALIGN_CENTER = 2;

    String bgImage;
    String family;
    String text;
    String textColor;

    int height;
    int imgMargin;
    int left;
    int limitNum;
    int lineNum;
    int textAlign;
    int textFont;
    int textType;
    int top;
    int type;
    int width;

    TemplateImage templateImage;

    public TemplateAreaObj(String bgImage, String family, String text, String textColor, int height, int imgMargin, int left, int limitNum, int lineNum, int textAlign, int textFont, int textType, int top, int type, int width, TemplateImage templateImage) {
        this.bgImage = bgImage;
        this.family = family;
        this.text = text;
        this.textColor = textColor;
        this.height = height;
        this.imgMargin = imgMargin;
        this.left = left;
        this.limitNum = limitNum;
        this.lineNum = lineNum;
        this.textAlign = textAlign;
        this.textFont = textFont;
        this.textType = textType;
        this.top = top;
        this.type = type;
        this.width = width;
        this.templateImage = templateImage;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getImgMargin() {
        return imgMargin;
    }

    public void setImgMargin(int imgMargin) {
        this.imgMargin = imgMargin;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public int getTextFont() {
        return textFont;
    }

    public void setTextFont(int textFont) {
        this.textFont = textFont;
    }

    public int getTextType() {
        return textType;
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public TemplateImage getTemplateImage() {
        return templateImage;
    }

    public void setTemplateImage(TemplateImage templateImage) {
        this.templateImage = templateImage;
    }

    public View getView(Context context, float scale) {
        switch (type) {
            case TYPE_TEXT:
                return getTextView(context, scale);
            case TYPE_IMAGE:
                return getImageView(context, scale);
        }
        return null;
    }

    public PhotoView getImageView(Context context, float scale) {
        PhotoView imageView = new PhotoView(context);
        imageView.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(((int) (width * scale)) + 10, ((int) (height * scale)) + 10);
        lp.topMargin = ((int) (top * scale)) - 5;
        lp.leftMargin = ((int) (left * scale)) - 5;
        imageView.setLayoutParams(lp);
        imageView.setPadding(((int) (imgMargin * scale)) + 5, ((int) (imgMargin * scale)) + 5, ((int) (imgMargin * scale)) + 5, ((int) (imgMargin * scale)) + 5);
        Glide.with(context)
                .load(R.mipmap.ic_launcher)
                .into(imageView);
        return imageView;
    }

    public TextView getTextView(Context context, float scale) {
        TextView textView = new TextView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(((int) (width * scale)), ((int) (height * scale)));
        lp.topMargin = ((int) (top * scale));
        lp.leftMargin = ((int) (left * scale));
        if (!TextUtils.isEmpty(textColor)) {
            textView.setTextColor(Color.parseColor("#" + textColor));
        } else {
            textView.setTextColor(Color.BLACK);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, java.lang.Math.min(textFont * scale, height * scale));
        switch (textAlign) {
            case TEXT_ALIGN_LEFT:
                textView.setGravity(Gravity.CENTER_VERTICAL);
                break;
            case TEXT_ALIGN_RIGHT:
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                break;
            case TEXT_ALIGN_CENTER:
                textView.setGravity(Gravity.CENTER);
                break;
        }
        textView.setLayoutParams(lp);
        // TODO: 9/15/16  limitNum 和 lineNum 没有做限制
        return textView;
    }
}
