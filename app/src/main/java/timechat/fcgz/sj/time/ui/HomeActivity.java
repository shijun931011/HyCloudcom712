package timechat.fcgz.sj.time.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.tencent.TIMManager;
import com.tencent.TIMUserStatusListener;
import com.tencent.qcloud.presentation.event.MessageEvent;
import timechat.fcgz.sj.time.R;
import timechat.fcgz.sj.time.model.FriendshipInfo;
import timechat.fcgz.sj.time.model.UserInfo;
import timechat.fcgz.sj.time.ui.customview.CircleImageView;
import timechat.fcgz.sj.time.ui.customview.DialogActivity;
import timechat.fcgz.sj.time.ui.customview.NotifyDialog;
import timechat.fcgz.sj.time.utils.ImageUtils;

import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import java.io.File;

import static timechat.fcgz.sj.time.MyApplication.getContext;

/**
 * Tab页主界面
 */
public class HomeActivity extends FragmentActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {ConversationFragment.class, ContactFragment.class, SettingFragment.class};
    private int mTitleArray[] = {R.string.home_conversation_tab, R.string.home_contact_tab, R.string.home_setting_tab};
    private int mImageViewArray[] = {R.drawable.tab_conversation, R.drawable.tab_contact, R.drawable.tab_setting};
    private String mTextviewArray[] = {"contact", "conversation", "setting"};
    private ImageView msgUnread;
    private CircleImageView head_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        //互踢下线逻辑
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");
                Intent intent = new Intent(HomeActivity.this, DialogActivity.class);
                startActivity(intent);
            }
            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
            }
        });
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);
        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }
//        head_img = (CircleImageView) findViewById(R.id.head_me);
//        head_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImageUtils.showImagePickDialog(HomeActivity.this);
//            }
//        });
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.home_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mImageViewArray[index]);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mTitleArray[index]);
        if (index == 0){
            msgUnread = (ImageView) view.findViewById(R.id.tabUnread);
        }
        return view;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("1111", "onActivityResult: " + data + "    " + resultCode);
        super.onActivityResult(requestCode,resultCode,data);
        //防止空指针异常
        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {
                if (resultCode == RESULT_CANCELED) {   //取消操作
                    Log.d("shijun","111");
                    return;
                }
                Uri imageUri = data.getData();
                ImageUtils.copyImageUri(this,imageUri);
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_FROM_CAMERA: {
                if (resultCode == RESULT_CANCELED) {     //取消操作
                    Log.d("shijun","222");
                    ImageUtils.deleteImageUri(getContext(), ImageUtils.getCurrentUri());   //删除Uri
                }
                Log.d("shijun","333");
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case ImageUtils.REQUEST_CODE_CROP: {
                if (resultCode == RESULT_CANCELED) {     //取消操作
                    Log.d("shijun","444");
                    return;
                }
                Uri imageUri = ImageUtils.getCurrentUri();
                Log.d("shijun","imageUri"+imageUri);
                if (imageUri != null) {
                    Log.d("shijun","555");
                    head_img.setImageURI(imageUri);
                }
                break;
            }
            default:
                Log.d("shijun", "999");
                break;
        }
    }

    public void logout(){
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
//        GroupInfo.getInstance().clear();
        Intent intent = new Intent(HomeActivity.this,SplashActivity.class);
        finish();
        startActivity(intent);
    }


    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(boolean noUnread){
        msgUnread.setVisibility(noUnread?View.GONE:View.VISIBLE);
    }

//    public CircleImageView getTiltes(){
//       return head_img;
//    }
}
