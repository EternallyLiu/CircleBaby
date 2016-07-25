package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.managers.interfaces.IPageScale;

/**
 * @author liuxz:
 * @version OpenPlatPod 1.0 (页信息)
 * @date 2016-4-18 下午1:47:43
 */
public class TFOBookContentModel implements Parcelable, IPageScale {

    public static int CONTENT_TYPE_COVER = 0;
    public static int CONTENT_TYPE_TEXT = 1;
    public static int CONTENT_TYPE_BACK_COVER = 2;
    public static int PAGE_LEFT = 0;
    public static int PAGE_RIGHT = 1;

    public static int CONTENT_TYPE_ZHENGWEN = 1;
    public static int CONTENT_TYPE_CHAYE = 2;
    public static int CONTENT_TYPE_FENG1 = 3;
    public static int CONTENT_TYPE_FENG2 = 4;
    public static int CONTENT_TYPE_FENG3 = 5;
    public static int CONTENT_TYPE_FENG4 = 6;
    public static int CONTENT_TYPE_KONGBAIYE = 7;
    public static int CONTENT_TYPE_FEIYE = 8;
    public static int CONTENT_TYPE_JIYUYE = 9;
    public static int CONTENT_TYPE_SHUJI2 = 10;
    float my_view_scale = 1.f;


    String content_id;// 书页内容ID
    int page_number;//页码
    int content_type;// 书页类型 1 正文 2-插页 3－封面1 4-封面2 5-封面3 6-封面4 7-空白页 8-扉页 9-寄语页 10-书脊2
    int page_type;// 书页类型0 左页 1 右页
    float page_zoom;// 书页缩放比例0 默认尺寸 1 缩放至整个版面，0-1之间
    String page_color;// 书页背景色
    String page_image;// 书页背景图片
    String web_content;//版芯html内容
    List<TFOBookElementModel> element_list = new ArrayList<>();// 书页版面元素列表

    public float getMyViewScale() {
        return my_view_scale;
    }

    public void setMyViewScale(float my_view_scale) {
        this.my_view_scale = my_view_scale;
    }

    public String getContentId() {
        return content_id;
    }

    public void setContentId(String content_id) {
        this.content_id = content_id;
    }

    public int getContentType() {
        return content_type;
    }

    public void setContentType(int content_type) {
        this.content_type = content_type;
    }

    public int getPageType() {
        return page_type;
    }

    public void setPageType(int page_type) {
        this.page_type = page_type;
    }

    public float getPageZoom() {
        return page_zoom;
    }

    public void setPageZoom(float page_zoom) {
        this.page_zoom = page_zoom;
    }

    public String getPageColor() {
        return page_color;
    }

    public void setPageColor(String page_color) {
        this.page_color = page_color;
    }

    public String getPageImage() {
        return page_image;
    }

    public void setPageImage(String page_image) {
        this.page_image = page_image;
    }

    public List<TFOBookElementModel> getElementList() {
        return element_list;
    }

    public void setElementList(List<TFOBookElementModel> element_list) {
        this.element_list = element_list;
    }

    public int getPageNumber() {
        return page_number;
    }

    public void setPageNumber(int page_number) {
        this.page_number = page_number;
    }

    public String getWebContent() {
        return web_content;
    }

    public void setWebContent(String web_content) {
        this.web_content = web_content;
    }

    public TFOBookContentModel() {
    }

    public TFOBookContentModel(boolean firstPage, int page_width, int page_height) {
        if (firstPage) {
            genFirstPage(page_width, page_height);
        } else {
            genLastPage(page_width, page_height);
        }
    }

    //创建双页模式的封底右页
    private void genLastPage(int page_width, int page_height) {
    }

    //创建双页模式的封面左页
    private void genFirstPage(int page_width, int page_height) {
    }

    @Override
    public void setPageScale(float scale) {
        this.my_view_scale = scale;

        if (element_list != null) {
            for (TFOBookElementModel ele : element_list) {
                ele.setPageScale(scale);
            }
        }
    }

    @Override
    public void resetPageScale() {
        if (element_list != null) {
            for (TFOBookElementModel ele : element_list) {
                ele.resetPageScale();
            }
        }

        my_view_scale = 1.f;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.my_view_scale);
        dest.writeString(this.content_id);
        dest.writeInt(this.page_number);
        dest.writeInt(this.content_type);
        dest.writeInt(this.page_type);
        dest.writeFloat(this.page_zoom);
        dest.writeString(this.page_color);
        dest.writeString(this.page_image);
        dest.writeString(this.web_content);
        dest.writeTypedList(this.element_list);
    }

    protected TFOBookContentModel(Parcel in) {
        this.my_view_scale = in.readFloat();
        this.content_id = in.readString();
        this.page_number = in.readInt();
        this.content_type = in.readInt();
        this.page_type = in.readInt();
        this.page_zoom = in.readFloat();
        this.page_color = in.readString();
        this.page_image = in.readString();
        this.web_content = in.readString();
        this.element_list = in.createTypedArrayList(TFOBookElementModel.CREATOR);
    }

    public static final Creator<TFOBookContentModel> CREATOR = new Creator<TFOBookContentModel>() {
        @Override
        public TFOBookContentModel createFromParcel(Parcel source) {
            return new TFOBookContentModel(source);
        }

        @Override
        public TFOBookContentModel[] newArray(int size) {
            return new TFOBookContentModel[size];
        }
    };
}
