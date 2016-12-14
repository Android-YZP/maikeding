package com.mcwonders.mkd.session.action;

import android.widget.Toast;

import com.mcwonders.mkd.rts.activity.RTSActivity;
import com.mcwonders.uikit.common.util.sys.NetworkUtil;
import com.mcwonders.uikit.session.actions.BaseAction;

/**
 * Created by huangjun on 2015/7/7.
 */
public class RTSAction extends BaseAction {

    public RTSAction() {
        super(com.mcwonders.mkd.R.drawable.message_plus_rts_selector, com.mcwonders.mkd.R.string.input_panel_RTS);
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            RTSActivity.startSession(getActivity(), getAccount(), RTSActivity.FROM_INTERNAL);
        } else {
            Toast.makeText(getActivity(), com.mcwonders.mkd.R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
        }

    }
}