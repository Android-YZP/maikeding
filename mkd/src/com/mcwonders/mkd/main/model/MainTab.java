package com.mcwonders.mkd.main.model;

import com.mcwonders.mkd.main.fragment.SettingListFragment;
import com.mcwonders.mkd.main.reminder.ReminderId;
import com.mcwonders.mkd.main.fragment.ContactListFragment;
import com.mcwonders.mkd.main.fragment.SessionListFragment;

public enum MainTab {
    RECENT_CONTACTS(0, ReminderId.SESSION, SessionListFragment.class, com.mcwonders.mkd.R.string.main_tab_session,
            com.mcwonders.mkd.R.layout.session_list),
    CONTACT(1, ReminderId.CONTACT, ContactListFragment.class, com.mcwonders.mkd.R.string.main_tab_contact,
            com.mcwonders.mkd.R.layout.contacts_list),
    SETTING(2, ReminderId.SETTING, SettingListFragment.class, com.mcwonders.mkd.R.string.main_tab_setting,
            com.mcwonders.mkd.R.layout.setting_list);
    public final int tabIndex;

    public final int reminderId;

    public final Class clazz;

    public final int resId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class clazz, int resId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }
        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }
        return null;
    }
}
