package com.mcwonders.mkd.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcwonders.mkd.DemoCache;
import com.mcwonders.uikit.cache.NimUserInfoCache;
import com.mcwonders.uikit.common.ui.dialog.DialogMaker;
import com.mcwonders.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.mcwonders.uikit.model.ToolBarOptions;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * 添加好友页面
 * Created by huangjun on 2015/8/11.
 */
public class AddFriendActivity extends UI {

    private ClearableEditTextWithIcon searchEdit;

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AddFriendActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mcwonders.mkd.R.layout.add_friend_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = com.mcwonders.mkd.R.string.add_buddy;
        setToolBar(com.mcwonders.mkd.R.id.toolbar, options, com.mcwonders.mkd.R.id.toolbar_add_friend_title);

        findViews();
        initActionbar();
    }

    private void findViews() {
        searchEdit = findView(com.mcwonders.mkd.R.id.search_friend_edit);
        searchEdit.setDeleteImage(com.mcwonders.mkd.R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(com.mcwonders.mkd.R.id.action_bar_right_clickable_textview);
        toolbarView.setText(com.mcwonders.mkd.R.string.search);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchEdit.getText().toString())) {
                    Toast.makeText(AddFriendActivity.this, com.mcwonders.mkd.R.string.not_allow_empty, Toast.LENGTH_SHORT).show();
                } else if (searchEdit.getText().toString().equals(DemoCache.getAccount())) {
                    Toast.makeText(AddFriendActivity.this, com.mcwonders.mkd.R.string.add_friend_self_tip, Toast.LENGTH_SHORT).show();
                } else {
                    query();
                }
            }
        });
    }

    private void query() {
        DialogMaker.showProgressDialog(this, null, false);
        final String account = searchEdit.getText().toString().toLowerCase();
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo user) {
                DialogMaker.dismissProgressDialog();
                if (user == null) {
                    EasyAlertDialogHelper.showOneButtonDiolag(AddFriendActivity.this, com.mcwonders.mkd.R.string.user_not_exsit,
                            com.mcwonders.mkd.R.string.user_tips, com.mcwonders.mkd.R.string.ok, false, null);
                } else {
                    UserProfileActivity.start(AddFriendActivity.this, account);
                }
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                if (code == 408) {
                    Toast.makeText(AddFriendActivity.this, com.mcwonders.mkd.R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddFriendActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(AddFriendActivity.this, "on exception:" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
