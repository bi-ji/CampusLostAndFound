package com.edu.claf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BmobCacheUtils;
import com.edu.claf.Utils.ImageLoader;
import com.edu.claf.Utils.LableUtils;
import com.edu.claf.Utils.NetStatusUtils;
import com.edu.claf.Utils.SharedPrefUtils;
import com.edu.claf.adapter.InfoAdapter;
import com.edu.claf.bean.FoundInfo;
import com.edu.claf.bean.MyBmobInstallation;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.holder.InfoHolder;
import com.edu.claf.view.CallOrSmsDialog;
import com.edu.claf.view.CommentsPopupWindow;
import com.edu.claf.view.CustomListView;
import com.edu.claf.view.ProgressView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class FoundInfoActivity extends AppCompatActivity {

    private static final int LOAD_DATA_FINISH = 10;
    private static final int REFRESH_DATA_FINISH = 11;

    private static CustomListView lvFound;

    private PlaceholderFragment fragment;

    private static ArrayList<String> mArrayList = new ArrayList<String>();

    private static List<FoundInfo> mFoundInfos = new ArrayList<>();
    private long mExitTime;
    private static BmobQuery<FoundInfo> foundQuery;
    private static int skip = 0;
    private static final int LIMIT = 5;
    private static User mUser;
    private static boolean refreshFlag = false;

    static CommentsPopupWindow commentsPopup;
    private static boolean someInfoChanged;
    private static boolean clickFlag = false;
    private static String commentsPhoneNumber;
    private static String mCommentedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_found_info);
        BaseApplication.getApplication().addActivity(this);
        fragment = new PlaceholderFragment();
        mUser = BmobUser.getCurrentUser(this, User.class);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private TextView tvFoundEmpty;
        private FrameLayout flNetError;
        private Button btnReconnect;
        private FoundInfo commentsFoundInfo;
        public MyFoundAdapter foundAdapter;
        private int itemPositon;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_found,
                    container, false);
            lvFound = (CustomListView) rootView.findViewById(R.id.lv_found_list);
            tvFoundEmpty = (TextView) rootView.findViewById(R.id.tv_found_empty);
            flNetError = (FrameLayout) rootView.findViewById(R.id.fl_net_error);
            btnReconnect = (Button) rootView.findViewById(R.id.btn_reconnect);
            tvFoundEmpty.setVisibility(View.GONE);
            flNetError.setVisibility(View.GONE);
            foundQuery = new BmobQuery<FoundInfo>();
            loadClassifyData();
            ProgressView.progressDialogDismiss();
            lvFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickFlag = true;
                    itemPositon = position;
                    if (NetStatusUtils.isNetworkAvailable(getActivity())) {
                        Intent intent = new Intent(getActivity(), FoundInfoDetailsActivity.class);
                        intent.putExtra("foundID", mFoundInfos.get((int) id).getObjectId());
                        System.out.println(mFoundInfos.get((int) id).getObjectId() + "objectID");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            lvFound.setOnRefreshListener(new CustomListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData(0);
                }
            });
            lvFound.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (refreshFlag == true) {
                        skip = LIMIT;
                        refreshFlag = false;
                    } else {
                        skip += LIMIT;
                    }
                    loadData(1);
                }
            });

            return rootView;
        }

        private void loadData(final int type) {
            new Thread() {
                @Override
                public void run() {
                    switch (type) {
                        case 0:
                            getFoundData(0);
                            break;
                        case 1:
                            getFoundData(1);
                            break;
                    }
                    SystemClock.sleep(2000);
                    if (type == 0) {
                        BmobQuery.clearAllCachedResults(getActivity());
                        handler.sendEmptyMessage(REFRESH_DATA_FINISH);
                    } else if (type == 1) {
                        handler.sendEmptyMessage(LOAD_DATA_FINISH);
                    }
                }

            }.start();

        }

        private void getFoundData(int type) {
            switch (type) {
                case 0:
                    refreshData();
                    break;
                case 1:
                    loadMoreData();
                    break;
            }
        }

        private void refreshData() {
            refreshFlag = true;
            skip = 0;
            foundQuery = setQueryCondition(getActivity(), foundQuery, skip, LIMIT);
            foundQuery.doSQLQuery(getActivity(), new SQLQueryListener<FoundInfo>() {
                @Override
                public void done(BmobQueryResult<FoundInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<FoundInfo> list = bmobQueryResult.getResults();
                        if (list.size() == LIMIT) {
                            lvFound.setCanLoadMore(true);
                            skip += LIMIT;
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvFound.setCanLoadMore(false);
                        }
                        mFoundInfos.clear();
                        mFoundInfos.addAll(0, list);
                        new MyFoundAdapter().notifyDataSetChanged();
                    }
                }
            });

        }

        private void loadMoreData() {
            foundQuery = setQueryCondition(getActivity(), foundQuery, skip, LIMIT);
            foundQuery.doSQLQuery(getActivity(), new SQLQueryListener<FoundInfo>() {
                @Override
                public void done(BmobQueryResult<FoundInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<FoundInfo> list = bmobQueryResult.getResults();
                        if (list.size() == LIMIT) {
                            lvFound.setCanLoadMore(true);
                        } else if (list.size() == 0) {
                            skip -= LIMIT;
                            lvFound.setCanLoadMore(false);
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvFound.setCanLoadMore(false);
                        }
                        mFoundInfos.addAll(list);
                        new MyFoundAdapter().notifyDataSetChanged();

                    } else {
                        if (e.getErrorCode() == 9010) {
                            ProgressView.progressDialogDismiss();
                            tvFoundEmpty.setVisibility(View.GONE);
                            flNetError.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                        } else if (e.getErrorCode() == 9016) {
                            ProgressView.progressDialogDismiss();
                            flNetError.setVisibility(View.VISIBLE);
                            tvFoundEmpty.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }


        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REFRESH_DATA_FINISH:
                        if (new MyFoundAdapter() != null) {
                            new MyFoundAdapter().notifyDataSetChanged();
                        }
                        lvFound.onRefreshComplete();
                        break;
                    case LOAD_DATA_FINISH:
                        if (new MyFoundAdapter() != null) {
                            new MyFoundAdapter().notifyDataSetChanged();
                        }
                        lvFound.onLoadMoreComplete();
                        break;
                    default:
                        break;
                }
            }
        };


        public void loadClassifyData() {
            skip = 0;
            BmobQuery<FoundInfo> query = new BmobQuery<>();
            query = setQueryCondition(getContext(), query, skip, LIMIT);
            query = BmobCacheUtils.setQueryCacheConditon(query, getActivity(), FoundInfo.class);
            query.doSQLQuery(getActivity(), new SQLQueryListener<FoundInfo>() {
                @Override
                public void done(BmobQueryResult<FoundInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<FoundInfo> list = bmobQueryResult.getResults();
                        if (list.size() == 0) {
                            tvFoundEmpty.setVisibility(View.VISIBLE);
                            ProgressView.progressDialogDismiss();
                            return;
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvFound.setCanLoadMore(false);
                            tvFoundEmpty.setVisibility(View.GONE);
                            mFoundInfos.clear();
                            mFoundInfos.addAll(0, list);
                            lvFound.requestLayout();
                            if (foundAdapter != null) {
                                foundAdapter.notifyDataSetChanged();
                            } else {
                                new MyFoundAdapter().notifyDataSetChanged();
                            }
                        } else if (list.size() == LIMIT) {
                            tvFoundEmpty.setVisibility(View.GONE);
                            lvFound.setCanLoadMore(true);
                            mFoundInfos.clear();
                            mFoundInfos.addAll(0, list);
                            lvFound.requestLayout();
                            if (foundAdapter != null) {
                                foundAdapter.notifyDataSetChanged();
                            } else {
                                new MyFoundAdapter().notifyDataSetChanged();
                            }
                        }
                        foundAdapter = new MyFoundAdapter(getActivity(), mFoundInfos);
                        lvFound.setAdapter(foundAdapter);
                        ProgressView.progressDialogDismiss();
                    } else {
                        if (e.getErrorCode() == 9010) {
                            tvFoundEmpty.setVisibility(View.GONE);
                            flNetError.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                        } else if (e.getErrorCode() == 9016) {
                            flNetError.setVisibility(View.VISIBLE);
                            tvFoundEmpty.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                        }
                        ProgressView.progressDialogDismiss();
                        btnReconnect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (NetStatusUtils.isNetworkAvailable(getActivity())) {
                                    flNetError.setVisibility(View.GONE);
                                    lvFound.onLoadMoreComplete();
                                    if (foundAdapter != null) {
                                        foundAdapter.notifyDataSetChanged();
                                    } else {
                                        new MyFoundAdapter().notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }

        private class MyFoundAdapter extends InfoAdapter<FoundInfo> {

            private ImageLoader imageLoader;

            MyFoundAdapter() {
                imageLoader = new ImageLoader(getActivity());
            }

            public MyFoundAdapter(Context context, List<FoundInfo> mInfos) {
                super(context, mInfos);
                imageLoader = new ImageLoader(getActivity());
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = null;
                InfoHolder holder = null;
                String url = mFoundInfos.get(position).getPublishUser().getPhotoUrl();
                String phone = mFoundInfos.get(position).getPublishUser().getMobilePhoneNumber();
                if (convertView == null) {
                    holder = new InfoHolder();
                    view = View.inflate(getActivity(), R.layout.item_found_list, null);
                    holder.tvUserName = (TextView) view.findViewById(R.id.tv_found_user_name);
                    holder.tvInfoDate = (TextView) view.findViewById(R.id.tv_found_publish_time);
                    holder.tvInfoTitle = (TextView) view.findViewById(R.id.tv_found_title);
                    holder.tvInfoLocation = (TextView) view.findViewById(R.id.tv_found_location);
                    holder.tvInfoDesc = (TextView) view.findViewById(R.id.tv_found_desc);
                    holder.tvLable = (TextView) view.findViewById(R.id.label);
                    holder.tvInfoTime = (TextView) view.findViewById(R.id.tv_found_time);
                    holder.btnCallPhone = (Button) view.findViewById(R.id.btn_found_call_phone);
                    holder.btnSendSms = (Button) view.findViewById(R.id.btn_found_send_sms);
                    holder.btnComments = (Button) view.findViewById(R.id.btn_found_comments);
                    holder.ivInfoPhoto = (ImageView) view.findViewById(R.id.iv_found_head_image);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (InfoHolder) view.getTag();

                }

                holder.btnComments.setTag(position);
                holder.tvUserName.setText(mFoundInfos.get(position).getPublishUser().getNickName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateFound = mFoundInfos.get(position).getFoundDate().getDate();
                String datePublish = mFoundInfos.get(position).getCreatedAt();
                try {
                    infoDate = dateFormat.parse(dateFound);
                    publishDate = dateFormat.parse(datePublish);
                    String format = dateFormat.format(infoDate);
                    String createTime = dateFormat.format(publishDate);
                    holder.tvInfoDate.setText("发布时间:" + createTime);
                    holder.tvInfoTime.setText(format);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                imageLoader.DisplayImage(url, phone, holder.ivInfoPhoto);
                holder.tvInfoTitle.setText("标题：" + mFoundInfos.get(position).getFoundTitle());
                holder.tvInfoLocation.setText("拾取地点：" + mFoundInfos.get(position).getFoundLocation());
                holder.tvInfoDesc.setText("拾取描述:\n" + mFoundInfos.get(position).getFoundDesc());
                LableUtils.setLableTypeImage(holder, mFoundInfos.get(position).getFoundType());
                holder.btnCallPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {
                            CallOrSmsDialog.getCallDialog(getActivity(), mFoundInfos.get(position).getPublishUser().getMobilePhoneNumber());
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                holder.btnSendSms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {
                            CallOrSmsDialog.getSendSmsDialog(getActivity(), mFoundInfos.get(position).getPublishUser().getMobilePhoneNumber());
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                holder.btnComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = Integer.parseInt(view.getTag().toString());
                        if (mUser != null) {
                            commentsPopup = new CommentsPopupWindow(getActivity(), itemsOnClick);
                            commentsPopup.showAtLocation(getActivity().findViewById(R.id.ll_found_fragment),
                                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
                            commentsPopup.setFocusable(true);
                            commentsPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                            commentsPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                commentsPhoneNumber = mFoundInfos.get(position).getContactNumber();
                commentsFoundInfo = mFoundInfos.get(position);

                return view;
            }


        }


        private View.OnClickListener itemsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsPopup.dismiss();
                switch (view.getId()) {
                    case R.id.btn_comments:
                        if (mUser != null) {
                            BmobPushManager bmobPush = new BmobPushManager(getActivity());
                            BmobQuery<MyBmobInstallation> query = new BmobQuery();
                            query.addWhereEqualTo("phoneNumber", commentsPhoneNumber);
                            bmobPush.setQuery(query);
                            bmobPush.pushMessage(commentsPopup.getComments());
                            DBHelper.saveComments(getActivity(), "found", commentsFoundInfo, mCommentedUserId, commentsPopup);
                            Toast.makeText(getActivity(), "评论成功", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

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
        if (clickFlag) { 
            clickFlag = false;
            return;
        }
        skip = 0;
        fragment.loadClassifyData();
        lvFound.setAdapter(fragment.foundAdapter);
        lvFound.setSelection(fragment.itemPositon);
        foundQuery = setQueryCondition(FoundInfoActivity.this, foundQuery, skip, LIMIT);
    }


    public static BmobQuery<FoundInfo> setQueryCondition(Context context, BmobQuery<FoundInfo> query, int skip, int LIMIT) {
        String location = SharedPrefUtils.readString(context, "main_province", "全国");
        String school = SharedPrefUtils.readString(context, "main_school", "全部学校");
        String classify = SharedPrefUtils.readString(context, "main_lost_classify", "全部");
        String bql = null;
        if (location.equals("全国") && school.equals("全部学校") && classify.equals("全部")) {
            bql = "select include publishUser,* from FoundInfo LIMIT " + skip + "," + LIMIT + " order by -createdAt";
            query.setSQL(bql);
        }
        if (location.equals("全国") && school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("全国 全部学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from FoundInfo where foundType = ? " +
                    "limit " + skip + "," + LIMIT;//+" order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{classify});
        }
        if (!location.equals("全国") && school.equals("全部学校") && classify.equals("全部")) {
            System.out.println("某省 全部学校 全部分类_______________" + skip);
            bql = "select include publishUser,* from FoundInfo where foundLocation = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{location});

        }
        if (!location.equals("全国") && school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("某省 全部学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from FoundInfo where foundLocation = ? and foundType = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{location, classify});

        }
        if (!location.equals("全国") && !school.equals("全部学校") && classify.equals("全部")) {
            System.out.println("某省 某学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from FoundInfo where publishUserSchool  = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{school});

        }
        if (!location.equals("全国") && !school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("某省 某学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from FoundInfo where publishUserSchool = ? and foundType = ?" +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{school, classify});

        }
        return query;
    }


}



