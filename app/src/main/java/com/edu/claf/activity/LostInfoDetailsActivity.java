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
import com.edu.claf.adapter.ReplyAdapter;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.Reply;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.view.CommentsPopupWindow;
import com.edu.claf.view.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

public class LostInfoDetailsActivity extends AppCompatActivity {

    private static final int LIMIT = 5;
    private int skip = 0;
    private TextView textView;

    private String mLostRecordID;

    @InjectView(R.id.tv_detail_nickname)
    TextView tvLostDetailNickname;
    @InjectView(R.id.tv_detail_pubish_time)
    TextView tvLostDetailPublishTime;
    @InjectView(R.id.tv_detail_title)
    TextView tvLostDetailTitle;
    @InjectView(R.id.tv_detail_time)
    TextView tvLostDetailTime;
    @InjectView(R.id.tv_detail_location)
    TextView tvLostDetailLocation;
    @InjectView(R.id.tv_detail_desc)
    TextView tvLostDetailDesc;
    @InjectView(R.id.lv_detail_comments)
    ListView lvLostComments;
    @InjectView(R.id.ll_detail_bottom)
    LinearLayout llLostDetailBottom;
    @InjectView(R.id.iv_detail_user_portrait)
    ImageView ivPhoto;
    @InjectView(R.id.tv_detail_label)
    TextView tvLable;
    private LostInfo mLostInfo;

    private List<Comments> mCommentsList;
    private List<Reply> mReplyList;
    private Date commentsDate;

    static CommentsPopupWindow replyPopup;
    private String replyContent;
    private User mUser;
    private String mCommentUserNickName;
    private Comments mComment;
    private ReplyAdapter replyAdapter;
    private String nickName;
    private Comments mReplyComment;

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
        mLostRecordID = intent.getStringExtra("objectID");
        initLostRecord();
    }

    private void initLostRecord() {
        mLostInfo = new LostInfo();
        mCommentsList = new ArrayList<>();
        llLostDetailBottom.setVisibility(View.GONE);
        BmobQuery<LostInfo> query = new BmobQuery<>();
        ProgressView.getProgressDialog(this, "正在加载...");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));
        boolean isCache = query.hasCachedResult(this, LostInfo.class);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK); 
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        query.include("publishUser");
        query.getObject(this, mLostRecordID, new GetListener<LostInfo>() {
            @Override
            public void onSuccess(LostInfo lostInfo) {
                nickName = lostInfo.getPublishUser().getNickName();
                tvLostDetailNickname.setText(lostInfo.getPublishUser().getNickName());
                tvLostDetailTitle.setText("标题：" + lostInfo.getLostTitle());
                tvLostDetailPublishTime.setText("发布时间：" + lostInfo.getLostDate().getDate());
                tvLostDetailTime.setText(lostInfo.getLostDate().getDate());
                tvLostDetailLocation.setText("丢失地点：" + lostInfo.getLostLocation());
                tvLostDetailDesc.setText("丢失描述：" + lostInfo.getLostDesc());
                LableUtils.setLableDetailImage(tvLable, lostInfo.getLostType());
                String realPath = FileUtils.getImageDir() + File.separator +
                        lostInfo.getPublishUser().getMobilePhoneNumber() + ".jpg";
                if (realPath != null) {
                    ivPhoto.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 1));
                }
                DBHelper.loadCommnets(lostInfo,mCommentsList,lvLostComments,mUser,
                        nickName,mReplyList,LostInfoDetailsActivity.this);

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


    @Override
    protected void onResume() {
        super.onResume();
        if (replyAdapter != null) {
            replyAdapter.notifyDataSetChanged();
        }

    }
}
