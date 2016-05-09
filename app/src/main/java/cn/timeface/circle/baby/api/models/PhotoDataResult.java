package cn.timeface.circle.baby.api.models;

import java.util.List;

import cn.timeface.circle.baby.api.models.objs.ImgObj;

/**
 * 存储图片数据
 * author : YW.SUN Created on 2016/4/11
 * email : sunyw10@gmail.com
 */
public class PhotoDataResult {
    private static PhotoDataResult instance;
    private List<ImgObj> imgObjs = null;
    private List<ImgObj> selImgObjs = null;

    public static PhotoDataResult getInstance(){
        if(instance == null){
            instance = new PhotoDataResult();
        }
        return instance;
    }

    private PhotoDataResult(){

    }

    public List<ImgObj> getImgObjs() {
        return imgObjs;
    }

    public void setImgObjs(List<ImgObj> imgObjs) {
        this.imgObjs = imgObjs;
    }

    public List<ImgObj> getSelImgObjs() {
        return selImgObjs;
    }

    public void setSelImgObjs(List<ImgObj> selImgObjs) {
        this.selImgObjs = selImgObjs;
    }
}
