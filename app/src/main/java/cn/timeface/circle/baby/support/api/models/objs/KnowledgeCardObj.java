package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 识图卡片obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class KnowledgeCardObj extends CardObj {
    private String content;
    private TemplateImage imageInfo;
    private String pinyin;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TemplateImage getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(TemplateImage imageInfo) {
        this.imageInfo = imageInfo;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.content);
        dest.writeParcelable(this.imageInfo, flags);
        dest.writeString(this.pinyin);
        dest.writeLong(this.cardId);
        dest.writeParcelable(this.media, flags);
        dest.writeInt(this.select);
    }

    public KnowledgeCardObj() {
    }

    protected KnowledgeCardObj(Parcel in) {
        super(in);
        this.content = in.readString();
        this.imageInfo = in.readParcelable(TemplateImage.class.getClassLoader());
        this.pinyin = in.readString();
        this.cardId = in.readLong();
        this.media = in.readParcelable(MediaObj.class.getClassLoader());
        this.select = in.readInt();
    }

    public static final Creator<KnowledgeCardObj> CREATOR = new Creator<KnowledgeCardObj>() {
        @Override
        public KnowledgeCardObj createFromParcel(Parcel source) {
            return new KnowledgeCardObj(source);
        }

        @Override
        public KnowledgeCardObj[] newArray(int size) {
            return new KnowledgeCardObj[size];
        }
    };
}
