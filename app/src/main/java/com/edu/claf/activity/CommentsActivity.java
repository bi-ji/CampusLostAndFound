package com.edu.claf.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.ImageLoader;
import com.edu.claf.Utils.ListViewUtility;
import com.edu.claf.Utils.NetStatusUtils;
import com.edu.claf.adapter.ReplyAdapter;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.MyBmobInstallation;
import com.edu.claf.bean.Reply;
import com.edu.claf.bean.User;
import com.edu.claf.view.CommentsPopupWindow;
import com.edu.claf.view.CustomListView;
import com.edu.claf.view.NoScrollListView;
import com.edu.claf.view.ProgressView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentsActivity extends AppCompatActivity {

    private static final int LIMIT = 50;
    @InjectView(R.id.lv_my_comments)
    CustomListView lvMyComments;
    @InjectView(R.id.tv_comments_empty)
    TextView tvCommentsEmpty;
    @InjectView(R.id.fl_net_error)
    FrameLayout flNetError;
    @InjectView(R.id.btn_reconnect)
    Button btnReconnect;

    List<Comments> mCommentsList;
    List<Comments> mComments2meList;
    private Date commentsDate;
    private Comments mComment;
    private String mCommentUserNickName;
    static CommentsPopupWindow replyPopup;
    private ReplyAdapter replyAdapter;
    private List<Reply> mReplyList;

    private User mUser;
    private static String lostInfoTitle;
    private MyCommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_comments);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        mReplyList = new ArrayList<>();
        mCommentsList = new ArrayList<>();
        mComments2meList = new ArrayList<>();
        flNetError.setVisibility(View.GONE);
        ProgressView.getProgressDialog(this,"正在加载....");
        if (mUser != null) {
            initCommentsInfo();
        }
        btnReconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetStatusUtils.isNetworkAvailable(CommentsActivity.this)) {
                    flNetError.setVisibility(View.GONE);
                    tvCommentsEmpty.setVisibility(View.GONE);
                    initCommentsInfo();
                }
            }
        });
    }

    private void initCommentsInfo() {
        BmobQuery<Comments> query = new BmobQuery<>();
        query.addWhereEqualTo("commentedUserId", mUser.getObjectId());
        BmobQuery<Comments> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("commentUserId", mUser.getObjectId());
        List<BmobQuery<Comments>> queries = new ArrayList<BmobQuery<Comments>>();
        queries.add(query);
        queries.add(query1);
        BmobQuery<Comments> mainQuery = new BmobQuery<Comments>();
        mainQuery.or(queries);
        mainQuery.setLimit(LIMIT);
        mainQuery.order("-createdAt");
        mainQuery.include("commentUserId");
        mainQuery.findObjects(CommentsActivity.this, new FindListener<Comments>() {
            @Override
            public void onSuccess(List<Comments> list) { 
                if (list.size() == 0) {
                    tvCommentsEmpty.setVisibility(View.VISIBLE);
                    flNetError.setVisibility(View.GONE);
                }
                for (Comments comments : list) { 
                    if (comments.getCommentUserId().getObjectId().equals(mUser.getObjectId())) {
                        mCommentsList.add(comments);
                    } else if (comments.getCommentedUserId().equals(mUser.getObjectId())) {
                        mComments2meList.add(comments);
                    }

                }
                handler.sendEmptyMessage(0);
                ProgressView.progressDialogDismiss();
            }

            @Override
            public void onError(int i, String s) {
                if (i == 9010) {
                    tvCommentsEmpty.setVisibility(View.GONE);
                    flNetError.setVisibility(View.VISIBLE);
                    Toast.makeText(CommentsActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                } else if (i == 9016) {
                    tvCommentsEmpty.setVisibility(View.GONE);
                    flNetError.setVisibility(View.VISIBLE);
                    Toast.makeText(CommentsActivity.this, "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                }
                ProgressView.progressDialogDismiss();
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new MyCommentsAdapter();
            lvMyComments.setAdapter(adapter);
        }
    };
    public void backMyNotify(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class MyCommentsAdapter extends BaseAdapter {

        public ImageLoader imageLoader;

        MyCommentsAdapter() {
            imageLoader = new ImageLoader(CommentsActivity.this);
        }


        @Override
        public int getCount() {    
            return mCommentsList.size() + 1 + mComments2meList.size() + 1;
        }

        @Override
        public Object getItem(int i) { 
            if (i == 0) {
                return null;
            } else if (i == mCommentsList.size() + 1) {
                return null;
            }
            Comments comment = null;
            if (i < (mCommentsList.size() + 1)) {
                comment = mCommentsList.get(i - 1);
            } else {
                int location = mCommentsList.size() + 2;
                comment = mComments2meList.get(i - location);
            }
            return comment;
        }

        @Override
        public long getItemId(int i) { 
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
            String url = null;
            String phone = null;
            if (position == 0) { 
                TextView tvMyCommentsTitle = new TextView(CommentsActivity.this);
                tvMyCommentsTitle.setTextColor(Color.WHITE);
                tvMyCommentsTitle.setBackgroundColor(Color.GRAY);
                tvMyCommentsTitle.setText("我的评论（" + mCommentsList.size() + "）");
                return tvMyCommentsTitle;
            } else if (position == mCommentsList.size() + 1) {
                TextView tvComments2MeTitle = new TextView(CommentsActivity.this);
                tvComments2MeTitle.setTextColor(Color.WHITE);
                tvComments2MeTitle.setBackgroundColor(Color.GRAY);
                tvComments2MeTitle.setText("评论我的（" + mComments2meList.size() + "）");
                return tvComments2MeTitle;
            }
            View view = null;
            ViewHolder holder = new ViewHolder();
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(CommentsActivity.this, R.layout.comment_item, null);
                holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_comment_portrait);
                holder.tvCommentsNickname = (TextView) view.findViewById(R.id.tv_comments_nickname);
                holder.tvCommentsReply = (TextView) view.findViewById(R.id.tv_comments_reply);
                holder.tvCommentsTime = (TextView) view.findViewById(R.id.tv_comments_time);
                holder.tvCommentsContent = (TextView) view.findViewById(R.id.tv_comments_content);
                holder.nslvReplyList = (NoScrollListView) view.findViewById(R.id.nslv_reply_list);
                view.setTag(holder);
            }

            Comments comment = null;
            if (position < (mCommentsList.size() + 1)) {
                comment = mCommentsList.get(position - 1);
                url = comment.getCommentUserId().getPhotoUrl();
                phone = comment.getCommentUserId().getMobilePhoneNumber();
                if (comment.getLostInfoId() != null && comment.getFoundInfoId() == null) {
                    setCommentsInfoTitle(comment, comment.getLostInfoId().getObjectId(), holder, 0, 0);
                } else if (comment.getFoundInfoId() != null && comment.getLostInfoId() == null) {
                    setCommentsInfoTitle(comment, comment.getFoundInfoId().getObjectId(), holder, 0, 1);
                }
            } else {
                int location = mCommentsList.size() + 2;
                comment = mComments2meList.get(position - location);
                url = comment.getCommentUserId().getPhotoUrl();
                phone = comment.getCommentUserId().getMobilePhoneNumber();
                if (comment.getLostInfoId() != null) {
                    setCommentsInfoTitle(comment, comment.getLostInfoId().getObjectId(), holder, 1, 0);
                } else if (comment.getFoundInfoId() != null) {
                    setCommentsInfoTitle(comment, comment.getFoundInfoId().getObjectId(), holder, 1, 1);
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateComments = comment.getCommentTime().getDate();
            System.out.println(dateComments);
            holder.tvCommentsContent.setText(comment.getCommentContext());
            holder.tvCommentsTime.getText();
            imageLoader.DisplayImage(url, phone, holder.ivPhoto);
            try {
                commentsDate = dateFormat.parse(dateComments);
                holder.tvCommentsTime.setText(dateFormat.format(commentsDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (comment.getReplyList() != null) {
                findCommentReply(comment, holder.nslvReplyList);
            }
            TextviewClickListener tcl = new TextviewClickListener(position);
            holder.tvCommentsReply.setOnClickListener(tcl);
            return view;
        }
    }

    private void findCommentReply(final Comments comment, final NoScrollListView listViewReply) {
        BmobQuery<Reply> query = new BmobQuery<>();
        query.setLimit(50);
        query.addWhereEqualTo("comments", comment);
        query.findObjects(this, new FindListener<Reply>() {
            @Override
            public void onSuccess(List<Reply> list) {
                comment.setReplyList(list);
                listViewReply.setAdapter(new ReplyAdapter(CommentsActivity.this, comment.getReplyList(), R.layout.reply_item));
                ListViewUtility.setListViewHeightBasedOnChildren(listViewReply);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    static class ViewHolder {
        ImageView ivPhoto;
        TextView tvCommentsNickname;
        TextView tvCommentsTime;
        TextView tvCommentsReply;
        TextView tvCommentsContent;
        NoScrollListView nslvReplyList;
    }

    public void setCommentsInfoTitle(final Comments comment, final String infoId,
                                     final ViewHolder holder, final int type, final int kind) {

        if (kind == 0) { 
            BmobQuery<LostInfo> query = new BmobQuery<>();
            query.getObject(this, infoId, new GetListener<LostInfo>() {
                @Override
                public void onSuccess(LostInfo lostInfo) {
                    if (type == 0) {
                        holder.tvCommentsNickname.setTextSize(16);
                        holder.tvCommentsNickname.setText("您评论了" +
                                comment.getCommentUserNickName() + "的：" +
                                lostInfo.getLostTitle());
                    } else if (type == 1) {
                        holder.tvCommentsNickname.setTextSize(16);
                        holder.tvCommentsNickname.setText(comment.getCommentUserNickName() +
                                "评论了您的：" + lostInfo.getLostTitle());

                    }

                }

                @Override
                public void onFailure(int i, String s) {
                    System.out.println(s + "onFailure");
                }
            });
        } else if (kind == 1) { 
            BmobQuery<FoundInfo> query = new BmobQuery<>();
            query.getObject(this, infoId, new GetListener<FoundInfo>() {
                @Override
                public void onSuccess(FoundInfo foundInfo) {
                    if (type == 0) {
                        holder.tvCommentsNickname.setTextSize(16);
                        holder.tvCommentsNickname.setText("您评论了" +
                                comment.getCommentUserNickName() + "的：" +
                                foundInfo.getFoundTitle());
                    } else if (type == 1) {
                        holder.tvCommentsNickname.setTextSize(16);
                        holder.tvCommentsNickname.setText(comment.getCommentUserNickName() +
                                "评论了您的：" + foundInfo.getFoundTitle());

                    }

                }

                @Override
                public void onFailure(int i, String s) {
                    System.out.println(s + "onFailure");
                }
            });
        }

    }
    private final class TextviewClickListener implements View.OnClickListener {
        private int position;

        public TextviewClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_comments_reply:
                    replyPopup = new CommentsPopupWindow(CommentsActivity.this, itemsOnClick);
                    replyPopup.setBtnCommetnText("回复");
                    replyPopup.showAtLocation(CommentsActivity.this.findViewById(R.id.ll_comments),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
                    replyPopup.setFocusable(true);
                    replyPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    replyPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    break;
            }
        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replyPopup.dismiss();
            switch (view.getId()) {
                case R.id.btn_comments:
                    BmobPushManager bmobPush = new BmobPushManager(CommentsActivity.this);
                    BmobQuery<MyBmobInstallation> query = new BmobQuery();
                    query.addWhereEqualTo("phoneNumber", "");
                    bmobPush.setQuery(query);
                    bmobPush.pushMessage(replyPopup.getComments());
                    Toast.makeText(CommentsActivity.this, replyPopup.getComments(), Toast.LENGTH_SHORT).show();
                    saveReply();
                    replyAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private void saveReply() {
        final Reply reply = new Reply();
        reply.setReplyTime(new BmobDate(new Date(System.currentTimeMillis())));
        reply.setReplyContent(replyPopup.getComments());
        reply.setReplyUserNickName(mUser.getNickName());
        reply.setCommentNickName(mCommentUserNickName);
        reply.setComments(mComment);
        reply.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mReplyList.add(reply);
                updateComment(mComment, reply);
                Toast.makeText(CommentsActivity.this, "回复成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CommentsActivity.this, "回复失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateComment(Comments comment, Reply reply) {
        comment.setValue("replyList", mReplyList);
        comment.update(this, comment.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                replyAdapter.notifyDataSetChanged();
                Toast.makeText(CommentsActivity.this, "回复成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CommentsActivity.this, "回复失败" + s, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
