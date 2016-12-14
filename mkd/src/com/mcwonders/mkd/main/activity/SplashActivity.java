package com.mcwonders.mkd.main.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mcwonders.mkd.R;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.login.maixinlogin.UserLoginActivity;
import com.mcwonders.mkd.utils.CommonUtil;

public class SplashActivity extends AppCompatActivity {

    private static final int GO = 1;
    private User userInfo;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GO){
                if (userInfo==null){
                    UserLoginActivity.start(SplashActivity.this, false);
                }else {
                    MainActivity.start(SplashActivity.this, null);
                }
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userInfo = CommonUtil.getUserInfo(this);
        Message msg = Message.obtain();
        msg.what = GO;
        handler.sendMessageDelayed(msg,2000);
    }
}
