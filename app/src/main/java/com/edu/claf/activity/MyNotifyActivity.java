package com.edu.claf.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.bean.User;

import cn.bmob.v3.BmobUser;

public class MyNotifyActivity extends AppCompatActivity {


    private long mExitTime;

    private static User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_notify);
        BaseApplication.getApplication().addActivity(this);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new PlaceholderFragment()).commit();
        }
        mUser = BmobUser.getCurrentUser(this,User.class);
    }





    public static class PlaceholderFragment extends Fragment {

        private RelativeLayout rlCommentRela;

        private RelativeLayout rlWordsRela;

        private RelativeLayout rlSystemRela;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_notify,
                    container, false);
            rlCommentRela = (RelativeLayout) rootView.findViewById(R.id.comment_rela);
            rlWordsRela  = (RelativeLayout) rootView.findViewById(R.id.words_rela);
            rlSystemRela = (RelativeLayout) rootView.findViewById(R.id.system_rela);
            rlCommentRela.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUser != null){
                        startActivity(new Intent(getActivity(), CommentsActivity.class));
                    }else{
                        Toast.makeText(getActivity(),"请登录",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rlSystemRela.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUser != null) {
                        startActivity(new Intent(getActivity(), SystemInfoActivity.class));
                    } else {
                        Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rootView;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis() - mExitTime) > 2000){
                Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                BaseApplication.getApplication().exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
