package com.edu.claf.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {

    public static final String SHARED_PREF_NAME = "shrf_first";


    private SharedPrefUtils(){

    }

    public static  boolean getBoolean(Context context,String key,boolean defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return preferences.getBoolean(key,defaultValue);
    }

    public static void setBoolean(Context context,String key,boolean value){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key,value).commit();
    }
    public static void writeString(Context context,String key,String value){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).commit();
    }

    public static void writeInt(Context context,String key,int value){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        preferences.edit().putInt(key, value).commit();
    }

    public static String readString(Context context,String key,String defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return  preferences.getString(key,defaultValue);
    }
    public static int readInt(Context context,String key,int defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return  preferences.getInt(key,defaultValue);
    }
}
