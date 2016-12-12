package com.netease.nim.demo.utils;

import android.support.v7.app.AppCompatActivity;

import com.netease.nim.demo.NimApplication;
import com.netease.nim.uikit.common.activity.UI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Smith on 2016/11/16.
 */

public class MyApplication extends NimApplication {
    public static ArrayList<UI> activities = new ArrayList<>();

    public void onCreate() {
        super.onCreate();
        //初始化sdk
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
        JPushInterface.init(this);
        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<>();
        set.add("test");//名字任意，可多添加几个
        JPushInterface.setTags(this, set, null);//设置标签
    }

}
