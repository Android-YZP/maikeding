package com.mcwonders.uikit.common.ui.dialog;

import android.util.Pair;
import android.widget.TextView;

import com.mcwonders.uikit.common.adapter.TViewHolder;

public class CustomDialogViewHolder extends TViewHolder {

	private TextView itemView;

	@Override
	protected int getResId() {
		return com.mcwonders.uikit.R.layout.nim_custom_dialog_list_item;
	}

	@Override
	protected void inflate() {
		itemView = (TextView) view.findViewById(com.mcwonders.uikit.R.id.custom_dialog_text_view);
	}

	@Override
	protected void refresh(Object item) {
        if(item instanceof Pair<?,?>){
            Pair<String,Integer> pair = (Pair<String, Integer>) item;
            itemView.setText(pair.first);
            itemView.setTextColor(context.getResources().getColor(pair.second));
        }
	}

}
