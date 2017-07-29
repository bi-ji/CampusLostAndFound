package com.edu.claf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BitmapUtils;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.UIUtils;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;


public class PersonCentralActivity extends AppCompatActivity {

    private static List<Map<String, Object>> lstImageItem;

    private int[] icon = {R.drawable.lost_record, R.drawable.found_recod, R.drawable.setting
            , R.drawable.share, R.drawable.blush, R.drawable.feedback
            , R.drawable.update, R.drawable.about_us, R.drawable.exit};

    private String[] iconName = {"失物记录", "招领记录", "账户设置",
            "软件分享", "清空缓存", "意见反馈",
            "系统更新", "关于应用", "退出登录"};
    private long mExitTime;
    private static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_peron_central);
        BaseApplication.getApplication().addActivity(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
        lstImageItem = new ArrayList<>();
        lstImageItem = initNightGrid();
    }

    public List<Map<String, Object>> initNightGrid() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", icon[i]);
            map.put("ItemText", iconName[i]);
            lstImageItem.add(map);
        }
        return lstImageItem;
    }

    public void login(View view) {
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
    public static class PlaceholderFragment extends Fragment {
        TextView tvLoginDesc;
        ImageView ivUserLogo;
        Button btnUserLogin;
        private static final int LOST_RECORD = 0;
        private static final int FOUND_RECORD = 1;
        private static final int ACCOUNT_SETTING = 2;
        private static final int SHARE_APP = 3;
        private static final int CLEAN_CATCH = 4;
        private static final int FEED_BACK = 5;
        private static final int SYSTEM_UPDATE = 6;
        private static final int ABOUT_APP = 7;
        private static final int EXIT = 8;
        private GridView gvPersonCentral;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_person_central,
                    container, false);
            gvPersonCentral = (GridView) rootView.findViewById(R.id.gv_person_central);
            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), lstImageItem, R.layout.night_item,
                    new String[]{"ItemImage", "ItemText"},
                    new int[]{R.id.ItemImage, R.id.ItemText});
            tvLoginDesc = (TextView) rootView.findViewById(R.id.tv_login_desc);
            ivUserLogo = (ImageView) rootView.findViewById(R.id.iv_user_logo);
            btnUserLogin = (Button) rootView.findViewById(R.id.btn_user_login);
            user = BmobUser.getCurrentUser(getActivity(), User.class);
            if (user != null) {
                ivUserLogo.setVisibility(View.VISIBLE);
                btnUserLogin.setVisibility(View.GONE);
                tvLoginDesc.setText(user.getNickName());
                String realPath = FileUtils.getIconDir(user.getMobilePhoneNumber()) + File.separator + user.getMobilePhoneNumber() + ".jpg";
                ivUserLogo.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 1));
                System.out.println(realPath);
            } else {
                ivUserLogo.setVisibility(View.GONE);
                btnUserLogin.setVisibility(View.VISIBLE);
                tvLoginDesc.setText("您还未登录，请点击登录");
            }

            gvPersonCentral.setAdapter(simpleAdapter);
            gvPersonCentral.setOnItemClickListener(new ItemOnClickListener());
            return rootView;
        }
        public void setPortrait(){
            String realPath = FileUtils.getIconDir(user.getMobilePhoneNumber()) + File.separator + user.getMobilePhoneNumber() + ".jpg";
            ivUserLogo.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 1));
            System.out.println(realPath);
        }


        private class ItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pageServlet(position);
            }
        }

        public void pageServlet(int position) {
            switch (position) {
                case LOST_RECORD: 
                    if (user != null) {
                        startActivity(new Intent(getActivity(), LostRecordActivity.class));
                    }
                    else{
                        Toast.makeText(getActivity(),"请登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FOUND_RECORD:
                    if (user != null) {
                        startActivity(new Intent(getActivity(), FoundRecordActivity.class));
                    }
                    else{
                        Toast.makeText(getActivity(),"请登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case EXIT: 
                    if (user != null){
                        ProgressView.getProgressDialog(getActivity(), "正在退出");
                        BmobUser.logOut(getActivity());
                        BmobUser currentUser = BmobUser.getCurrentUser(getActivity());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("type","exitApp");
                        startActivity(intent);
                        ProgressView.progressDialogDismiss();
                    }else{
                        Toast.makeText(getActivity(),"您未登录，无需退出",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ACCOUNT_SETTING: 
                    if (user != null) {
                        startActivity(new Intent(getActivity(), AccountSettingActivity.class));
                    }else{
                        Toast.makeText(getActivity(),"请登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FEED_BACK: 
                    startActivity(new Intent(getActivity(), FeedBackActivity.class));
                    break;
                case SHARE_APP:
                    Intent shareIntent = new Intent("android.intent.action.SEND");
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                    shareIntent.putExtra("android.intent.extra.TEXT", "Hi,推荐您使用软件：" + "下载地址：");
                    this.startActivity(Intent.createChooser(shareIntent, "分享"));
                    break;
                case CLEAN_CATCH:
                    BmobQuery.clearAllCachedResults(UIUtils.getContext());
                    Toast.makeText(getActivity(), "清理完成", Toast.LENGTH_SHORT).show();
                    break;
                case SYSTEM_UPDATE:
                    startActivity(new Intent(getActivity(), SystemUpdateActivity.class));
                    break;
                case ABOUT_APP: 
                    startActivity(new Intent(getActivity(), AboutAppActivity.class));
                    break;

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                BaseApplication.getApplication().exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
