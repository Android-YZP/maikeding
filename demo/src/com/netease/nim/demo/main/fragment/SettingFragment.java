package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.avchat.activity.AVChatSettingsActivity;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.activity.UserProfileSettingActivity;
import com.netease.nim.demo.login.maixinlogin.UserLoginActivity;
import com.netease.nim.demo.main.activity.AboutActivity;
import com.netease.nim.demo.main.activity.CustomNotificationActivity;
import com.netease.nim.demo.main.activity.MainActivity;
import com.netease.nim.demo.main.activity.NoDisturbActivity;
import com.netease.nim.demo.main.adapter.SettingsAdapter;
import com.netease.nim.demo.main.model.SettingTemplate;
import com.netease.nim.demo.main.model.SettingType;
import com.netease.nim.demo.utils.CommonUtil;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.contact.ContactsCustomization;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.lucene.LuceneService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.settings.SettingsService;
import com.netease.nimlib.sdk.settings.SettingsServiceObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置Fragment
 * Created by YZP on 2015/9/7.
 */
public class SettingFragment extends TFragment implements SettingsAdapter.SwitchChangeListener {
    private static final int TAG_HEAD = 1;
    private static final int TAG_NOTICE = 2;
    private static final int TAG_NO_DISTURBE = 3;
    private static final int TAG_CLEAR = 4;
    private static final int TAG_CUSTOM_NOTIFY = 5;
    private static final int TAG_ABOUT = 6;
    private static final int TAG_SPEAKER = 7;
    private static final int TAG_NRTC_SETTINGS = 8;
    private static final int TAG_MSG_IGNORE = 10;
    private static final int TAG_RING = 11;
    private static final int TAG_LED = 12;
    private static final int TAG_NOTICE_CONTENT = 13; // 通知栏提醒配置
    private static final int TAG_CLEAR_INDEX = 18; // 清空全文检索缓存
    private static final int TAG_MULTIPORT_PUSH = 19; // 桌面端登录，是否推送

    private ContactsCustomization customization;
    ListView listView;
    SettingsAdapter adapter;
    private List<SettingTemplate> items = new ArrayList<SettingTemplate>();
    private String noDisturbTime;
    private SettingTemplate disturbItem;
    private SettingTemplate clearIndexItem;


    public void setContactsCustomization(ContactsCustomization customization) {
        this.customization = customization;
    }

    /**
     * ***************************************** 生命周期 *****************************************
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerObservers(true);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(SettingsServiceObserver.class).observeMultiportPushConfigNotify(pushConfigObserver, register);
    }

    Observer<Boolean> pushConfigObserver = new Observer<Boolean>() {
        @Override
        public void onEvent(Boolean aBoolean) {
//            Toast.makeText(getActivity(), "收到multiport push config：" + aBoolean, Toast.LENGTH_SHORT).show();
        }
    };

    private void initData() {
        if (UserPreferences.getStatusConfig() == null || !UserPreferences.getStatusConfig().downTimeToggle) {
            noDisturbTime = getString(com.netease.nim.demo.R.string.setting_close);
        } else {
            noDisturbTime = String.format("%s到%s", UserPreferences.getStatusConfig().downTimeBegin,
                    UserPreferences.getStatusConfig().downTimeEnd);
        }
    }

    private void initUI() {
        initItems();
        listView = (ListView) getView().findViewById(com.netease.nim.demo.R.id.settings_listview);
        View footer = LayoutInflater.from(getActivity()).inflate(com.netease.nim.demo.R.layout.settings_logout_footer, null);
        listView.addFooterView(footer);

        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingTemplate item = items.get(position);
                onListItemClick(item);
            }
        });

        View logoutBtn = footer.findViewById(com.netease.nim.demo.R.id.settings_button_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void initAdapter() {
        adapter = new SettingsAdapter(getActivity(), this, items);
        listView.setAdapter(adapter);
    }

    private void initItems() {
        items.clear();
        items.add(new SettingTemplate(TAG_HEAD, SettingType.TYPE_HEAD));
        items.add(new SettingTemplate(TAG_NOTICE, getString(com.netease.nim.demo.R.string.msg_notice), SettingType.TYPE_TOGGLE,
                UserPreferences.getNotificationToggle()));
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_SPEAKER, getString(com.netease.nim.demo.R.string.msg_speaker), SettingType.TYPE_TOGGLE,
                com.netease.nim.uikit.UserPreferences.isEarPhoneModeEnable()));
//        items.add(SettingTemplate.makeSeperator());
//        items.add(new SettingTemplate(TAG_RING, getString(com.netease.nim.demo.R.string.ring), SettingType.TYPE_TOGGLE,
//                UserPreferences.getRingToggle()));
//        items.add(new SettingTemplate(TAG_LED, getString(com.netease.nim.demo.R.string.led), SettingType.TYPE_TOGGLE,
//                UserPreferences.getLedToggle()));
//        items.add(SettingTemplate.makeSeperator());
//        items.add(new SettingTemplate(TAG_NOTICE_CONTENT, getString(com.netease.nim.demo.R.string.notice_content), SettingType.TYPE_TOGGLE,
//                UserPreferences.getNoticeContentToggle()));
//        items.add(SettingTemplate.makeSeperator());

//        disturbItem = new SettingTemplate(TAG_NO_DISTURBE, getString(com.netease.nim.demo.R.string.no_disturb), noDisturbTime);
//        items.add(disturbItem);
//        items.add(SettingTemplate.addLine());
//        items.add(new SettingTemplate(TAG_MULTIPORT_PUSH, getString(com.netease.nim.demo.R.string.multiport_push), SettingType.TYPE_TOGGLE,
//                !NIMClient.getService(SettingsService.class).isMultiportPushOpen()));
        items.add(SettingTemplate.makeSeperator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            items.add(new SettingTemplate(TAG_NRTC_SETTINGS, getString(com.netease.nim.demo.R.string.nrtc_settings)));
            items.add(SettingTemplate.makeSeperator());
        }

        items.add(new SettingTemplate(TAG_MSG_IGNORE, "过滤通知",
                SettingType.TYPE_TOGGLE, UserPreferences.getMsgIgnore()));
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_CLEAR, getString(com.netease.nim.demo.R.string.about_clear_msg_history)));
        items.add(SettingTemplate.addLine());

        clearIndexItem = new SettingTemplate(TAG_CLEAR_INDEX, getString(com.netease.nim.demo.R.string.clear_index), getIndexCacheSize() + " M");
        items.add(clearIndexItem);
        items.add(SettingTemplate.addLine());

//        items.add(new SettingTemplate(TAG_CUSTOM_NOTIFY, getString(com.netease.nim.demo.R.string.custom_notification)));
//        items.add(SettingTemplate.addLine());
        items.add(SettingTemplate.addLine());
        items.add(new SettingTemplate(TAG_ABOUT, getString(com.netease.nim.demo.R.string.setting_about)));

    }

    private void onListItemClick(SettingTemplate item) {
        if (item == null) return;
        switch (item.getId()) {
            case TAG_HEAD:
                UserProfileSettingActivity.start(getActivity(), DemoCache.getAccount());
                break;
            case TAG_NO_DISTURBE:
                startNoDisturb();
                break;
            case TAG_CUSTOM_NOTIFY:
                CustomNotificationActivity.start(getActivity());
                break;
            case TAG_ABOUT:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case TAG_CLEAR:
                NIMClient.getService(MsgService.class).clearMsgDatabase(true);
                Toast.makeText(getActivity(), com.netease.nim.demo.R.string.clear_msg_history_success, Toast.LENGTH_SHORT).show();
                break;
            case TAG_CLEAR_INDEX:
                clearIndex();
                break;
            case TAG_NRTC_SETTINGS:
                startActivity(new Intent(getActivity(), AVChatSettingsActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 注销
     */
    private void logout() {
        removeLoginState();
        MainActivity.logout(getActivity(), false);
        CommonUtil.clearUserInfo(getActivity());
        UserLoginActivity.start(getActivity(), false);
        NIMClient.getService(AuthService.class).logout();
        getActivity().finish();
    }

    /**
     * 清除登陆状态
     */
    private void removeLoginState() {
        Preferences.saveUserToken("");
    }

    @Override
    public void onSwitchChange(SettingTemplate item, boolean checkState) {
        switch (item.getId()) {
            case TAG_NOTICE:
                try {
                    setNotificationToggle(checkState);
                    NIMClient.toggleNotification(checkState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TAG_SPEAKER:
                com.netease.nim.uikit.UserPreferences.setEarPhoneModeEnable(checkState);
                MessageAudioControl.getInstance(getActivity()).setEarPhoneModeEnable(checkState);
                break;
            case TAG_MSG_IGNORE:
                UserPreferences.setMsgIgnore(checkState);
                break;
            case TAG_RING:
                UserPreferences.setRingToggle(checkState);
                StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
                config.ring = checkState;
                UserPreferences.setStatusConfig(config);
                NIMClient.updateStatusBarNotificationConfig(config);
                break;
            case TAG_LED:
                UserPreferences.setLedToggle(checkState);
                StatusBarNotificationConfig config1 = UserPreferences.getStatusConfig();
                if (checkState) {
                    config1.ledARGB = Color.GREEN;
                    config1.ledOnMs = 1000;
                    config1.ledOffMs = 1500;
                } else {
                    config1.ledARGB = -1;
                    config1.ledOnMs = -1;
                    config1.ledOffMs = -1;
                }
                UserPreferences.setStatusConfig(config1);
                NIMClient.updateStatusBarNotificationConfig(config1);
                break;
            case TAG_NOTICE_CONTENT:
                UserPreferences.setNoticeContentToggle(checkState);
                StatusBarNotificationConfig config2 = UserPreferences.getStatusConfig();
                config2.titleOnlyShowAppName = checkState;
                UserPreferences.setStatusConfig(config2);
                NIMClient.updateStatusBarNotificationConfig(config2);
                break;
            case TAG_MULTIPORT_PUSH:
                updateMultiportPushConfig(!checkState);
                break;
            default:
                break;
        }
        item.setChecked(checkState);
    }

    private void setNotificationToggle(boolean on) {
        UserPreferences.setNotificationToggle(on);
    }

    private void startNoDisturb() {
        NoDisturbActivity.startActivityForResult(getActivity(), UserPreferences.getStatusConfig(), noDisturbTime, NoDisturbActivity.NO_DISTURB_REQ);
    }

    private String getIndexCacheSize() {
        long size = NIMClient.getService(LuceneService.class).getCacheSize();
        return String.format("%.2f", size / (1024.0f * 1024.0f));
    }

    private void clearIndex() {
        NIMClient.getService(LuceneService.class).clearCache();
        clearIndexItem.setDetail("0.00 M");
        adapter.notifyDataSetChanged();
    }

    private void updateMultiportPushConfig(final boolean checkState) {
        NIMClient.getService(SettingsService.class).updateMultiportPushConfig(checkState).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(getActivity(), "设置失败,code:" + code, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NoDisturbActivity.NO_DISTURB_REQ:
                    setNoDisturbTime(data);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置免打扰时间
     *
     * @param data
     */
    private void setNoDisturbTime(Intent data) {
        boolean isChecked = data.getBooleanExtra(NoDisturbActivity.EXTRA_ISCHECKED, false);
        noDisturbTime = getString(com.netease.nim.demo.R.string.setting_close);
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (isChecked) {
            config.downTimeBegin = data.getStringExtra(NoDisturbActivity.EXTRA_START_TIME);
            config.downTimeEnd = data.getStringExtra(NoDisturbActivity.EXTRA_END_TIME);
            noDisturbTime = String.format("%s到%s", config.downTimeBegin, config.downTimeEnd);
        } else {
            config.downTimeBegin = null;
            config.downTimeEnd = null;
        }
        disturbItem.setDetail(noDisturbTime);
        adapter.notifyDataSetChanged();
        UserPreferences.setDownTimeToggle(isChecked);
        config.downTimeToggle = isChecked;
        UserPreferences.setStatusConfig(config);
        NIMClient.updateStatusBarNotificationConfig(config);
    }

}
