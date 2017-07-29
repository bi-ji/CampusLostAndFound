package com.edu.claf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class ChooseSchoolActivity extends AppCompatActivity {

    @InjectView(R.id.ib_chooseschool_back)
    ImageButton chooseSchoolBack;

    @InjectView(R.id.lv_choose_school)
    ListView lvChooseSchool;
    @InjectView(R.id.sidebar_choose_school)
    SideBar sideBarChooseSchool;

    private SortAdapter adapter;

    private CharacterParserUtils characterParser;

    private List<SortBean> mSchoolData;

    private PinyinComparator pinyinComparator;
    private String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_school);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews() {
        characterParser = CharacterParserUtils.getInstance();
        pinyinComparator = new PinyinComparator();
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String schoolName = null;
        final String sourceProvince = intent.getStringExtra("choose_province");
        SharedPrefUtils.writeString(this,"main_province",sourceProvince);
        String selling = characterParser.getSelling(sourceProvince);
        sideBarChooseSchool.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != 1) {
                    lvChooseSchool.setSelection(position);
                }
            }
        });
        lvChooseSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                school = ((SortBean) adapter.getItem(position)).getName();
                Intent intent = new Intent();
                intent.putExtra("choose_province", sourceProvince);
                intent.putExtra("choose_school", school);
                SharedPrefUtils.writeString(ChooseSchoolActivity.this, "main_province", sourceProvince);
                SharedPrefUtils.writeString(ChooseSchoolActivity.this, "main_school", school);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        schoolName = selling + "school";
        if (type.equals("register_school")) { 
        } else if (type.equals("province_to_school")) { 
            Intent mainIntent = new Intent(this, LostInfoActivity.class);
            mainIntent.putExtra("change_school", school);
        }
        int resId = getSourceProvinceResID(schoolName);
        mSchoolData = filledData(getResources().getStringArray(resId));
        Collections.sort(mSchoolData, pinyinComparator);
        adapter = new SortAdapter(this, mSchoolData);
        lvChooseSchool.setAdapter(adapter);

    }

    private List<SortBean> filledData(String[] schoolData) {
        List<SortBean> mSortList = new ArrayList<>();
        for (int i = 0; i < schoolData.length; i++) {
            SortBean sortBean = new SortBean();
            sortBean.setName(schoolData[i]);
            String pinyin = characterParser.getSelling(schoolData[i]);
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

    public int getSourceProvinceResID(String sourceProvince) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(sourceProvince, "array", ctx.getPackageName());
        return resId;
    }

    @OnClick(R.id.ib_chooseschool_back)
    public void chooseSchoolBack(View view) {
        onBackPressed();
    }
}
