package timechat.fcgz.sj.time.ui.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;



public class FileHelper {

    // 1、 建立文件夹
    // 2、 建立.txt
    // 3、 删除.txt
    // 4、 写入.txt
    // 5、 读取.txt
    // 6、 获取路径.txt数目

    private Context mContext;

    private String SDPATH;// SD卡路径
    private String FILESPATH;// 文件路径

    public FileHelper(Context context) {
        this.mContext = context;
        SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
        FILESPATH = this.mContext.getFilesDir().getPath() + "//";
    }

    /**
     * 判断SDCard是否存在？是否可以进行读写
     */
    public boolean isSDCardExit() {
        // 表示SDCard存在并且可以读写
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SDCard文件路径
     */
    public String getSDCardPath() {
        // 如果SDCard存在并且可以读写
        if (isSDCardExit()) {
            SDPATH = Environment.getExternalStorageDirectory().getPath();
            return SDPATH;
        } else {
            return null;
        }
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName 要创建的目录名
     * @return 创建得到的目录
     */
    public File createSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * 删除SD卡上的目录
     *
     * @param dirName
     */
    public boolean delSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        return delDir(dir);
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (isFileExist(fileName)) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 判断文件是否已经存在
     *
     * @param fileName 要检查的文件名
     * @return boolean , true表示存在，false表示不存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 删除SD卡上的文件
     *
     * @param fileName
     */
    public boolean delSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (file == null || !file.exists() || file.isDirectory())
            return false;
        file.delete();
        return true;
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param dir
     */
    public boolean delDir(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 从SD卡读取文件。如:readSDFile("test.txt");
     */
    public Input readSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        FileInputStream fis = new FileInputStream(file);
        return new Input(fis);
    }

    /**
     * 将文件写入sd卡。如:writeSDFile("test.txt");
     */
    public Output writeSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        return new Output(fos);
    }

    /**
     * 获取指定目录下的文件数
     */
    public int filesNumber(String cataLogName) {
        File file = new File(SDPATH + cataLogName);
        int size = file.listFiles().length;
        return size;
    }

    /**
     * 返回指定目录下的文件
     */
    public String[] fileList(String path) {
        File file = new File(SDPATH + path);
        return file.list();
    }

    /**
     * 返回指定目录下的文件对象
     */
    public File[] listFiles(String path) {
        File file = new File(SDPATH + path);
        return file.listFiles();
    }

    // 读取临时的配置文件
    public static byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    /**
     * @return
     */
//    public boolean saveInfo() {
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date(System.currentTimeMillis());
//        String mDate = df.format(date);
//
//        String username = (String) SharedPreferenceUtils.get(mContext,
//                "username", "");
//
//        String IMEI = TravelApplication.getIMEI();
//
//        String province = LocationService.getInstance().getmProvince();
//        String city = LocationService.getInstance().getmCity();
//        String county = LocationService.getInstance().getmDistrict();
//
//        String lat = LocationService.getInstance().getmLatitude() + "";
//
//        String lng = LocationService.getInstance().getmLongitude() + "";
//
//        String[] str = {"user", "IMEI", "date", "province", "city",
//                "county", "lat", "lng"};
//        String[] data = {username, IMEI, mDate, province, city, county, lat,
//                lng};
//
//        String stringtitle = "<root>";
//        String stringtitleend = "</root>";
//        String stringlog = "<log ";
//        String stringloginfo = null;
//
//        String fileName = "usinfo-" + mDate + ".cache";
//        String path = Constant.EXTERNAL_LOG_DIR;
//        String file_path = Constant.EXTERNAL_LOG_DIR + "/" + fileName;
//
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            File dir = new File(path);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            File file_dir = new File(file_path);
//            if(!file_dir.exists()) {
//                stringloginfo = stringtitle + stringlog;
//            }else{
//                stringloginfo = stringlog;
//            }
//        }
//
//        for (int i = 0; i < str.length; i++) {
//            stringloginfo += str[i] + "=\"" + data[i] + "\" ";
//        }
//        stringloginfo += "/>";
//
//        Logg.d("www", stringloginfo);
//
//        boolean issavesuccess = FileUtil.appendcontent_raf(
//                file_path, stringloginfo);
//
//        return issavesuccess;
//    }

}
