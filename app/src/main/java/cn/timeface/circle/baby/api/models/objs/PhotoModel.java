package cn.timeface.circle.baby.api.models.objs;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class PhotoModel {
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

    @Column(name = "thumbPath")
    String thumbPath;

    @Column(name = "md5")
    String md5;
}
