package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.bean.User;

import cn.bmob.v3.BmobUser;

public class PublishInfoActivity extends AppCompatActivity {

    private PlaceholderFragment fragment;
    private long mExitTime;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_publish_info);
        BaseApplication.getApplication().addActivity(this);
        fragment = new PlaceholderFragment();
        mUser = BmobUser.getCurrentUser(this, User.class);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_publish,
                    container, false);
            return rootView;
        }

    }

    public void publishLostInfo(View view) {
        if (mUser != null) {
            startActivity(new Intent(PublishInfoActivity.this, PublishLostInfoActivity.class));
            fragment.onDestroy();
        }else{
            Toast.makeText(PublishInfoActivity.this,"请登录",Toast.LENGTH_SHORT).show();
        }
    }

    public void publishFoundInfo(View view) {
        if (mUser != null) {
            startActivity(new Intent(PublishInfoActivity.this, PublishFoundInfoActivity.class));
            fragment.onDestroy();
        }else{
            Toast.makeText(PublishInfoActivity.this,"请登录",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                BaseApplication.getApplication().exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
