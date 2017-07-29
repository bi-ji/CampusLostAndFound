package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BitmapUtils;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.LableUtils;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.Reply;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.view.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;


public class FoundInfoDetailsActivity extends AppCompatActivity {

    private static final int LIMIT = 5;
    private String mFoundRecordID;

    @InjectView(R.id.tv_detail_nickname)
    TextView tvFoundDetailNickname;
    @InjectView(R.id.tv_detail_pubish_time)
    TextView tvFoundDetailPublishTime;
    @InjectView(R.id.tv_detail_title)
    TextView tvFoundDetailTitle;
    @InjectView(R.id.tv_detail_time)
    TextView tvFoundDetailTime;
    @InjectView(R.id.tv_detail_location)
    TextView tvFoundDetailLocation;
    @InjectView(R.id.tv_detail_desc)
    TextView tvFoundDetailDesc;
    @InjectView(R.id.lv_detail_comments)
    ListView lvFoundComments;
    @InjectView(R.id.ll_detail_bottom)
    LinearLayout llFoundDetailBottom;
    @InjectView(R.id.iv_detail_user_portrait)
    ImageView ivPhoto;
    @InjectView(R.id.tv_detail_label)
    TextView tvLable;

    private FoundInfo mFoundInfo;
    private List<Comments> mCommentsList;
    private List<Reply> mReplyList;
    private User mUser;
    private String nickName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_info_details);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        mReplyList = new ArrayList<>();
        Intent intent = this.getIntent();
        mFoundRecordID = intent.getStringExtra("foundID");
        llFoundDetailBottom.setVisibility(View.GONE);
        initFoundRecord();
    }

    private void initFoundRecord() {
        mFoundInfo = new FoundInfo();
        mCommentsList = new ArrayList<>();
        BmobQuery<FoundInfo> query = new BmobQuery<>();
        ProgressView.getProgressDialog(this, "正在加载...");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));
        boolean isCache = query.hasCachedResult(this, FoundInfo.class);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK); 
            System.out.println("has cache");
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            System.out.println("no cache");
        }
        query.include("publishUser");
        query.getObject(this, mFoundRecordID, new GetListener<FoundInfo>() {
            @Override
            public void onSuccess(FoundInfo foundInfo) {
                nickName = foundInfo.getPublishUser().getNickName();
                tvFoundDetailNickname.setText(foundInfo.getPublishUser().getNickName());
                tvFoundDetailTitle.setText("标题：" + foundInfo.getFoundTitle());
                tvFoundDetailPublishTime.setText("发布时间" + foundInfo.getFoundDate().getDate());
                tvFoundDetailTime.setText(foundInfo.getFoundDate().getDate());
                tvFoundDetailLocation.setText("拾物地点：" + foundInfo.getFoundLocation());
                tvFoundDetailDesc.setText("拾物描述：" + foundInfo.getFoundDesc());
                LableUtils.setLableDetailImage(tvLable,foundInfo.getFoundType());
                String realPath = FileUtils.getImageDir() + File.separator +
                        foundInfo.getPublishUser().getMobilePhoneNumber() + ".jpg";
                if (realPath != null) {
                    ivPhoto.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 1));
                }
                DBHelper.loadCommnets(foundInfo, mCommentsList, lvFoundComments, mUser,
                        nickName, mReplyList, FoundInfoDetailsActivity.this);
                ProgressView.progressDialogDismiss();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void backInfo(View view) {
        onBackPressed();
    }

}
