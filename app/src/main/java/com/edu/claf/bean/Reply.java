package com.edu.claf.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Reply extends BmobObject {

    private int id;                    
    private String replyAccount;    
    private String replyNickname;    
    private String commentAccount;    
    private BmobDate replyTime; 

    private String replyContent;    

    private Comments comments;

    private String replyUserNickName; 

    private String commentNickName;

    public String getCommentNickName() {
        return commentNickName;
    }

    public void setCommentNickName(String commentNickName) {
        this.commentNickName = commentNickName;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getReplyUserNickName() {
        return replyUserNickName;
    }

    public void setReplyUserNickName(String replyUserNickName) {
        this.replyUserNickName = replyUserNickName;
    }

    public BmobDate getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(BmobDate replyTime) {
        this.replyTime = replyTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReplyAccount() {
        return replyAccount;
    }

    public void setReplyAccount(String replyAccount) {
        this.replyAccount = replyAccount;
    }

    public String getReplyNickname() {
        return replyNickname;
    }

    public void setReplyNickname(String replyNickname) {
        this.replyNickname = replyNickname;
    }

    public String getCommentAccount() {
        return commentAccount;
    }

    public void setCommentAccount(String commentAccount) {
        this.commentAccount = commentAccount;
    }


    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", replyAccount='" + replyAccount + '\'' +
                ", replyNickname='" + replyNickname + '\'' +
                ", commentAccount='" + commentAccount + '\'' +
                ", replyTime=" + replyTime +
                ", replyContent='" + replyContent + '\'' +
                ", comments=" + comments +
                ", replyUserNickName='" + replyUserNickName + '\'' +
                ", commentNickName='" + commentNickName + '\'' +
                '}';
    }
}
