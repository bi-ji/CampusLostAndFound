package com.edu.claf.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.bean.FeedBackInfo;
import com.edu.claf.watcher.MyTextWatcher;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.SaveListener;

public class FeedBackActivity extends AppCompatActivity {

    @InjectView(R.id.et_feedback_content)
    EditText etFeedbackContent;
    @InjectView(R.id.et_feedback_contact_way)
    EditText etFeedbackContactWay;
    @InjectView(R.id.tv_feedback_text_count)
    TextView tv_feedback_text_count;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_feedback_text_count.setText((int)msg.obj+"");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_feed_back);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        etFeedbackContent.addTextChangedListener(new MyTextWatcher(etFeedbackContent,20,handler));
    }



    public void submitFeedback(View view){
        FeedBackInfo feedBackInfo = new FeedBackInfo();
        feedBackInfo.setFeedbackContent(etFeedbackContent.getText().toString().trim());
        feedBackInfo.setFeedbackContactWay(etFeedbackContactWay.getText().toString().trim());
        feedBackInfo.setFeedbackDate(new BmobDate(new Date(System.currentTimeMillis())));
        feedBackInfo.setFeedbackUserName("失主");
        feedBackInfo.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(FeedBackActivity.this,"非常感谢您的宝贵建议！",Toast.LENGTH_SHORT).show();
                FeedBackActivity.this.finish();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }


    public void backPersonCentral(View view){
        onBackPressed();
    }


}
