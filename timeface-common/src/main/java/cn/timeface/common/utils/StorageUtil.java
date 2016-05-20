package cn.timeface.common.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author rayboot
 * @from 14-3-13 8:54
 * @TODO 存储工具
 */
public class StorageUtil {
    public static final String TF_HIDE_ROOT = ".TimeFace";
    public static final String TF_PHOTO_ROOT = TF_HIDE_ROOT + "/photo/";
    public static final String TF_POD_ROOT = TF_HIDE_ROOT + "/pod/";
    public static final String TF_POD_PREVIEW_ROOT = TF_HIDE_ROOT + "/pod/preview/";

    /**
     * 时光流影外部缓存目录
     *
     * @return dir like /mnt/sdcard/Android/data/cn.timeface/cache
     */
    public static File getExternalCacheDir() {
        return TimeFaceUtilInit.getContext().getExternalCacheDir();
    }

    /**
     * 时光流影内部缓存目录
     *
     * @return dir like /data/cn.timeface/cache
     */
    public static File getCacheDir() {
        return TimeFaceUtilInit.getContext().getCacheDir();
    }

    /**
     * 时光流影图片缓存目录，文件夹为.TimeFace，系统不会扫描该文件夹图片
     *
     * @return dir like /mnt/sdcard/Picture/.TimeFace
     */
    public static File getTFHidePictureDir() {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TF_HIDE_ROOT);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 时光流影下载目录
     *
     * @return dir like /mnt/sdcard/Download
     */
    public static File getDownloadDir() {
        File downloadDir =
                TimeFaceUtilInit.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir == null) {
            downloadDir = TimeFaceUtilInit.getContext().getFilesDir();
        }
        return downloadDir;
    }

    // SD card 是否挂载
    public static boolean isSdCardWrittenable() {

        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    // 获取可用的存储空间
    public static long getAvailableStorage() {
        String storageDirectory =
                Environment.getExternalStorageDirectory().toString();
        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks()
                    * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    // 转换文件大小
    public static String FormatFileSize(long fileS) {
        if (fileS == 0) {
            return "0K";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除指定目录下文件及目录
     */
    public static void deleteFolderFile(File file, boolean deleteThisPath)
            throws IOException {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {// 处理目录
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolderFile(files[i], true);
            }
        }
        if (deleteThisPath) {
            if (!file.isDirectory()) {// 如果是文件，删除
                file.delete();
            } else {// 目录
                if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                    file.delete();
                }
            }
        }
    }

    /**
     * 只删除目录下文件及目录，不删除这个目录！
     */
    public static void deleteFolderFile(File file)
            throws IOException {
        if (file == null) {
            return;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        deleteFolderFile(subFile);
                    } else {
                        subFile.delete();
                    }
                }
            } else {
                file.delete();
            }
        }
    }

    public static File getSystemPhotoDir() {
        File root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        root = new File(root, "Camera");

        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    public static File genSystemPhotoFile(String fileName) {
        return new File(getSystemPhotoDir(), fileName);
    }

    public static File genSystemPhotoFile() {
        return genSystemPhotoFile(getTFPhotoName());
    }

    public static File getTFPhotoPath(String fileName) {
        File root = getTFHidePictureDir();
        File dir = new File(root, TF_PHOTO_ROOT);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("savePhoto",
                        "create folder " + dir.getPath() + " failed!");
                return null;
            }
        }
        return new File(dir, fileName);
    }

    public static File getTFPodPath(String fileName) {
        // 双SD卡的外置SD卡
        File root = getTFHidePictureDir();
        File dir = new File(root, TF_POD_ROOT);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("savePhoto",
                        "create folder " + dir.getPath() + " failed!");
                return null;
            }
        }
        return new File(dir, fileName);
    }

    public static File getTFPodPreviewPath(String fileName) {
        // 双SD卡的外置SD卡
        File root = getTFHidePictureDir();
        File dir = new File(root, TF_POD_PREVIEW_ROOT);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("savePhoto",
                        "create folder " + dir.getPath() + " failed!");
                return null;
            }
        }
        return new File(dir, fileName);
    }

    public static File getTFPodPreviewPath() {
        // 双SD卡的外置SD卡
        File root = getTFHidePictureDir();
        File dir = new File(root, TF_POD_PREVIEW_ROOT);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("savePhoto",
                        "create folder " + dir.getPath() + " failed!");
                return null;
            }
        }
        return new File(dir.getAbsolutePath());
    }

    public static File getTFPhotoPath() {
        return getTFPhotoPath(getTFPhotoName());
    }

    private static String getTFPhotoName() {
        return "TF"
                + System.currentTimeMillis()
                + new Random().nextInt(256)
                + ".jpg";
    }

    public static String savePhotoToAlbum(Bitmap bitmap) {
        File photo = genSystemPhotoFile();
        if (photo != null) {
            try {
                FileOutputStream fos = new FileOutputStream(photo);
                fos.write(bitmap2Bytes(bitmap));
                fos.close();
                return photo.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    public static String savePhoto(Bitmap bitmap) {
        File photo = getTFPhotoPath();
        if (photo != null) {
            try {
                FileOutputStream fos = new FileOutputStream(photo);
                fos.write(bitmap2Bytes(bitmap));
                fos.close();
                return photo.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
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
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static void copyAssetsFileToSD(String assetsFileName, String strOutFileName) {
        try {
            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(strOutFileName);
            myInput = TimeFaceUtilInit.getContext().getAssets().open(assetsFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return "";
        }
    }

    public static void saveRotaionImg(){

    }
}
