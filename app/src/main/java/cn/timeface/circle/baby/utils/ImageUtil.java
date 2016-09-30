package cn.timeface.circle.baby.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
//import com.lightbox.android.photoprocessing.PhotoProcessing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.utils.album.ImageItem;
import cn.timeface.common.utils.StorageUtil;

/**
 * @author rayboot
 * @from 14/10/28 16:14
 * @TODO Image compress factory class
 */
public class ImageUtil {
    private static ImageUtil imageUtil;
    //正在压缩的列表  保证不大于3
    final int MAX_TASK;
    final int MAX_PIXELS = 4000 * 4000;
    final int MAX_SIZE = 500;
    private List<ImageItem> compressList = new ArrayList<ImageItem>(10);
    //需要压缩的列表
    private List<ImageItem> compressingList = new ArrayList<ImageItem>(10);

    public ImageUtil() {
        MAX_TASK = (int) Math.round(Runtime.getRuntime().availableProcessors()
                * 1.5);
    }

    public static ImageUtil getDefault() {
        if (imageUtil == null) {
            imageUtil = new ImageUtil();
        }
        return imageUtil;
    }

    /**
     * Get bitmap from specified image path
     */
    public Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    public Bitmap getBitmap(String imgPath, int sample) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = sample;
        newOpts.inPreferredConfig = Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    public Point getImgSize(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options); // 此时返回的bitmap为null
        return new Point(options.outWidth, options.outHeight);
    }

    /**
     * Store bitmap into specified image path
     *
     * @throws FileNotFoundException
     */
    public void storeImage(Bitmap bitmap, String outPath)
            throws FileNotFoundException {
        storeImage(bitmap, outPath, 90);
    }

    /**
     * Store bitmap into specified image path
     *
     * @throws FileNotFoundException
     */
    public void storeImage(Bitmap bitmap, String outPath, int quality)
            throws FileNotFoundException {
        if (bitmap != null) {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(outPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
                os.close();
            } catch (IOException e) {
                Log.e("DEBUG", "ignore this error.", e);
            } finally {
                IOUtils.safeClose(os);
            }
        }
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     */
    public Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            //保证铺满屏幕
            if (h > hh) {
                be = (int) Math.floor(((float) newOpts.outHeight / hh) + 0.5f);
            } else {
                be = (int) Math.floor(((float) newOpts.outWidth / ww) + 0.5f);
            }
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放

            //保证铺满屏幕
            if (w > ww) {
                be = (int) Math.floor(((float) newOpts.outWidth / ww) + 0.5f);
            } else {
                be = (int) Math.floor(((float) newOpts.outHeight / hh) + 0.5f);
            }
        }

        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        //        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     */
    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50,
                    os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例 2的N次方
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
        //	    return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param maxSize target will be compressed to be smaller than this
     *                size.(kb)
     * @throws IOException
     */
    public void compressAndGenImage(Bitmap image, String outPath, int maxSize)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 90;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            if (options <= 60) {
                break;
            }
            // Clean up os
            os.reset();
            // interval 10
            options -= 5;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param maxSize     target will be compressed to be smaller than this
     *                    size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public void compressAndGenImage(String imgPath, String outPath, int maxSize,
                                    boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @throws FileNotFoundException
     */
    public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW,
                                 float pixelH) throws FileNotFoundException {
        Bitmap bitmap = ratio(image, pixelW, pixelH);
        storeImage(bitmap, outPath);
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param pixelW      target pixel of width
     * @param pixelH      target pixel of height
     * @param needsDelete Whether delete original file after compress
     * @throws FileNotFoundException
     */
    public void ratioAndGenThumb(String imgPath, String outPath, float pixelW,
                                 float pixelH, boolean needsDelete) throws FileNotFoundException {
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        storeImage(bitmap, outPath);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void TFSampleGenThumb(String imgPath, String outPath)
            throws IOException {
        ratioAndCompressAndGenThumb(imgPath, outPath, (int) Math.sqrt(MAX_PIXELS), (int) Math.sqrt(MAX_PIXELS), MAX_SIZE, false);
    }

    public void ratioAndCompressAndGenThumb(String imgPath, String outPath,
                                            float pixelW, float pixelH, int maxSize, boolean needsDelete)
            throws IOException {
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        compressAndGenImage(bitmap, outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public int getMaxPixels(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imgPath, options);
        return options.outWidth * options.outHeight;
    }

    //使用JNI来压缩处理图片尺寸，速度比较慢，但是压缩尺寸精准
    public void TFAdvanceCompress(ImageItem item, File outFile)
            throws IOException {
        if (getMaxPixels(item.getImagePath()) <= MAX_PIXELS) {
            //如果本身尺寸小于最大尺寸，则进行图片质量压缩
            compressAndGenImage(item.getImagePath(), outFile.getAbsolutePath(), MAX_SIZE, false);
            //StorageUtil.copyFile(item.imagePath, outFile.getAbsolutePath());
        } else {
            //如果本身尺寸大于最大尺寸，则先使用jni对尺寸压缩，然后再对质量压缩
//            int resultCode = PhotoProcessing.nativeLoadResizedBitmap(item.getImagePath(), MAX_PIXELS);
//            if (resultCode == PhotoProcessing.RESULT_OK) {
//                Bitmap resultBitmap = PhotoProcessing.getBitmapFromNative(null);
//                compressAndGenImage(resultBitmap, outFile.getAbsolutePath(), MAX_SIZE);
//            }

        }
        item.setImageOrientation(outFile.getAbsolutePath());
    }

    //使用jni来压缩图片尺寸，比较毫时间
    public void TFPost2AdvanceCompressTask(ImageItem imageItem) {
        if (this.compressingList.size() < MAX_TASK) {
            this.compressingList.add(imageItem);
            new TFCompressTask().execute(imageItem);
        } else {
            this.compressList.add(imageItem);
        }
    }

    public Bitmap getBrightnessBitmap(Bitmap srcBitmap, int brightness) {
        if (srcBitmap.isRecycled()) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(),
                Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        return bmp;
    }

    public int getImageOrientation(String imgPath) {
        int result = ExifInterface.ORIENTATION_NORMAL;
        ExifInterface exif;
        try {
            exif = new ExifInterface(imgPath);
            String rotate = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            result = Integer.parseInt(rotate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    class TFCompressTask extends AsyncTask<ImageItem, Void, ImageItem> {
        @Override
        protected ImageItem doInBackground(ImageItem... params) {
            ImageItem item = params[0];
            File outFile =
                    StorageUtil.getTFPhotoPath(item.getTFCompressFileName());
            if (!outFile.exists()) {
                try {
                    TFAdvanceCompress(item, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return item;
        }

        @Override
        protected void onPostExecute(ImageItem imageItem) {
            super.onPostExecute(imageItem);
            for (ImageItem item : compressingList) {
                if (imageItem.equals(item)) {
                    compressingList.remove(item);
                    break;
                }
            }
            synchronized (this) {
                if (compressList.size() > 0
                        && compressingList.size() < MAX_TASK) {
                    ImageItem item = compressList.get(0);
                    compressingList.add(item);
                    new TFCompressTask().execute(item);
                    compressList.remove(item);
                }
            }
        }
    }

    // And to convert the image URI to the direct file system path of the image
    // file
    public static String getPathFromContentUri(ContentResolver cr, Uri contentUri) {
        if (BuildConfig.DEBUG) {
            Log.d("Utils", "Getting file path for Uri: " + contentUri);
        }

        String returnValue = null;

        if (ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())) {
            // can post image
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = cr.query(contentUri, proj, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    returnValue = cursor
                            .getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(contentUri.getScheme())) {
            returnValue = contentUri.getPath();
        }

        return returnValue;
    }

    public static Bitmap decodeImage(final ContentResolver resolver, final Uri uri,
                                     final int MAX_DIM)
            throws FileNotFoundException {

        // Get original dimensions
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(resolver.openInputStream(uri), null, o);
        } catch (SecurityException se) {
            se.printStackTrace();
            return null;
        }

        final int origWidth = o.outWidth;
        final int origHeight = o.outHeight;

        // Holds returned bitmap
        Bitmap bitmap;

        o.inJustDecodeBounds = false;
        o.inScaled = false;
        o.inPurgeable = true;
        o.inInputShareable = true;
        o.inDither = true;
        o.inPreferredConfig = Config.RGB_565;

        if (origWidth > MAX_DIM || origHeight > MAX_DIM) {
            int k = 1;
            int tmpHeight = origHeight, tmpWidth = origWidth;
            while ((tmpWidth / 2) >= MAX_DIM || (tmpHeight / 2) >= MAX_DIM) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                k *= 2;
            }
            o.inSampleSize = k;

            bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, o);
        } else {
            bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, o);
        }

        if (null != bitmap) {
            if (BuildConfig.DEBUG) {
                Log.d("Utils",
                        "Resized bitmap to: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            }
        }

        return bitmap;
    }

    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    public static Bitmap rotate(Bitmap original, final int angle) {
        if ((angle % 360) == 0) {
            return original;
        }

        final boolean dimensionsChanged = angle == 90 || angle == 270;
        final int oldWidth = original.getWidth();
        final int oldHeight = original.getHeight();
        final int newWidth = dimensionsChanged ? oldHeight : oldWidth;
        final int newHeight = dimensionsChanged ? oldWidth : oldHeight;

        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, original.getConfig());
        Canvas canvas = new Canvas(bitmap);

        Matrix matrix = new Matrix();
        matrix.preTranslate((newWidth - oldWidth) / 2f, (newHeight - oldHeight) / 2f);
        matrix.postRotate(angle, bitmap.getWidth() / 2f, bitmap.getHeight() / 2);
        canvas.drawBitmap(original, matrix, null);

        original.recycle();

        return bitmap;
    }

    public static void scanMediaJpegFile(final Context context, final File file,
                                         final MediaScannerConnection.OnScanCompletedListener listener) {
        MediaScannerConnection
                .scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/jpg"},
                        listener);
    }

    public static void scanMediaVideoFile(final Context context, final File file,
                                         final MediaScannerConnection.OnScanCompletedListener listener) {
        MediaScannerConnection
                .scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"video/mp4"},
                        listener);
    }

    public static File getCameraPhotoFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "tf_" + System.currentTimeMillis() + ".jpg");
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}

