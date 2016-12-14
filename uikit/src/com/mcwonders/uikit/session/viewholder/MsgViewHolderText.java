package com.mcwonders.uikit.session.viewholder;

import android.content.Intent;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mcwonders.uikit.NimUIKit;
import com.mcwonders.uikit.common.util.sys.ScreenUtil;
import com.mcwonders.uikit.demand.activity.DemandDetailActivity;
import com.mcwonders.uikit.session.emoji.MoonUtil;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {

    @Override
    protected int getContentResId() {
        return com.mcwonders.uikit.R.layout.nim_message_item_text;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void onItemClick() {
        int result = message.getContent().indexOf("www.maikejia.com/demand");
        if (result != -1) {
            int demandId = Integer.valueOf(message.getContent().substring(result + 24, message.getContent().lastIndexOf(".")));
            Intent intent = new Intent(context, DemandDetailActivity.class);
            intent.putExtra("demandId", 371);
            context.startActivity(intent);
        } else {
            Log.d("zzz------text", "result = -1");
        }
    }

    @Override
    protected void bindContentView() {
        layoutDirection();

        TextView bodyTextView = findViewById(com.mcwonders.uikit.R.id.nim_message_item_text_body);
        bodyTextView.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void layoutDirection() {
        TextView bodyTextView = findViewById(com.mcwonders.uikit.R.id.nim_message_item_text_body);
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(com.mcwonders.uikit.R.drawable.nim_message_item_left_selector);
            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            bodyTextView.setBackgroundResource(com.mcwonders.uikit.R.drawable.nim_message_item_right_selector);
            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }
}
