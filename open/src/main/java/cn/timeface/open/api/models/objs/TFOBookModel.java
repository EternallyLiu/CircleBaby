package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.managers.interfaces.IPageScale;


/**
 * @author liuxz:
 * @version OpenPlatPod 1.0 POD排版内容返回信息
 * @date 2016-4-18 上午10:21:02
 */
public class TFOBookModel implements Parcelable, IPageScale {
    float my_view_scale = 1.f;//页面级的缩放比例


    List<TFOBookContentModel> content_list = new ArrayList<>();// 时光书内容列表，包含封面，正文，封底

    String book_id;// 书籍唯一ID
    String book_cover;// 封面1地址
    String book_author;// 书籍作者
    String author_avatar;// 书籍作者头像
    String book_title;// 书籍标题
    String book_summary;// 书籍描述
    int book_type;// 书籍分类
    int book_width;// 时光书实际像素尺寸－宽度
    int book_height;// 时光书实际像素尺寸－高度
    int content_width;// 时光书版心实际像素尺寸－宽度
    int content_height;// 时光书版心实际像素尺寸－高度
    int content_padding_left;// 时光书版心距离版面左边距
    int content_padding_top;// 时光书版心距离版面顶部边距
    long create_date;// 时光书创建时间，时间戳，秒
    int total_page;// 分页查询后的总页数
    int book_total_page;// 书籍总页数
    int book_orientation;// 书的方向
    int template_id;//模板ID

    public float getMyViewScale() {
        return my_view_scale;
    }

    public void setMyViewScale(float my_view_scale) {
        this.my_view_scale = my_view_scale;
    }

    public int getTotalPage() {
        return total_page;
    }

    public void setTotalPage(int total_page) {
        this.total_page = total_page;
    }

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public String getBookCover() {
        return book_cover;
    }

    public void setBookCover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBookAuthor() {
        return book_author;
    }

    public void setBookAuthor(String book_author) {
        this.book_author = book_author;
    }

    public String getAuthorAvatar() {
        return author_avatar;
    }

    public void setAuthorAvatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public String getBookTitle() {
        return book_title;
    }

    public void setBookTitle(String book_title) {
        this.book_title = book_title;
    }

    public String getBookSummary() {
        return book_summary;
    }

    public void setBookSummary(String book_summary) {
        this.book_summary = book_summary;
    }

    public int getBookType() {
        return book_type;
    }

    public void setBookType(int book_type) {
        this.book_type = book_type;
    }

    public int getBookWidth() {
        return book_width;
    }

    public void setBookWidth(int book_width) {
        this.book_width = book_width;
    }

    public int getBookHeight() {
        return book_height;
    }

    public void setBookHeight(int book_height) {
        this.book_height = book_height;
    }

    public int getContentWidth() {
        return content_width;
    }

    public void setContentWidth(int content_width) {
        this.content_width = content_width;
    }

    public int getContentHeight() {
        return content_height;
    }

    public void setContentHeight(int content_height) {
        this.content_height = content_height;
    }

    public int getContentPaddingLeft() {
        return content_padding_left;
    }

    public void setContentPaddingLeft(int content_padding_left) {
        this.content_padding_left = content_padding_left;
    }

    public int getContentPaddingTop() {
        return content_padding_top;
    }

    public void setContentPaddingTop(int content_padding_top) {
        this.content_padding_top = content_padding_top;
    }

    public long getCreateDate() {
        return create_date;
    }

    public void setCreateDate(long create_date) {
        this.create_date = create_date;
    }

    public List<TFOBookContentModel> getContentList() {
        return content_list;
    }

    public void setContentList(List<TFOBookContentModel> content_list) {
        this.content_list = content_list;
    }

    public int getBookTotalPage() {
        return book_total_page;
    }

    public void setBookTotalPage(int book_total_page) {
        this.book_total_page = book_total_page;
    }

    public int getBookOrientation() {
        return book_orientation;
    }

    public void setBookOrientation(int book_orientation) {
        this.book_orientation = book_orientation;
    }

    public int getTemplateId() {
        return template_id;
    }

    public void setTemplateId(int template_id) {
        this.template_id = template_id;
    }

    public TFOBookModel() {
    }

    @Override
    public void setPageScale(float scale) {
        this.my_view_scale = scale;

        this.book_width *= scale;
        this.book_height *= scale;
        this.content_width *= scale;
        this.content_height *= scale;
        this.content_padding_left *= scale;
        this.content_padding_top *= scale;
        for (TFOBookContentModel contentModel : content_list) {
            contentModel.setPageScale(scale);
        }
    }

    @Override
    public void resetPageScale(float scale) {
        this.book_width /= scale;
        this.book_height /= scale;
        this.content_width /= scale;
        this.content_height /= scale;
        this.content_padding_left /= scale;
        this.content_padding_top /= scale;
        for (TFOBookContentModel contentModel : content_list) {
            contentModel.resetPageScale(scale);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.my_view_scale);
        dest.writeTypedList(this.content_list);
        dest.writeString(this.book_id);
        dest.writeString(this.book_cover);
        dest.writeString(this.book_author);
        dest.writeString(this.author_avatar);
        dest.writeString(this.book_title);
        dest.writeString(this.book_summary);
        dest.writeInt(this.book_type);
        dest.writeInt(this.book_width);
        dest.writeInt(this.book_height);
        dest.writeInt(this.content_width);
        dest.writeInt(this.content_height);
        dest.writeInt(this.content_padding_left);
        dest.writeInt(this.content_padding_top);
        dest.writeLong(this.create_date);
        dest.writeInt(this.total_page);
        dest.writeInt(this.book_total_page);
        dest.writeInt(this.book_orientation);
        dest.writeInt(this.template_id);
    }

    protected TFOBookModel(Parcel in) {
        this.my_view_scale = in.readFloat();
        this.content_list = in.createTypedArrayList(TFOBookContentModel.CREATOR);
        this.book_id = in.readString();
        this.book_cover = in.readString();
        this.book_author = in.readString();
        this.author_avatar = in.readString();
        this.book_title = in.readString();
        this.book_summary = in.readString();
        this.book_type = in.readInt();
        this.book_width = in.readInt();
        this.book_height = in.readInt();
        this.content_width = in.readInt();
        this.content_height = in.readInt();
        this.content_padding_left = in.readInt();
        this.content_padding_top = in.readInt();
        this.create_date = in.readLong();
        this.total_page = in.readInt();
        this.book_total_page = in.readInt();
        this.book_orientation = in.readInt();
        this.template_id = in.readInt();
    }

    public static final Creator<TFOBookModel> CREATOR = new Creator<TFOBookModel>() {
        @Override
        public TFOBookModel createFromParcel(Parcel source) {
            return new TFOBookModel(source);
        }

        @Override
        public TFOBookModel[] newArray(int size) {
            return new TFOBookModel[size];
        }
    };
}
