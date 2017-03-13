package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.imcore.IBatchOprCallback;
import com.tencent.qcloud.tlslibrary.activity.IndependentLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/13.
 * 处理短信注册的业务
 */
public class AccountRegisterService {

    private final static String TAG = "AccountRegisterService";

    private Context context;
    private EditText txt_countryCode;           //包含国家码的EditView的控件
    private EditText txt_phoneNumber;           //包含手机号的Editview的控件
    private EditText txt_checkCode;             //包含验证码的Editview控件
    private Button btn_requireCheckCode;        //获取验证码的按钮
    private Button btn_register;                //注册按钮
    private SmsRegListener smsRegListener;        //处理短信登陆过程中遇到的各种情况

    private TLSService tlsService;
//    private StrAccRegListener strAccRegListener;
//    private PwdRegListener pwdRegListener;

    private String countryCode;
    private String phoneNumber;
    private String checkCode;

    public AccountRegisterService(final Context context,
                               EditText txt_countryCode,
                               EditText txt_phoneNumber,
                               EditText txt_checkCode,
                               Button btn_requireCheckCode,
                               Button btn_register) {
        this.context = context;
        this.txt_countryCode = txt_countryCode;
        this.txt_phoneNumber= txt_phoneNumber;
        this.txt_checkCode= txt_checkCode;
        this.btn_requireCheckCode=btn_requireCheckCode;
        this.btn_register = btn_register;
        tlsService = TLSService.getInstance();
        this.smsRegListener = new SmsRegListener();

//        pwdRegListener = new PwdRegListener();
        this.btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryCode = AccountRegisterService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+')+1);
                phoneNumber=AccountRegisterService.this.txt_phoneNumber.getText().toString();
                checkCode=AccountRegisterService.this.txt_checkCode.getText().toString();
                //判断手机号是否有效
                if (!Util.validPhoneNumber(countryCode,phoneNumber)) {
                    Util.showToast(AccountRegisterService.this.context, "请输入有效的手机号");
                    return;
                }
                //判断验证码是否为空
                if (checkCode.length() == 0) {
                    Util.showToast(AccountRegisterService.this.context, "请求验证码");
                    return;
                }
                //向TLS验证手机号和验证码
                AccountRegisterService.this.tlsService.smsRegVerifyCode(checkCode,smsRegListener);
            }
        });

        btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryCode=AccountRegisterService.this.txt_countryCode.getText().toString();
                countryCode=countryCode.substring(countryCode.indexOf('+')+1);
                phoneNumber = AccountRegisterService.this.txt_phoneNumber.getText().toString();
                //判断手机号是否有效
                if (!Util.validPhoneNumber(countryCode,phoneNumber)){
                    Util.showToast(AccountRegisterService.this.context,"请输入有效的手机号码");
                    return;
                }
                //请求验证码
//                tlsService.TLSPwdRegAskCode(countryCode,phoneNumber,smsRegListener);
                AccountRegisterService.this.tlsService.smsRegAskCode(countryCode,phoneNumber,smsRegListener);
            }
        });
    }

    //短信注册监听器
    class SmsRegListener implements TLSSmsRegListener{
        //请求下发送短信成功
        @Override
        public void OnSmsRegAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功，验证码"+expireDuration/60+"分钟内有效");
            //在获取验证码按钮上显    示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取",reaskDuration,1);
        }
        //重新请求下发送短信成功
        @Override
        public void OnSmsRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "注册短信重新下发验证码"+ expireDuration/60+"分钟内有效");
        }
        //短信验证成功，接下来只需要用户确认操作，然后调用SmsRegCommit完成注册流程
        @Override
        public void OnSmsRegVerifyCodeSuccess() {
            tlsService.smsRegCommit(smsRegListener);
        }

        //最终注册成功，接下来可以引导用户进行短信登陆
        @Override
        public void OnSmsRegCommitSuccess(TLSUserInfo tlsUserInfo) {
            Util.showToast(context, "短信注册成功");
            Intent intent = new Intent(context, IndependentLoginActivity.class);
            intent.putExtra(Constants.EXTRA_SMS_REG, Constants.SMS_REG_SUCCESS);
            intent.putExtra(Constants.COUNTRY_CODE, countryCode);
            intent.putExtra(Constants.PHONE_NUMBER,phoneNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        //无密码注册过程中任意一步都可以到达这里
        //可以根据tlsErrInfo中ErrCode，Title， Msg给用户弹提示语，引导相关操作
        @Override
        public void OnSmsRegFail(TLSErrInfo tlsErrInfo) {
            Util.notOK(context,tlsErrInfo);
        }
        //无密码注册过程中任意一步都可以到达这里
        //网络超时，用户网络环境并不稳定，一般可以让用户重试即可
        @Override
        public void OnSmsRegTimeout(TLSErrInfo tlsErrInfo) {
            Util.notOK(context, tlsErrInfo);
        }
    }
}

