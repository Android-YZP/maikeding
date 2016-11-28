package com.netease.nim.demo.session.action;

import android.content.Intent;
import android.util.Log;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class FileAction extends BaseAction {
    private static final int FILE_CODE = 1;//选择文件

    public FileAction() {
        super(R.drawable.message_plus_file_selector, R.string.input_panel_file);
    }

    /**
     * **********************文件************************
     */
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        getActivity().startActivityForResult(intent, FILE_CODE);
//        FileBrowserActivity.startActivityForResult(getActivity(), makeRequestCode(RequestCode.GET_LOCAL_FILE));
    }

    @Override
    public void onClick() {
        chooseFile();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CODE://选择文件后
                Log.d("zzz----", "111111111");
                File file = new File(data.getData().getPath());
                IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
                sendMessage(message);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
