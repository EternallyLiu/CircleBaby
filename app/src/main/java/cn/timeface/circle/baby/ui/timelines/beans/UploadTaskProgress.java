package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/4/14
 * email : wangs1992321@gmail.com
 */
@Table(database = AppDatabase.class)
public class UploadTaskProgress extends BaseModel implements Parcelable {

    public static final int TYPE_TIMELINE = 90001;
    public static final int TYPE_CIRCLETIMELINE = 90002;
    public static final int TYPE_LOCAL_URL = 90003;

    public static void saveTask(UploadTaskProgress taskProgress) {
        if (taskProgress != null) taskProgress.save();
    }

    public static Observable<UploadTaskProgress> getTaskList() {
        return Observable.defer(() -> Observable.from(SQLite.select().from(UploadTaskProgress.class).queryList()))
                .filter(taskProgress -> taskProgress.getProgress() <= taskProgress.getCount());
    }

    public static List<UploadTaskProgress> queryTaskList() {
        return SQLite.select().from(UploadTaskProgress.class).queryList();
    }

    @PrimaryKey
    private String id;//唯一主键
    @Column
    private long taskId;//任务id 本地任务管理主键
    @Column
    private int type;//任务类型
    @Column
    private int progress = 0;//上传的当前进度
    @Column
    private int count;//任务总共的上传数量
    @Column
    private String contentString;//任务的json字符串 用于业务显示
    @Column
    private long createTime;//创建时间
    @Column
    private long updateTime;//最后一次修改时间

    public UploadTaskProgress(String id, long taskId, int type, String contentString, long createTime, long updateTime, int progress, int count) {
        this.id = id;
        this.taskId = taskId;
        this.type = type;
        this.contentString = contentString;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.progress = progress;
        this.count = count;
    }

    public UploadTaskProgress(String id, long taskId, List<String> list) {
        this.id = id;
        this.taskId = taskId;
        this.type = TYPE_LOCAL_URL;
        this.createTime = System.currentTimeMillis();
        this.count = list.size();
        this.progress = 0;
        this.contentString = JSONUtils.parse2JSONString(list);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public UploadTaskProgress() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.taskId);
        dest.writeInt(this.type);
        dest.writeString(this.contentString);
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.progress);
        dest.writeInt(this.count);
    }

    protected UploadTaskProgress(Parcel in) {
        this.id = in.readString();
        this.taskId = in.readLong();
        this.type = in.readInt();
        this.contentString = in.readString();
        this.createTime = in.readLong();
        this.updateTime = in.readLong();
        this.progress = in.readInt();
        this.count = in.readInt();
    }

    public static final Creator<UploadTaskProgress> CREATOR = new Creator<UploadTaskProgress>() {
        @Override
        public UploadTaskProgress createFromParcel(Parcel source) {
            return new UploadTaskProgress(source);
        }

        @Override
        public UploadTaskProgress[] newArray(int size) {
            return new UploadTaskProgress[size];
        }
    };
}
