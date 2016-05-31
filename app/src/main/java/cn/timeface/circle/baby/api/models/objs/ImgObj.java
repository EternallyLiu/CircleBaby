package cn.timeface.circle.baby.api.models.objs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
//import com.lightbox.android.photoprocessing.utils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.base.BaseObj;
import cn.timeface.circle.baby.utils.ImageFactory;
import cn.timeface.circle.baby.utils.ImageUtil;


/**
 * @author SUN
 * @from 2015/4/9
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ImgObj extends BaseObj implements Parcelable {

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
    static final int MINI_THUMBNAIL_SIZE = 300;
    @JsonIgnore
    static final int MICRO_THUMBNAIL_SIZE = 96;

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

    private String id;
    private String url;
    private int selected;
    private int width;
    private int height;
    private int primaryColor;
    private List<ImgTagObj> tags;
    private String remark;
    private double lat;
    private double lng;
    private String localPath;
    private String md5;
    private String content;
    public String date;

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

    public void setImgSize(String filePath) {
        Point size = ImageUtil.getDefault().getImgSize(filePath);
        int orientation = ImageUtil.getDefault().getImageOrientation(filePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            this.width = size.y;
            this.height = size.x;
        } else {
            this.width = size.x;
            this.height = size.y;
        }
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getDate(){
        return date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public Uri getOriginalPhotoUri() {
        if (null == mFullUri && !TextUtils.isEmpty(mFullUriString)) {
            mFullUri = Uri.parse(mFullUriString);
        } else {
            mFullUri = getUri();
        }
        return mFullUri;
    }

//    public BitmapUtils.BitmapSize getOriginalPhotoSize(Context context) {
//        String path = ImageUtil.getPathFromContentUri(context.getContentResolver(), getOriginalPhotoUri());
//        if (null != path) {
//            BitmapUtils.BitmapSize size = BitmapUtils.getBitmapSize(path);
//            return size;
//        }
//        return null;
//    }

    public Bitmap getDisplayImage(Context context) {
        try {
            final int size = getSmallestScreenDimension(context);
            Bitmap bitmap = ImageUtil.decodeImage(context.getContentResolver(), getOriginalPhotoUri(), size);
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
        return "dsply_" + getOriginalPhotoUri();
    }

    public String getThumbnailImageKey() {
        return "thumb_" + getOriginalPhotoUri();
    }

    public Bitmap getThumbnailImage(Context context) {
        if (ContentResolver.SCHEME_CONTENT.equals(getOriginalPhotoUri().getScheme())) {
            return getThumbnailImageFromMediaStore(context);
        }

        final Resources res = context.getResources();
        int size = res.getBoolean(R.bool.load_mini_thumbnails) ? MINI_THUMBNAIL_SIZE
                : MICRO_THUMBNAIL_SIZE;
        if (size == MINI_THUMBNAIL_SIZE && res.getBoolean(R.bool.sample_mini_thumbnails)) {
            size /= 2;
        }

        try {
            Bitmap bitmap = ImageUtil.decodeImage(context.getContentResolver(), getOriginalPhotoUri(), size);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getThumbnailImageFromMediaStore(Context context) {
        Resources res = context.getResources();

        final int kind = res.getBoolean(R.bool.load_mini_thumbnails) ? MediaStore.Images.Thumbnails.MINI_KIND
                : MediaStore.Images.Thumbnails.MICRO_KIND;

        BitmapFactory.Options opts = null;
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND && res.getBoolean(R.bool.sample_mini_thumbnails)) {
            opts = new BitmapFactory.Options();
            opts.inSampleSize = 2;
        }

        try {
            final long id = Long.parseLong(getOriginalPhotoUri().getLastPathSegment());

            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, kind, opts);
            return bitmap;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Uri getUri() {
        if (!TextUtils.isEmpty(localPath)) {
            return Uri.fromFile(new File(localPath));
        } else if (!TextUtils.isEmpty(url)) {
            return Uri.parse(url);
        } else {
            return null;
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mState);
        dest.writeParcelable(this.mFullUri, flags);
        dest.writeString(this.mFullUriString);
        dest.writeString(this.originalPath);
        dest.writeLong(this.size);
        dest.writeInt(this.compressRate);
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.selected);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.primaryColor);
        dest.writeTypedList(tags);
        dest.writeString(this.remark);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.localPath);
        dest.writeString(this.md5);
        dest.writeString(this.content);
        dest.writeString(this.date);
    }

    protected ImgObj(Parcel in) {
        this.mState = in.readInt();
        this.mFullUri = in.readParcelable(Uri.class.getClassLoader());
        this.mFullUriString = in.readString();
        this.originalPath = in.readString();
        this.size = in.readLong();
        this.compressRate = in.readInt();
        this.id = in.readString();
        this.url = in.readString();
        this.selected = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.primaryColor = in.readInt();
        this.tags = in.createTypedArrayList(ImgTagObj.CREATOR);
        this.remark = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.localPath = in.readString();
        this.md5 = in.readString();
        this.content = in.readString();
        this.date = in.readString();
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

    /**
     * 获取一个完整的图片url地址，省去阿里云所有参数
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
     * @return
     */
    public String getAliKey() {
        return url.replace("http://img1.timeface.cn/", "");
    }

}
