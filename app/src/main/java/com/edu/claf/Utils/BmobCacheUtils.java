package com.edu.claf.Utils;

import android.content.Context;

import com.edu.claf.view.ProgressView;

import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;

public class BmobCacheUtils {
    public static BmobQuery setQueryCacheConditon(BmobQuery query,Context context,Class clazz){

        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));
        final boolean isCache = query.hasCachedResult(context, clazz);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK); 
        } else {
            ProgressView.getProgressDialog(context, "正在加载...");
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        return query;
    }
}
