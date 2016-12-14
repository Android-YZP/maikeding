package com.mcwonders.mkd.avchat.activity;

import android.os.Bundle;

import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.model.ToolBarOptions;

/**
 * Created by liuqijun on 7/19/16.
 * 注意:全局配置,不区分用户
 */
public class AVChatSettingsActivity extends UI {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.mcwonders.mkd.R.layout.avchat_settings_layout);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = com.mcwonders.mkd.R.string.nrtc_settings;
        setToolBar(com.mcwonders.mkd.R.id.toolbar, options);


    }

}
