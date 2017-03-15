package cn.timeface.circle.baby.ui.circle.response;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleCommentObj;

/**
 * 圈时光评论response
 * Created by lidonglin on 2017/3/14.
 */
public class CircleCommentResponse extends BaseResponse {
    private CircleCommentObj commentInfo;

    public CircleCommentObj getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CircleCommentObj commentInfo) {
        this.commentInfo = commentInfo;
    }
}
