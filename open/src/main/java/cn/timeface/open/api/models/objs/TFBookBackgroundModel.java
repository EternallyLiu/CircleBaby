package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhsheng on 2016/7/8.
 */
public class TFBookBackgroundModel implements Parcelable {
    String background_left;
    String background_right;

    public String getBackgroundLeft() {
        return background_left;
    }

    public void setBackgroundLeft(String background_left) {
        this.background_left = background_left;
    }

    public String getBackgroundRight() {
        return background_right;
    }

    public void setBackgroundRight(String background_right) {
        this.background_right = background_right;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.background_left);
        dest.writeString(this.background_right);
    }

    public TFBookBackgroundModel() {
    }

    protected TFBookBackgroundModel(Parcel in) {
        this.background_left = in.readString();
        this.background_right = in.readString();
    }

    public static final Parcelable.Creator<TFBookBackgroundModel> CREATOR = new Parcelable.Creator<TFBookBackgroundModel>() {
        @Override
        public TFBookBackgroundModel createFromParcel(Parcel source) {
            return new TFBookBackgroundModel(source);
        }

        @Override
        public TFBookBackgroundModel[] newArray(int size) {
            return new TFBookBackgroundModel[size];
        }
    };

    @Override
    public String toString() {
        return "TFBookBgModel{" +
                "background_left='" + background_left + '\'' +
                ", background_right='" + background_right + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TFBookBackgroundModel that = (TFBookBackgroundModel) o;

        if (background_left != null ? !background_left.equals(that.background_left) : that.background_left != null)
            return false;
        return background_right != null ? background_right.equals(that.background_right) : that.background_right == null;

    }

    @Override
    public int hashCode() {
        int result = background_left != null ? background_left.hashCode() : 0;
        result = 31 * result + (background_right != null ? background_right.hashCode() : 0);
        return result;
    }
}
