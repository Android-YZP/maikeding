package com.mcwonders.mkd.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.mcwonders.mkd.DemoCache;

/**
 * Created by huangjun on 2015/7/28.
 */
public class RTSAttachment extends CustomAttachment {

    private byte flag;

    public RTSAttachment() {
        super(CustomAttachmentType.RTS);
    }

    public RTSAttachment(byte flag) {
        this();
        this.flag = flag;
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("flag", flag);
        return data;
    }

    @Override
    protected void parseData(JSONObject data) {
        flag = data.getByte("flag");
    }

    public byte getFlag() {
        return flag;
    }

    public String getContent() {
        return DemoCache.getContext().getString(getFlag() == 0 ? com.mcwonders.mkd.R.string.start_session_record : com.mcwonders.mkd.R.string
                .session_end_record);
    }
}
