package com.edu.claf.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.bean.User;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class MainActivity extends TabActivity {

    private static final String FRAGMENT_CONTENT = "fragment_content";

    private static final int SWIPE_MIN_DISTANCE = 160;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final int RESULT_MAIN_PROVINCE = 112;
    private static final int RESULT_MAIN_LOCATION = 111;
    private static final int RESULT_MAIN_ALL = 114;
    private TabHost tabHost;
    private TextView main_tab_new_message;

    private TextView tvTitle;

    private ImageButton ivClassifyLost;
    private ImageButton ivClassifyFound;
    @InjectView(R.id.iv_chose_location)
    ImageButton ivChoseLocation;
    private GestureDetector gestureDetector;
    @InjectView(R.id.tv_location)
    TextView tvLocation;
    @InjectView(R.id.main_tab_found_info)
    RadioButton rbFoundInfo;
    private View.OnTouchListener gestureListener;

    int currentView = 0;
    private static int maxTabIndex = 4;
    private RadioGroup mRadioGroup;
    private long mExitTime = 0;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        main_tab_new_message = (TextView) findViewById(R.id.main_tab_new_message);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivClassifyLost = (ImageButton) findViewById(R.id.iv_classify_lost);
        ivClassifyFound = (ImageButton) findViewById(R.id.iv_classify_found);
        main_tab_new_message.setVisibility(View.INVISIBLE);
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        };
        user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            SharedPrefUtils.setBoolean(this, "isCurrentUser", true);
            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            System.out.println(userJson);
        } else {
            SharedPrefUtils.setBoolean(this, "isCurrentUser", false);
        }


        tabHost = this.getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, LostInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("失物信息").setIndicator("").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FoundInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("招领信息").setIndicator("").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PublishInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("发布信息").setIndicator("").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, MyNotifyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("我的通知").setIndicator("").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PersonCentralActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("个人中心").setIndicator("").setContent(intent);
        tabHost.addTab(spec);



        mRadioGroup = (RadioGroup) findViewById(R.id.main_tab_group);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab_lost_info: 
                        tabHost.setCurrentTabByTag("失物信息");
                        tvTitle.setText("失物信息");
                        tvLocation.setVisibility(View.VISIBLE);
                        ivClassifyLost.setVisibility(View.VISIBLE);
                        ivClassifyFound.setVisibility(View.INVISIBLE);
                        ivChoseLocation.setVisibility(View.VISIBLE);
                        break;
                    case R.id.main_tab_found_info: 
                        tabHost.setCurrentTabByTag("招领信息");
                        tvTitle.setText("招领信息");
                        tvLocation.setVisibility(View.VISIBLE);
                        ivClassifyLost.setVisibility(View.INVISIBLE);
                        ivClassifyFound.setVisibility(View.VISIBLE);
                        ivChoseLocation.setVisibility(View.VISIBLE);
                        break;
                    case R.id.main_tab_publish_info: 
                        tabHost.setCurrentTabByTag("发布信息");
                        tvTitle.setText("发布信息");
                        tvLocation.setVisibility(View.INVISIBLE);
                        ivClassifyLost.setVisibility(View.INVISIBLE);
                        ivClassifyFound.setVisibility(View.INVISIBLE);
                        ivChoseLocation.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.main_tab_notify:  
                        tabHost.setCurrentTabByTag("我的通知");
                        tvTitle.setText("我的通知");
                        tvLocation.setVisibility(View.INVISIBLE);
                        ivClassifyLost.setVisibility(View.INVISIBLE);
                        ivClassifyFound.setVisibility(View.INVISIBLE);
                        ivChoseLocation.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.main_tab_person_central:
                        tabHost.setCurrentTabByTag("个人中心");
                        tvTitle.setText("个人中心");
                        tvLocation.setVisibility(View.INVISIBLE);
                        ivClassifyLost.setVisibility(View.INVISIBLE);
                        ivClassifyFound.setVisibility(View.INVISIBLE);
                        ivChoseLocation.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }

            }
        });

        String type = getIntent().getStringExtra("type");
        if (type.equals("publishFoundInfo") && type != null){
            tabHost.setCurrentTab(1);
            tvTitle.setText("招领信息");
            tvLocation.setVisibility(View.VISIBLE);
            ivClassifyLost.setVisibility(View.INVISIBLE);
            ivClassifyFound.setVisibility(View.VISIBLE);
            ivChoseLocation.setVisibility(View.VISIBLE);
            rbFoundInfo.setChecked(true);

        }else if(type.equals("splashActivity") || type.equals("exitApp") || type.equals("publishLostInfo")
            || type.equals("guidePage") || type.equals("login") || type.equals("register") && type != null){
            tabHost.setCurrentTab(0);
        }
        ivClassifyLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClassifyActivity.class);
                intent.putExtra("lost", "lost");
                startActivityForResult(intent, 0);
            }
        });
        ivClassifyFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClassifyActivity.class);
                intent.putExtra("lost", "found");
                startActivityForResult(intent, 0);
            }
        });


    }


    @OnClick(R.id.iv_chose_location)
    public void choseLocation(View view) {
        Intent intent = new Intent(this, ChooseLocationActivity.class);
        intent.putExtra("type", "main_location");
        startActivityForResult(intent, RESULT_MAIN_PROVINCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            System.out.println(data.getStringExtra("type"));
            tabHost.setCurrentTab(0);
        } else if (resultCode == 201) {
            tabHost.setCurrentTab(1);
        } else if (resultCode == 112 && requestCode == RESULT_MAIN_PROVINCE) {
            SharedPrefUtils.writeString(MainActivity.this, "main_province", data.getStringExtra("main_province"));

        } else if (resultCode == RESULT_MAIN_ALL) {
            tvLocation.setText(SharedPrefUtils.readString(this, "main_province", "全国"));
        }

    }


    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            TabHost tabHost = getTabHost();
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (currentView == maxTabIndex) {
                        currentView = maxTabIndex;
                    } else {
                        currentView++;
                    }
                    changeCheckGroupState(currentView);
                    tabHost.setCurrentTab(currentView);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (currentView == 0) {
                        currentView = 0;
                    } else {
                        currentView--;
                    }
                    tabHost.setCurrentTab(currentView);
                    changeCheckGroupState(currentView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return false;
        }

    }

    public void changeCheckGroupState(int position) {
        switch (position) {
            case 0:
                mRadioGroup.check(R.id.main_tab_lost_info);
                break;
            case 1:
                mRadioGroup.check(R.id.main_tab_found_info);
                break;
            case 2:
                mRadioGroup.check(R.id.main_tab_publish_info);
                break;
            case 3:
                mRadioGroup.check(R.id.main_tab_notify);
                break;
            case 4:
                mRadioGroup.check(R.id.main_tab_person_central);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = SharedPrefUtils.readString(this, "main_province", "全国");
        String school = SharedPrefUtils.readString(this, "main_school", "全部学校");
        if (location.equals("全国") && school.equals("全部学校")) {
            tvLocation.setText("全国");
        }else if (!location.equals("全国") && school.equals("全部学校")){
            tvLocation.setText(location);
        }else if (!location.equals("全国") && !school.equals("全部学校")){
            tvLocation.setText(school);
        }
    }

}
