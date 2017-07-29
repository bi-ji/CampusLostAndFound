package com.edu.claf.bean;

import java.sql.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class FoundInfo extends BmobObject {
    private String userName;
    private BmobDate foundDate;
    private String foundTitle;
    private String foundLocation;
    private String foundDesc;
    private String foundWhat;
    private Float foundLatitude;
    private Float foundLongtude;
    private String commitPerson;
    private String contactNumber;
    private String shareNumber;
    private String commentsNumber;
    private String lookPersons;
    private String foundImage;
    private String personPortrait;
    private String personLocation;
    private User publishUser;

    private String foundType;

    private String publishUserSchool;

    private BmobDate publishTime;

    public BmobDate getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(BmobDate publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishUserSchool() {
        return publishUserSchool;
    }

    public void setPublishUserSchool(String publishUserSchool) {
        this.publishUserSchool = publishUserSchool;
    }

    public String getFoundType() {
        return foundType;
    }

    public void setFoundType(String foundType) {
        this.foundType = foundType;
    }

    public User getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public String getFoundWhat() {
        return foundWhat;
    }

    public void setFoundWhat(String foundWhat) {
        this.foundWhat = foundWhat;
    }

    public Float getFoundLatitude() {
        return foundLatitude;
    }

    public void setFoundLatitude(Float foundLatitude) {
        this.foundLatitude = foundLatitude;
    }

    public Float getFoundLongtude() {
        return foundLongtude;
    }

    public void setFoundLongtude(Float foundLongtude) {
        this.foundLongtude = foundLongtude;
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


    public String getFoundImage() {
        return foundImage;
    }

    public void setFoundImage(String foundImage) {
        this.foundImage = foundImage;
    }

    public String getPersonPortrait() {
        return personPortrait;
    }

    public void setPersonPortrait(String personPortrait) {
        this.personPortrait = personPortrait;
    }

    public String getPersonLocation() {
        return personLocation;
    }

    public void setPersonLocation(String personLocation) {
        this.personLocation = personLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BmobDate getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(BmobDate foundDate) {
        this.foundDate = foundDate;
    }

    public String getFoundTitle() {
        return foundTitle;
    }

    public void setFoundTitle(String foundTitle) {
        this.foundTitle = foundTitle;
    }

    public String getFoundLocation() {
        return foundLocation;
    }

    public void setFoundLocation(String foundLocation) {
        this.foundLocation = foundLocation;
    }

    public String getFoundDesc() {
        return foundDesc;
    }

    public void setFoundDesc(String foundDesc) {
        this.foundDesc = foundDesc;
    }
}
