package timechat.fcgz.sj.time.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.provider.MediaStore;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timechat.fcgz.sj.time.model.MyConstant;

/**
 * Created by user on 2017/3/8.
 */

public class ImageUtils {
    public static final int REQUEST_CODE_FROM_CAMERA = 5001;
    public static final int REQUEST_CODE_FROM_ALBUM = 5002;
    public static final int REQUEST_CODE_CROP = 5003;
    /**

     * 存放拍照图片的uri地址

     */

    private static Uri imageUriFromALBUM;

    private static Uri imageUriFromCamera;

    /**

     * 记录是处于什么状态：拍照or相册

     */

    private static int state = 0;
    /**

     * 显示获取照片不同方式对话框

     */

    public static void showImagePickDialog(final Activity activity){
        String title = "选择获取图片方式";
        String[] items = new String[]{"拍照","相册"};
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which){
                            case 0:
                                state = 1;
                                pickImageFromCamera(activity);
                                break;
                            case 1:
                                state = 2;
                                pickImageFromAlbum(activity);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();

    }
    /**

     * 打开本地相册选取图片

     */

    public static void pickImageFromAlbum(final Activity activity){

        //隐式调用，可能出现多种选择

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setType("image/*");

        activity.startActivityForResult(intent,REQUEST_CODE_FROM_ALBUM);



        /**

         Intent intent = new Intent();

         intent.setAction(Intent.ACTION_PICK);

         intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

         activity.startActivityForResult(intent,REQUEST_CODE_FROM_ALBUM);

         */

    }
    /**

     * 打开相机拍照获取图片

     */

    public static void pickImageFromCamera(final Activity activity){

        imageUriFromCamera = getImageUri();
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUriFromCamera);    //指定拍照的图片存在imageUriFromCamera下,如果直接在返回时使用getData()获取的是压缩过的Bitmap数据
        activity.startActivityForResult(intent,REQUEST_CODE_FROM_CAMERA);
        Log.d("1111", "pickImageFromCamera: " + activity);

    }

    /**

     * 根据指定目录产生一条图片Uri

     */

    private static Uri getImageUri(){

        String imageName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";

        String path = MyConstant.PhotoDir + imageName;

        return UriUtils.getUriFromFilePath(path);

    }
    /**

     * 复制一条Uri，避免因为操作而对原图片产生影响

     */

    public static void copyImageUri(Activity activity, Uri uri){
        String imageName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";
        String copyPath = MyConstant.PhotoDir + imageName;
        FileUtils.copyfile(UriUtils.getRealFilePath(activity,uri),copyPath,true);
        imageUriFromALBUM = UriUtils.getUriFromFilePath(copyPath);

    }
    /**

     * 删除一条图片Uri

     */

    public static void deleteImageUri(Context context, Uri uri){

        context.getContentResolver().delete(uri, null, null);

    }

    /**

     * 裁剪图片返回

     */

    public static void cropImageUri(Activity activity, Uri uri, int outputX, int outputY){

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, REQUEST_CODE_CROP);
    }
    /**

     * 根据状态返回图片Uri

     */

    public static Uri getCurrentUri(){

        if (state == 1){
            return imageUriFromCamera;
        }
        else if (state == 2){
            return imageUriFromALBUM;

        }
        else return null;

    }


    public static Uri getOutputFileUri(){
        return Uri.fromFile(getOutputFile());
    }
    private  static File getOutputFile(){
        File StoreageDir = null;
        try{
            StoreageDir = new File(Constant.EXTERNAL_HEADICO_DIR);
        }catch(Exception e){
            e.printStackTrace();
        }
        if (!StoreageDir.exists()) {
            if (!StoreageDir.mkdirs()) {
                return null;
            }
        }
        // 原照片
        File mediaFile;
        mediaFile = new File(StoreageDir.getPath() + File.separator
                + "userPhoto.cache");

        return mediaFile;

    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return bitmap;
    }


    /**
     * 保存图像图片
     *
     * @param bitmap
     * @param _file
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            if (file.exists()) {
                file.delete();
            }
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
//            Log.d(TAG, "Successfully created StorageDir: " + file);
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));

            getnewBitmap(bitmap).compress(Bitmap.CompressFormat.PNG, 100, os);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d("ImageUtil", "保存图片出错");
                }
            }
        }
    }

    /**
     * 缩放到固定大小
     *
     */
    private static Bitmap getnewBitmap(Bitmap bitmap) {

        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 定义预转换成的图片的宽度和高度
        int newWidth = 72;
        int newHeight = 72;

        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        // 旋转图片 动作
        // matrix.postRotate(45);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        return resizedBitmap;

    }

    public static Drawable filepathImagetoDrawable(String photopath) {

        Drawable drawable = null;
        File mPhoto = new File(photopath);
        if (mPhoto.exists()) {
            drawable = new BitmapDrawable(photopath);
        }
        return drawable;

    }




}
