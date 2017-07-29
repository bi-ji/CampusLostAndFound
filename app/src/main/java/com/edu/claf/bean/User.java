package com.edu.claf.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {


    private String photoUrl;

    private String schoolCity;

    private String schoolName;

    private String nickName;

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "photoUrl='" + photoUrl + '\'' +
                ", schoolCity='" + schoolCity + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
