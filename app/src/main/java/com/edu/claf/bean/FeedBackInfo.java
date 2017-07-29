package com.edu.claf.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class FeedBackInfo extends BmobObject {

    private String feedbackUserName;
    private BmobDate feedbackDate;
    private String feedbackContent;
    private String feedbackContactWay;

    public String getFeedbackUserName() {
        return feedbackUserName;
    }

    public void setFeedbackUserName(String feedbackUserName) {
        this.feedbackUserName = feedbackUserName;
    }

    public BmobDate getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(BmobDate feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getFeedbackContactWay() {
        return feedbackContactWay;
    }

    public void setFeedbackContactWay(String feedbackContactWay) {
        this.feedbackContactWay = feedbackContactWay;
    }
}
