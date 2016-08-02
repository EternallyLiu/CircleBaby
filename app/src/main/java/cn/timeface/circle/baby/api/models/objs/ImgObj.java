package cn.timeface.circle.baby.api.models.objs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.api.models.base.BaseImgObj;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.utils.ImageFactory;
import cn.timeface.circle.baby.utils.ImageUtil;
import cn.timeface.circle.baby.utils.MD5;

/**
 * @author SUN
 * @from 2015/4/9
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ImgObj extends BaseImgObj {

    @JsonIgnore
    public static final int STATE_UPLOAD_COMPLETED = 5;
    @JsonIgnore
    public static final int STATE_UPLOAD_ERROR = 4;
    @JsonIgnore
    public static final int STATE_UPLOAD_IN_PROGRESS = 3;
    @JsonIgnore
    public static final int STATE_UPLOAD_WAITING = 2;
    @JsonIgnore
    public static final int STATE_SELECTED = 1;
    @JsonIgnore
    public static final int STATE_NONE = 0;

    @JsonIgnore
    private static final HashMap<Uri, ImgObj> SELECTION_CACHE = new HashMap<>();
    @JsonIgnore
    private int mState;
    @JsonIgnore
    private Uri mFullUri;
    @JsonIgnore
    private String mFullUriString;
    @JsonIgnore
    private String originalPath;//极速成书用来临时保存原图url(注意：只能为本地原图路径)
    @JsonIgnore
    private long size;//大小
    @JsonIgnore
    private int compressRate;//尺寸压缩比率

    private int selected;

    private int primaryColor;
    private List<ImgTagObj> tags;
    private String remark;

    public ImgObj() {
    }

    public ImgObj(String filePath, String url) {
        setImgSize(filePath);
        this.url = url;
        this.localPath = filePath;
    }

    public ImgObj(Uri uri) {
        this.localPath = ImageFactory.getPathFromContentUri(App.getInstance().getContentResolver(), uri);
//        this.url = TypeConstant.UPLOAD_FOLDER + "/" + MD5.encode(new File(localPath)) + localPath.substring(localPath.lastIndexOf("."));
        this.mFullUri = uri;
        this.mFullUriString = uri.toString();
        setOriginalPath(localPath);
        setImgSize(localPath);
    }

    public int getSelected() {
        return selected;
    }

    public boolean isSelected() {
        return selected > 0;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public List<ImgTagObj> getTags() {
        return tags;
    }

    public void setTags(List<ImgTagObj> tags) {
        this.tags = tags;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public static int getStateUploadCompleted() {
        return STATE_UPLOAD_COMPLETED;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getCompressRate() {
        return compressRate;
    }

    public void setCompressRate(int compressRate) {
        this.compressRate = compressRate;
    }

    public String getmFullUriString() {
        return mFullUriString;
    }

    public Uri getmFullUri() {
        return mFullUri;
    }

    public void setmFullUri(Uri mFullUri) {
        this.mFullUri = mFullUri;
    }

    public void setmFullUriString(String mFullUriString) {
        this.mFullUriString = mFullUriString;
    }

    public int getmState() {
        return mState;
    }

    public void setmState(int mState) {
        this.mState = mState;
    }

    //+++++++++++++++++add photoedit method ++++++++++++++++++++++++++++++
    public void setUploadState(final int state) {
        if (mState != state) {
            mState = state;
        }
    }

    public static ImgObj getSelection(Uri uri) {
        // Check whether we've already got a Selection cached
        ImgObj item = SELECTION_CACHE.get(uri);

        if (null == item) {
            item = new ImgObj(uri);
            SELECTION_CACHE.put(uri, item);
        }
        return item;
    }

    public static ImgObj getSelection(Uri baseUri, long id) {
        return getSelection(Uri.withAppendedPath(baseUri, String.valueOf(id)));
    }


    public Bitmap getDisplayImage(Context context) {
        try {
            final int size = getSmallestScreenDimension(context);
            Bitmap bitmap = ImageUtil.decodeImage(context.getContentResolver(), getUri(), size);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getSmallestScreenDimension(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return Math.min(display.getHeight(), display.getWidth());
    }

    public String getDisplayImageKey() {
        return "dsply_" + getUri();
    }

    public String getThumbnailImageKey() {
        return "thumb_" + getUri();
    }


    public int getMatchFaces() {
        int count = 0;
        if (tags == null) return count;
        for (ImgTagObj tagObj : tags) {
            if (tagObj.getUserInfo() != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        ImgObj other = (ImgObj) o;
        if (id != null) {
            return id.equals(other.getId());
        } else {
            return ((url != null && url.equals(other.url))
                    || (localPath != null && localPath.equals(other.localPath)))
                    && width == other.width
                    && height == other.height;
        }
    }

    /**
     * 获取一个完整的图片url地址，省去阿里云所有参数
     *
     * @return
     */
    public String getClearUrl() {
        String result = url;
        if (url.contains("@")) {
            result = url.substring(0, url.lastIndexOf("@"));
        }
        return result;
    }

    /**
     * 获取图片地址的阿里云key
     *
     * @return
     */
    public String getAliKey() {
        return url.replace("http://img1.timeface.cn/", "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mState);
        dest.writeParcelable(this.mFullUri, flags);
        dest.writeString(this.mFullUriString);
        dest.writeString(this.originalPath);
        dest.writeLong(this.size);
        dest.writeInt(this.compressRate);
        dest.writeInt(this.selected);
        dest.writeInt(this.primaryColor);
        dest.writeTypedList(this.tags);
        dest.writeString(this.remark);
    }

    protected ImgObj(Parcel in) {
        super(in);
        this.mState = in.readInt();
        this.mFullUri = in.readParcelable(Uri.class.getClassLoader());
        this.mFullUriString = in.readString();
        this.originalPath = in.readString();
        this.size = in.readLong();
        this.compressRate = in.readInt();
        this.selected = in.readInt();
        this.primaryColor = in.readInt();
        this.tags = in.createTypedArrayList(ImgTagObj.CREATOR);
        this.remark = in.readString();
    }

    public static final Creator<ImgObj> CREATOR = new Creator<ImgObj>() {
        @Override
        public ImgObj createFromParcel(Parcel source) {
            return new ImgObj(source);
        }

        @Override
        public ImgObj[] newArray(int size) {
            return new ImgObj[size];
        }
    };

    public MediaObj getMediaObj() {
        MediaObj mediaObj = new MediaObj(getContent(), getUrl(), 0, 0, getDateMills());
        return mediaObj;
    }


    public void setUrl(){
        this.url =  TypeConstants.UPLOAD_FOLDER
                + "/"
                + getMd5()
                + localPath.substring(localPath.lastIndexOf("."));
        System.out.println("ImgObj.setUrl()  =============  " + url);
    }

    public void setObjectKey(){
        this.objectKey =  TypeConstants.UPLOAD_FOLDER
                + "/"
                + getMd5()
                + localPath.substring(localPath.lastIndexOf("."));
    }

    public void setMd5(){
        this.md5 = MD5.encode(new File(localPath));
    }
}
