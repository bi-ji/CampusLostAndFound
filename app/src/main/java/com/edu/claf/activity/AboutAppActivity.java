package com.edu.claf.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about_app);
        BaseApplication.getApplication().addActivity(this);
    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }
}
