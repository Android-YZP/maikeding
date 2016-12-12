package com.mcwonders.mkd.session.viewholder;

import android.widget.ImageView;

import com.mcwonders.mkd.session.extension.StickerAttachment;
import com.mcwonders.uikit.common.util.sys.ScreenUtil;
import com.mcwonders.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.mcwonders.uikit.session.emoji.StickerManager;
import com.mcwonders.uikit.session.viewholder.MsgViewHolderBase;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderSticker extends MsgViewHolderBase {

    private ImageView baseView;

    @Override
    protected int getContentResId() {
        return com.mcwonders.mkd.R.layout.nim_message_item_sticker;
    }

    @Override
    protected void inflateContentView() {
        baseView = findViewById(com.mcwonders.mkd.R.id.message_item_sticker_image);
        baseView.setMaxWidth(MsgViewHolderThumbBase.getImageMaxEdge());
    }

    @Override
    protected void bindContentView() {
        StickerAttachment attachment = (StickerAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        ImageLoader.getInstance().displayImage(StickerManager.getInstance().getStickerBitmapUri(attachment.getCatalog
                (), attachment.getChartlet()), baseView, StickerManager.getInstance().getStickerImageOptions
                (ScreenUtil.dip2px(com.mcwonders.mkd.R.dimen.mask_sticker_bubble_width)));
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
