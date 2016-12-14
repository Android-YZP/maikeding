package com.mcwonders.mkd.main.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.mcwonders.mkd.BuildConfig;
import com.mcwonders.mkd.R;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.model.ToolBarOptions;

public class AboutActivity extends UI {
	
	private TextView versionGit;
	private TextView versionDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.mcwonders.mkd.R.layout.about_layout);
		ToolBarOptions options = new ToolBarOptions();
		options.titleString = " ";
		options.logoId = R.drawable.logo_login;
		setToolBar(com.mcwonders.mkd.R.id.toolbar, options);
		setToolBar(com.mcwonders.mkd.R.id.toolbar, options);
		findViews();
		initViewData();
	}

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findViews() {
		versionGit = (TextView) findViewById(com.mcwonders.mkd.R.id.version_detail_git);
		versionDate = (TextView) findViewById(com.mcwonders.mkd.R.id.version_detail_date);

//        CustomActions.customButton((Button) findViewById(R.id.about_custom_button_1));
	}

	private void initViewData() {
        // 如果使用的IDE是Eclipse， 将该函数体注释掉。这里使用了Android Studio编译期添加BuildConfig字段的特性
        versionGit.setText(" Version: V 1.0" );
//		versionDate.setText("Build Date:" + BuildConfig.BUILD_DATE);
	}
}
