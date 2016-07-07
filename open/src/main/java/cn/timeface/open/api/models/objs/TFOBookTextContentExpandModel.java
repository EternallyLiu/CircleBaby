package cn.timeface.open.api.models.objs;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import cn.timeface.open.managers.interfaces.IMoveParams;
import cn.timeface.open.managers.interfaces.IPageScale;

public class TFOBookTextContentExpandModel implements Parcelable, IPageScale, IMoveParams {

    String font_family;// 字体名称
    String font_file_path;// 字体绝对路径
    float font_size;// 字号
    int text_vertical_align;//垂直方向对齐方式 1
    int text_align;// 对齐方式 1 左对齐 2 右对齐 3居中对齐
    int text_background_with;//背景与实际文本的跟随关系0不跟随 1左右跟随 2 上下跟随 3左右上下跟随
    String text_color;// 字体颜色
    float text_line_height;// 行高
    int max_text_count;

    public int getMaxTextCount() {
        return max_text_count;
    }

    public void setMaxTextCount(int max_text_count) {
        this.max_text_count = max_text_count;
    }

    public String getFontFamily() {
        return font_family;
    }

    public void setFontFamily(String font_family) {
        this.font_family = font_family;
    }

    public String getFontFilePath() {
        return font_file_path;
    }

    public void setFontFilePath(String font_file_path) {
        this.font_file_path = font_file_path;
    }

    public float getFontSize() {
        return font_size;
    }

    public void setFontSize(float font_size) {
        this.font_size = font_size;
    }

    public int getTextAlign() {
        return text_align;
    }

    public void setTextAlign(int text_align) {
        this.text_align = text_align;
    }

    public String getTextColor() {
        return text_color;
    }

    public void setTextColor(String text_color) {
        this.text_color = text_color;
    }

    public float getTextLineHeight() {
        return text_line_height;
    }

    public void setTextLineHeight(float text_line_height) {
        this.text_line_height = text_line_height;
    }

    public void setTextExpandInfo(TextView textView) {
        switch (text_align) {
            case 1:
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case 2:
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case 3:
                textView.setGravity(Gravity.CENTER);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
        if (!TextUtils.isEmpty(text_color)) {
            textView.setTextColor(Color.parseColor(text_color));
        }
        textView.setLineSpacing(text_line_height, 0);

        //字体设置待完善
    }

    public TFOBookTextContentExpandModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.font_family);
        dest.writeString(this.font_file_path);
        dest.writeFloat(this.font_size);
        dest.writeInt(this.text_vertical_align);
        dest.writeInt(this.text_align);
        dest.writeInt(this.text_background_with);
        dest.writeString(this.text_color);
        dest.writeFloat(this.text_line_height);
        dest.writeInt(this.max_text_count);
    }

    protected TFOBookTextContentExpandModel(Parcel in) {
        this.font_family = in.readString();
        this.font_file_path = in.readString();
        this.font_size = in.readFloat();
        this.text_vertical_align = in.readInt();
        this.text_align = in.readInt();
        this.text_background_with = in.readInt();
        this.text_color = in.readString();
        this.text_line_height = in.readFloat();
        this.max_text_count = in.readInt();
    }

    public static final Creator<TFOBookTextContentExpandModel> CREATOR = new Creator<TFOBookTextContentExpandModel>() {
        @Override
        public TFOBookTextContentExpandModel createFromParcel(Parcel source) {
            return new TFOBookTextContentExpandModel(source);
        }

        @Override
        public TFOBookTextContentExpandModel[] newArray(int size) {
            return new TFOBookTextContentExpandModel[size];
        }
    };

    @Override
    public void setPageScale(float scale) {
        this.font_size *= scale;
        this.text_line_height *= scale;
    }

    @Override
    public void resetPageScale(float scale) {
        this.font_size /= scale;
        this.text_line_height /= scale;
    }

    @Override
    public void moveParams(float movedX, float movedY, int eleW, int eleH, float scale, float rotation) {
        this.font_size *= scale;
        this.text_line_height *= scale;
    }
}