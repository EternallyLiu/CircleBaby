package cn.timeface.circle.baby.support.utils.album;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cn.timeface.circle.baby.support.utils.ImageUtil;
import cn.timeface.common.utils.StorageUtil;
import cn.timeface.common.utils.TimeFaceUtilInit;
import cn.timeface.circle.baby.R;

/**
 * 一个图片对象
 *
 * @author rayboot
 */
public class ImageItem implements Serializable, Comparable<ImageItem> {
    static final int MINI_THUMBNAIL_SIZE = 300;
    static final int MICRO_THUMBNAIL_SIZE = 96;
    private static final long serialVersionUID = 1L;
    protected int imageId;
    protected String thumbnailPath;
    protected String imagePath;
    protected Long date;
    protected String imgUrl;
    protected int orientation = 0;

    public ImageItem(int imageId, String imagePath, String thumbnailPath,
                     long date, int orientation) {
        this.imageId = imageId;
        this.thumbnailPath = thumbnailPath;
        this.imagePath = imagePath;
        this.date = date;
        this.orientation = orientation;
        switch (orientation) {
            case 90:
                this.orientation = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            case 180:
                this.orientation = ExifInterface.ORIENTATION_ROTATE_180;
                break;
            case 270:
                this.orientation = ExifInterface.ORIENTATION_ROTATE_270;
                break;
            default:
                this.orientation = orientation;
        }
        if (TextUtils.isEmpty(this.thumbnailPath)) {
            genThumbnailFile();
        }
    }

    public ImageItem(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ImageItem(File file) {
        this.imageId = -1;
        this.thumbnailPath = null;
        this.imagePath = file.getAbsolutePath();
        this.date = System.currentTimeMillis();
        this.orientation = getImageOrientation();
        genThumbnailFile();
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void genThumbnailFile() {
        File thumb = StorageUtil.getTFPhotoPath(getTFThumbnailFileName());
        try {
            ImageUtil.getDefault()
                    .storeImage(
                            getThumbnailImage(),
                            thumb.getAbsolutePath(), 90);
            this.thumbnailPath = thumb.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getThumbnailPath() {
        return TextUtils.isEmpty(thumbnailPath) ? imagePath : thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.thumbnailPath = null;
        this.orientation = getImageOrientation();
        genThumbnailFile();
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Uri getOriginalPhotoUri() {
        return Uri.fromFile(new File(imagePath));
    }

    @Override
    public int compareTo(ImageItem another) {
        return another.date.compareTo(this.date);
    }

    public String getTFCompressFileName() {
        return this.imagePath.hashCode() + ".jpg";
    }

    public String getTFThumbnailFileName() {
        return this.imagePath.hashCode() + "_mini.jpg";
    }

    public String getTFFilterFileName() {
        return this.imagePath.hashCode() + System.currentTimeMillis() + "_filter.jpg";
    }

    public File getFinalPublishImg() {
        File img = StorageUtil.getTFPhotoPath(getTFCompressFileName());
        if (!img.exists()) {
            try {
                ImageUtil.getDefault().TFAdvanceCompress(this, img);
            } catch (IOException e) {
                e.printStackTrace();
                img = new File(imagePath);
            }
        }
        return img;
    }

    public boolean isUrlImg() {
        return !TextUtils.isEmpty(imgUrl);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageItem other = (ImageItem) obj;
        return this.imagePath.equals(other.imagePath);
    }

    public int getImageOrientation() {
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        } catch (IOException ex) {
        }
        return 0;
    }

    public void setImageOrientation(String path) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
            exif.setAttribute(ExifInterface.TAG_ORIENTATION,
                    Integer.toString(this.orientation));
            exif.saveAttributes();
        } catch (IOException ex) {
        }
    }

    public Bitmap getThumbnailImage() {
        if (ContentResolver.SCHEME_CONTENT.equals(
                getOriginalPhotoUri().getScheme())) {
            return getThumbnailImageFromMediaStore(TimeFaceUtilInit.getContext());
        }

        final Resources res = TimeFaceUtilInit.getContext().getResources();
        int size = res.getBoolean(R.bool.load_mini_thumbnails)
                ? MINI_THUMBNAIL_SIZE : MICRO_THUMBNAIL_SIZE;
        if (size == MINI_THUMBNAIL_SIZE && res.getBoolean(
                R.bool.sample_mini_thumbnails)) {
            size /= 2;
        }

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                ImageUtil.getDefault().getBitmap(imagePath), size, size,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private Bitmap getThumbnailImageFromMediaStore(Context context) {
        Resources res = context.getResources();

        final int kind = res.getBoolean(R.bool.load_mini_thumbnails)
                ? MediaStore.Images.Thumbnails.MINI_KIND
                : MediaStore.Images.Thumbnails.MICRO_KIND;

        BitmapFactory.Options opts = null;
        if (kind == MediaStore.Images.Thumbnails.MINI_KIND && res.getBoolean(
                R.bool.sample_mini_thumbnails)) {
            opts = new BitmapFactory.Options();
            opts.inSampleSize = 2;
        }

        try {
            final long id =
                    Long.parseLong(getOriginalPhotoUri().getLastPathSegment());

            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    context.getContentResolver(), id, kind, opts);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
