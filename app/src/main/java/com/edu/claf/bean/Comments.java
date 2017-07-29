package com.edu.claf.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;


public class Comments extends BmobObject {


    
    private User commentUserId;
    
    private String commentedUserId;


    
    private BmobDate commentTime;
    
    private String commentContext;

    private String commentType;
    private String commentUserNickName;

    private LostInfo lostInfoId;

    private FoundInfo foundInfoId;

    public FoundInfo getFoundInfoId() {
        return foundInfoId;
    }

    public void setFoundInfoId(FoundInfo foundInfoId) {
        this.foundInfoId = foundInfoId;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }



    private List<Reply> replyList;
    public String getCommentedUserId() {
        return commentedUserId;
    }

    public void setCommentedUserId(String commentedUserId) {
        this.commentedUserId = commentedUserId;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }


    public String getCommentUserNickName() {
        return commentUserNickName;
    }

    public void setCommentUserNickName(String commentUserNickName) {
        this.commentUserNickName = commentUserNickName;
    }

    public LostInfo getLostInfoId() {
        return lostInfoId;
    }

    public void setLostInfoId(LostInfo lostInfoId) {
        this.lostInfoId = lostInfoId;
    }


    public User getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(User commentUserId) {
        this.commentUserId = commentUserId;
    }

    public BmobDate getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(BmobDate commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentContext() {
        return commentContext;
    }

    public void setCommentContext(String commentContext) {
        this.commentContext = commentContext;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "commentUserId='" + commentUserId + '\'' +
                ", commentedUserId='" + commentedUserId + '\'' +
                ", commentTime=" + commentTime +
                ", commentContext='" + commentContext + '\'' +
                ", commentUserNickName='" + commentUserNickName + '\'' +
                ", lostInfoId=" + lostInfoId +
                ", replyList=" + replyList +
                '}';
    }
}
