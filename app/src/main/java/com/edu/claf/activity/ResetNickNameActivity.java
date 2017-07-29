package com.edu.claf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class ResetNickNameActivity extends AppCompatActivity {

    @InjectView(R.id.et_nick_name)
    EditText etResetNickName;
    @InjectView(R.id.btn_reset_nick_name)
    Button btnResetNickName;
    @InjectView(R.id.iv_reset_nick_name_back)
    ImageButton ivResetNickNameBack;
    private String mNewNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_nick_name);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        String nickname = getIntent().getStringExtra("nickname");
        etResetNickName.setText(nickname);
    }

    @OnClick(R.id.btn_reset_nick_name)
    public void resetNickName(View view){
        mNewNickName = etResetNickName.getText().toString().trim();
        if (TextUtils.isEmpty(mNewNickName)){
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_SHORT).show();
        }else{
            final ProgressView progressView = new ProgressView(this);
            ProgressView.getProgressDialog(this, "重置中。。。");
            User user = BmobUser.getCurrentUser(this,User.class);
            user.setNickName(mNewNickName);
            user.update(this, user.getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ResetNickNameActivity.this,"昵称重置成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("newNickName", mNewNickName);
                    setResult(RESULT_OK, intent);
                    ResetNickNameActivity.this.finish();
                    ProgressView.progressDialogDismiss();
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(ResetNickNameActivity.this,"昵称重置失败"+s,Toast.LENGTH_SHORT).show();
                    ProgressView.progressDialogDismiss();
                }
            });
        }

    }

    @OnClick(R.id.iv_reset_nick_name_back)
    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
