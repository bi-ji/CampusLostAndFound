package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.bean.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class ChooseLocationActivity extends AppCompatActivity {

    private static final int REQUEST_PROVINCE = 1;
    private static final int REQUEST_SCHOOL = 2;
    private static final int RESULT_MAIN_PROVINCE = 112;
    private static final int RESULT_MAIN_SCHOOL = 113;
    private static final int RESULT_MAIN_ALL = 114;
    @InjectView(R.id.rl_choose_location_province)
    RelativeLayout rlChangeLocationProvince;
    @InjectView(R.id.rl_choose_location_school)
    RelativeLayout rlChangeLocationSchool;
    @InjectView(R.id.ib_choose_location_back)
    ImageButton ibChangeLocartionBack;
    @InjectView(R.id.btn_choose_location_cancel)
    Button btnCancel;
    @InjectView(R.id.tv_change_locatio_province)
    TextView tvProvince;
    @InjectView(R.id.tv_change_location_school)
    TextView tvSchool;
    @InjectView(R.id.tv_change_location_now)
    TextView tvNowProvince;
    @InjectView(R.id.tv_lost_data_all)
    TextView tvLostDataAll;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_location);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        String location = SharedPrefUtils.readString(this, "main_province", "全国");
        String school = SharedPrefUtils.readString(this, "main_school", "全部学校");
        if (location.equals("全国") && school.equals("全部学校")) {
            tvNowProvince.setText("全国");
        } else if (!location.equals("全国") && school.equals("全部学校")) {
            tvNowProvince.setText(location);
        } else if (!location.equals("全国") && !school.equals("全部学校")) {
            tvNowProvince.setText(school);
        }
    }

    @OnClick(R.id.rl_choose_location_province)
    public void changeLocationCity(View view) {
        Intent provinceIntent = new Intent(this, ChooseProvinceActivity.class);
        provinceIntent.putExtra("type", "location_province");//getIntent().getStringExtra("type"));
        startActivityForResult(provinceIntent, REQUEST_PROVINCE);

    }


    @OnClick(R.id.rl_choose_location_school)
    public void changeLocationSchool(View view) {
        Intent schoolIntent = new Intent(this, ChooseProvinceActivity.class);
        schoolIntent.putExtra("type", "location_school");
        startActivityForResult(schoolIntent, REQUEST_SCHOOL);

    }

    @OnClick(R.id.tv_lost_data_all)
    public void lostDataAll() {
        Intent intent = new Intent();
        intent.putExtra("main_province", "全国");
        setResult(RESULT_MAIN_ALL, intent);
        SharedPrefUtils.writeString(this, "main_province", "全国");
        SharedPrefUtils.writeString(this, "main_school", "全部学校");
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PROVINCE:
                if (data != null) { 
                    String changeProvince = data.getStringExtra("main_province");
                    if (!changeProvince.equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("main_province", changeProvince);
                        setResult(RESULT_MAIN_PROVINCE, intent);
                        this.finish();
                    } else {
                        tvProvince.setText("切换到城市");
                    }
                } else { 
                    Intent intent = new Intent();
                    intent.putExtra("main_province", "");
                    setResult(RESULT_MAIN_PROVINCE, intent);
                    this.finish();
                }
                break;
            case REQUEST_SCHOOL:
                if (data != null) { 
                    String chooseSchool = data.getStringExtra("choose_school");
                    tvSchool.setText(chooseSchool);
                    if (!chooseSchool.equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("main_school", chooseSchool);
                        setResult(RESULT_MAIN_SCHOOL, intent);
                        this.finish();
                    } else {
                        tvProvince.setText("切换到城市");
                    }
                } else { 
                    Intent intent = new Intent();
                    intent.putExtra("main_school", "");
                    setResult(RESULT_MAIN_SCHOOL, intent);
                    this.finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.ib_choose_location_back)
    public void changeLocartionBack(View view) {
        onBackPressed();
    }

    @OnClick(R.id.btn_choose_location_cancel)
    public void changeLocationCancel(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
