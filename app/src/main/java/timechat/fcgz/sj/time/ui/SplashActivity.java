package timechat.fcgz.sj.time.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMLogLevel;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import timechat.fcgz.sj.time.model.UserInfo;
import timechat.fcgz.sj.time.ui.customview.NotifyDialog;
import timechat.fcgz.sj.time.utils.PushUtil;
//import com.tencent.qcloud.tlslibrary.activity.HostLoginActivity;
import com.tencent.qcloud.tlslibrary.activity.IndependentLoginActivity;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends FragmentActivity implements SplashView,TIMCallBack{

    SplashPresenter presenter;
    private int LOGIN_RESULT_CODE = 100;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(timechat.fcgz.sj.time.R.layout.activity_splash);
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0){
                init();
            }else{
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }else{
            init();
        }

    }


    /**
     * 跳转到主界面
     */
    @Override
    public void navToHome() {
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);
    }

    /**
     * 跳转到登录界面
     */
    @Override
    public void navToLogin() {
//        Intent intent = new Intent(getApplicationContext(), HostLoginActivity.class);
        Intent intent = new Intent(getApplicationContext(), IndependentLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    /**
     * 是否已有用户登录
     */
    @Override
    public boolean isUserLogin() {
        return UserInfo.getInstance().getId()!= null && (!TLSService.getInstance().needLogin(UserInfo.getInstance().getId()));
    }

    /**
     * imsdk登录失败后回调
     */
    @Override
    public void onError(int i, String s) {
        Log.e(TAG, "login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(timechat.fcgz.sj.time.R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            default:
                Toast.makeText(this,getString(timechat.fcgz.sj.time.R.string.login_error),Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }

    }

    /**
     * imsdk登录成功后回调
     */
    @Override
    public void onSuccess() {
        Log.i(TAG, "login succeed");
        //初始化程序后台后消息推送
        PushUtil.getInstance();
        //初始化消息监听
        MessageEvent.getInstance();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOGIN_RESULT_CODE == requestCode) {
            if (0 == TLSService.getInstance().getLastErrno()){
                String id = TLSService.getInstance().getLastUserIdentifier();
                UserInfo.getInstance().setId(id);
                UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
                finish();
            } else if (resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(timechat.fcgz.sj.time.R.string.need_permission),Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void init(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(),loglvl);
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        //设置刷新监听
        RefreshEvent.getInstance();
        String id =  TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
        presenter = new SplashPresenter(this);
        presenter.start();
    }

}
