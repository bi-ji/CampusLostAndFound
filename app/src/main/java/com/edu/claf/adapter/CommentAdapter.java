package com.edu.claf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.R;
import com.edu.claf.Utils.ImageLoader;
import com.edu.claf.Utils.ListViewUtility;
import com.edu.claf.activity.FoundInfoDetailsActivity;
import com.edu.claf.activity.LostInfoDetailsActivity;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.MyBmobInstallation;
import com.edu.claf.bean.Reply;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.holder.CommentsHolder;
import com.edu.claf.view.CommentsPopupWindow;
import com.edu.claf.view.NoScrollListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Source;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;

public class CommentAdapter extends BaseAdapter {

    private int resourceId;
    private Context context;
    private List<Comments> list;
    private LayoutInflater inflater;
    private Comments mComment;
    private User mUser;
    private String mCommentUserNickName;
    private Date commentsDate;
    private ReplyAdapter replyAdapter;
    private CommentsPopupWindow replyPopup;
    private Comments mReplyComment;
    private Activity activity;
    private String nickName;
    private List mReplyList;
    public ImageLoader imageLoader;
    private String mCommentUserNName;


    public CommentAdapter(User mUser, String nickName, List mReplyList,
                          int resourceId, Context context, List<Comments> list, Activity activity) {
        inflater = LayoutInflater.from(context);
        this.nickName = nickName;
        this.mReplyList = mReplyList;
        this.resourceId = resourceId;
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.mUser = mUser;
        imageLoader = new ImageLoader(activity);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        mComment = list.get(position);
        String url = null;
        String phone = null;
        url = mComment.getCommentUserId().getPhotoUrl();
        phone = mComment.getCommentUserId().getMobilePhoneNumber();
        View view = null;
        CommentsHolder holder = null;
        if (convertView == null) {
            holder = new CommentsHolder();
            view = inflater.inflate(resourceId, null);
            holder.ivPhoto = (ImageView) view.findViewById(R.id.iv_comment_portrait);
            holder.tvCommentsNickname = (TextView) view.findViewById(R.id.tv_comments_nickname);
            holder.tvCommentsReply = (TextView) view.findViewById(R.id.tv_comments_reply);
            holder.tvCommentsTime = (TextView) view.findViewById(R.id.tv_comments_time);
            holder.tvCommentsContent = (TextView) view.findViewById(R.id.tv_comments_content);
            holder.nslvReplyList = (NoScrollListView) view.findViewById(R.id.nslv_reply_list);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (CommentsHolder) view.getTag();

        }
        if (mUser == null) {
            holder.tvCommentsReply.setVisibility(View.INVISIBLE);
        } else if ((mUser.getObjectId().equals(mComment.getCommentedUserId()) && mUser != null)
                || (mUser.getObjectId().equals(mComment.getCommentUserId().getObjectId()) && mUser != null)) {
            holder.tvCommentsReply.setVisibility(View.VISIBLE);
        }
        imageLoader.DisplayImage(url, phone, holder.ivPhoto);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateComments = list.get(position).getCommentTime().getDate();
        mCommentUserNickName = list.get(position).getCommentUserNickName();
        holder.tvCommentsNickname.setText(mCommentUserNickName);
        holder.tvCommentsContent.setText(list.get(position).getCommentContext());
        holder.tvCommentsTime.getText();
        try {
            commentsDate = dateFormat.parse(dateComments);
            holder.tvCommentsTime.setText(dateFormat.format(commentsDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (list.get(position).getReplyList() != null) {
            List<Reply> replyList = list.get(position).getReplyList();
            replyAdapter = new ReplyAdapter(context, list.get(position).getReplyList(), R.layout.reply_item);
            holder.nslvReplyList.setAdapter(replyAdapter);
            ListViewUtility.setListViewHeightBasedOnChildren(holder.nslvReplyList);
        }
        TextviewClickListener tcl = new TextviewClickListener(position);
        holder.tvCommentsReply.setOnClickListener(tcl);
        return view;
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
                    replyPopup = new CommentsPopupWindow(activity, itemsOnClick);
                    replyPopup.setBtnCommetnText("回复");
                    replyPopup.showAtLocation(activity.findViewById(R.id.rl_detail),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
                    replyPopup.setFocusable(true);
                    replyPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    replyPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    mReplyComment = list.get(position);
                    mCommentUserNName = list.get(position).getCommentUserNickName();
                    System.out.println(mCommentUserNickName + "____mCommentUserNickName________" + nickName + "______" + mComment.getCommentUserNickName());
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
                    BmobPushManager bmobPush = new BmobPushManager(context);
                    BmobQuery<MyBmobInstallation> query = new BmobQuery();
                    System.out.println(mCommentUserNName + "____mCommentUserNickName________" + nickName + "______" + mComment.getCommentUserNickName());
                    DBHelper.saveReply(context, replyPopup, mUser,
                            nickName, mCommentUserNName, mReplyComment, mReplyList);
                    if (mUser.getNickName().equals(nickName)) {
                        query.addWhereEqualTo("phoneNumber", mComment.getCommentUserId().getMobilePhoneNumber());
                    } else if (mUser.getNickName().equals(mCommentUserNickName)) {
                        query.addWhereEqualTo("phoneNumber", mUser.getMobilePhoneNumber());
                    }
                    bmobPush.setQuery(query);
                    bmobPush.pushMessage(replyPopup.getComments());
                    Toast.makeText(context, replyPopup.getComments(), Toast.LENGTH_SHORT).show();
                    replyAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

}



