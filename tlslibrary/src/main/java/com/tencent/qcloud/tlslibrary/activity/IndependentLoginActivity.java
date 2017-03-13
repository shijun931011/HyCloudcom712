package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.imcore.IFileTrans;
import com.tencent.qcloud.tlslibrary.R;
import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.PhonePwdLoginService;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import java.util.Timer;
import java.util.TimerTask;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;

public class IndependentLoginActivity extends Activity {

    private final static String TAG = "IndependentLoginActivity";

    private TLSService tlsService;
    //private int login_way = Constants.USRPWD_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;
    private int login_way = Constants.PHONEPWD_LOGIN;
    private String countryCode;
    private String phoneNumber;

    final static int SMS_RESET_REQUEST=10005;
    final static int SMS_REG_REQUEST=10004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_independent_login"));
        Intent intent = getIntent();
        if (Constants.thirdappPackageNameSucc == null)
            Constants.thirdappPackageNameSucc = intent.getStringExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_SUCC);
        if (Constants.thirdappClassNameSucc == null)
            Constants.thirdappClassNameSucc = intent.getStringExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_SUCC);
        if (Constants.thirdappPackageNameFail == null)
            Constants.thirdappPackageNameFail = intent.getStringExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_FAIL);
        if (Constants.thirdappClassNameFail == null)
            Constants.thirdappClassNameFail = intent.getStringExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_FAIL);
        tlsService = TLSService.getInstance();
        if ((login_way & Constants.PHONEPWD_LOGIN) != 0) { // 手机号密码登录
            initPhonePwdService();
        }

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.PHONEPWD_LOGIN);
        editor.commit();
    }

//    private void initTLSLogin() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TLSUserInfo userInfo = tlsService.getLastUserInfo();
//                if (userInfo != null) {
//                    EditText editText = (EditText) IndependentLoginActivity.this
//                            .findViewById(MResource.getIdByName(getApplication(), "id", "username"));
//                    editText.setText(userInfo.identifier);
//                }
//            }
//        });
//    }

    private void initPhonePwdService() {
        tlsService.initPhonePwdLoginService(this,
                (EditText) findViewById(MResource.getIdByName(getApplication(),"id","selectCountryCode")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")));

        // 设置点击"用户注册"事件
        findViewById(MResource.getIdByName(getApplication(), "id", "registerNewUser"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(IndependentLoginActivity.this, IndependentRegisterActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
//                        startActivity(intent);
//                        finish();
                        startActivityForResult(intent,SMS_REG_REQUEST);
                    }
                });
        //点击忘记密码
        findViewById(MResource.getIdByName(getApplication(),"id","frogetpassword"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(IndependentLoginActivity.this, ForgetPasswordActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
//                        startActivity(intent);
//                        finish();
                        startActivityForResult(intent, SMS_RESET_REQUEST);
                    }
                });


    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMS_REG_REQUEST) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        }else if(requestCode == SMS_RESET_REQUEST){
            setResult(RESULT_OK,data);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        //判断是从注册界面还是重置密码界面返回
        int where = intent.getIntExtra(Constants.EXTRA_PHONEPWD_REG_RST,Constants.PHONEPWD_NON);
         countryCode = intent.getStringExtra(Constants.COUNTRY_CODE);
         phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);
        if (where == Constants.PHONEPWD_REGISTER){
            if (countryCode != null && phoneNumber != null){
                setPassword(1);     //弹出填写密码的对话框
            }
            return;
        }else if (where == Constants.PHONEPWD_RESET){
            if (countryCode !=null && phoneNumber !=null){
                setPassword(2);         //弹出填写密码的对话框
            }
            return;
        }
    }
    public void setPassword(final int type){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_dialog"),null);
        final EditText editText = (EditText) view.findViewById(MResource.getIdByName(getApplication(),"id","password"));
        Button btn_confirm = (Button) view.findViewById(MResource.getIdByName(getApplication(),"id","btn_confirm"));
        Button btn_cancel = (Button) view.findViewById(MResource.getIdByName(getApplication(),"id", "btn_cancel"));

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).setCancelable(false).create();
        dialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String regPassword = editText.getText().toString();
                if (regPassword.length() == 0){
                    Util.showToast(IndependentLoginActivity.this, "密码不能为空");
                    return;
                }
                if (type == 1){  //设置密码
                    tlsService.TLSPwdRegCommit(regPassword, new TLSPwdRegListener() {
                        @Override
                        public void OnPwdRegAskCodeSuccess(int i, int i1) {

                        }

                        @Override
                        public void OnPwdRegReaskCodeSuccess(int i, int i1) {

                        }

                        @Override
                        public void OnPwdRegVerifyCodeSuccess() {

                        }

                        @Override
                        public void OnPwdRegCommitSuccess(TLSUserInfo tlsUserInfo) {
                            Util.showToast(IndependentLoginActivity.this,"注册成功");
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryCode);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone"))).setText(phoneNumber);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "psaaword"))).setText(regPassword);
                             findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();

                        }

                        @Override
                        public void OnPwdRegFail(TLSErrInfo tlsErrInfo) {
                            Util.notOK(IndependentLoginActivity.this,tlsErrInfo);
                        }

                        @Override
                        public void OnPwdRegTimeout(TLSErrInfo tlsErrInfo) {
                            Util.notOK(IndependentLoginActivity.this,tlsErrInfo);
                        }
                    });
                }
                if (type ==2){//重置密码
                    tlsService.TLSPwdResetCommit(regPassword, new TLSPwdResetListener() {
                        @Override
                        public void OnPwdResetAskCodeSuccess(int i, int i1) {

                        }

                        @Override
                        public void OnPwdResetReaskCodeSuccess(int i, int i1) {

                        }

                        @Override
                        public void OnPwdResetVerifyCodeSuccess() {

                        }

                        @Override
                        public void OnPwdResetCommitSuccess(TLSUserInfo tlsUserInfo) {
                            Util.showToast(IndependentLoginActivity.this,"重置密码成功");
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode"))).setText(countryCode);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phone"))).setText(phoneNumber);
                            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "psaaword"))).setText(regPassword);
                            findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();
                        }

                        @Override
                        public void OnPwdResetFail(TLSErrInfo tlsErrInfo) {
                            Util.notOK(IndependentLoginActivity.this, tlsErrInfo);
                        }

                        @Override
                        public void OnPwdResetTimeout(TLSErrInfo tlsErrInfo) {
                             Util.notOK(IndependentLoginActivity.this,tlsErrInfo);
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        showSoftInput(getApplicationContext());
    }
    private static void showSoftInput(final Context ctx){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        },200);
    }
}
