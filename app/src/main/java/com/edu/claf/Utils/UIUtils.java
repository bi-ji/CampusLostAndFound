package com.edu.claf.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.edu.claf.BaseApplication;


public class UIUtils {
    public static String[] getStringArray(int ResId){
        return  getResources().getStringArray(ResId);
    }

    public static Resources getResources() {
        return BaseApplication.getApplication().getResources();
    }

    public static  int dip2px(int dip){
        final  float scale = getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }

    public static  int px2dip(int px){
        final  float scale = getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }

    public static  void runOnUiThread(Runnable runnable){
        if (android.os.Process.myTid() == BaseApplication.mainTid){
            runnable.run();
        }else{
            BaseApplication.getHandler().post(runnable);
        }
    }

    public static Context getContext(){
        return BaseApplication.getApplication();
    }

    public static Drawable getDrawalbe(int ResId) {
        return getResources().getDrawable(ResId);
    }
}
