package timechat.fcgz.sj.time.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.math.BigDecimal;

/**
 * Created by user on 2017/3/14.
 */

public class DensityUtils {
    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static float getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float density_f = 8.0f; // 默认值
        if (dm != null) {
            density_f = dm.density;
        }
        return density_f;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * double转 string
     *
     * @param a
     * @return
     */
    public static String double2string(double a) {

        return Double.toString((double) a);

    }

    /**
     * string转 double
     *
     * @param a
     * @return
     */
    public static Double string2double(String a) {

        return Double.parseDouble(a);

    }

    /**
     * string转 float
     *
     * @param a
     * @return
     */
    public static Float string2float(String a) {

        return Float.parseFloat(a);

    }

    /**
     * string 转 int
     *
     * @param a
     * @return
     */
    public static Integer string2int(String a) {
        return Integer.parseInt(a);
    }

    /**
     * BigDecimal 转 double
     * @param a
     * @return
     */
    public static Double bigdecimal2double(BigDecimal a){
        return a.doubleValue();
    }

    /**
     * 字节转bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * Object类型转化为int
     *
     * @param value
     * @param defaultValue
     * @return
     * @deprecated 如需long double string 按照此方法即可
     */
    public static int convertToIn(Object value, int defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception e2) {
                return defaultValue;
            }
        }
    }

}
