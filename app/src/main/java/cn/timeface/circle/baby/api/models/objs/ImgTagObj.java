package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * @author SUN
 * @from 2015/4/9
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ImgTagObj implements Parcelable {
    private String faceId;//face++ 的faceId
    private String tagId;
    private int pointX;
    private int pointY;
    private int width;
    private int height;
    private UserObj userInfo;
    private String content;
    // @JsonIgnore
    private FacePosition facePosition;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCenterX() {
        return pointX + (width >> 1);

    }

    public int getCenterY() {
        return pointY + (height >> 1);
    }

    public FacePosition getFacePosition() {
        return facePosition;
    }

    public void setFacePosition(FacePosition facePosition) {
        this.facePosition = facePosition;
    }

    /**
     * 人脸框的相对位置
     */
    @JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.ANNOTATIONS_ONLY)
    public static class FacePosition implements Parcelable {
        @JsonField
        public float pointX;
        @JsonField
        public float pointY;
        @JsonField
        public float width;
        @JsonField
        public float height;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.pointX);
            dest.writeFloat(this.pointY);
            dest.writeFloat(this.width);
            dest.writeFloat(this.height);
        }

        public FacePosition() {
        }

        protected FacePosition(Parcel in) {
            this.pointX = in.readFloat();
            this.pointY = in.readFloat();
            this.width = in.readFloat();
            this.height = in.readFloat();
        }

        public static final Creator<FacePosition> CREATOR = new Creator<FacePosition>() {
            @Override
            public FacePosition createFromParcel(Parcel source) {
                return new FacePosition(source);
            }

            @Override
            public FacePosition[] newArray(int size) {
                return new FacePosition[size];
            }
        };
    }

    public ImgTagObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.faceId);
        dest.writeString(this.tagId);
        dest.writeInt(this.pointX);
        dest.writeInt(this.pointY);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeString(this.content);
        dest.writeParcelable(this.facePosition, flags);
    }

    protected ImgTagObj(Parcel in) {
        this.faceId = in.readString();
        this.tagId = in.readString();
        this.pointX = in.readInt();
        this.pointY = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.userInfo = in.readParcelable(UserObj.class.getClassLoader());
        this.content = in.readString();
        this.facePosition = in.readParcelable(FacePosition.class.getClassLoader());
    }

    public static final Creator<ImgTagObj> CREATOR = new Creator<ImgTagObj>() {
        @Override
        public ImgTagObj createFromParcel(Parcel source) {
            return new ImgTagObj(source);
        }

        @Override
        public ImgTagObj[] newArray(int size) {
            return new ImgTagObj[size];
        }
    };
}
