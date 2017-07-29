package com.edu.claf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifyActivity extends AppCompatActivity {

    private static List<Map<String, Object>> lstImageItem;

    private final int[] icon = {R.drawable.all_ch, R.drawable.purse_ch, R.drawable.card_ch
            , R.drawable.keys_ch, R.drawable.digital_ch, R.drawable.ring_ch
            , R.drawable.cloth_ch, R.drawable.file_ch, R.drawable.book_ch
            , R.drawable.pat_ch, R.drawable.car_ch, R.drawable.people_ch
            , R.drawable.others_ch};

    private final String[] iconName = {"全部", "钱包", "证件", "钥匙", "数码", "饰品", "衣服"
            , "文件", "书籍", "宠物", "车辆", "找人", "其他"};

    private GridView gvClassify;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_classify);
        BaseApplication.getApplication().addActivity(this);
        lstImageItem = new ArrayList<>();
        lstImageItem = initClassifyGrid();
        gvClassify = (GridView) findViewById(R.id.gv_classify);
        simpleAdapter = new SimpleAdapter(this, lstImageItem, R.layout.night_item,
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.ItemImage, R.id.ItemText});
        gvClassify.setAdapter(simpleAdapter);
        gvClassify.setOnItemClickListener(new ItemOnclickListener());
    }

    public void backLostOrFound(View view) {
        onBackPressed();
    }

    public List<Map<String, Object>> initClassifyGrid() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", icon[i]);
            map.put("ItemText", iconName[i]);
            lstImageItem.add(map);
        }
        return lstImageItem;
    }

    private class ItemOnclickListener implements AdapterView.OnItemClickListener {
        private static final int TYPE_ALL = 0;
        private static final int TYPE_WALLET = 1;
        private static final int TYPE_CERTIFICATE = 2;
        private static final int TYPE_KEY = 3;
        private static final int TYPE_DIGIT = 4;
        private static final int TYPE_DECORATION = 5;
        private static final int TYPE_CLOTHES = 6;
        private static final int TYPE_FILE = 7;
        private static final int TYPE_BOOK = 8;
        private static final int TYPE_PET = 9;
        private static final int TYPE_CAR = 10;
        private static final int TYPE_PERSON = 11;
        private static final int TYPE_OTHER = 12;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String flag = getIntent().getStringExtra("lost");
            String type = null;
            if (flag.equals("lost")) { 
                type = getType(position);
                System.out.println(type);
                Intent intent = new Intent();
                intent.putExtra("type", type);
                SharedPrefUtils.writeString(ClassifyActivity.this, "main_lost_classify", type);
                ClassifyActivity.this.setResult(RESULT_OK, intent);
            } else if (flag.equals("found")) { 
                type = getType(position);
                System.out.println(type);
                Intent intent = new Intent();
                intent.putExtra("type", type);
                SharedPrefUtils.writeString(ClassifyActivity.this, "main_lost_classify", type);
                ClassifyActivity.this.setResult(201, intent);
            }
            onBackPressed();
            ClassifyActivity.this.finish();

        }

        private String getType(int position) {
            switch (position) {
                case TYPE_ALL:
                    return "全部";
                case TYPE_WALLET:
                    return "钱包";
                case TYPE_CERTIFICATE:
                    return "证件";
                case TYPE_KEY:
                    return "钥匙";
                case TYPE_DIGIT:
                    return "数码";
                case TYPE_DECORATION:
                    return "饰品";
                case TYPE_CLOTHES:
                    return "衣服";
                case TYPE_FILE:
                    return "文件";
                case TYPE_BOOK:
                    return "书籍";
                case TYPE_PET:
                    return "宠物";
                case TYPE_CAR:
                    return "车辆";
                case TYPE_PERSON:
                    return "找人";
                case TYPE_OTHER:
                    return "其他";
                default:
                    break;
            }
            return null;
        }
    }
}
