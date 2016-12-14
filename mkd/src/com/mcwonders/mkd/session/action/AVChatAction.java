package com.mcwonders.mkd.session.action;

import android.widget.Toast;

import com.mcwonders.uikit.common.util.sys.NetworkUtil;
import com.mcwonders.uikit.session.actions.BaseAction;
import com.mcwonders.mkd.avchat.activity.AVChatActivity;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class AVChatAction extends BaseAction {
    private AVChatType avChatType;

    public AVChatAction(AVChatType avChatType) {
        super(avChatType == AVChatType.AUDIO ? com.mcwonders.mkd.R.drawable.message_plus_audio_chat_selector : com.mcwonders.mkd.R.drawable.message_plus_video_chat_selector,
                avChatType == AVChatType.AUDIO ? com.mcwonders.mkd.R.string.input_panel_audio_call : com.mcwonders.mkd.R.string.input_panel_video_call);
        this.avChatType = avChatType;
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            startAudioVideoCall(avChatType);
        } else {
            Toast.makeText(getActivity(), com.mcwonders.mkd.R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
        AVChatActivity.start(getActivity(), getAccount(), avChatType.getValue(), AVChatActivity.FROM_INTERNAL);
    }
}
