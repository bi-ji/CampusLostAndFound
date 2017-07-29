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
import com.edu.claf.bean.LostInfo;
import com.edu.claf.bean.MyBmobInstallation;
import com.edu.claf.bean.User;
import com.edu.claf.dao.DBHelper;
import com.edu.claf.holder.InfoHolder;
import com.edu.claf.view.CallOrSmsDialog;
import com.edu.claf.view.CommentsPopupWindow;
import com.edu.claf.view.CustomListView;
import com.edu.claf.view.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class LostInfoActivity extends AppCompatActivity {

    private static final int LOAD_DATA_FINISH = 10;
    private static final int REFRESH_DATA_FINISH = 11;
    private static CustomListView lvLost;
    private PlaceholderFragment fragment;

    private static ArrayList<String> mArrayList = new ArrayList<String>();

    private static List<LostInfo> mLostInfos = new ArrayList<>();
    private long mExitTime;
    private static BmobQuery<LostInfo> lostQuery;
    private static int skip = 0;
    private static final int LIMIT = 5;
    private static User mUser;
    private static boolean refreshFlag = false;
    static CommentsPopupWindow commentsPopup;
    private static boolean clickFlag = false;
    private static String commentsPhoneNumber;
    private static String mCommentedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lost_info);
        fragment = new PlaceholderFragment();
        mUser = BmobUser.getCurrentUser(this, User.class);
        BaseApplication.getApplication().addActivity(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }
    }


    public static class PlaceholderFragment extends Fragment {


        private TextView tvLostEmpty;
        private FrameLayout flNetError;
        private Button btnReconnect;
        private LostInfo commentsLostInfo;
        public MyLostAdapter lostAdapter;
        private int itemPositon;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lost,
                    container, false);
            lvLost = (CustomListView) rootView.findViewById(R.id.lv_lost_list);
            tvLostEmpty = (TextView) rootView.findViewById(R.id.tv_lost_empty);
            flNetError = (FrameLayout) rootView.findViewById(R.id.fl_net_error);
            btnReconnect = (Button) rootView.findViewById(R.id.btn_reconnect);
            flNetError.setVisibility(View.GONE);
            tvLostEmpty.setVisibility(View.GONE);
            lostQuery = new BmobQuery<LostInfo>();
            loadClassifyData();
            ProgressView.progressDialogDismiss();
            lvLost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickFlag = true;
                    itemPositon = position;
                    if (NetStatusUtils.isNetworkAvailable(getActivity())) {
                        Intent intent = new Intent(getActivity(), LostInfoDetailsActivity.class);
                        int realPosition = position - lvLost.getHeaderViewsCount();
                        intent.putExtra("objectID", mLostInfos.get((int) id).getObjectId());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            lvLost.setOnRefreshListener(new CustomListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData(0);
                }
            });
            lvLost.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
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
                            getLostData(0);
                            break;
                        case 1:
                            getLostData(1);
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

        private void getLostData(int type) {
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
            lostQuery = setQueryCondition(getActivity(), lostQuery, skip, LIMIT);
            lostQuery.doSQLQuery(getActivity(), new SQLQueryListener<LostInfo>() {
                @Override
                public void done(BmobQueryResult<LostInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<LostInfo> list = bmobQueryResult.getResults();
                        if (list.size() == LIMIT) {
                            lvLost.setCanLoadMore(true);
                            skip += LIMIT;
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvLost.setCanLoadMore(false);
                        }
                        mLostInfos.clear();
                        mLostInfos.addAll(0, list);
                        new MyLostAdapter().notifyDataSetChanged();
                    }
                }
            });

        }


        private void loadMoreData() {
            lostQuery = setQueryCondition(getActivity(), lostQuery, skip, LIMIT);
            lostQuery.doSQLQuery(getActivity(), new SQLQueryListener<LostInfo>() {
                @Override
                public void done(BmobQueryResult<LostInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<LostInfo> list = bmobQueryResult.getResults();
                        if (list.size() == LIMIT) {
                            lvLost.setCanLoadMore(true);
                        } else if (list.size() == 0) {
                            lvLost.setCanLoadMore(false);
                            skip -= LIMIT;
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvLost.setCanLoadMore(false);
                        }
                        mLostInfos.addAll(list);
                        new MyLostAdapter().notifyDataSetChanged();

                    } else {
                        if (e.getErrorCode() == 9010) {
                            ProgressView.progressDialogDismiss();
                            tvLostEmpty.setVisibility(View.GONE);
                            flNetError.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                        } else if (e.getErrorCode() == 9016) {
                            ProgressView.progressDialogDismiss();
                            flNetError.setVisibility(View.VISIBLE);
                            tvLostEmpty.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }


        public void loadClassifyData() {
            skip = 0;
            BmobQuery<LostInfo> query = new BmobQuery<>();
            query = setQueryCondition(getActivity(), query, skip, LIMIT);
            query = BmobCacheUtils.setQueryCacheConditon(query,getActivity(),LostInfo.class);
            query.doSQLQuery(getActivity(), new SQLQueryListener<LostInfo>() {
                @Override
                public void done(BmobQueryResult<LostInfo> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<LostInfo> list = bmobQueryResult.getResults();
                        if (list.size() == 0) {
                            tvLostEmpty.setVisibility(View.VISIBLE);
                            ProgressView.progressDialogDismiss();
                            return;
                        } else if (list.size() > 0 && list.size() < LIMIT) {
                            lvLost.setCanLoadMore(false);
                            tvLostEmpty.setVisibility(View.GONE);
                            mLostInfos.clear();
                            mLostInfos.addAll(0, list);
                            if (lostAdapter != null) {
                                lvLost.requestLayout();
                                lostAdapter.notifyDataSetChanged();
                            } else {
                                lvLost.requestLayout();
                                new MyLostAdapter().notifyDataSetChanged();
                            }
                        } else if (list.size() == LIMIT) {
                            tvLostEmpty.setVisibility(View.GONE);
                            lvLost.setCanLoadMore(true);
                            mLostInfos.clear();
                            mLostInfos.addAll(0, list);
                            lvLost.requestLayout();
                            if (lostAdapter != null) {
                                lostAdapter.notifyDataSetChanged();
                            } else {
                                new MyLostAdapter().notifyDataSetChanged();
                            }
                        }
                        lostAdapter = new MyLostAdapter(getActivity(), mLostInfos);
                        lvLost.setAdapter(lostAdapter);
                        ProgressView.progressDialogDismiss();
                    } else {
                        if (e.getErrorCode() == 9010) {
                            tvLostEmpty.setVisibility(View.GONE);
                            flNetError.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                        } else if (e.getErrorCode() == 9016) {
                            flNetError.setVisibility(View.VISIBLE);
                            tvLostEmpty.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "无网络连接，请检查您的手机网络", Toast.LENGTH_SHORT).show();
                        }
                        ProgressView.progressDialogDismiss();
                        btnReconnect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (NetStatusUtils.isNetworkAvailable(getActivity())) {
                                    flNetError.setVisibility(View.GONE);
                                    ProgressView.getProgressDialog(getActivity(), "正在加载....");
                                    if (lostAdapter != null) {
                                        lostAdapter.notifyDataSetChanged();
                                    } else {
                                        new MyLostAdapter().notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            });

        }


        public class MyLostAdapter extends InfoAdapter<LostInfo> {

            public ImageLoader imageLoader;

            MyLostAdapter() {
                imageLoader = new ImageLoader(getActivity());
            }

            public MyLostAdapter(Context context, List<LostInfo> mInfos) {
                super(context, mInfos);
                imageLoader = new ImageLoader(getActivity());
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                String commentedUserId = mLostInfos.get(position).getPublishUser().getObjectId();
                String url = mLostInfos.get(position).getPublishUser().getPhotoUrl();
                String phone = mLostInfos.get(position).getPublishUser().getMobilePhoneNumber();
                View view = null;
                InfoHolder holder = null;
                if (convertView == null) {
                    holder = new InfoHolder();
                    view = View.inflate(getActivity(), R.layout.item_lost_list, null);
                    holder.tvUserName = (TextView) view.findViewById(R.id.tv_lost_public_user_name);
                    holder.tvInfoDate = (TextView) view.findViewById(R.id.tv_lost_public_time);
                    holder.tvInfoTitle = (TextView) view.findViewById(R.id.tv_lost_public_title);
                    holder.tvInfoLocation = (TextView) view.findViewById(R.id.tv_lost_public_location);
                    holder.tvInfoDesc = (TextView) view.findViewById(R.id.tv_lost_public_desc);
                    holder.btnCallPhone = (Button) view.findViewById(R.id.btn_lost_list_call_phone);
                    holder.btnSendSms = (Button) view.findViewById(R.id.btn_lost_list_send_sms);
                    holder.btnComments = (Button) view.findViewById(R.id.btn_lost_list_comments);
                    holder.tvLable = (TextView) view.findViewById(R.id.label);
                    holder.tvInfoTime = (TextView) view.findViewById(R.id.tv_lost_time);
                    holder.ivInfoPhoto = (ImageView) view.findViewById(R.id.iv_head_image);
                    holder.btnShare = (Button) view.findViewById(R.id.btn_lost_list_share);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (InfoHolder) view.getTag();

                }
                holder.btnComments.setTag(position);
                holder.tvUserName.setText(mLostInfos.get(position).getPublishUser().getNickName());//.getPublishUser().getNickName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateLost = mLostInfos.get(position).getLostDate().getDate();
                String datePublish = mLostInfos.get(position).getCreatedAt();
                try {
                    infoDate = dateFormat.parse(dateLost);
                    publishDate = dateFormat.parse(datePublish);
                    String format = dateFormat.format(infoDate);
                    String createTime = dateFormat.format(publishDate);
                    holder.tvInfoDate.setText("发布时间:" + createTime);//+ format);
                    holder.tvInfoTime.setText(format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageLoader.DisplayImage(url, phone, holder.ivInfoPhoto);

                holder.tvInfoTitle.setText("标题：" + mLostInfos.get(position).getLostTitle());
                holder.tvInfoLocation.setText("丢失地点：" + mLostInfos.get(position).getLostLocation());
                holder.tvInfoDesc.setText("丢失描述:\n" + mLostInfos.get(position).getLostDesc());
                LableUtils.setLableTypeImage(holder, mLostInfos.get(position).getLostType());
                holder.btnCallPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {
                            CallOrSmsDialog.getCallDialog(getActivity(), mLostInfos.get(position).getPublishUser().getMobilePhoneNumber());
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                holder.btnSendSms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {
                            CallOrSmsDialog.getSendSmsDialog(getActivity(), mLostInfos.get(position).getPublishUser().getMobilePhoneNumber());
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                holder.btnComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = Integer.parseInt(view.getTag().toString());
                        mCommentedUserId = mLostInfos.get(index).getPublishUser().getObjectId();
                        if (mUser != null) {
                            commentsPopup = new CommentsPopupWindow(getActivity(), itemsOnClick);
                            commentsPopup.showAtLocation(getActivity().findViewById(R.id.ll_lost_fragment),
                                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
                            commentsPopup.setFocusable(true);
                            commentsPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                            commentsPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                holder.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {
                            Intent shareIntent = new Intent("android.intent.action.SEND");
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                            shareIntent.putExtra("android.intent.extra.TEXT", "Hi,我刚才发现了一条记录，你说不定会感兴趣：" +
                                    mLostInfos.get(position).getLostDesc());
                            getActivity().startActivity(Intent.createChooser(shareIntent, "分享"));
                        } else {
                            Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                commentsPhoneNumber = mLostInfos.get(position).getContactNumber();
                commentsLostInfo = mLostInfos.get(position);
                return view;
            }

        }


        private View.OnClickListener itemsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsPopup.dismiss();
                switch (view.getId()) {
                    case R.id.btn_comments:
                        BmobPushManager bmobPush = new BmobPushManager(getActivity());
                        BmobQuery<MyBmobInstallation> query = new BmobQuery();
                        query.addWhereEqualTo("phoneNumber", commentsPhoneNumber);
                        bmobPush.setQuery(query);
                        bmobPush.pushMessage(commentsPopup.getComments());
                        DBHelper.saveComments(getActivity(), "lost", commentsLostInfo, mCommentedUserId, commentsPopup);
                        Toast.makeText(getActivity(), "评论成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };


        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REFRESH_DATA_FINISH:
                        if (lostAdapter != null) {
                            lostAdapter.notifyDataSetChanged();
                        }
                        lvLost.onRefreshComplete();
                        break;
                    case LOAD_DATA_FINISH:
                        if (lostAdapter != null) {
                            lostAdapter.notifyDataSetChanged();
                        }
                        lvLost.onLoadMoreComplete();
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
        lvLost.setAdapter(fragment.lostAdapter);
        lvLost.setSelection(fragment.itemPositon);
        lostQuery = setQueryCondition(LostInfoActivity.this, lostQuery, skip, LIMIT);


    }

    public static BmobQuery<LostInfo> setQueryCondition(Context context, BmobQuery<LostInfo> query, int skip, int LIMIT) {
        String location = SharedPrefUtils.readString(context, "main_province", "全国");
        String school = SharedPrefUtils.readString(context, "main_school", "全部学校");
        String classify = SharedPrefUtils.readString(context, "main_lost_classify", "全部");
        String bql = null;
        if (location.equals("全国") && school.equals("全部学校") && classify.equals("全部")) {
            bql = "select include publishUser,* from LostInfo LIMIT " + skip + "," + LIMIT + " order by -createdAt";
            query.setSQL(bql);
        }
        if (location.equals("全国") && school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("全国 全部学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from LostInfo where lostType = ? " +
                    "limit " + skip + "," + LIMIT;//+" order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{classify});
        }
        if (!location.equals("全国") && school.equals("全部学校") && classify.equals("全部")) {
            System.out.println("某省 全部学校 全部分类_______________" + skip);
            bql = "select include publishUser,* from LostInfo where lostLocation = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{location});

        }
        if (!location.equals("全国") && school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("某省 全部学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from LostInfo where lostLoc ation = ? and lostType = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{location, classify});

        }
        if (!location.equals("全国") && !school.equals("全部学校") && classify.equals("全部")) {
            System.out.println("某省 某学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from LostInfo where publishUserSchool  = ? " +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{school});

        }
        if (!location.equals("全国") && !school.equals("全部学校") && !classify.equals("全部")) {
            System.out.println("某省 某学校 选择分类_______________" + skip);
            bql = "select include publishUser,* from LostInfo where publishUserSchool = ? and lostType = ?" +
                    "limit " + skip + "," + LIMIT;// + " order by -createdAt";
            query.setSQL(bql);
            query.setPreparedParams(new Object[]{school, classify});

        }
        return query;
    }


}
