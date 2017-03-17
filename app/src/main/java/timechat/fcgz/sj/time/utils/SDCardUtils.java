package timechat.fcgz.sj.time.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 2017/3/14.
 */

public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return /storage/sdcard0/
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取系统存储路径
     *
     * @return /system
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 判断文件是否已经存在
     *
     * @param path     文件路径
     * @param fileName 要检查的文件名
     * @return boolean , true表示存在，false表示不存在
     */
    public static boolean isFileExist(String path, String fileName) {
        File file = new File(path + "/" + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建目录
     *
     * @param path 文件路径
     */
    public static void getFilePath(String path) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

}
