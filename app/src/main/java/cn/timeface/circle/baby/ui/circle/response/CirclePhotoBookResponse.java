package cn.timeface.circle.baby.ui.circle.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;

/**
 * 圈照片书response(返回的是圈子的活动相册列表)
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CirclePhotoBookResponse extends BaseResponse {
    private List<CircleActivityAlbumObjWrapper> dataList;

    public List<CircleActivityAlbumObjWrapper> getDataList() {
        if(dataList == null){
            return new ArrayList<>();
        } else {
            Iterator iterator = dataList.iterator();
            while (iterator.hasNext()){
                CircleActivityAlbumObjWrapper albumObjWrapper = (CircleActivityAlbumObjWrapper) iterator.next();
                if(albumObjWrapper.getActivityAlbum().getMediaCount() <= 0){
                    iterator.remove();
                }
            }
            return dataList;
        }
    }

    public void setDataList(List<CircleActivityAlbumObjWrapper> dataList) {
        this.dataList = dataList;
    }
}
