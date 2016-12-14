package com.mcwonders.mkd.login;

import com.mcwonders.uikit.LoginSyncDataStatusObserver;
import com.mcwonders.mkd.DemoCache;
import com.mcwonders.uikit.NimUIKit;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
//        DropManager.getInstance().destroy();
    }
}
