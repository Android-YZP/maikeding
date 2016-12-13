package com.mcwonders.uikit.demand.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by SunnyJiang on 2016/9/1.
 */
public class CommonUtil {
    /**
     * 发送错误信息到消息队列
     *
     * @param errorMsg
     */
    public static void sendErrorMessage(String errorMsg, Handler handler) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("ErrorMsg", errorMsg);
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
