package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.imcore.IFileTrans;
import com.tencent.qcloud.tlslibrary.R;
import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.service.TLSService;

public class ForgetPasswordActivity extends Activity {
    private TLSService tlsService;
    private SmsContentObserver smsContentObserver=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //设置返回按钮
        findViewById(MResource.getIdByName(getApplication(), "id", "btn_back")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetPasswordActivity.this.onBackPressed();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null){
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
