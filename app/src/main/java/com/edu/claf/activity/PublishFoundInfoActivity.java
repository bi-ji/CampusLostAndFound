package com.edu.claf.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.RegexUtils;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;
import com.edu.claf.watcher.MyTextWatcher;
import com.edu.claf.wheel.StrericWheelAdapter;
import com.edu.claf.wheel.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;

public class PublishFoundInfoActivity extends AppCompatActivity {

    private int minYear = 1970;  
    private int fontSize = 13;     
    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;
    private static List<Map<String, Object>> lstImageItem;

    @InjectView(R.id.iv_post_found_message_back)
    ImageButton postFoundMessageBack;

    @InjectView(R.id.et_post_found_message_title)
    EditText etPostFoundMessageTitle;

    @InjectView(R.id.et_post_found_message_time)
    EditText etPostFoundMessageTime;

    @InjectView(R.id.et_post_found_message_place)
    EditText etPostFoundMessagePlace;

    @InjectView(R.id.et_post_found_message_telephone)
    EditText etPostFoundMessageTelephone;

    @InjectView(R.id.et_post_found_message_desc)
    EditText etPostFoundMessageDesc;

    @InjectView(R.id.btn_post_found_message_time)
    Button btnPublishFoundTime;

    @InjectView(R.id.gv_post_found_type_message)
    GridView gvPostFoundMessageGridview;

    @InjectView(R.id.post_found_message_Num)
    TextView tvPostFoundMessageNum;

    private User mUser;
    private String foundType;
    private SimpleAdapter simpleAdapter;

    private int[] icon = {R.drawable.purse_ch, R.drawable.card_ch, R.drawable.keys_ch
            , R.drawable.digital_ch, R.drawable.ring_ch, R.drawable.cloth_ch
            , R.drawable.file_ch, R.drawable.book_ch, R.drawable.pat_ch, R.drawable.car_ch
            , R.drawable.people_ch, R.drawable.others_ch};

    private String[] iconName = {"钱包", "证件", "钥匙", "数码", "饰品", "衣服"
            , "文件", "书籍", "宠物", "车辆", "找人", "其他"};


    private StringBuffer mFoundTime;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 222:
                    tvPostFoundMessageNum.setText((int) msg.obj + "");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        initUI();
        initData();
        initContent();

    }


    private void initData() {
        postFoundMessageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initUI() {
        setContentView(R.layout.activity_publish_found_info);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        lstImageItem = new ArrayList<>();
        lstImageItem = initNightGrid();
        mUser = new BmobUser().getCurrentUser(this, User.class);
        if (mUser != null) {
            System.out.println(mUser.getMobilePhoneNumber());
            etPostFoundMessageTelephone.setText(mUser.getMobilePhoneNumber());
        }
        simpleAdapter = new SimpleAdapter(this, lstImageItem, R.layout.night_item,
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.ItemImage, R.id.ItemText});
        gvPostFoundMessageGridview.setAdapter(simpleAdapter);
        gvPostFoundMessageGridview.setOnItemClickListener(new ItemOnClickListener());
        etPostFoundMessageDesc.addTextChangedListener(new MyTextWatcher(etPostFoundMessageDesc,222,handler));
    }

    private void initContent() {
        yearContent = new String[10];
        for (int i = 0; i < 10; i++)
            yearContent[i] = String.valueOf(i + 2013);

        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }

        dayContent = new String[31];
        for (int i = 0; i < 31; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if (dayContent[i].length() < 2) {
                dayContent[i] = "0" + dayContent[i];
            }
        }
        hourContent = new String[24];
        for (int i = 0; i < 24; i++) {
            hourContent[i] = String.valueOf(i);
            if (hourContent[i].length() < 2) {
                hourContent[i] = "0" + hourContent[i];
            }
        }

        minuteContent = new String[60];
        for (int i = 0; i < 60; i++) {
            minuteContent[i] = String.valueOf(i);
            if (minuteContent[i].length() < 2) {
                minuteContent[i] = "0" + minuteContent[i];
            }
        }
        secondContent = new String[60];
        for (int i = 0; i < 60; i++) {
            secondContent[i] = String.valueOf(i);
            if (secondContent[i].length() < 2) {
                secondContent[i] = "0" + secondContent[i];
            }
        }
    }

    @OnClick(R.id.btn_post_found_message_time)
    public void getLostTime(View view) {
        View viewTime = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, null);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);

        yearWheel = (WheelView) viewTime.findViewById(R.id.yearwheel);
        monthWheel = (WheelView) viewTime.findViewById(R.id.monthwheel);
        dayWheel = (WheelView) viewTime.findViewById(R.id.daywheel);
        hourWheel = (WheelView) viewTime.findViewById(R.id.hourwheel);
        minuteWheel = (WheelView) viewTime.findViewById(R.id.minutewheel);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewTime);

        yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
        yearWheel.setCurrentItem(curYear - 2013);
        yearWheel.setCyclic(true);
        yearWheel.setInterpolator(new AnticipateOvershootInterpolator());


        monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

        monthWheel.setCurrentItem(curMonth - 1);

        monthWheel.setCyclic(true);
        monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

        dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
        dayWheel.setCurrentItem(curDay - 1);
        dayWheel.setCyclic(true);
        dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

        hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
        hourWheel.setCurrentItem(curHour);
        hourWheel.setCyclic(true);
        hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

        minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
        minuteWheel.setCurrentItem(curMinute);
        minuteWheel.setCyclic(true);
        minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

        builder.setTitle("选取失物时间");
        builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mFoundTime = new StringBuffer();
                mFoundTime.append(yearWheel.getCurrentItemValue()).append("-")
                        .append(monthWheel.getCurrentItemValue()).append("-")
                        .append(dayWheel.getCurrentItemValue());

                mFoundTime.append(" ");
                mFoundTime.append(hourWheel.getCurrentItemValue())
                        .append(":").append(minuteWheel.getCurrentItemValue());
                etPostFoundMessageTime.setText(mFoundTime);
                dialog.cancel();
            }
        });

        builder.show();
    }


    private class ItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        int flag = 0;
        private int itemIndex = 0;
        private int itemRecord;
        private List<Map<String, Object>> list = new ArrayList<>();

        Message msg = new Message();
        HashMap<String, Object> temp = null;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
            foundType = iconName[position];
            if (flag == 1 || itemIndex == position) {
                if (itemIndex != position) {
                    lstImageItem.set(itemIndex, temp);
                    temp = (HashMap<String, Object>) lstImageItem.get(position);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ItemImage", R.drawable.chosen_state);
                    map.put("ItemText", iconName[position]);
                    lstImageItem.set(position, map);
                    itemIndex = position;

                } else {

                }

                System.out.println(position);
            } else {
                temp = (HashMap<String, Object>) lstImageItem.get(position);
                HashMap<String, Object> map = new HashMap<>();
                map.put("ItemImage", R.drawable.chosen_state);
                map.put("ItemText", iconName[position]);
                lstImageItem.set(position, map);
                flag = 1;
                itemIndex = position;
            }
            simpleAdapter.notifyDataSetChanged();
            msg.obj = view;
            msg.what = position;
        }
    }

    public List<Map<String, Object>> initNightGrid() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", icon[i]);
            map.put("ItemText", iconName[i]);
            lstImageItem.add(map);
        }
        return lstImageItem;
    }


    public void submitFoundInfo(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        FoundInfo foundInfo = new FoundInfo();
        String lTitle = etPostFoundMessageTitle.getText().toString();
        String lTime = etPostFoundMessageTime.getText().toString();
        String lLocation = etPostFoundMessagePlace.getText().toString();
        String lDesc = etPostFoundMessageDesc.getText().toString();
        String lPhone = etPostFoundMessageTelephone.getText().toString();
        if (RegexUtils.vertifyPublishInfo(lTitle,lTime,lLocation,lDesc,lPhone,foundType)){
            Date currentDate =null;
            System.out.println(lTitle + lTime + lLocation + lDesc + lPhone);

            foundInfo.setFoundTitle(lTitle);
            foundInfo.setFoundLocation(lLocation);
            foundInfo.setPublishUser(mUser);
            foundInfo.setFoundType(foundType);
            foundInfo.setFoundWhat("失物");
            foundInfo.setPublishUserSchool(mUser.getSchoolName());
            try {
                Date date = dateFormat.parse(lTime);
                foundInfo.setFoundDate(new BmobDate(date));
                currentDate = dateFormat.parse(System.currentTimeMillis()+"");
                foundInfo.setPublishTime(new BmobDate(currentDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            foundInfo.setContactNumber(lPhone);
            foundInfo.setFoundDesc(lDesc);
            final ProgressView progressView = new ProgressView(this);
            ProgressView.getProgressDialog(this, "正在发布中...");
            foundInfo.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(PublishFoundInfoActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    ProgressView.progressDialogDismiss();
                    PublishFoundInfoActivity.this.finish();
                    Intent intent = new Intent(PublishFoundInfoActivity.this,MainActivity.class);
                    intent.putExtra("type","publishFoundInfo");
                    startActivity(intent);
                }

                @Override
                public void onFailure(int i, String s) {
                    ProgressView.progressDialogDismiss();
                    if (i == 9010) {
                        Toast.makeText(PublishFoundInfoActivity.this, "发布失败,网络超时" , Toast.LENGTH_SHORT).show();
                    }else if(i == 9016){
                        Toast.makeText(PublishFoundInfoActivity.this, "发布失败，网络连接错误，请检查手机网络", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(PublishFoundInfoActivity.this, "请填写失物信息的所有项", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
