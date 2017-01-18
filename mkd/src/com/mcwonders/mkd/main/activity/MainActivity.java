package com.mcwonders.mkd.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mcwonders.mkd.avchat.AVChatProfile;
import com.mcwonders.mkd.avchat.activity.AVChatActivity;
import com.mcwonders.mkd.contact.activity.AddFriendActivity;
import com.mcwonders.mkd.login.LogoutHelper;
import com.mcwonders.mkd.main.checkupdate.CheckUpdate;
import com.mcwonders.mkd.main.fragment.HomeFragment;
import com.mcwonders.mkd.main.model.Extras;
import com.mcwonders.mkd.session.SessionHelper;
import com.mcwonders.mkd.team.TeamCreateHelper;
import com.mcwonders.mkd.utils.ExampleUtil;
import com.mcwonders.mkd.utils.MyApplication;
import com.mcwonders.uikit.LoginSyncDataStatusObserver;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.common.ui.dialog.DialogMaker;
import com.mcwonders.uikit.common.util.log.LogUtil;
import com.mcwonders.uikit.contact_selector.activity.ContactSelectActivity;
import com.mcwonders.uikit.permission.MPermission;
import com.mcwonders.uikit.permission.annotation.OnMPermissionDenied;
import com.mcwonders.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * 主界面
 * Created by huangjun on 2015/3/25.
 */
public class MainActivity extends UI {

    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;
    public static boolean isForeground = false;
    private HomeFragment mainFragment;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    // 注销
    public static void logout(Context context, boolean quit) {
        Intent extra = new Intent();
        extra.putExtra(EXTRA_APP_QUIT, quit);
        start(context, extra);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mcwonders.mkd.R.layout.activity_main_tab);
        MyApplication.activities.add(MainActivity.this);
        //极光推送相关内容
        registerMessageReceiver();  // used for receive msg
        init();
        //检查更新
//        CheckUpdate.getInstance().startCheck(this);

    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        JPushInterface.init(getApplicationContext());
    }


    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        JPushInterface.onResume(getApplicationContext());

        requestBasicPermission();
        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });

        Log.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(com.mcwonders.mkd.R.string.prepare_data)).setCanceledOnTouchOutside(false);
        }
        onInit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.activities.clear();
        unregisterReceiver(mMessageReceiver);
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);

//        Set<String> set = new HashSet<>();
////        set.add("呵呵");//名字任意，可多添加几个
//        set.add(CommonUtil.getUserInfo(UIUtils.getContext()).getDatas().getUser_id() + "");//名字任意，可多添加几个
//        UIUtils.LogUtils(CommonUtil.getUserInfo(UIUtils.getContext()).getDatas().getUser_id() + "");
//        JPushInterface.setTags(getApplicationContext(), set, null);

    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }

        //显示自定义消息
        private void setCostomMsg(String msg) {
            if (null != msg) {

            }
        }
    }


    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(MainActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
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

    private void onInit() {
        // 加载主页面
        showMainFragment();
        LogUtil.ui("NIM SDK cache path=" + NIMClient.getSdkStorageDirPath());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }

    @Override
    public void onBackPressed() {
        if (mainFragment != null) {
            if (mainFragment.onBackPressed()) {
                return;
            } else {
                moveTaskToBack(true);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.mcwonders.mkd.R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.about:
//                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                break;
//            case R.id.create_normal_team:
//                ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(null, 50);
//                NimUIKit.startContactSelect(MainActivity.this, option, REQUEST_CODE_NORMAL);
//                break;
//            case R.id.create_regular_team:
//                ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
//                NimUIKit.startContactSelect(MainActivity.this, advancedOption, REQUEST_CODE_ADVANCED);
//                break;
//            case R.id.search_advanced_team:
//                AdvancedTeamSearchActivity.start(MainActivity.this);
//                break;
            case com.mcwonders.mkd.R.id.add_btn:
                AddFriendActivity.start(MainActivity.this);
                break;
            case com.mcwonders.mkd.R.id.search_btn:
                GlobalSearchActivity.start(MainActivity.this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        } else if (intent.hasExtra(EXTRA_APP_QUIT)) {
            onLogout();
        } else if (intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
            if (AVChatProfile.getInstance().isAVChatting()) {
                Intent localIntent = new Intent();
                localIntent.setClass(this, AVChatActivity.class);
                startActivity(localIntent);
            }
        } else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P)) {
            Intent data = intent.getParcelableExtra(Extras.EXTRA_DATA);
            String account = data.getStringExtra(Extras.EXTRA_ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                SessionHelper.startP2PSession(this, account);
            }
        }
    }

    private void showMainFragment() {
        if (mainFragment == null && !isDestroyedCompatible()) {
            mainFragment = new HomeFragment();
            switchFragmentContent(mainFragment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //免打扰的数据返回
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NoDisturbActivity.NO_DISTURB_REQ:
                    //接收到消息之后发送广播
                    boolean isChecked = data.getBooleanExtra(NoDisturbActivity.EXTRA_ISCHECKED, false);
                    String startTime = data.getStringExtra(NoDisturbActivity.EXTRA_START_TIME);
                    String endTime = data.getStringExtra(NoDisturbActivity.EXTRA_END_TIME);

                    Intent intent = new Intent();  //Itent就是我们要发送的内容

                    intent.putExtra(NoDisturbActivity.EXTRA_ISCHECKED, isChecked);
                    intent.putExtra(NoDisturbActivity.EXTRA_START_TIME, startTime);
                    intent.putExtra(NoDisturbActivity.EXTRA_END_TIME, endTime);
                    intent.setAction("NoDisturbing");   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
                    sendBroadcast(intent);   //发送广播
                    return;
                default:
                    break;
            }

        }


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MainActivity.this, selected, false, null);
                } else {
                    Toast.makeText(MainActivity.this, "请选择至少一个联系人！", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_ADVANCED) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(MainActivity.this, selected);
            }
        }


    }


    // 注销
    private void onLogout() {
        // 清理缓存&注销监听
        LogoutHelper.logout();
        // 启动登录
        finish();
    }
}
