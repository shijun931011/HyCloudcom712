package timechat.fcgz.sj.time.utils;

/**
 * Created by user on 2017/3/14.
 */

public class Constant {

    public static final String USER_PHOTO_NAME = "userPhoto.jpg";


    public   static final String SD_PATH = SDCardUtils.getSDCardPath();//sd卡目录
    public   static final String ANDROID_PATH = "Android/data";
    public   static final String PACKAGE_NAME = "timechat.fcgz.sj.time";//包名

    public   static final String APP_ROOT_PATH = SD_PATH +
            ANDROID_PATH + "/" + PACKAGE_NAME;



    public static final int IMAGE_REQUEST_CODE = 300; // 本地相册
    public static final int CAMERA_REQUEST_CODE = 301; // 照相机
    public static final int RESULT_REQUEST_CODE = 302; // 剪裁结果返回


    //二级目录
    public static final String EXTERNAL_LOG_DIR = APP_ROOT_PATH + "/log";
    public static final String EXTERNAL_LOG = APP_ROOT_PATH + "/catchlog";
    public static final String EXTERNAL_IMAGECACHE_DIR = APP_ROOT_PATH
            + "/imagecache";
    public static final String EXTERNAL_HEADICO_DIR = APP_ROOT_PATH
            + "/headicon";
    public static final String EXTERNAL_WEATHER = APP_ROOT_PATH + "/weather";

    public static final String PHOTO = "/user/editPhoto.do";
    public static String photo3 = "Android/data/timechat.fcgz.sj.time/headicon/userPhoto.jpg";




}
