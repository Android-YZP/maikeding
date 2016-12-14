package com.mcwonders.mkd.contact.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mcwonders.uikit.cache.NimUserInfoCache;
import com.mcwonders.uikit.common.adapter.TViewHolder;
import com.mcwonders.mkd.contact.activity.BlackListAdapter;
import com.mcwonders.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by huangjun on 2015/9/22.
 */
public class BlackListViewHolder extends TViewHolder {
    private HeadImageView headImageView;
    private TextView accountText;
    private Button removeBtn;
    private UserInfoProvider.UserInfo user;

    @Override
    protected int getResId() {
        return com.mcwonders.mkd.R.layout.black_list_item;
    }

    @Override
    protected void inflate() {
        headImageView = findView(com.mcwonders.mkd.R.id.head_image);
        accountText = findView(com.mcwonders.mkd.R.id.account);
        removeBtn = findView(com.mcwonders.mkd.R.id.remove);
    }

    @Override
    protected void refresh(Object item) {
        user = (NimUserInfo) item;

        accountText.setText(NimUserInfoCache.getInstance().getUserDisplayName(user.getAccount()));
        headImageView.loadBuddyAvatar(user.getAccount());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAdapter().getEventListener().onItemClick(user);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAdapter().getEventListener().onRemove(user);
            }
        });
    }

    protected final BlackListAdapter getAdapter() {
        return (BlackListAdapter) adapter;
    }
}
