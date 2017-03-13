package timechat.fcgz.sj.time.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import timechat.fcgz.sj.time.R;
import timechat.fcgz.sj.time.ui.customview.CircleImageView;
import timechat.fcgz.sj.time.utils.ImageUtils;

public class PhotoActivity extends AppCompatActivity {
    private CircleImageView head_img;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
//        head_img=(CircleImageView) findViewById(R.id.head_me);

//        manager = getFragmentManager();
//        transaction = manager.beginTransaction();
//        transaction.add(R.id.head_me, new SettingFragment());
//        transaction.commit();

        head_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtils.showImagePickDialog(PhotoActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {
                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }
                Uri imageUri = data.getData();
                ImageUtils.copyImageUri(this,imageUri);
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;

            }
            case ImageUtils.REQUEST_CODE_FROM_CAMERA: {
                if (resultCode == RESULT_CANCELED) {     //取消操作
                    ImageUtils.deleteImageUri(this, ImageUtils.getCurrentUri());   //删除Uri
                }
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_CROP: {
                if (resultCode == RESULT_CANCELED) {     //取消操作
                    return;
                }
                Uri imageUri = ImageUtils.getCurrentUri();
                if (imageUri != null) {

                    head_img.setImageURI(imageUri);
                }
                break;
            }
            default:
                break;

        }
    }
}
