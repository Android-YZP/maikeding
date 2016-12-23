package com.mcwonders.mkd.main.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mcwonders.mkd.R;
import com.mcwonders.mkd.main.model.UserMessage;

import java.util.List;


public class UserMessageListAdapter extends BaseAdapter   {
	private List<UserMessage> mUserMessages;
	private Context mContext;
	
	public UserMessageListAdapter() {
	}

	public UserMessageListAdapter(Context context, List<UserMessage> mUserMessages) {
		this.mContext = context;
		this.mUserMessages = mUserMessages;
	}
	@Override
	public int getCount() {
		return mUserMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mUserMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_user_message_list_item, null);
		if(mUserMessages.size()>0){
			UserMessage _user_comment = mUserMessages.get(position);
			TextView tvUserCommentReceiveListTitle = (TextView)view.findViewById(R.id.tv_user_message_list_item_title);
			tvUserCommentReceiveListTitle.setText(_user_comment.getTitle());
			
			TextView tvUserCommentReceiveListRegisterTime = (TextView)view.findViewById(R.id.tv_user_message_list_item_time);
			tvUserCommentReceiveListRegisterTime.setText(_user_comment.getDate()); 
			
			
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
