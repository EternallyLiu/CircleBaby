package cn.timeface.open.api.models.response;

/**
 * author: rayboot  Created on 16/6/21.
 * email : sy0725work@gmail.com
 */
public class Authorize {
    String access_token;//YES	string	接口调用凭证
    long expires_in;//YES	long	接口调用凭证到期时间（时间戳，秒）
    String unionid;//YES	string	用户在开放平台内的唯一ID

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public long getExpiresIn() {
        return expires_in;
    }

    public void setExpiresIn(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getUnionId() {
        return unionid;
    }

    public void setUnionId(String unionid) {
        this.unionid = unionid;
    }

}
