package com.edu.claf.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BitmapUtils;
import com.edu.claf.Utils.BmobCacheUtils;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.LableUtils;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.holder.InfoHolder;
import com.edu.claf.view.CustomListView;
import com.edu.claf.view.ProgressView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class FoundRecordActivity extends AppCompatActivity {

    @InjectView(R.id.lv_found_record)
    CustomListView lvFoundRecord;
    @InjectView(R.id.ib_found_record_back)
    ImageButton ibFoundRecordBack;
    @InjectView(R.id.tv_found_record_empty)
    TextView tvFoundRecordEmpty;
    @InjectView(R.id.fl_net_error)
    FrameLayout flNetError;
    @InjectView(R.id.btn_reconnect)
    Button btnReconnect;

    private DBHelper<FoundInfo> foundHelper;
    private List<FoundInfo> mFoundRecord;
    private User mUser;
    private String mDeleteObjectId;
    private static boolean mDeleteFlag = false;
    private MyFoundRecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_found_record);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        tvFoundRecordEmpty.setVisibility(View.GONE);
        flNetError.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        mFoundRecord = new ArrayList<>();
        foundHelper = new DBHelper<>();
        mUser = BmobUser.getCurrentUser(this, User.class);
        if (mUser != null) {
            mFoundRecord = getFoundData();
            if (mFoundRecord.size() != 0) {
                recordAdapter = new MyFoundRecordAdapter();
                lvFoundRecord.setAdapter(recordAdapter);
            } else {
                tvFoundRecordEmpty.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
        }
    }

    private List<FoundInfo> getFoundData() {
        BmobQuery<FoundInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("publishUser", new BmobPointer(mUser));
        query.setLimit(50);
        query.include("publishUser");
        query = BmobCacheUtils.setQueryCacheConditon(query, FoundRecordActivity.this, FoundInfo.class);
        query.findObjects(this, new FindListener<FoundInfo>() {
            @Override
            public void onSuccess(List<FoundInfo> list) {
                if (list.size() == 0){
                    tvFoundRecordEmpty.setVisibility(View.VISIBLE);
                }
                if (mDeleteFlag){
                    mFoundRecord.clear();
                }
                mFoundRecord.addAll(list);
                lvFoundRecord.setAdapter(new MyFoundRecordAdapter());
                flNetError.setVisibility(View.GONE);
                ProgressView.progressDialogDismiss();
            }

            @Override
            public void onError(int i, String s) {
                mFoundRecord = null;
                if (i == 9010) {
                    tvFoundRecordEmpty.setVisibility(View.GONE);
                    flNetError.setVisibility(View.VISIBLE);
                    Toast.makeText(FoundRecordActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                } else if (i == 9016) {
                    flNetError.setVisibility(View.VISIBLE);
                    tvFoundRecordEmpty.setVisibility(View.GONE);
                    Toast.makeText(FoundRecordActivity.this, "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                }
                ProgressView.progressDialogDismiss();
            }
        });
        return mFoundRecord;
    }

    @OnClick(R.id.btn_reconnect)
    public void reLoadData(View view){
        initData();
    }


    private class MyFoundRecordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFoundRecord.size();
        }

        @Override
        public Object getItem(int position) {
            return mFoundRecord.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            InfoHolder holder = null;
            if (convertView == null) {
                holder = new InfoHolder();
                view = View.inflate(FoundRecordActivity.this, R.layout.item_lost_list, null);
                holder.tvUserName = (TextView) view.findViewById(R.id.tv_lost_public_user_name);
                holder.tvInfoDate = (TextView) view.findViewById(R.id.tv_lost_public_time);
                holder.tvInfoTitle = (TextView) view.findViewById(R.id.tv_lost_public_title);
                holder.ivInfoDelete = (ImageView) view.findViewById(R.id.iv_lost_info_delete);
                holder.tvInfoLocation = (TextView) view.findViewById(R.id.tv_lost_public_location);
                holder.tvInfoDesc = (TextView) view.findViewById(R.id.tv_lost_public_desc);
                holder.llInfoItemBottom = (LinearLayout) view.findViewById(R.id.ll_lost_item_bottom);
                holder.tvLable = (TextView) view.findViewById(R.id.label);
                holder.ivInfoPhoto = (ImageView) view.findViewById(R.id.iv_head_image);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (InfoHolder) view.getTag();

            }
            String realPath = FileUtils.getIconDir(mUser.getMobilePhoneNumber()) + File.separator + mUser.getMobilePhoneNumber() + ".jpg";
            if (realPath != null) {
                holder.ivInfoPhoto.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 1));
            }
            holder.llInfoItemBottom.setVisibility(View.GONE);
            holder.tvUserName.setText(mUser.getNickName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = mFoundRecord.get(position).getFoundDate().getDate();
            try {
                Date lostDate = dateFormat.parse(date);
                String format = dateFormat.format(lostDate);
                holder.tvInfoDate.setText("发布时间:" + format);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.ivInfoDelete.setVisibility(View.VISIBLE);
            holder.tvInfoTitle.setText(mFoundRecord.get(position).getFoundTitle());
            holder.tvInfoLocation.setText("丢失地点：" + mFoundRecord.get(position).getFoundLocation());
            holder.tvInfoDesc.setText("丢失描述:\n" + mFoundRecord.get(position).getFoundDesc());
            holder.ivInfoDelete.setOnClickListener(new deleteListener(position));
            LableUtils.setLableTypeImage(holder, mFoundRecord.get(position).getFoundType());
            return view;
        }
    }

    private class deleteListener implements View.OnClickListener {
        private int position;

        public deleteListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            mDeleteObjectId = mFoundRecord.get(position).getObjectId();
            deleteLostInfo(mDeleteObjectId);
            initData();
            if (recordAdapter != null) {
                recordAdapter.notifyDataSetChanged();
            }
        }
    }

    private void deleteLostInfo(final String mDeleteObjectId) {
        FoundInfo foundInfo = new FoundInfo();
        foundInfo.setObjectId(mDeleteObjectId);
        foundInfo.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(FoundRecordActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                mDeleteFlag = true;
                if (recordAdapter != null) {
                    recordAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(FoundRecordActivity.this, "删除失败" + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }

}
