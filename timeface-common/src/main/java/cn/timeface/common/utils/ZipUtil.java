package cn.timeface.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by yusen on 2014/12/8.
 */
public class ZipUtil {
    private static final int BUFFER_SIZE = 1024;

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        try {
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    System.out.println(location + "*******************" + ze.getName().substring(ze.getName().indexOf("/")));
                    String path = location + ze.getName().substring(ze.getName().indexOf("/"));
                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.exists()) {
                            unzipFile.mkdirs();
                        }
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        String dir = path.substring(0, path.lastIndexOf("/"));
                        File dirFile = new File(dir);
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        File file = new File(path);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fout = new FileOutputStream(path, false);
                        byte buffer[] = new byte[4096];
                        int realLength;
                        try {
                            while ((realLength = zin.read(buffer)) > 0) {
                                fout.write(buffer, 0, realLength);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
