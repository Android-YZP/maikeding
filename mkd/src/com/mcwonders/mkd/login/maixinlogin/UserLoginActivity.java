package com.mcwonders.mkd.login.maixinlogin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mcwonders.mkd.common.util.sys.SystemUtil;
import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.contact.constant.UserConstant;
import com.mcwonders.mkd.contact.helper.UserUpdateHelper;
import com.mcwonders.mkd.utils.CommonUtil;
import com.mcwonders.uikit.cache.DataCacheManager;
import com.mcwonders.uikit.permission.annotation.OnMPermissionGranted;
import com.mcwonders.mkd.DemoCache;
import com.mcwonders.mkd.business.IUserBusiness;
import com.mcwonders.mkd.business.imp.UserBusinessImp;
import com.mcwonders.mkd.config.preference.UserPreferences;
import com.mcwonders.mkd.login.maixinreg.UserRegPhoneActivity;
import com.mcwonders.mkd.main.activity.MainActivity;
import com.mcwonders.mkd.R;
import com.mcwonders.mkd.config.preference.Preferences;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.mcwonders.uikit.common.util.string.MD5;
import com.mcwonders.uikit.permission.MPermission;
import com.mcwonders.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

public class UserLoginActivity extends AppCompatActivity {

    private EditText mEtAccount;//用户名
    private EditText mEtPassword;//密码
    private static final String KICK_OUT = "KICK_OUT";
    private Button mBtnLogin;//登录按钮
    private TextView mTvGoRegister;//去注册
    private TextView mTvGoForgot;//去忘记密码
    private AbortableFuture<LoginInfo> loginRequest;
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private static ProgressDialog mProgressDialog = null;
    private User mUser;
    private Map<Integer, UserInfoFieldEnum> fieldMap;
    private MKJUserInfo mMkjUserInfo;
    private User user;
    private String mAccount;

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        //清理帳號缓存
        Log.d("YZP========>", "22222222");
        Intent intent = new Intent(context, UserLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User userInfo = CommonUtil.getUserInfo(this);
        if (userInfo != null) {
            //自动登录
            // 直接进入MainActivity进入主界面
            MainActivity.start(UserLoginActivity.this, null);
            finish();
            return;
        }
        setContentView(R.layout.activity_user_login);
        initView();
        initData();
        setListener();
        onParseIntent();
        requestBasicPermission();
    }

    private void initView() {
        mEtAccount = (EditText) findViewById(R.id.et_user_login_account);
        mEtPassword = (EditText) findViewById(R.id.et_user_login_password);

        mBtnLogin = (Button) findViewById(R.id.btn_user_login_commit);

        mTvGoRegister = (TextView) findViewById(R.id.tv_user_login_reg);
        mTvGoForgot = (TextView) findViewById(R.id.tv_user_login_forget);

    }

    private void initData() {

    }

    private void setListener() {
        mBtnLogin.setOnClickListener(new UserLoginOnClickListener());
        mTvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, UserRegPhoneActivity.class));
            }
        });

        mTvGoForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, UserForgotPhoneActivity.class));
            }
        });
    }

    private void onParseIntent() {
        if (getIntent().getBooleanExtra(KICK_OUT, false)) {
            int type = NIMClient.getService(AuthService.class).getKickedClientType();
            String client;
            switch (type) {
                case ClientType.Web:
                    client = "网页端";
                    break;
                case ClientType.Windows:
                    client = "电脑端";
                    break;
                case ClientType.REST:
                    client = "服务端";
                    break;
                default:
                    client = "移动端";
                    break;
            }
            EasyAlertDialogHelper.showOneButtonDiolag(UserLoginActivity.this, getString(R.string.kickout_notify),
                    String.format(getString(R.string.kickout_content), client), getString(R.string.ok), true, null);
        }
    }


    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(UserLoginActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
//        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }


    private class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public boolean isEmail(String str) {
            String regex = "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
            return match(regex, str);
        }

        private boolean match(String regex, String str) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }

        public MyHandler(UserLoginActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mProgressDialog != null && msg.what != CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS) {
                mProgressDialog.dismiss();
                Log.d("mProgressDialog", "mProgressDialog");
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((UserLoginActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS:
                    Log.d("YZP=========>", "FLAG_GET_REG_USER_LOGIN_SUCCESS");
                    //更新云信的个人信息
                    UserLoginOnClickListener userProFile = new UserLoginOnClickListener();
                    //储存个人信息到本地
                    CommonUtil.saveUserInfo(user, UserLoginActivity.this);
                    Log.d("登录信息1", user.getMobile() + user.getToken());

                    //更新昵称
                    if (!TextUtils.isEmpty(mMkjUserInfo.getUsername())) {
                        userProFile.updateProFile(mMkjUserInfo.getUsername(), UserConstant.KEY_NICKNAME);
                        Log.d("YZP=========>", mMkjUserInfo.getUsername());
                    }

                    //更新生日
                    if (mMkjUserInfo.getUserbirthday() != null) {
                        if (mMkjUserInfo.getUserbirthday().getYear() != 0) {
                            String relMonth;
                            String relDay;
                            int year = mMkjUserInfo.getUserbirthday().getYear() + 1900;
                            int month = mMkjUserInfo.getUserbirthday().getMonth() + 1;
                            if (month < 10) {
                                relMonth = "0" + month;
                            } else {
                                relMonth = "" + month;
                            }
                            int day = mMkjUserInfo.getUserbirthday().getDate();
                            if (day < 10) {
                                relDay = "0" + day;
                            } else {
                                relDay = "" + day;
                            }
                            userProFile.updateProFile(year + "-" + relMonth + "-" + relDay, UserConstant.KEY_BIRTH);
                            Log.d("YZP=========>", year + "-" + relMonth + "-" + relDay);
                        }
                    }


                    //更新手机
                    if (!TextUtils.isEmpty(mMkjUserInfo.getUserphone())) {
                        userProFile.updateProFile(mMkjUserInfo.getUserphone(), UserConstant.KEY_PHONE);
                        Log.d("YZP=========>", mMkjUserInfo.getUserphone());
                    }


                    //更新邮箱
                    if (!TextUtils.isEmpty(mMkjUserInfo.getUseremail()) && isEmail(mMkjUserInfo.getUseremail())) {
                        userProFile.updateProFile(mMkjUserInfo.getUseremail(), UserConstant.KEY_EMAIL);
                        Log.d("YZP=========>", mMkjUserInfo.getUseremail());
                    }

                    //更新签名
                    if (!TextUtils.isEmpty(mMkjUserInfo.getUsersignature())) {
                        userProFile.updateProFile(mMkjUserInfo.getUsersignature(), UserConstant.KEY_SIGNATURE);
                        Log.d("YZP=========>", mMkjUserInfo.getUsersignature());
                    }

                    // 更新性别
//
                    if (mMkjUserInfo.getUsergender().equals("男")) {//男
                        userProFile.updateProFile(1, UserConstant.KEY_GENDER);
                        Log.d("YZP=========>", mMkjUserInfo.getUsergender());
                    } else if (mMkjUserInfo.getUsergender().equals("女")) {//女
                        userProFile.updateProFile(2, UserConstant.KEY_GENDER);
                        Log.d("YZP=========>", mMkjUserInfo.getUsergender());
                    } else if (mMkjUserInfo.getUsergender().equals("保密")) {//女
                        userProFile.updateProFile(0, UserConstant.KEY_GENDER);
                        Log.d("YZP=========>", mMkjUserInfo.getUsergender());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void saveUserInfo() {
        //保存用户信息，并关闭该界面
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_user_login_info", mUser.toString());
        CommonUtil.saveUserInfo(mUser, this);
        Toast.makeText(UserLoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
        UserLoginActivity.this.finish();
    }

    public class UserLoginOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent _intent = null;
            switch (view.getId()) {
                case R.id.btn_user_login_commit:

                    mAccount = mEtAccount.getText().toString();
                    String password = mEtPassword.getText().toString();

                    if (mAccount == null || mAccount.equals("")) {
                        Toast.makeText(UserLoginActivity.this, "您未填写用户名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password == null || password.equals("")) {
                        Toast.makeText(UserLoginActivity.this, "您未填写密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //弹出加载进度条
                    mProgressDialog = ProgressDialog.show(UserLoginActivity.this, "请稍等", "正在玩命登录中...", true, true);
                    //开启副线程-发起登录
                    userLoginFromNet(mAccount, password);

                default:
                    break;
            }
        }


        public void userLoginFromNet(final String account, final String password) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = mUserBusiness.getUserLogin(account, password);
                        Log.d("test", result);
                        JSONObject jsonObj = new JSONObject(result);
                        boolean Success = jsonObj.getBoolean("success");
                        if (Success) {
                            //填充用户信息
                            user = new Gson().fromJson(result, User.class);
                            yunXinlogin(user.getId() + "", user.getToken());
                            //第一次手动登录时设置标签，自动登录时就不要设置标签了。
                            /****************************设置极光推送的推送标签************************************************************/
                            JPushInterface.setAlias(UserLoginActivity.this, user.getMobile() + "", null);//设置标签
                        } else {
                            //获取错误代码，并查询出错误文字 天外飞仙
                            String errorMsg = jsonObj.getString("errorMsg");
                            CommonUtil.sendErrorMessage(errorMsg, handler);
                        }
                    } catch (ConnectTimeoutException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT, handler);
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT, handler);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(e.getMessage(), handler);
                    } catch (Exception e) {
                        //what = 0;sendmsg 0;
                        CommonUtil.sendErrorMessage("注册-用户登录：" + CommonConstants.MSG_GET_ERROR, handler);
                    }
                }
            }).start();
        }


        /**
         * ***************************************** 云信登录 **************************************
         */

        public void yunXinlogin(final String account, final String token) {
            Log.d("YZP========>", account.toLowerCase() + "===" + tokenFromPassword(token));
            // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
            // 在这里直接使用同步到云信服务器的帐号和token登录。
            // 这里为了简便起见，demo就直接使用了密码的md5作为token。
            // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
            // 登录
            loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account.toLowerCase()
                    , tokenFromPassword(token), "65ee5f0a9a88cfe35b5077f96a85034a"));
            loginRequest.setCallback(new RequestCallback<LoginInfo>() {
                @Override
                public void onSuccess(LoginInfo param) {
                    Log.d("YZP========>", "成功");
                    onLoginDone();
                    DemoCache.setAccount(account);
                    saveLoginInfo(account, token);
                    // 初始化消息提醒
                    NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
                    // 初始化免打扰
                    if (UserPreferences.getStatusConfig() == null) {
                        UserPreferences.setStatusConfig(DemoCache.getNotificationConfig());
                    }
                    NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
                    // 构建缓存
                    DataCacheManager.buildDataCacheAsync();
                    //云信登录成功之时,调用麦客加个人信息接口,获得接口更新到云信接口
                    getMKJPerData(mAccount);

                }

                @Override
                public void onFailed(int code) {
                    Log.d("YZP========>", "失败" + code);
                    onLoginDone();
                    if (code == 302 || code == 404) {
                        Toast.makeText(UserLoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserLoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                    }
                    // 进入主界面
//                    MainActivity.start(UserLoginActivity.this, null);
//                    finish();
                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(UserLoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                    onLoginDone();
                    // 进入主界面
//                    MainActivity.start(UserLoginActivity.this, null);
//                    finish();
                }
            });
        }


        /**
         * 从麦客加接口得到个人信息
         */
        private void getMKJPerData(final String account) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = mUserBusiness.getUserInfo(account);
                        JSONObject jsonObj = new JSONObject(result);
                        boolean Success = jsonObj.getBoolean("success");
                        if (Success) {
                            mMkjUserInfo = new Gson().fromJson(result, MKJUserInfo.class);
                            //储存用户信息到本地
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS);
                            Log.d("个人信息", result);

                        } else {
                            //获取错误代码，并查询出错误文字
                            String errorMsg = jsonObj.getString("errorMsg");
                            CommonUtil.sendErrorMessage(errorMsg, handler);
                        }
                    } catch (ConnectTimeoutException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT, handler);
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT, handler);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage(e.getMessage(), handler);
                    } catch (Exception e) {
                        //what = 0;sendmsg 0;
                        CommonUtil.sendErrorMessage("注册-用户登录：" + CommonConstants.MSG_GET_ERROR, handler);
                    }
                }
            }).start();
        }

        /**
         * 更新个人信息
         *
         * @param content 内容
         * @param key     类型
         */
        private void updateProFile(final Serializable content, final int key) {
            final RequestCallbackWrapper callback = new RequestCallbackWrapper() {
                @Override
                public void onResult(int code, Object result, Throwable exception) {
                    if (code == ResponseCode.RES_SUCCESS) {
                        if (key == UserConstant.KEY_GENDER) {
//                            SystemClock.sleep(2000);
                            // 进入主界面,更新成功之后进入主界面
                            Log.d("YZP=========>", "更新成功之后进入主界面");
                            MainActivity.start(UserLoginActivity.this, null);

                            if (mProgressDialog != null) {
                                SystemClock.sleep(2000);
                                mProgressDialog.dismiss();
                                Log.d("mProgressDialog", "mProgressDialog.dismiss()");
                            }
                            finish();
                        }
                    } else if (code == ResponseCode.RES_ETIMEOUT) {
                        Toast.makeText(UserLoginActivity.this, com.mcwonders.mkd.R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            if (key == UserConstant.KEY_ALIAS) {
                Map<FriendFieldEnum, Object> map = new HashMap<>();
                map.put(FriendFieldEnum.ALIAS, content);
                NIMClient.getService(FriendService.class).updateFriendFields(null, map).setCallback(callback);
            } else {
                if (fieldMap == null) {
                    fieldMap = new HashMap<>();
                    fieldMap.put(UserConstant.KEY_NICKNAME, UserInfoFieldEnum.Name);
                    fieldMap.put(UserConstant.KEY_PHONE, UserInfoFieldEnum.MOBILE);
                    fieldMap.put(UserConstant.KEY_SIGNATURE, UserInfoFieldEnum.SIGNATURE);
                    fieldMap.put(UserConstant.KEY_EMAIL, UserInfoFieldEnum.EMAIL);
                    fieldMap.put(UserConstant.KEY_BIRTH, UserInfoFieldEnum.BIRTHDAY);
                    fieldMap.put(UserConstant.KEY_GENDER, UserInfoFieldEnum.GENDER);
                }
                UserUpdateHelper.update(fieldMap.get(key), content, callback);
            }
        }


        //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
        //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
        private String tokenFromPassword(String password) {
            String appKey = readAppKey(UserLoginActivity.this);
            boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey)
                    || "fe416640c8e8a72734219e1847ad2547".equals(appKey);

            return isDemo ? MD5.getStringMD5(password) : password;
        }

        private String readAppKey(Context context) {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (appInfo != null) {
                    return appInfo.metaData.getString("com.netease.nim.appKey");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 登录结束
         */
        private void onLoginDone() {
            loginRequest = null;
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        /**
         * 储存登录信息
         */
        private void saveLoginInfo(final String account, final String token) {
            Preferences.saveUserAccount(account);
            Preferences.saveUserToken(token);
        }
    }
}
