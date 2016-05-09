package cn.timeface.circle.baby.api.models.objs;


import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/20.
 * email : sy0725work@gmail.com
 */
public class CommentObj extends BaseObj {
    int commentId;
    String content;
    long time;
    UserObj userInfo;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserObj getUserinfo() {
        return userInfo;
    }

    public void setUserinfo(UserObj userinfo) {
        this.userInfo = userinfo;
    }
}
