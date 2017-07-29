package com.edu.claf.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;


public class MyBmobInstallation extends BmobInstallation {

    String phoneNumber;

    public MyBmobInstallation(Context context) {
        super(context);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
