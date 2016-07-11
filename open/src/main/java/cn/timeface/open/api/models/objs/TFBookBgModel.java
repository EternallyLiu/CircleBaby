package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhsheng on 2016/7/8.
 */
public class TFBookBgModel implements Parcelable {
    String background_left;
    String background_right;

    public String getBackground_left() {
        return background_left;
    }

    public void setBackground_left(String background_left) {
        this.background_left = background_left;
    }

    public String getBackground_right() {
        return background_right;
    }

    public void setBackground_right(String background_right) {
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

    public TFBookBgModel() {
    }

    protected TFBookBgModel(Parcel in) {
        this.background_left = in.readString();
        this.background_right = in.readString();
    }

    public static final Parcelable.Creator<TFBookBgModel> CREATOR = new Parcelable.Creator<TFBookBgModel>() {
        @Override
        public TFBookBgModel createFromParcel(Parcel source) {
            return new TFBookBgModel(source);
        }

        @Override
        public TFBookBgModel[] newArray(int size) {
            return new TFBookBgModel[size];
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

        TFBookBgModel that = (TFBookBgModel) o;

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