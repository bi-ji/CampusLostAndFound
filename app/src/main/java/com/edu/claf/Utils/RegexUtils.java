package com.edu.claf.Utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static boolean isPhoneNumber(String phoneNumber){
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        return  m.matches();
    }


    public static boolean passwordLength(String password){
        return !(password.length() < 6 || password.length() > 18);
    }

    public static boolean vertifyPublishInfo(String lTitle, String lTime, String lLocation, String lDesc, String lPhone,String type) {
        return !TextUtils.isEmpty(lTitle) && !TextUtils.isEmpty(lTime) && !TextUtils.isEmpty(lLocation)
                && !TextUtils.isEmpty(lDesc) && !TextUtils.isEmpty(lPhone) && !TextUtils.isEmpty(type);
    }
}
