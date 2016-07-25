package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.MD5;


/**
 * author: rayboot  Created on 16/1/21.
 * email : sy0725work@gmail.com
 */
public class MyUploadFileObj extends UploadFileObj {
    public MyUploadFileObj(String filePath) {
        super(filePath, "baby/" + MD5.encode(new File(filePath)) + filePath.substring(filePath.lastIndexOf(".")));
    }

    @Override
    public String getObjectKey() {
        return objectKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.objectKey);
    }

    protected MyUploadFileObj(Parcel in) {
        super(in.readString(), in.readString());
    }

    public static final Parcelable.Creator<UploadFileObj> CREATOR = new Parcelable.Creator<UploadFileObj>() {
        public MyUploadFileObj createFromParcel(Parcel source) {
            return new MyUploadFileObj(source);
        }

        public MyUploadFileObj[] newArray(int size) {
            return new MyUploadFileObj[size];
        }
    };
}