package com.edu.claf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edu.claf.bean.User;

import java.util.Date;
import java.util.List;


public class InfoAdapter<T> extends BaseAdapter {

    private int resourceId;

    private List<T> mInfos;

    private LayoutInflater inflater;

    private Context context;

    public Date infoDate;

    public  Date publishDate;

    private User mUser;

    public InfoAdapter(){

    }

    public InfoAdapter(Context context,List<T> mInfos) {
        this.mInfos = mInfos;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }


}
