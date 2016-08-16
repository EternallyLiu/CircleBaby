package cn.timeface.circle.baby.events;


import cn.timeface.circle.baby.api.models.objs.CommentObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class ActionCallBackEvent {
    private CommentObj comment;
    private int replacePosition;
    private int listPos;
    private TimeLineObj timeLineObj;

    public ActionCallBackEvent(CommentObj comment, int replacePosition, int listPos, TimeLineObj timeLineObj) {
        this.comment = comment;
        this.replacePosition = replacePosition;
        this.listPos = listPos;
        this.timeLineObj = timeLineObj;
    }

    public CommentObj getComment() {
        return comment;
    }

    public void setComment(CommentObj comment) {
        this.comment = comment;
    }

    public int getReplacePosition() {
        return replacePosition;
    }

    public void setReplacePosition(int replacePosition) {
        this.replacePosition = replacePosition;
    }

    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }

    public TimeLineObj getTimeLineObj() {
        return timeLineObj;
    }

    public void setTimeLineObj(TimeLineObj timeLineObj) {
        this.timeLineObj = timeLineObj;
    }

}
