package com.edu.claf.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.view.ProgressView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.update.BmobUpdateAgent;

public class SystemUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_system_update);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_update)
    public void updateApp(View view){
        ProgressView.getProgressDialog(this, "正在检查更新");
        ProgressView.progressDialogDismiss();

    }

    public void backPersonCentral(View view){
        onBackPressed();
    }
}
