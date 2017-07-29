package com.edu.claf.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.bean.LostInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterStepTwoActivity extends AppCompatActivity {

    private static final int REGISTER_REQUEST_PROVINCE = 1;
    private static final int REGISTER_REQUEST_SCHOOL = 2;
    @InjectView(R.id.ib_register_ind_page_two_back)
    ImageButton ibRegisterBack;
    @InjectView(R.id.rl_register_location_city)
    RelativeLayout rlRegisterCity;
    @InjectView(R.id.rl_register_location_school)
    RelativeLayout rlRegisterSchool;
    @InjectView(R.id.btn_register_step_next_page)
    Button btnRegisterNextPage;
    @InjectView(R.id.tv_register_location_school)
    TextView tvRegisterSchool;
    private String mChooseSchool;
    @InjectView(R.id.tv_register_location_province)
    TextView tvRegisterProvince;
    private String mChooseProvince;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_step_two);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra("phoneNumber");

    }

    @OnClick(R.id.rl_register_location_city)
    public void chooseRegisterCity(View view){
        Intent intent = new Intent(this,ChooseProvinceActivity.class);
        intent.putExtra("type","register_two_city");
        startActivityForResult(intent, REGISTER_REQUEST_PROVINCE);

    }

    @OnClick(R.id.rl_register_location_school)
    public void chooseRegisterSchool(View view){
        if (mChooseProvince == null){
            Toast.makeText(this,"请先选择您的城市",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this,ChooseSchoolActivity.class);
            String province = mChooseProvince;
            intent.putExtra("choose_province",province);
            intent.putExtra("type","register_school");
            startActivityForResult(intent, REGISTER_REQUEST_SCHOOL);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REGISTER_REQUEST_PROVINCE: 
                mChooseProvince = data.getStringExtra("choose_province");
                tvRegisterProvince.setText(mChooseProvince);
                break;
            case REGISTER_REQUEST_SCHOOL: 
                mChooseSchool = data.getStringExtra("choose_school");
                tvRegisterSchool.setText(mChooseSchool);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_register_step_next_page)
    public void registerNextPage(View view){
        Intent intent = new Intent(this,RegisterStepThreeActivity.class);
        intent.putExtra("register_phone_number",mPhoneNumber);
        intent.putExtra("register_province",mChooseProvince);
        intent.putExtra("register_school",mChooseSchool);
        startActivity(intent);
    }

    @OnClick(R.id.ib_register_ind_page_two_back)
    public void RegisterBack(View view){
        onBackPressed();
    }


}
