package cn.timeface.circle.baby.api.models.objs;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class SystemMsg {
    String avatar;     //系统图标
    String content;    //内容
    String name;       //系统名称
    long time;         //时间戳
    int dataId;        //数据id(订单uid)
    int msgType;       //0订单未支付 1成功提交订单申请 2订单审核未通过 3订单配送中 4印刷书已送达 5新活动上线
    int id;            //消息id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
