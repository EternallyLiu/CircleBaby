package cn.timeface.open.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author: rayboot  Created on 16/7/18.
 * email : sy0725work@gmail.com
 */
public class Utils {

    /**
     * 图片合成
     *
     * @param picBitmap 原图
     * @param maskFile  合成的形状
     * @return 合成后的bigmap
     */
    public static Bitmap fixBitmap(Bitmap picBitmap, File maskFile) {

        if (!maskFile.exists()) {
            return picBitmap;
        }

        Bitmap maskBitmap = BitmapFactory.decodeFile(maskFile.getAbsolutePath());
        maskBitmap = Bitmap.createScaledBitmap(maskBitmap, picBitmap.getWidth(), picBitmap.getHeight(), false);

        int w = maskBitmap.getWidth();
        int h = maskBitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);

//        int edgeColor = maskBitmap.getPixel(1, 1);
//        int centerColor = maskBitmap.getPixel(w / 2, h / 2);
//        Log.d(TAG, "edgeColor = " + Integer.toHexString(edgeColor) + ", centerColor = " + Integer.toHexString(centerColor));

        //前置相片添加蒙板效果
        int[] picPixels = new int[w * h];
        int[] maskPixels = new int[w * h];
        picBitmap.getPixels(picPixels, 0, w, 0, 0, w, h);
        maskBitmap.getPixels(maskPixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < maskPixels.length; i++) {
            picPixels[i] = maskPixels[i] == 0 ? 0 : picPixels[i];
        }

        //生成前置图片添加蒙板后的bitmap:resultBitmap
        resultBitmap.setPixels(picPixels, 0, w, 0, 0, w, h);

        // TODO: 8/30/16 不能回收该bitmap,这个bitmap会重新使用,可能会导致内存问题,待观察
//        if (!picBitmap.isRecycled()) {
//            picBitmap.recycle();
//            picBitmap = null;
//        }
//
        if (!maskBitmap.isRecycled()) {
            maskBitmap.recycle();
            maskBitmap = null;
        }

        return resultBitmap;
    }

    static public void copyFileFromAssets(Context context, String file, String dest) throws Exception {
        InputStream in = null;
        OutputStream fout = null;
        int count = 0;

        try {
            in = context.getAssets().open(file);
            fout = new FileOutputStream(new File(dest));

            byte data[] = new byte[1024];
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    static public int getOrientationRotation(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }
}
