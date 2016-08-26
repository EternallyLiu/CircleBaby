package cn.timeface.open.api.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * author: shiyan  Created on 8/25/16.
 * email : sy0725work@gmail.com
 */
public class EditBookCover implements Parcelable {
    String book_id;//	YES	string	时光书在开放平台内的唯一ID
    List<String> book_cover;//	YES	string	时光书封面地址

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public List<String> getBookCover() {
        return book_cover;
    }

    public void setBookCover(List<String> book_cover) {
        this.book_cover = book_cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.book_id);
        dest.writeStringList(this.book_cover);
    }

    public EditBookCover() {
    }

    protected EditBookCover(Parcel in) {
        this.book_id = in.readString();
        this.book_cover = in.createStringArrayList();
    }

    public static final Creator<EditBookCover> CREATOR = new Creator<EditBookCover>() {
        @Override
        public EditBookCover createFromParcel(Parcel source) {
            return new EditBookCover(source);
        }

        @Override
        public EditBookCover[] newArray(int size) {
            return new EditBookCover[size];
        }
    };
}
