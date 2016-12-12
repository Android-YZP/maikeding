package com.mcwonders.uikit.session.viewholder;

import android.util.Log;

import com.mcwonders.uikit.R;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderFile extends MsgViewHolderThumbBase {

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_picture;
    }

    @Override
    protected void onItemClick() {
        Log.d("zzz------File","22222222220");
//        Intent intent = null;
//        intent = IntentUtil.getRightIntent(youpanFile.getSuf(), local_address);
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            UIUtils.showTip("暂不支持打开此类型文件");
//        }
//        WatchMessagePictureActivity.start(context, message);
    }

    @Override
    protected String thumbFromSourceFile(String path) {
        return path;
    }
}
