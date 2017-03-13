package cn.timeface.circle.baby.support.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/3/30.
 */
public class Record extends BaseObj {

    RecordObj recordObj;
    List<UserObj> goodList;
    List<CommentObj> commentList;

    public RecordObj getRecordObj() {
        return recordObj;
    }

    public void setRecordObj(RecordObj recordObj) {
        this.recordObj = recordObj;
    }

    public List<UserObj> getGoodList() {
        return goodList;
    }

    public void setGoodList(List<UserObj> goodList) {
        this.goodList = goodList;
    }

    public List<CommentObj> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentObj> commentList) {
        this.commentList = commentList;
    }
}
