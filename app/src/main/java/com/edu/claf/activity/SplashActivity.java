package com.edu.claf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.MyBmobInstallation;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.push.config.Constant;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class SplashActivity extends Activity {

    LinearLayout ll_splash;

    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashpage);
        Bmob.initialize(this, "7ec374d074227dded018ddae62f584e2");
        BmobInstallation.getCurrentInstallation(this).save();
        BmobPush.startWork(this);
        user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            initInstallInfo();
        }
        SharedPrefUtils.writeInt(this, "me_2_other_notify", 0);
        SharedPrefUtils.writeInt(this, "other_2_me_notify", 0);
        SharedPrefUtils.writeString(this, "main_province", "全国");
        SharedPrefUtils.writeString(this, "main_school", "全部学校");
        SharedPrefUtils.writeString(this, "main_lost_classify", "全部");
        SharedPrefUtils.writeString(this, "main_found_classify", "全部");
        ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
        startSplash();
    }


    private void startSplash() {
        AnimationSet set = new AnimationSet(false);

        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(1000);
        rotate.setFillAfter(true);

        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(1000);
        scale.setFillAfter(true);

        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);

        set.addAnimation(rotate);
        set.addAnimation(scale);
        set.addAnimation(alpha);

        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                jumpNextPage();
            }
        });
        ll_splash.startAnimation(set);
    }

    private void jumpNextPage() {
        boolean userGuide = SharedPrefUtils.getBoolean(this, "is_user_guide_showed", false);
        if (!userGuide) {
            startActivity(new Intent(SplashActivity.this, GuidePagesActivity.class));
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("type", "splashActivity");
            startActivity(intent);
        }
        finish();
    }


    public void initInstallInfo() {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(this));
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setPhoneNumber(user.getMobilePhoneNumber());
                    mbi.update(SplashActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                        }
                    });
                } else {
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

}


