package com.edu.claf.dao;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.claf.R;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.ListViewUtility;
import com.edu.claf.adapter.CommentAdapter;
import com.edu.claf.bean.Comments;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.Reply;
import com.edu.claf.bean.User;
import com.edu.claf.view.CommentsPopupWindow;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class DBHelper<T> {

    private static boolean isComment = false;
    private static final int LIMIT = 50;
    private static List replyList;
    private static List commentList;

    public List<T> getRecord(Context context, User user) {
        final List<T> lists = new ArrayList<>();
        BmobQuery<T> query = new BmobQuery<>();
        query.addWhereEqualTo("publishUser", new BmobPointer(user));
        query.setLimit(50);
        query.findObjects(context, new FindListener<T>() {
            @Override
            public void onSuccess(List<T> list) {
                lists.addAll(list);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        if (lists.size() != 0) {
            return lists;
        } else {
            return null;
        }
    }


    public static void saveComments(final Context context, String commentType,
                                    Object commentsInfo, String mCommentedUserId, CommentsPopupWindow commentsPopup) {
        Comments comments = new Comments();
        comments.setCommentContext(commentsPopup.getComments());
        comments.setCommentTime(new BmobDate(new Date(System.currentTimeMillis())));
        comments.setCommentUserId(BmobUser.getCurrentUser(context, User.class));
        comments.setCommentUserNickName(BmobUser.getCurrentUser(context, User.class).getNickName());
        if (commentType.equals("lost")) {
            comments.setLostInfoId((LostInfo) commentsInfo);
        } else if (commentType.equals("found")) {
            comments.setFoundInfoId((FoundInfo) commentsInfo);
        }
        comments.setCommentUserNickName(BmobUser.getCurrentUser(context, User.class).getNickName());
        comments.setCommentType(commentType);
        comments.setCommentedUserId(mCommentedUserId);
        comments.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "发表评论成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "发表评论失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static void saveReply(final Context context, CommentsPopupWindow replyPopup, User mUser, String nickName,
                                 String mCommentUserNickName, final Comments mReplyComment, final List mReplyList) {
        final Reply reply = new Reply();
        reply.setReplyTime(new BmobDate(new Date(System.currentTimeMillis())));
        reply.setReplyContent(replyPopup.getComments());
        reply.setReplyUserNickName(mUser.getNickName());
        if (mUser.getNickName().equals(nickName)) {
            reply.setCommentNickName(mCommentUserNickName);
        } else {
            reply.setCommentNickName(nickName);
        }
        reply.setComments(mReplyComment);
        System.out.println(mReplyComment);
        reply.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                mReplyList.add(reply);
                updateComment(context, mReplyComment, reply, mReplyList);

                Toast.makeText(context, "回复成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "回复失败" + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void updateComment(final Context context, Comments comment, Reply reply, List mReplyList) {
        comment.setValue("replyList", mReplyList);
        comment.update(context, comment.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "回复成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "回复失败" + s, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static Comments findCommentsReply(final User mUser,final String nickName,
                                             final Activity activity,Context context,
                                             final Comments comment, final ListView listView,final List mReplyList,
                                             final List mCommentsList) {
        BmobQuery<Reply> query = new BmobQuery<>();
        query.setLimit(50);
        query.addWhereEqualTo("comments", comment);
        query.findObjects(context, new FindListener<Reply>() {
            @Override
            public void onSuccess(List<Reply> list) {
                comment.setReplyList(list);
                listView.setAdapter(new CommentAdapter(mUser, nickName, mReplyList, R.layout.comment_item,
                        activity, mCommentsList, activity));
                ListViewUtility.setListViewHeightBasedOnChildren(listView);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        return comment;
    }

    public static void loadCommnets(Object infoType, final List mCommentsList
            , final ListView listView, final User mUser, final String nickName, final List mReplyList
            , final Activity activity) {
        BmobQuery<Comments> queryComments = new BmobQuery<>();
        queryComments.setLimit(LIMIT);
        queryComments.order("-createdAt");
        queryComments.include("commentUserId");
        queryComments.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        queryComments.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));
        boolean hasCache = queryComments.hasCachedResult(activity, Comments.class);
        if (hasCache) {
            queryComments.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK); 
            System.out.println("has cache");
        } else {
            queryComments.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            System.out.println("no cache");
        }
        if (infoType instanceof LostInfo) {
            queryComments.addWhereEqualTo("lostInfoId", infoType);
        } else if (infoType instanceof FoundInfo) {
            queryComments.addWhereEqualTo("foundInfoId", infoType);
        }
        queryComments.findObjects(activity, new FindListener<Comments>() {
            @Override
            public void onSuccess(List<Comments> list) {
                for (Comments comment : list) {
                    mCommentsList.add(DBHelper.findCommentsReply(mUser,nickName,activity,activity,comment,
                            listView,mReplyList,mCommentsList));
                }


            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(activity, "查询失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static List getCommentList() {
        if (isComment) {
            return commentList;
        } else
            return null;
    }

    public static List getReplyList() {
        return replyList;
    }




    public static void saveUrlToJson(List mPhotoUrlList) {
        Gson gson = new Gson();
        String userJson = gson.toJson(mPhotoUrlList);
        File imageDir = FileUtils.getImageDir();
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        File file = new File(FileUtils.getImageDir(), "photoUrl.json");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(userJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
