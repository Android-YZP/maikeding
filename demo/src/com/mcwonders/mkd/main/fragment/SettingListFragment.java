package com.mcwonders.mkd.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.mcwonders.mkd.main.helper.SystemMessageUnreadManager;
import com.mcwonders.uikit.contact.ContactsCustomization;
import com.mcwonders.mkd.DemoCache;
import com.mcwonders.mkd.main.model.MainTab;
import com.mcwonders.mkd.main.reminder.ReminderId;
import com.mcwonders.mkd.main.reminder.ReminderItem;
import com.mcwonders.mkd.session.SessionHelper;
import com.mcwonders.mkd.R;
import com.mcwonders.mkd.contact.activity.BlackListActivity;
import com.mcwonders.mkd.main.activity.SystemMessageActivity;
import com.mcwonders.mkd.main.reminder.ReminderManager;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.contact.core.item.AbsContactItem;
import com.mcwonders.uikit.contact.core.item.ItemTypes;
import com.mcwonders.uikit.contact.core.model.ContactDataAdapter;
import com.mcwonders.uikit.contact.core.viewholder.AbsContactViewHolder;
import java.util.ArrayList;
import java.util.List;


/**
 * 集成通讯录列表
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class SettingListFragment extends MainTabFragment {

    private SettingFragment fragment;

    public SettingListFragment() {
        setContainerId(MainTab.SETTING.fragmentId);
    }

    /**
     * ******************************** 功能项定制 ***********************************
     */
    public final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        static final FuncItem BLACK_LIST = new FuncItem();
        static final FuncItem MY_COMPUTER = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }

        public static final class FuncViewHolder extends AbsContactViewHolder<FuncItem> {
            private ImageView image;
            private TextView funcName;
            private TextView unreadNum;

            @Override
            public View inflate(LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.func_contacts_item, null);
                this.image = (ImageView) view.findViewById(R.id.img_head);
                this.funcName = (TextView) view.findViewById(R.id.tv_func_name);
                this.unreadNum = (TextView) view.findViewById(R.id.tab_new_msg_label);
                return view;
            }

            @Override
            public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {
                if (item == VERIFY) {
                    funcName.setText("验证提醒");
                    image.setImageResource(R.drawable.icon_verify_remind);
                    image.setScaleType(ScaleType.FIT_XY);
                    int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
                    updateUnreadNum(unreadCount);
                    ReminderManager.getInstance().registerUnreadNumChangedCallback(new ReminderManager.UnreadNumChangedCallback() {
                        @Override
                        public void onUnreadNumChanged(ReminderItem item) {
                            if (item.getId() != ReminderId.CONTACT) {
                                return;
                            }

                            updateUnreadNum(item.getUnread());
                        }
                    });
                } else if (item == BLACK_LIST) {
                    funcName.setText("黑名单");
                    image.setImageResource(R.drawable.ic_black_list);
                } else if (item == MY_COMPUTER) {
                    funcName.setText("我的电脑");
                    image.setImageResource(R.drawable.ic_my_computer);
                }

                if (item != VERIFY) {
                    image.setScaleType(ScaleType.FIT_XY);
                    unreadNum.setVisibility(View.GONE);
                }
            }

            private void updateUnreadNum(int unreadCount) {
                // 2.*版本viewholder复用问题
                if (unreadCount > 0 && funcName.getText().toString().equals("验证提醒")) {
                    unreadNum.setVisibility(View.VISIBLE);
                    unreadNum.setText("" + unreadCount);
                } else {
                    unreadNum.setVisibility(View.GONE);
                }
            }
        }

        static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<AbsContactItem>();
            items.add(VERIFY);
//            items.add(NORMAL_TEAM);
//            items.add(ADVANCED_TEAM);
            items.add(BLACK_LIST);
            items.add(MY_COMPUTER);

            return items;
        }

        static void handle(Context context, AbsContactItem item) {
            if (item == VERIFY) {
                SystemMessageActivity.start(context);
            }
//            else if (item == NORMAL_TEAM) {
//                TeamListActivity.start(context, ItemTypes.TEAMS.NORMAL_TEAM);
//            } else if (item == ADVANCED_TEAM) {
//                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
//            }
            else if (item == MY_COMPUTER) {
                SessionHelper.startP2PSession(context, DemoCache.getAccount());
            } else if (item == BLACK_LIST) {
                BlackListActivity.start(context);
            }
        }
    }


    /**
     * ******************************** 生命周期 ***********************************
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent(); // 触发onInit，提前加载
    }


    @Override
    protected void onInit() {
        addContactFragment();  // 集成通讯录页面
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addContactFragment() {
        fragment = new SettingFragment();
        fragment.setContainerId(R.id.setting_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (SettingFragment) activity.addFragment(fragment);

        // 功能项定制
        fragment.setContactsCustomization(new ContactsCustomization() {
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncItem.FuncViewHolder.class;
            }

            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncItem.provide();
            }

            @Override
            public void onFuncItemClick(AbsContactItem item) {
                FuncItem.handle(getActivity(), item);
            }
        });
    }


}