package com.edu.claf.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.edu.claf.bean.LostInfo;
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
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class LostRecordActivity extends AppCompatActivity {

    @InjectView(R.id.lv_lost_record)
    CustomListView lvLostRecord;
    @InjectView(R.id.tv_lost_record_empty)
    TextView tvLostRecordEmpty;
    @InjectView(R.id.fl_net_error)
    FrameLayout flNetError;
    @InjectView(R.id.btn_reconnect)
    Button btnReconnect;

    private static boolean mDeleteFlag = false;

    private DBHelper<LostInfo> lostHelper;
    private List<LostInfo> mLostRecord;
    private User mUser;
    private String mDeleteObjectId;
    private MyLostRecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lost_record);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        tvLostRecordEmpty.setVisibility(View.GONE);
        flNetError.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        mLostRecord = new ArrayList<>();
        lostHelper = new DBHelper<>();
        mUser = BmobUser.getCurrentUser(this, User.class);
        if (mUser != null) {
            mLostRecord = getLostData();
            if (mLostRecord.size() != 0) {
                recordAdapter = new MyLostRecordAdapter();
                lvLostRecord.setAdapter(recordAdapter);
            } else {
                tvLostRecordEmpty.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
        }
    }

    public List<LostInfo> getLostData() {
        BmobQuery<LostInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("publishUser", new BmobPointer(mUser));
        query.setLimit(50);
        query.include("publishUser");
        query = BmobCacheUtils.setQueryCacheConditon(query,LostRecordActivity.this,LostInfo.class);
        query.findObjects(this, new FindListener<LostInfo>() {
            @Override
            public void onSuccess(List<LostInfo> list) {
                if (list.size() == 0){
                    tvLostRecordEmpty.setVisibility(View.VISIBLE);
                }
                if (mDeleteFlag){
                    mLostRecord.clear();
                }
                mLostRecord.addAll(list);
                lvLostRecord.setAdapter(new MyLostRecordAdapter());
                ProgressView.progressDialogDismiss();
            }

            @Override
            public void onError(int i, String s) {
                mLostRecord = null;
                if (i == 9010) {
                    tvLostRecordEmpty.setVisibility(View.GONE);
                    flNetError.setVisibility(View.VISIBLE);
                    Toast.makeText(LostRecordActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                } else if (i == 9016) {
                    flNetError.setVisibility(View.VISIBLE);
                    tvLostRecordEmpty.setVisibility(View.GONE);
                    Toast.makeText(LostRecordActivity.this, "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                }
                ProgressView.progressDialogDismiss();
            }
        });
        return mLostRecord;
    }

    @OnClick(R.id.btn_reconnect)
    public void reLoadData(View view) {
        initData();
    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }
	
    private class MyLostRecordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mLostRecord.size();
        }

        @Override
        public Object getItem(int position) {
            return mLostRecord.get(position);
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
                view = View.inflate(LostRecordActivity.this, R.layout.item_lost_list, null);
                holder.tvUserName = (TextView) view.findViewById(R.id.tv_lost_public_user_name);
                holder.tvInfoDate = (TextView) view.findViewById(R.id.tv_lost_public_time);
                holder.tvInfoTitle = (TextView) view.findViewById(R.id.tv_lost_public_title);
                holder.tvInfoLocation = (TextView) view.findViewById(R.id.tv_lost_public_location);
                holder.tvInfoDesc = (TextView) view.findViewById(R.id.tv_lost_public_desc);
                holder.ivInfoDelete = (ImageView) view.findViewById(R.id.iv_lost_info_delete);
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
            holder.ivInfoDelete.setVisibility(View.VISIBLE);
            holder.tvUserName.setText(mUser.getNickName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = mLostRecord.get(position).getLostDate().getDate();
            try {
                Date lostDate = dateFormat.parse(date);
                String format = dateFormat.format(lostDate);
                holder.tvInfoDate.setText("发布时间:" + format);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvInfoTitle.setText(mLostRecord.get(position).getLostTitle());
            holder.tvInfoLocation.setText("丢失地点：" + mLostRecord.get(position).getLostLocation());
            holder.tvInfoDesc.setText("丢失描述:\n" + mLostRecord.get(position).getLostDesc());
            holder.ivInfoDelete.setOnClickListener(new deleteListener(position));
            LableUtils.setLableTypeImage(holder, mLostRecord.get(position).getLostType());
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
            mDeleteObjectId = mLostRecord.get(position).getObjectId();
            deleteLostInfo(mDeleteObjectId);
            initData();
            if (recordAdapter != null) {
                recordAdapter.notifyDataSetChanged();
            }
        }
    }

    private void deleteLostInfo(final String mDeleteObjectId) {
        LostInfo lostInfo = new LostInfo();
        lostInfo.setObjectId(mDeleteObjectId);
        lostInfo.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(LostRecordActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                mDeleteFlag = true;
                if (recordAdapter != null) {
                    recordAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(LostRecordActivity.this, "删除失败" + s, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
