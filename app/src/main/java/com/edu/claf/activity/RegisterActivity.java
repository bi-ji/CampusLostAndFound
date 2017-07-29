package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.RegexUtils;
import com.edu.claf.bean.User;
import com.edu.claf.timer.MyCountTimer;
import com.edu.claf.view.ProgressView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_verify_code)
    EditText etVerifyCode;
    @InjectView(R.id.btn_send)
    Button btnSendVertifyCode;
    @InjectView(R.id.btn_register_step_one)
    Button btnRegisterNext;
    @InjectView(R.id.iv_register_page_one_back)
    ImageButton ivRegisterBack;


    private MyCountTimer timer;
    private String phoneNumber;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }

    @OnClick(R.id.btn_send)
    public void sendVertifyCode(View view) {
        phoneNumber = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber) && RegexUtils.isPhoneNumber(phoneNumber)) {
            timer = new MyCountTimer(60000, 1000, btnSendVertifyCode);
            timer.start();
            BmobSMS.requestSMSCode(this, phoneNumber, "smsVertify", new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (TextUtils.isEmpty(phoneNumber) || !RegexUtils.isPhoneNumber(phoneNumber)) {
            Toast.makeText(RegisterActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.btn_register_step_one)
    public void register(View view) {
        String vertifyCode = etVerifyCode.getText().toString().trim();
        final ProgressView progress = new ProgressView(RegisterActivity.this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobSMS.verifySmsCode(this, phoneNumber, vertifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    progress.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, RegisterStepTwoActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                } else {
                    progress.dismiss();
                    Toast.makeText(RegisterActivity.this, "验证码错误，请重新获取", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @OnClick(R.id.iv_register_page_one_back)
    public void registerBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
