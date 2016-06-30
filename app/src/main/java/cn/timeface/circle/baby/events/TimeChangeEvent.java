package cn.timeface.circle.baby.events;

import java.io.File;
import java.util.List;

import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;


/**
 * @author rayboot
 * @from 14/11/20 17:24
 * @TODO
 */
public class TimeChangeEvent {
    public static final int TYPE_DELETE = 0;
    public static final int TYPE_LIKE = 1;
    public static final int TYPE_COMMENT = 2;
    public static final int TYPE_USER_INFO = 3;
    public static final int TYPE_PUBLISH_SUCCESS = 4;
    public static final int TYPE_PUBLISH_EDIT = 5;
    public static final int TYPE_PRE_PUBLISH_SUCCESS = 6;
    public static final int TYPE_PRE_PUBLISH_EDIT = 7;
    public static final int TYPE_BOOK_NAME = 8;//修改所属时光书
    public static final int DATA_TYPE_TIME = 0;
    public static final int DATA_TYPE_TOPIC = 1;
    public int type;
    public int dataType;
    public String dataId;
    public boolean like;
    public File avatar;
    public String bookName;//所属时光书
    public List<UploadFileObj> uploadFileObjs;

    public TimeChangeEvent(int dataType, String dataId, int type,
                           boolean like) {
        this.type = type;
        this.dataType = dataType;
        this.dataId = dataId;
        this.like = like;
    }

    public TimeChangeEvent(File uri, int type) {
        this.type = type;
        this.avatar = uri;
    }

    public TimeChangeEvent(int type) {
        this.type = type;
    }

    public TimeChangeEvent(int type, String dataId) {
        this.type = type;
        this.dataId = dataId;
    }

    public TimeChangeEvent(int dataType, int type) {
        this.type = type;
        this.dataType = dataType;
    }

    public TimeChangeEvent(int dataType, int type, String dataId) {
        this.dataType = dataType;
        this.type = type;
        this.dataId = dataId;
    }

    public TimeChangeEvent(int dataType, int type, String dataId, File uri) {
        this.dataType = dataType;
        this.type = type;
        this.dataId = dataId;
        this.avatar = uri;
    }

    public TimeChangeEvent(int type, String dataId, List<UploadFileObj> uploadFileObjs) {
        this.type = type;
        this.dataId = dataId;
        this.uploadFileObjs = uploadFileObjs;
    }
}
