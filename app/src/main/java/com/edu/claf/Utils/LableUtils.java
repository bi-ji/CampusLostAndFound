package com.edu.claf.Utils;

import android.widget.TextView;

import com.edu.claf.R;
import com.edu.claf.holder.InfoHolder;


public class LableUtils {


    public static void setLableDetailImage(TextView tvLable,String infoType){
        switch (infoType) {
            case "钱包":
                tvLable.setBackgroundResource(R.drawable.lable_purse);
                break;
            case "证件":
                tvLable.setBackgroundResource(R.drawable.lable_card);
                break;
            case "钥匙":
                tvLable.setBackgroundResource(R.drawable.lable_keys);
                break;
            case "数码":
                tvLable.setBackgroundResource(R.drawable.lable_digital);
                break;
            case "饰品":
                tvLable.setBackgroundResource(R.drawable.lable_ring);
                break;
            case "衣服":
                tvLable.setBackgroundResource(R.drawable.lable_clothes);
                break;
            case "文件":
                tvLable.setBackgroundResource(R.drawable.lable_file);
                break;
            case "书籍":
                tvLable.setBackgroundResource(R.drawable.label_book);
                break;
            case "宠物":
                tvLable.setBackgroundResource(R.drawable.lable_pat);
                break;
            case "车辆":
                tvLable.setBackgroundResource(R.drawable.lable_car);
                break;
            case "找人":
                tvLable.setBackgroundResource(R.drawable.lable_people);
                break;
            case "其他":
                tvLable.setBackgroundResource(R.drawable.lable_more);
                break;
        }
    }

    public static void setLableTypeImage(InfoHolder holder, String infoLableType) {
        switch (infoLableType) {
            case "钱包":
                holder.tvLable.setBackgroundResource(R.drawable.lable_purse);
                break;
            case "证件":
                holder.tvLable.setBackgroundResource(R.drawable.lable_card);
                break;
            case "钥匙":
                holder.tvLable.setBackgroundResource(R.drawable.lable_keys);
                break;
            case "数码":
                holder.tvLable.setBackgroundResource(R.drawable.lable_digital);
                break;
            case "饰品":
                holder.tvLable.setBackgroundResource(R.drawable.lable_ring);
                break;
            case "衣服":
                holder.tvLable.setBackgroundResource(R.drawable.lable_clothes);
                break;
            case "文件":
                holder.tvLable.setBackgroundResource(R.drawable.lable_file);
                break;
            case "书籍":
                holder.tvLable.setBackgroundResource(R.drawable.label_book);
                break;
            case "宠物":
                holder.tvLable.setBackgroundResource(R.drawable.lable_pat);
                break;
            case "车辆":
                holder.tvLable.setBackgroundResource(R.drawable.lable_car);
                break;
            case "找人":
                holder.tvLable.setBackgroundResource(R.drawable.lable_people);
                break;
            case "其他":
                holder.tvLable.setBackgroundResource(R.drawable.lable_more);
                break;
        }
    }

}
