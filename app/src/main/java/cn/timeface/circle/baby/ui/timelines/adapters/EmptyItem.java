package cn.timeface.circle.baby.ui.timelines.adapters;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * author : wangshuai Created on 2017/3/27
 * email : wangs1992321@gmail.com
 */
public class EmptyItem implements Parcelable {
    private View emptyView;
    private int operationType;
    private int id;
    private Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    private ChangeCallBack changeCallBack;

    public ChangeCallBack getChangeCallBack() {
        return changeCallBack;
    }

    public void setChangeCallBack(ChangeCallBack changeCallBack) {
        this.changeCallBack = changeCallBack;
    }

    public EmptyItem(int id) {
        this.id = id;
    }

    public EmptyItem() {
    }

    public EmptyItem(View emptyView, int operationType) {
        this.emptyView = emptyView;
        this.operationType = operationType;
        setId(emptyView.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyItem emptyItem = (EmptyItem) o;
        if (getId() == emptyItem.getId()) return true;
        return getEmptyView() == emptyItem.getEmptyView();
    }

    public EmptyItem(View emptyView, int operationType, int id) {
        this.emptyView = emptyView;
        this.operationType = operationType;
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public interface ChangeCallBack{
        public void changeCallBack(int operationType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(this.emptyView, flags);
        dest.writeInt(this.operationType);
        dest.writeInt(this.id);
    }

    protected EmptyItem(Parcel in) {
        this.emptyView = in.readParcelable(View.class.getClassLoader());
        this.operationType = in.readInt();
        this.id = in.readInt();
    }

    public static final Creator<EmptyItem> CREATOR = new Creator<EmptyItem>() {
        @Override
        public EmptyItem createFromParcel(Parcel source) {
            return new EmptyItem(source);
        }

        @Override
        public EmptyItem[] newArray(int size) {
            return new EmptyItem[size];
        }
    };
}
