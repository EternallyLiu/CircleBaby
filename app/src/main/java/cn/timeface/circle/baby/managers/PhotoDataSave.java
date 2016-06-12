package cn.timeface.circle.baby.managers;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.api.models.db.PhotoModel;


/**
 * author: rayboot  Created on 16/4/28.
 * email : sy0725work@gmail.com
 */
public class PhotoDataSave {
    private List<PhotoModel> imgs = new ArrayList<>(10);
    private List<PhotoModel> selImgs = new ArrayList<>(10);

    //http://www.race604.com/java-double-checked-singleton/
    private static volatile PhotoDataSave sInst = null;  // volatile

    public static PhotoDataSave getInstance() {
        PhotoDataSave inst = sInst;
        if (inst == null) {
            synchronized (PhotoDataSave.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new PhotoDataSave();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    private PhotoDataSave() {
    }

    public List<PhotoModel> getImgs() {
        return imgs;
    }

    public void setImgs(List<PhotoModel> imgs) {
        this.imgs = imgs;
    }

    public List<PhotoModel> getSelImgs() {
        return selImgs;
    }

    public void setSelImgs(List<PhotoModel> selImgs) {
        this.selImgs = selImgs;
    }

    public void clear() {
        if (imgs != null) {
            imgs.clear();
            imgs = null;
        }
        if (selImgs != null) {
            selImgs.clear();
            selImgs = null;
        }
    }
}
