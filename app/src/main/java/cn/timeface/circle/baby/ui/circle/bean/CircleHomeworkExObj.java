package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.open.api.bean.obj.TFOCustomData;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleHomeworkExObj implements Parcelable {
    private CircleHomeworkObj homework;
    private String schoolTaskName;

    public CircleHomeworkObj getHomework() {
        return homework;
    }

    public void setHomework(CircleHomeworkObj homework) {
        this.homework = homework;
    }

    public String getSchoolTaskName() {
        return schoolTaskName;
    }

    public void setSchoolTaskName(String schoolTaskName) {
        this.schoolTaskName = schoolTaskName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.homework, flags);
        dest.writeString(this.schoolTaskName);
    }

    public CircleHomeworkExObj() {
    }

    protected CircleHomeworkExObj(Parcel in) {
        this.homework = in.readParcelable(CircleHomeworkObj.class.getClassLoader());
        this.schoolTaskName = in.readString();
    }

    public static final Parcelable.Creator<CircleHomeworkExObj> CREATOR = new Parcelable.Creator<CircleHomeworkExObj>() {
        @Override
        public CircleHomeworkExObj createFromParcel(Parcel source) {
            return new CircleHomeworkExObj(source);
        }

        @Override
        public CircleHomeworkExObj[] newArray(int size) {
            return new CircleHomeworkExObj[size];
        }
    };

    public TFOPublishObj toTFOPublishObj(){
        TFOPublishObj tfoPublishObj = new TFOPublishObj();
        tfoPublishObj.setTitle(getHomework().getTitle());
        tfoPublishObj.setContent(getHomework().getContent().replace("\n", "<br/>"));
        tfoPublishObj.setResourceList(toTFOResourceObjs());
        TFOCustomData tfoCustomData = new TFOCustomData();
        StringBuffer sbCustomData = new StringBuffer();
        //"homeschool_data": {"comment": "","tags": [{"tag_content": "标签内容1"},{"tag_content": "标签内容2"}]
        sbCustomData.append("{\"comment\":")
                .append(TextUtils.isEmpty(getHomework().getTeacherReview()) ? "\"\"" : getHomework().getTeacherReview())
                .append(",")
                .append("\"tags\":[");

        if(getHomework().getNotations().size() > 0){
            for(String tag : getHomework().getNotations()){
                sbCustomData.append("{\"tag_content\":").append(tag).append("},");
            }
            sbCustomData.replace(sbCustomData.lastIndexOf(","), sbCustomData.lastIndexOf(",") + 1, "");
        }
        sbCustomData.append("]}");
        tfoCustomData.setHomeschoolData(sbCustomData.toString());
        tfoPublishObj.setCustomData(tfoCustomData);

        return tfoPublishObj;
    }

    public List<TFOResourceObj> toTFOResourceObjs() {
        List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
        for (MediaObj mediaObj : getHomework().getMediaList()) {
            tfoResourceObjs.add(mediaObj.toTFOResourceObj());
        }
        return tfoResourceObjs;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CircleHomeworkExObj){
            return ((CircleHomeworkExObj) obj).getHomework().equals(getHomework());
        }
        return super.equals(obj);
    }
}
