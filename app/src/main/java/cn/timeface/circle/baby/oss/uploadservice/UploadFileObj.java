package cn.timeface.circle.baby.oss.uploadservice;

import android.os.Parcelable;

import java.io.File;

/**
 * author: rayboot  Created on 15/9/28.
 * email : sy0725work@gmail.com
 */
public abstract class UploadFileObj implements Parcelable {
    protected String filePath;
    protected String objectKey;

    /**
     * 图片路径已经是阿里云服务器
     * @param filePath
     */
    public UploadFileObj(String filePath) {
        this(filePath, filePath.replace("http://img1.xialingying.tv/", ""));
    }

    public UploadFileObj(String filePath, String objectKey) {
        this.filePath = filePath;
        this.objectKey = objectKey;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getFinalUploadFile() {
        return new File(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return new File(filePath);
    }

    public abstract String getObjectKey();

}
