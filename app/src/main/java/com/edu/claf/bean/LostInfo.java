package com.edu.claf.bean;


import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

public class LostInfo extends BmobObject {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    private String lostTitle;

    private BmobDate lostDate;

    private String lostLocation;

    private String contactNumber;

    private String lostWhat;

    private String lostType;

    private String lostDesc;

    private Float lostLatitude;
    private Float lostLongtude;

    private BmobDate publishTime;

    private String commitPerson;

    private String publishUserSchool;

    private String shareNumber;

    private String commentsNumber;

    private String lookPersons;

    private String lostImage;

    private User publishUser;

    private String personLocation;

    private String personSchool;

    public User getPublishUser() {
        return publishUser;
    }


    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public String getPersonSchool() {
        return personSchool;
    }

    public void setPersonSchool(String personSchool) {
        this.personSchool = personSchool;
    }


    public String getPublishUserSchool() {
        return publishUserSchool;
    }

    public void setPublishUserSchool(String publishUserSchool) {
        this.publishUserSchool = publishUserSchool;
    }

    public String getLostType() {
        return lostType;
    }

    public void setLostType(String lostType) {
        this.lostType = lostType;
    }

    public String getLostWhat() {
        return lostWhat;
    }

    public void setLostWhat(String lostWhat) {
        this.lostWhat = lostWhat;
    }

    public Float getLostLatitude() {
        return lostLatitude;
    }

    public void setLostLatitude(Float lostLatitude) {
        this.lostLatitude = lostLatitude;
    }

    public Float getLostLongtude() {
        return lostLongtude;
    }

    public void setLostLongtude(Float lostLongtude) {
        this.lostLongtude = lostLongtude;
    }

    public String getCommitPerson() {
        return commitPerson;
    }

    public void setCommitPerson(String commitPerson) {
        this.commitPerson = commitPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(String shareNumber) {
        this.shareNumber = shareNumber;
    }

    public String getCommentsNumber() {
        return commentsNumber;
    }

    public void setCommentsNumber(String commentsNumber) {
        this.commentsNumber = commentsNumber;
    }

    public String getLookPersons() {
        return lookPersons;
    }

    public void setLookPersons(String lookPersons) {
        this.lookPersons = lookPersons;
    }

    public BmobDate getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(BmobDate publishTime) {
        this.publishTime = publishTime;
    }

    public String getLostImage() {
        return lostImage;
    }

    public void setLostImage(String lostImage) {
        this.lostImage = lostImage;
    }


    public String getPersonLocation() {
        return personLocation;
    }

    public void setPersonLocation(String personLocation) {
        this.personLocation = personLocation;
    }

    public BmobDate getLostDate() {
        return lostDate;
    }

    public void setLostDate(BmobDate lostDate) {
        this.lostDate = lostDate;
    }

    public String getLostTitle() {
        return lostTitle;
    }

    public void setLostTitle(String lostTitle) {
        this.lostTitle = lostTitle;
    }

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }

    public String getLostDesc() {
        return lostDesc;
    }

    public void setLostDesc(String lostDesc) {
        this.lostDesc = lostDesc;
    }
}
