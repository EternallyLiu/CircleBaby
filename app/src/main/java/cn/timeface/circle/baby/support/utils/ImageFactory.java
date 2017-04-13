package cn.timeface.circle.baby.support.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import cn.timeface.circle.baby.BuildConfig;

/**
 * @author rayboot
 * @from 14/10/28 16:14
 * @TODO Image compress factory class
 */
public class ImageFactory {
    private static ImageFactory imageFactory;
    //正在压缩的列表  保证不大于3
    final int MAX_TASK;
    final int MAX_PIXELS = 4000 * 4000;
    final int MAX_SIZE = 500;

    public ImageFactory() {
        MAX_TASK = (int) Math.round(Runtime.getRuntime().availableProcessors()
                * 1.5);
    }

    public static ImageFactory getDefault() {
        if (imageFactory == null) {
            imageFactory = new ImageFactory();
        }
        return imageFactory;
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
            FileOutputStream os = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
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
        newOpts.inSampleSize = be;//设置缩放比例
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
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            if (options <= 10) {
                break;
            }
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
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
        compressAndGenImage(compressBySize(imgPath, 800, 480), outPath, maxSize);

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

    public static String getSDPath(Context context) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        } else {
            String absolutePath = context.getCacheDir().getAbsolutePath();// 获取内置内存卡目录
            return absolutePath;
        }
        return sdDir.toString();
    }

    public static String getAppPath(Context context) {
        String appDir = "";
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            appDir = context.getCacheDir().getAbsolutePath();// 获取内置内存卡目录
        }
        return appDir;
    }

    public static Bitmap compressBySize(String pathName, int targetWidth,
                                        int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        // 得到图片的宽度、高度；
        float imgWidth = options.outWidth;
        float imgHeight = options.outHeight;

        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        options.inSampleSize = 1;

        // 如果尺寸接近则不压缩，否则进行比例压缩
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                options.inSampleSize = widthRatio;
            } else {
                options.inSampleSize = heightRatio;
            }
        }

        //设置好缩放比例后，加载图片进内容；
        options.inJustDecodeBounds = false; // 这里一定要设置false
        bitmap = BitmapFactory.decodeFile(pathName, options);
        return bitmap;
    }

    public static String saveImage(Bitmap bitmap) {

        if (bitmap == null) return null;
        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File("/mnt/sdcard/baby");
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/baby");
        if (!file.exists()) {
            file.mkdirs();
        }
        File outDir = new File(file, fileName);//将要保存图片的路径，android推荐这种写法，将目录名和文件名分开，不然容易报错。
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outDir.toString();

    }

    public static String saveImageCache(Bitmap bitmap) {

        if (bitmap == null)
            return null;

        String fileName = UUID.randomUUID().toString() + ".jpg";
//        File file = new File("/mnt/sdcard/baby");
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "baby" + File.separator + "cache");
        if (!file.exists()) {
            file.mkdirs();
        }
        File outDir = new File(file, fileName);//将要保存图片的路径，android推荐这种写法，将目录名和文件名分开，不然容易报错。
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outDir.toString();

    }

    public static String saveImage(String path, File file) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new URL(path).openStream();
            byte[] bytes = new byte[1024];
            int len;
            fos = new FileOutputStream(file);
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (Exception e) {
            }
        }
        return file.toString();

    }


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

    public static String saveVideo(String path) {
        String fileName = path.substring(path.lastIndexOf("/"));
//        File file = new File("/mnt/sdcard/baby");
        File file = new File(Environment.getExternalStorageDirectory() + "/baby");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file, fileName);
        if (file1.exists()) {
//            ToastUtil.showToast("视频已保存到baby文件夹下");
            return file1.toString();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                    int contentLength = conn.getContentLength();
                    byte[] bytes = new byte[1024];
                    int len;
                    FileOutputStream fos = new FileOutputStream(file1);
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return file1.toString();
    }

    public static String saveVideo(String path, File file) {
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            int contentLength = conn.getContentLength();
            byte[] bytes = new byte[1024];
            int len;
            FileOutputStream fos = new FileOutputStream(file);
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.toString();
    }

    //过滤图片.jpg/.jpeg/.png
    public static boolean photoFilter(String file) {
        if ((file.toLowerCase().endsWith(".jpg") || file.toLowerCase().endsWith(".jpeg") || file.toLowerCase().endsWith(".png"))
                && new File(file).exists()) {
            return true;
        }
        return false;
    }

}

