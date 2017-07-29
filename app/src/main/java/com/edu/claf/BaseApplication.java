package com.edu.claf;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;
public class BaseApplication extends Application {

    private List<Activity> mList = new LinkedList<Activity>();
    private static BaseApplication application;
    public static Handler handler;

    public static Context context;

    public static int mainTid;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
        application = this;
        handler = new Handler();
        mainTid = android.os.Process.myTid();

    }

    public static Handler getHandler() {
        return handler;
    }

    public static Context getContext() {
        return context;
    }

    public synchronized static BaseApplication getApplication() {
        return application;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static int getMainTid() {
        return mainTid;
    }
}
