package cn.timeface.circle.baby.support.api.models.db;

import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.common.utils.MD5;
import cn.timeface.common.utils.StorageUtil;
import rx.Observable;
import rx.functions.Func0;

/**
 * author: rayboot  Created on 16/4/13.
 * email : sy0725work@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
@Table(database = AppDatabase.class)
public class PhotoModel extends BaseModel implements Parcelable {
    // 信息缓存标志 0:未缓存 1:无位置信息 2:有位置信息
    public static final int CACHE_FLAG_UNCACHED = 0;//未缓存
    public static final int CACHE_FLAG_NONE = 1;//无信息
    public static final int CACHE_FLAG_CACHED = 2;//已缓存信息

    public PhotoModel() {
    }

    @Column(name = "photo_id")
    @PrimaryKey
    String photoId;

    @Column(name = "display_name")
    String displayName;

    @Column(name = "bucket_id")
    String bucketId;

    @Column(name = "bucket_display_name")
    String bucketDisplayName;

    @Column(name = "title")
    String title;

    @Column(name = "local_path")
    String localPath;

    @Column(name = "width")
    int width;

    @Column(name = "height")
    int height;

    @Column(name = "latitude")
    double latitude;

    @Column(name = "longitude")
    double longitude;

    @Column(name = "mini_thumb_magic")
    String miniThumbMagic;

    @Column(name = "orientation")
    int orientation;

    @Column(name = "size")
    long size;

    @Column(name = "date_taken")
    long dateTaken;

    @Column(name = "date_added")
    long dateAdded;

    @Column(name = "mime_type")
    String mimeType;

    @Column(name = "exif_flash")
    int flash;

    @Column(name = "exif_maker")
    String maker;

    @Column(name = "exif_model")
    String model;

    @Column(name = "exif_white_balance")
    int whiteBalance;

    @Column(name = "loc_info_cache_flag")
    int locInfoCacheFlag;//位置信息缓存标志 0:未缓存 1:无位置信息 2:有位置信息

//    @Column(name = "loc_info_country")
//     String country;//国家

    @Column(name = "loc_info_province")
    String province;//省

    @Column(name = "loc_info_city")
    String city;//市

    @Column(name = "loc_info_district")
    String district;//区

    @Column(name = "loc_info_scenic")
    String scenic;//景区

    @Column(name = "loc_info_ex")
    String locInfoEx;//位置信息扩展字段

    @Column(name = "face_info_cache_flag")
    int faceInfoCacheFlag;//人脸信息缓存标志 0:未缓存 1:无人脸信息 2:有人脸信息

    @Column(name = "face_count")
    int faceCount;//图片包含的人脸数量

    @Column(name = "selfie_flag")
    int selfieFlag = -1;//自拍标志 -1:未检测 0:非自拍 1:自拍

    @Column(name = "url")
    String url;

    //中文时间
    @Column(name = "string_date")
    String stringDate;

    @Column(name = "objectKey")
    String objectKey;

    @Column(name = "need_upload", defaultValue = "true")
    boolean needUpload;

    @Column(name = "thumbPath")
    String thumbPath;


    @Column(name = "md5")
    String md5;

    public PhotoModel(Cursor cursor) {
        final int IdColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
        final int displayNameColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        final int bucketIdColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID);
        final int bucketNameColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
        final int titleColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE);
        final int pathColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        final int mimeColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE);
//        final int widthColumn = cursor.getColumnIndex(ImageColumns.WIDTH);
//        final int heightColumn = cursor.getColumnIndex(ImageColumns.HEIGHT);
        final int latitudeColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE);
        final int longitudeColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE);
        final int thumbColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC);
        final int orientationColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
        final int sizeColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE);
        final int dateTakenColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
        final int dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED);

        this.photoId = cursor.getString(IdColumn);
        this.displayName = cursor.getString(displayNameColumn);
        this.bucketId = cursor.getString(bucketIdColumn);
        this.bucketDisplayName = cursor.getString(bucketNameColumn);
        this.title = cursor.getString(titleColumn);
        this.localPath = cursor.getString(pathColumn);
        this.mimeType = cursor.getString(mimeColumn);
//      this.width = cursor.getInt(widthColumn);
//      this.height = cursor.getInt(heightColumn);
        this.latitude = cursor.getDouble(latitudeColumn);
        this.longitude = cursor.getDouble(longitudeColumn);
        this.miniThumbMagic = cursor.getString(thumbColumn);
        this.orientation = cursor.getInt(orientationColumn);
        this.size = cursor.getLong(sizeColumn);
        this.dateTaken = cursor.getLong(dateTakenColumn);
        this.dateAdded = cursor.getLong(dateAddedColumn);
        this.needUpload = true;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketDisplayName() {
        return bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName) {
        this.bucketDisplayName = bucketDisplayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMiniThumbMagic() {
        return miniThumbMagic;
    }

    public void setMiniThumbMagic(String miniThumbMagic) {
        this.miniThumbMagic = miniThumbMagic;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getLocInfoCacheFlag() {
        return locInfoCacheFlag;
    }

    public void setLocInfoCacheFlag(int locInfoCacheFlag) {
        this.locInfoCacheFlag = locInfoCacheFlag;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getScenic() {
        return scenic;
    }

    public void setScenic(String scenic) {
        this.scenic = scenic;
    }

    public String getLocInfoEx() {
        return locInfoEx;
    }

    public void setLocInfoEx(String locInfoEx) {
        this.locInfoEx = locInfoEx;
    }

    public int getFaceInfoCacheFlag() {
        return faceInfoCacheFlag;
    }

    public void setFaceInfoCacheFlag(int faceInfoCacheFlag) {
        this.faceInfoCacheFlag = faceInfoCacheFlag;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }

    public int getSelfieFlag() {
        return selfieFlag;
    }

    public void setSelfieFlag(int selfieFlag) {
        this.selfieFlag = selfieFlag;
    }

    public int getFlash() {
        return flash;
    }

    public void setFlash(int flash) {
        this.flash = flash;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(int whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public String getObjectKey() {
        if (TextUtils.isEmpty(objectKey)) {
            objectKey = TypeConstants.UPLOAD_FOLDER
                    + "/"
                    + getMd5()
                    + localPath.substring(localPath.lastIndexOf("."));
        }
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public void setLocationInfo(LocationModel locationModel) {
        if (locationModel == null) return;
        this.province = locationModel.getAddressComponent().getProvince();
        this.city = locationModel.getAddressComponent().getCity();
        this.district = locationModel.getAddressComponent().getDistrict();
        this.scenic = locationModel.getScenic();
        this.locInfoEx = new Gson().toJson(locationModel);
        this.locInfoCacheFlag = PhotoModel.CACHE_FLAG_CACHED;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getUri() {
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                return Uri.fromFile(file);
            }
        } else if (!TextUtils.isEmpty(url)) {
            return Uri.parse(url);
        }
        return null;
    }

    public boolean isNeedUpload() {
        return needUpload;
    }

    public void setNeedUpload(boolean needUpload) {
        this.needUpload = needUpload;
    }

    public String getThumbPath() {
        if (TextUtils.isEmpty(this.thumbPath)) {
            this.thumbPath = StorageUtil.getTFPhotoPath(getMd5() + "_thumb" + getLocalPath().substring(getLocalPath().lastIndexOf("."))).getAbsolutePath();
        }
        return this.thumbPath;
    }

    public String getMd5() {
        if (TextUtils.isEmpty(md5)) {
            md5 = MD5.encode(new File(localPath));
        }
        return md5;
    }

    public static PhotoModel getPhotoModel(String id, String localPath, String url) {
        String value = !TextUtils.isEmpty(id) ? id : (!TextUtils.isEmpty(localPath) ? localPath : (!TextUtils.isEmpty(url) ? url : null));
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return SQLite.select()
                .from(PhotoModel.class)
                .where(PhotoModel_Table.photo_id.is(value))
                .or(PhotoModel_Table.local_path.is(value))
                .or(PhotoModel_Table.url.is(value))
                .querySingle();
    }

    public ImgObj getImgObj() {
        ImgObj imgObj = new ImgObj(getUri());
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            imgObj.setWidth(this.height);
            imgObj.setHeight(this.width);
        } else {
            imgObj.setWidth(this.width);
            imgObj.setHeight(this.height);
        }
        imgObj.setDateTimeMills(getDateTaken());
        imgObj.setMd5(this.md5);
        imgObj.setLat(this.latitude);
        imgObj.setLng(this.longitude);
        imgObj.setSize(this.size);
        imgObj.setId(this.photoId);
        imgObj.setUrl(getObjectKey());
        imgObj.setObjectKey(getObjectKey());
        imgObj.setDate(stringDate);//yyyyMMdd
        imgObj.setDateMills(dateTaken);
        return imgObj;
    }


    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "photoId='" + photoId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", bucketId='" + bucketId + '\'' +
                ", bucketDisplayName='" + bucketDisplayName + '\'' +
                ", title='" + title + '\'' +
                ", localPath='" + localPath + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", miniThumbMagic='" + miniThumbMagic + '\'' +
                ", orientation=" + orientation +
                ", size=" + size +
                ", dateTaken=" + dateTaken +
                ", dateAdded=" + dateAdded +
                ", mimeType='" + mimeType + '\'' +
                ", flash=" + flash +
                ", maker='" + maker + '\'' +
                ", model='" + model + '\'' +
                ", whiteBalance=" + whiteBalance +
                ", locInfoCacheFlag=" + locInfoCacheFlag +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", scenic='" + scenic + '\'' +
                ", locInfoEx='" + locInfoEx + '\'' +
                ", faceInfoCacheFlag=" + faceInfoCacheFlag +
                ", faceCount=" + faceCount +
                ", selfieFlag=" + selfieFlag +
                ", url='" + url + '\'' +
                ", stringDate='" + stringDate + '\'' +
                ", objectKey='" + objectKey + '\'' +
                ", needUpload=" + needUpload +
                ", thumbPath='" + thumbPath + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }

    //获取某相册下所有的拍摄时间集合
    public static Observable<PhotoModel> getAllDateString(String bucketId) {
        return Observable.defer(new Func0<Observable<PhotoModel>>() {
            @Override
            public Observable<PhotoModel> call() {
                List<PhotoModel> result;
                if (TextUtils.isEmpty(bucketId)) {
                    result = SQLite.select().from(PhotoModel.class).groupBy(PhotoModel_Table.string_date).orderBy(PhotoModel_Table.date_taken, false).queryList();
                } else {
                    result = SQLite.select().from(PhotoModel.class).where(PhotoModel_Table.bucket_id.is(bucketId)).groupBy(PhotoModel_Table.string_date).orderBy(PhotoModel_Table.date_taken, false).queryList();
                }

                return Observable.from(result);
            }
        });
    }

    public static Observable<String> getAllPhotoId() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                List<PhotoModel> result = SQLite.select().from(PhotoModel.class).queryList();
                ArrayList<String> arrays = new ArrayList<String>();
                LogUtil.showLog("result:" + result.size());
                for (int i = 0; i < result.size(); i++) {
                    arrays.add(result.get(i).getPhotoId());
                }
                return Observable.just(JSONUtils.parse2JSONString(arrays));
            }
        });
    }

    public static PhotoModel getPhotoModel(String path) {
        return SQLite.select().from(PhotoModel.class).where(PhotoModel_Table.local_path.eq(path)).or(PhotoModel_Table.objectKey.eq(path))
                .querySingle();
    }

    public static Observable<PhotoModel> getAllBuckets() {
        return Observable.defer(new Func0<Observable<PhotoModel>>() {
            @Override
            public Observable<PhotoModel> call() {
                List<PhotoModel> result = SQLite.select().from(PhotoModel.class).groupBy(PhotoModel_Table.bucket_id).queryList();
                return Observable.from(result);
            }
        });
    }

    public static List<PhotoModel> getAllFrom(String dateString) {
        return SQLite.select().from(PhotoModel.class).where(PhotoModel_Table.string_date.is(dateString)).orderBy(PhotoModel_Table.date_taken, false).queryList();
    }

    public static List<PhotoModel> getAllFrom(String bucketId, String dateString) {
        if (TextUtils.isEmpty(bucketId)) {
            return getAllFrom(dateString);
        }
        return SQLite.select().from(PhotoModel.class).where(PhotoModel_Table.string_date.is(dateString)).and(PhotoModel_Table.bucket_id.is(bucketId)).orderBy(PhotoModel_Table.date_taken, false).queryList();
    }

    public static long getCountFrom(String bucketId) {
        if (TextUtils.isEmpty(bucketId)) {
            return SQLite.selectCountOf().from(PhotoModel.class).count();
        }
        return SQLite.selectCountOf().from(PhotoModel.class).where(PhotoModel_Table.bucket_id.is(bucketId)).count();
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (!(o instanceof PhotoModel)) {
            return false;
        }
        PhotoModel other = (PhotoModel) o;
        return !(TextUtils.isEmpty(photoId) || TextUtils.isEmpty(localPath))
                && photoId.equals(other.getPhotoId())
                && localPath.equals(other.getLocalPath());
    }

    @Override
    public int hashCode() {
        return String.valueOf(photoId).hashCode() ^ String.valueOf(localPath).hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.photoId);
        dest.writeString(this.displayName);
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketDisplayName);
        dest.writeString(this.title);
        dest.writeString(this.localPath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.miniThumbMagic);
        dest.writeInt(this.orientation);
        dest.writeLong(this.size);
        dest.writeLong(this.dateTaken);
        dest.writeLong(this.dateAdded);
        dest.writeString(this.mimeType);
        dest.writeInt(this.flash);
        dest.writeString(this.maker);
        dest.writeString(this.model);
        dest.writeInt(this.whiteBalance);
        dest.writeInt(this.locInfoCacheFlag);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.scenic);
        dest.writeString(this.locInfoEx);
        dest.writeInt(this.faceInfoCacheFlag);
        dest.writeInt(this.faceCount);
        dest.writeInt(this.selfieFlag);
        dest.writeString(this.url);
        dest.writeString(this.stringDate);
        dest.writeString(this.objectKey);
        dest.writeByte(this.needUpload ? (byte) 1 : (byte) 0);
        dest.writeString(this.thumbPath);
        dest.writeString(this.md5);
    }

    protected PhotoModel(Parcel in) {
        this.photoId = in.readString();
        this.displayName = in.readString();
        this.bucketId = in.readString();
        this.bucketDisplayName = in.readString();
        this.title = in.readString();
        this.localPath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.miniThumbMagic = in.readString();
        this.orientation = in.readInt();
        this.size = in.readLong();
        this.dateTaken = in.readLong();
        this.dateAdded = in.readLong();
        this.mimeType = in.readString();
        this.flash = in.readInt();
        this.maker = in.readString();
        this.model = in.readString();
        this.whiteBalance = in.readInt();
        this.locInfoCacheFlag = in.readInt();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.scenic = in.readString();
        this.locInfoEx = in.readString();
        this.faceInfoCacheFlag = in.readInt();
        this.faceCount = in.readInt();
        this.selfieFlag = in.readInt();
        this.url = in.readString();
        this.stringDate = in.readString();
        this.objectKey = in.readString();
        this.needUpload = in.readByte() != 0;
        this.thumbPath = in.readString();
        this.md5 = in.readString();
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel source) {
            return new PhotoModel(source);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };
}
