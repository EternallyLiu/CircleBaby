package cn.timeface.circle.baby.support.api.models.objs;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/14.
 * email : sy0725work@gmail.com
 */
public class RecordObj extends BaseObj {
    int commentCount;
    String content;
    int good;
    int goodCount;
    int id;
    List<ImgObj> imgObjList;
    long time;
    UserObj userInfo;

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public List<ImgObj> getImgObjList() {
        return imgObjList;
    }

    public void setImgObjList(List<ImgObj> imgObjList) {
        this.imgObjList = imgObjList;
    }

    public boolean hasImage() {
        return imgObjList.size() > 0;
    }

    public ArrayList<String> getImagePaths() {
        ArrayList<String> arr = new ArrayList<>(imgObjList.size());
        if (imgObjList != null && imgObjList.size() > 0) {

            for (int i = 0; i < imgObjList.size(); i++) {
                arr.add(i, imgObjList.get(i).getUrl());
            }
        }
        return arr;
    }
}
