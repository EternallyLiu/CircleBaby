package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class InviteResponse extends BaseResponse {

    int inviteCode;
    String inviteUrl;
    String codeUrl;

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public int getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(int inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteUrl() {
        return inviteUrl;
    }

    public void setInviteUrl(String inviteUrl) {
        this.inviteUrl = inviteUrl;
    }
}
