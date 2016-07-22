package cn.timeface.open.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * author: rayboot  Created on 16/7/18.
 * email : sy0725work@gmail.com
 */
public class Utils {

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

        if (!picBitmap.isRecycled()) {
            picBitmap.recycle();
            picBitmap = null;
        }

        if (!maskBitmap.isRecycled()) {
            maskBitmap.recycle();
            maskBitmap = null;
        }

        return resultBitmap;
    }
}
