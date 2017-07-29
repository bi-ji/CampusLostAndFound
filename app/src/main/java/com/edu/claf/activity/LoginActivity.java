package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.RegexUtils;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.et_login_phone_number)
    EditText etLoginPhoneNumber;

    @InjectView(R.id.et_login_password)
    EditText etLoginPhonePassword;

    @InjectView(R.id.tv_new_user)
    TextView tvNewUser;
    @InjectView(R.id.tv_login_forget_password)
    TextView tvForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.btn_login_app)
    public void login(View view) {
        ProgressView.getProgressDialog(LoginActivity.this,"正在登录");
        String phoneNumber = etLoginPhoneNumber.getText().toString().trim();
        String password = etLoginPhonePassword.getText().toString().trim();
        if (!RegexUtils.isPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "您输入的电话号码有误！", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobUser.loginByAccount(this, phoneNumber, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    saveUserToJson(user);
                    downloadUserPhoto(user);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("type","login");
                    startActivity(intent);
                    LoginActivity.this.finish();
                    ProgressView.progressDialogDismiss();
                } else {
                    ProgressView.progressDialogDismiss();
                    Toast.makeText(LoginActivity.this, "登录信息有误，请确认后登录", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void downloadUserPhoto(User user) {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(this, user.getObjectId(), new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                String photoUrl = user.getPhotoUrl();
                BmobFile photo = new BmobFile(user.getMobilePhoneNumber() + ".jpg", null, photoUrl);
                downloadFile(photo, user);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }


    private void downloadFile(BmobFile file,User user) {
        File saveFile = new File(FileUtils.getIconDir(user.getMobilePhoneNumber()), file.getFilename());
        if (saveFile.exists()){
            return;
        }
        file.download(this, saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(String savePath) {
                System.out.println("login___________" + "下载成功");
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }

    @OnClick(R.id.tv_new_user)
    public void newUserRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @OnClick(R.id.tv_login_forget_password)
    public void loginForgetPassword(View view){
        Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
        intent.putExtra("type","loginReset");
        startActivity(intent);
    }

    public void saveUserToJson(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        File userDir = FileUtils.getUserDir(user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        File file = new File(FileUtils.getCacheDir(user), user.getMobilePhoneNumber() + ".json");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(userJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


}

