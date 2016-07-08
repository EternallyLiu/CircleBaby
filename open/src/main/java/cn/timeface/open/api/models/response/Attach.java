package cn.timeface.open.api.models.response;

import cn.timeface.open.api.models.objs.TFBookBgModel;
import cn.timeface.open.api.models.objs.TFOBookImageModel;

/**
 * Created by zhsheng on 2016/7/7.
 */
public class Attach {
    String color;

    TFBookBgModel tfBookBgModel;
    TFOBookImageModel imageModel;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public TFOBookImageModel getImageModel() {
        return imageModel;
    }

    public void setImageModel(TFOBookImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public TFBookBgModel getTfBookBgModel() {
        return tfBookBgModel;
    }

    public void setTfBookBgModel(TFBookBgModel tfBookBgModel) {
        this.tfBookBgModel = tfBookBgModel;
    }
}
