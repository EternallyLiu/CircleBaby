package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.db.PhotoModel;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class PrintParamResponse extends BaseResponse implements Parcelable{
    public static final String KEY_SIZE = "size";
    public static final String KEY_COLOR = "color";
    public static final String KEY_PACK = "pack";
    public static final String KEY_PAPER = "paper";
    private List<PrintParamObj> valueList; //返回数据集
    private String key; //键值
    private String name; //名称

    public PrintParamResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PrintParamObj> getValueList() {
        // 默认是未选中的状态（之前的操作会对选中状体造成影响）
        for (PrintParamObj paramObj : valueList) {
            paramObj.setIsSelect(false);
        }
        return valueList;
    }

    public void setValueList(List<PrintParamObj> valueList) {
        this.valueList = valueList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(valueList);
        dest.writeString(this.key);
        dest.writeString(this.name);
    }
    protected PrintParamResponse(Parcel in){
        this.valueList = in.createTypedArrayList(PrintParamObj.CREATOR);
        this.key = in.readString();
        this.name = in.readString();
    }
    public static final Creator<PrintParamResponse> CREATOR = new Creator<PrintParamResponse>() {
        @Override
        public PrintParamResponse createFromParcel(Parcel source) {
            return new PrintParamResponse(source);
        }

        @Override
        public PrintParamResponse[] newArray(int size) {
            return new PrintParamResponse[size];
        }
    };
}
