package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.CharacterParserUtils;
import com.edu.claf.Utils.PinyinComparator;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.adapter.SortAdapter;
import com.edu.claf.bean.SortBean;
import com.edu.claf.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChooseProvinceActivity extends AppCompatActivity {

    private static final int PROVINCE_REQUEST_SCHOOL = 1;
    private static final int CHOOSE_PROVINCE = 2;
    private static final int RESULT_MAIN_PROVINCE = 112;
    @InjectView(R.id.ib_chooseprovince_back)
    ImageButton chooseProvienceBack;
    @InjectView(R.id.lv_choose_province)
    ListView lvChooseProvience;
    @InjectView(R.id.sidebar_choose_province)
    SideBar sideBarChooseProvince;
    private SortAdapter adapter;

    private boolean clickFlag = false;
    private CharacterParserUtils characterParser;
    private List<SortBean> mProvinceData;
    private PinyinComparator pinyinComparator;
    private String mSourceProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_province);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initViews();
    }

    private void provinceToSchool(String source) {
        Intent toSchool = new Intent(ChooseProvinceActivity.this, ChooseSchoolActivity.class);
        toSchool.putExtra("type", "province_to_school");
        toSchool.putExtra("choose_province", mSourceProvince);
        startActivityForResult(toSchool, PROVINCE_REQUEST_SCHOOL);
    }

    private void initViews() {
        characterParser = CharacterParserUtils.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBarChooseProvince.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvChooseProvience.setSelection(position);
                }
            }
        });

        lvChooseProvience.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String type = getIntent().getStringExtra("type");
                clickFlag = true;
                mSourceProvince = ((SortBean) adapter.getItem(position)).getName();
                if (type.equals("location_province")) { 
                    SharedPrefUtils.writeString(ChooseProvinceActivity.this, "main_province", mSourceProvince);
                    SharedPrefUtils.writeString(ChooseProvinceActivity.this, "main_school", "全部学校");
                    Intent intent = new Intent();
                    intent.putExtra("main_province", mSourceProvince);
                    setResult(RESULT_MAIN_PROVINCE, intent);
                    finish();
                } else if (type.equals("location_school")) { 
                    provinceToSchool(mSourceProvince);
                } else { 
                    Intent cityIntent = new Intent();
                    cityIntent.putExtra("choose_province", mSourceProvince);
                    setResult(RESULT_OK, cityIntent);
                    finish();
                }
            }
        });

        mProvinceData = filledData(getResources().getStringArray(R.array.province));
        Collections.sort(mProvinceData, pinyinComparator);
        adapter = new SortAdapter(this, mProvinceData);
        lvChooseProvience.setAdapter(adapter);

    }

    private List<SortBean> filledData(String[] provinceData) {
        List<SortBean> mSortList = new ArrayList<>();
        for (int i = 0; i < provinceData.length; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setName(provinceData[i]);
            String pinyin = characterParser.getSelling(provinceData[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortBean.setSortLetters(sortString.toUpperCase());
            } else {
                sortBean.setSortLetters("#");
            }
            mSortList.add(sortBean);
        }
        return mSortList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PROVINCE_REQUEST_SCHOOL:
                setResult(RESULT_OK, data);
                finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.ib_chooseprovince_back)
    public void chooseProvienceBack(View view) {
        if (!clickFlag) {  
            Intent intent = new Intent();
            intent.putExtra("main_province", "");
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        if (!clickFlag) { 
            Intent intent = new Intent();
            intent.putExtra("main_province", "");
            setResult(RESULT_MAIN_PROVINCE, intent);
            finish();
        }
        super.onBackPressed();
    }
}
