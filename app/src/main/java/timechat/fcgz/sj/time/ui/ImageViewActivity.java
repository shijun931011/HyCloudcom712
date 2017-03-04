package timechat.fcgz.sj.time.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import timechat.fcgz.sj.time.utils.FileUtil;

import java.io.IOException;

public class ImageViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(timechat.fcgz.sj.time.R.layout.activity_image_view);
        RelativeLayout root = (RelativeLayout) findViewById(timechat.fcgz.sj.time.R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String file = getIntent().getStringExtra("filename");
        ImageView imageView = (ImageView) findViewById(timechat.fcgz.sj.time.R.id.image);
        imageView.setImageBitmap(getImage(FileUtil.getCacheFilePath(file)));
    }

    private Bitmap getImage(String path){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width=options.outWidth, height=options.outHeight;
        if (width > height){
            reqWidth = getWindowManager().getDefaultDisplay().getWidth();
            reqHeight = (reqWidth * height)/width;
        }else{
            reqHeight = getWindowManager().getDefaultDisplay().getHeight();
            reqWidth = (width * reqHeight)/height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try{
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface ei =  new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        }catch (IOException e){
            return null;
        }
    }
}
