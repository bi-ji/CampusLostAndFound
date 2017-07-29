package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class ResetPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.btn_reset_password)
    Button btnReset;
    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.et_new_password)
    EditText etNewPassword;
    @InjectView(R.id.et_verify_code)
    EditText etVertifyCode;
    @InjectView(R.id.ib_reset_password_back)
    ImageButton ibBack;
    @InjectView(R.id.btn_get_vertify_code)
    Button btnGetVertifyCode;

    private MyCountTimer timer;
    private User user;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        String type = getIntent().getStringExtra("type");
        user = BmobUser.getCurrentUser(this, User.class);
        if (user != null && type.equals("accountSettingReset")) {
            mPhoneNumber = user.getMobilePhoneNumber();
            etPhone.setText(mPhoneNumber);
            etPhone.setEnabled(false);
        } else if (type.equals("loginReset")){
            Toast.makeText(this, "重置密码", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_reset_password)
    public void resetPassword(View view) {
        final String newPassword = etNewPassword.getText().toString().trim();
        String vertifyCode = etVertifyCode.getText().toString().trim();
        final ProgressView progressView = new ProgressView(this);
        ProgressView.getProgressDialog(this, "正在重置密码。。。");
        BmobSMS.verifySmsCode(this, mPhoneNumber, vertifyCode, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    setNewPassWord(user,newPassword);
                    ProgressView.progressDialogDismiss();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "验证码错误，请重新获取", Toast.LENGTH_SHORT).show();
                    ProgressView.progressDialogDismiss();
                }
            }
        });
    }

    private void setNewPassWord(User user, String newPassword) {
        user.setPassword(newPassword);
        user.update(this,user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ResetPasswordActivity.this,"更改成功，请重新登录",Toast.LENGTH_LONG).show();
                BmobUser.logOut(ResetPasswordActivity.this);
                BmobUser currentUser = BmobUser.getCurrentUser(ResetPasswordActivity.this);
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                ResetPasswordActivity.this.finish();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @OnClick(R.id.btn_get_vertify_code)
    public void getVertifyCode(View view) {
        if (user != null) {
            etPhone.setText(user.getMobilePhoneNumber());
            String newPassword = etNewPassword.getText().toString().trim();
            if (RegexUtils.passwordLength(newPassword)) {
                timer = new MyCountTimer(60000, 1000, btnGetVertifyCode);
                timer.start();
                BmobSMS.requestSMSCode(this, user.getMobilePhoneNumber(), "smsVertify", new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            Toast.makeText(ResetPasswordActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "密码长度不符合要求，请重新填写", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ib_reset_password_back)
    public void backAccountSetting(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer = null;
        }
    }
}
