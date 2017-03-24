package timechat.fcgz.sj.time.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.qcloud.tlslibrary.activity.ForgetPasswordActivity;
import com.tencent.qcloud.tlslibrary.activity.IndependentLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import java.util.Timer;
import java.util.TimerTask;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;
import timechat.fcgz.sj.time.R;

public class ModifyPwdActivity extends Activity {
    private TLSService tlsService;
    private SmsContentObserver smsContentObserver=null;
    private String countryCode;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        tlsService = TLSService.getInstance();
        tlsService.initResetPhonePwdService(this,
                (EditText) findViewById(MResource.getIdByName(getApplication(),"id","selectCountryCode")),
                (EditText) findViewById(MResource.getIdByName(getApplication(),"id","phone")),
                (EditText) findViewById(MResource.getIdByName(getApplication(),"id","txt_checkcode")),
                (Button) findViewById(MResource.getIdByName(getApplication(),"id","btn_requirecheckcode")),
                (Button) findViewById(MResource.getIdByName(getApplication(),"id","btn_verify"))
        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        int where = intent.getIntExtra(Constants.EXTRA_PHONEPWD_REG_RST,Constants.PHONEPWD_NON);
        countryCode = intent.getStringExtra(Constants.COUNTRY_CODE);
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);
        if(where == Constants.PHONEPWD_RESET){
            if (countryCode !=null && phoneNumber !=null){
                setPassword();         //弹出填写密码的对话框
            }
            return;
        }
    }
    public void setPassword(){
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
                    Util.showToast(ModifyPwdActivity.this, "密码不能为空");
                    return;
                }
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
                        Util.showToast(ModifyPwdActivity.this,"重置密码成功");

                    }

                    @Override
                    public void OnPwdResetFail(TLSErrInfo tlsErrInfo) {
                        Util.notOK(ModifyPwdActivity.this, tlsErrInfo);

                    }

                    @Override
                    public void OnPwdResetTimeout(TLSErrInfo tlsErrInfo) {
                        Util.notOK(ModifyPwdActivity.this,tlsErrInfo);

                    }
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null){
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
