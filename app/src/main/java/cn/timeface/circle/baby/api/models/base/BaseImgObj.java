package cn.timeface.circle.baby.api.models.base;

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

import com.bluelinelabs.logansquare.annotation.JsonIgnore;

import java.io.File;
import java.io.FileNotFoundException;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.utils.ImageUtil;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class BaseImgObj extends BaseObj implements Parcelable {
    @JsonIgnore
    static final int MINI_THUMBNAIL_SIZE = 300;
    @JsonIgnore
    static final int MICRO_THUMBNAIL_SIZE = 96;
    protected String id;
    protected int width;
    protected int height;
    protected String url;
    protected double lat;
    protected double lng;
    protected String localPath;
    protected String md5;
    protected String content;
    protected String date;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    protected String objectKey;

    public long getDateMills() {
        return dateMills;
    }

    public void setDateMills(long dateMills) {
        this.dateMills = dateMills;
    }

    protected long dateMills;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    protected Bitmap getThumbnailImageFromMediaStore(Context context) {
        Resources res = context.getResources();

        final int kind = res.getBoolean(R.bool.load_mini_thumbnails) ? MediaStore.Images.Thumbnails.MINI_KIND
                : MediaStore.Images.Thumbnails.MICRO_KIND;

        BitmapFactory.Options opts = null;
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND && res.getBoolean(R.bool.sample_mini_thumbnails)) {
            opts = new BitmapFactory.Options();
            opts.inSampleSize = 2;
        }

        try {
            final long id = Long.parseLong(getUri().getLastPathSegment());

            return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, kind, opts);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public Bitmap getThumbnailImage(Context context) {
        if (ContentResolver.SCHEME_CONTENT.equals(getUri().getScheme())) {
            return getThumbnailImageFromMediaStore(context);
        }

        final Resources res = context.getResources();
        int size = res.getBoolean(R.bool.load_mini_thumbnails) ? MINI_THUMBNAIL_SIZE
                : MICRO_THUMBNAIL_SIZE;
        if (size == MINI_THUMBNAIL_SIZE && res.getBoolean(R.bool.sample_mini_thumbnails)) {
            size /= 2;
        }

        try {
            return ImageUtil.decodeImage(context.getContentResolver(), getUri(), size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


    public BaseImgObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.url);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.localPath);
        dest.writeString(this.md5);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeString(this.objectKey);
        dest.writeLong(this.dateMills);
    }

    protected BaseImgObj(Parcel in) {
        this.id = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.url = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.localPath = in.readString();
        this.md5 = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.objectKey = in.readString();
        this.dateMills = in.readLong();
    }

    public static final Creator<BaseImgObj> CREATOR = new Creator<BaseImgObj>() {
        @Override
        public BaseImgObj createFromParcel(Parcel source) {
            return new BaseImgObj(source);
        }

        @Override
        public BaseImgObj[] newArray(int size) {
            return new BaseImgObj[size];
        }
    };
}
