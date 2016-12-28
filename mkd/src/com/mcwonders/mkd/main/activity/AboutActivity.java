package com.mcwonders.mkd.main.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.mcwonders.mkd.R;
import com.mcwonders.mkd.utils.CommonUtil;
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
        setToolBar(R.id.toolbar, options, R.id.toolbar_about_title);
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
        try {
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager()
                    .getPackageInfo(pkName, 0).versionName;
            versionGit.setText(" 版本号: V " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//		versionDate.setText("Build Date:" + BuildConfig.BUILD_DATE);
    }
}
