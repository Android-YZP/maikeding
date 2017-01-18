package com.mcwonders.mkd.main.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcwonders.mkd.R;
import com.mcwonders.mkd.business.IUserBusiness;
import com.mcwonders.mkd.business.imp.UserBusinessImp;
import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.contact.activity.BlackListActivity;
import com.mcwonders.mkd.demand.util.JsonUtils;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.main.model.UserMessage;
import com.mcwonders.mkd.main.view.UserMessageListAdapter;
import com.mcwonders.mkd.main.view.YListView;
import com.mcwonders.mkd.utils.CommonUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private YListView mYlvMessage;
    private List<UserMessage> mUserMessages;
    private boolean isNoMoreData = false;
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private static int pageNo = 1;
    private static int pageSize = 25;
    private static int type = 1;//系统公告
    int mDeletePos = 0;
    private static User mUser;
    private UserMessageListAdapter mUserMessageListAdapter;
    private SwipeRefreshLayout mSRLMessage;
    //处理消息队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            String errorMsg = null;
            if (mSRLMessage.isRefreshing()) {
                mSRLMessage.setRefreshing(false);
            }
            switch (flag) {
                case 0:
                    errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    try {
                        Toast.makeText(MessageActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_SUCCESS:
                    if (mUserMessageListAdapter != null) {
                        updateUserMessagesListFromNetByRefresh();
                    } else {
                        updateUserMessagesListFromNet();
                    }
                    break;
                case CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_AGAIN_SUCCESS:
                    updateUserMessagesListFromNetByRefresh();
                    break;
                case CommonConstants.FLAG_CANCEL_USER_MESSAGES_SYS_PUB_SUCCESS:
                    updateUserMessagesListFromNetByCancel();
                    break;
                case CommonConstants.FLAG_GET_LIST_NO_DATA:
                    isNoMoreData = true;
                    mYlvMessage.removeFootView();
                    break;
                default:
                    break;
            }
        }


    };

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);
        mYlvMessage = (YListView) findViewById(R.id.ylv_message_item);
        mSRLMessage = (SwipeRefreshLayout) findViewById(R.id.srl_message_item);
    }

    private void initData() {
        mTvTitle.setText("系统通知");
        mUserMessages = new ArrayList<>();
        mUser = CommonUtil.getUserInfo(this);
//        mSRLMessage.setRefreshing(true);
        initDataMessagesList();//从网络获取数据
        //listView 的初始化
        mYlvMessage.initBottomView();
        mYlvMessage.setMyPullUpListViewCallBack(new YListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
                if (!isNoMoreData) {
                    loadNextPageData();//加载下一页数据
                }
            }
        });
    }

    private void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mYlvMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showNormalDia(position);
                return true;
            }
        });
        mSRLMessage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //清空数据
                isNoMoreData = false;
                mYlvMessage.initBottomView();
                initDataMessagesList();//从网络获取数据

            }
        });
    }


    /**
     * 从网络中首次获取通知列表数据
     */
    private void initDataMessagesList() {
        //开启副线程-获取通知列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //得到第一页的数据
                    getFirstPageData();
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("系统公告：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();
    }

    /**
     * 获取第一页数据
     *
     * @throws Exception
     */
    private void getFirstPageData() throws Exception {
        pageNo = 1;//重置页数
        String result = mUserBusiness.getUserMessagesList(pageNo, pageSize, mUser, type);
        Log.d("YZPLOG_url_status", result + ".");
        JSONObject jsonObj = new JSONObject(result);
        boolean Success = jsonObj.getBoolean("success");
        int totalPage = jsonObj.getInt("totalPage");
        if (Success) {
            JSONArray _json_array = jsonObj.getJSONArray("datas");
            if (pageNo > totalPage || (_json_array != null && _json_array.length() == 0)) {
                handler.sendEmptyMessage(CommonConstants.FLAG_GET_LIST_NO_DATA);
                return;
            }
            if (_json_array.length() > 0) {
                mUserMessages.clear();//重置list
                for (int i = 0; i < _json_array.length(); i++) {
                    JSONObject _jsonObject = _json_array.getJSONObject(i);
                    UserMessage _user_message = new UserMessage();
                    _user_message.setId(JsonUtils.getInt(_jsonObject, "id", 0));
                    _user_message.setTitle(JsonUtils.getString(_jsonObject, "title"));
                    _user_message.setType(JsonUtils.getInt(_jsonObject, "type", 0));
                    _user_message.setStatus(JsonUtils.getBoolean(_jsonObject, "status"));
                    _user_message.setDate(JsonUtils.getString(_jsonObject, "date"));
                    _user_message.setRedirectType(JsonUtils.getInt(_jsonObject, "redirectType", 0));
                    _user_message.setRedirectId(JsonUtils.getInt(_jsonObject, "redirectId", 0));
                    mUserMessages.add(_user_message);
                }
            }
            //获取成功
            handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_SUCCESS);
        } else {
            //获取错误代码，并查询出错误文字
            String errorMsg = jsonObj.getString("errorMsg");
            sendErrorMessage(errorMsg);
        }
    }


    /**
     * 加载第二页的数据
     */
    private void loadNextPageData() {
        //开启副线程-获取通知列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    String result = mUserBusiness.getUserMessagesList(pageNo, pageSize, mUser, type);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    int totalPage = jsonObj.getInt("totalPage");
                    if (Success) {
                        JSONArray _json_array = jsonObj.getJSONArray("datas");
                        if (pageNo > totalPage || (_json_array != null && _json_array.length() == 0)) {
                            String errorMsg = "没有更多数据了";
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_LIST_NO_DATA);
                            sendErrorMessage(errorMsg);
                            return;
                        }
                        if (_json_array.length() > 0) {
                            for (int i = 0; i < _json_array.length(); i++) {
                                JSONObject _jsonObject = _json_array.getJSONObject(i);
                                UserMessage _user_message = new UserMessage();
                                _user_message.setId(JsonUtils.getInt(_jsonObject, "id", 0));
                                _user_message.setTitle(JsonUtils.getString(_jsonObject, "title"));
                                _user_message.setType(JsonUtils.getInt(_jsonObject, "type", 0));
                                _user_message.setStatus(JsonUtils.getBoolean(_jsonObject, "status"));
                                _user_message.setDate(JsonUtils.getString(_jsonObject, "date"));
                                _user_message.setRedirectType(JsonUtils.getInt(_jsonObject, "redirectType", 0));
                                _user_message.setRedirectId(JsonUtils.getInt(_jsonObject, "redirectId", 0));
                                mUserMessages.add(_user_message);
                            }
                        }
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_AGAIN_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        sendErrorMessage(errorMsg);
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("系统公告：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();
    }

    /**
     * 发起网络请求移除通知
     */
    private void userMessageCancel(final int messageId, final int position) {
        //开启副线程-移除通知
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.deleteUserMessage(messageId, mUser);
                    Log.d("YZP========>", result + ".");
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        mUserMessages.remove(position);
                        //移除成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_CANCEL_USER_MESSAGES_SYS_PUB_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        sendErrorMessage(errorMsg);
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("移除通知：" + CommonConstants.MSG_GET_ERROR);
                }
            }


        }).start();
    }

    /*普通的对话框*/
    private void showNormalDia(final int position) {
        //AlertDialog.Builder normalDialog=new AlertDialog.Builder(getApplicationContext());
        AlertDialog.Builder normalDia = new AlertDialog.Builder(MessageActivity.this);
        normalDia.setMessage("确认删除这条消息吗?");

        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CommonUtil.isnetWorkAvilable(MessageActivity.this)) {
                    mDeletePos = position;
                    userMessageCancel(mUserMessages.get(position).getId(), position);
                } else {
                    Toast.makeText(MessageActivity.this, "网络不可用,请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        normalDia.create().show();
    }

    /**
     * 发送错误信息到消息队列
     *
     * @param errorMsg
     */
    private void sendErrorMessage(String errorMsg) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("ErrorMsg", errorMsg);
        msg.setData(data);
        handler.sendMessage(msg);
    }


    /**
     * 从网络初次加载-系统公告列表
     */
    private void updateUserMessagesListFromNet() {
        mUserMessageListAdapter = new UserMessageListAdapter(this, mUserMessages);
        mYlvMessage.setAdapter(mUserMessageListAdapter);
        pageNo++;
    }

    /**
     * 移除某个通知后，刷新UI通知列表
     */
    protected void updateUserMessagesListFromNetByCancel() {
        mUserMessageListAdapter.notifyDataSetChanged();
    }

    /**
     * 从网络更新系统公告列表
     */
    private void updateUserMessagesListFromNetByRefresh() {
        mUserMessageListAdapter.notifyDataSetChanged();
        pageNo++;
    }

//    /**
//     * 设置空数据时的emptyView
//     * @param errorMsg
//     */
//    protected void setListNoDataEmptyView(String errorMsg) {
//        if(errorMsg!=null&&!errorMsg.equals("")){
//            mTvUserMessagesListNone.setText(errorMsg);
//        }else{
//            mTvUserMessagesListNone.setText("没有数据");
//        }
//        mPullToRefreshListView.setEmptyView(mTvUserMessagesListNone);
//    }

}
