package com.mcwonders.mkd.session.action;

import android.content.Intent;

import com.mcwonders.uikit.session.actions.BaseAction;
import com.mcwonders.uikit.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class FileAction extends BaseAction {

    public FileAction() {
        super(com.mcwonders.mkd.R.drawable.message_plus_file_selector, com.mcwonders.mkd.R.string.input_panel_file);
    }

    @Override
    public void onClick() {
        int requestCode = makeRequestCode(RequestCode.GET_LOCAL_FILE);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.GET_LOCAL_FILE://选择文件后
                if (data != null) {
                    File file = new File(data.getData().getPath());
                    IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
                    sendMessage(message);
                }
                break;
        }
    }
}
