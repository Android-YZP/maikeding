package com.netease.nim.demo.business;


import com.netease.nim.demo.login.maixinlogin.User;

public interface ICreativeBusiness {

	public abstract String getHomeHotCreativeList() throws Exception;

	public abstract String getHomeHotDesignList() throws Exception;
	
	public abstract String getFindCreativeList(int pageNo, int pageSize, int channelId) throws Exception;
	
	public abstract String getCreativeDetail(String id, String userId) throws Exception;
	
	public abstract String getCreativeDetailBannerList() throws Exception;
	
	public abstract String getCreativeDetailCommentList(String contentId, String count, String orderBy) throws Exception;

	public abstract String getMemberAttentionSave(User mUser, int userId) throws Exception;

	public abstract String getMemberAttentionDelete(User mUser, int userId) throws Exception;

	public abstract String getMemberContentCollect(User mUser, int contentId) throws Exception;

	public abstract String getMemberContentUp(User mUser, int contentId) throws Exception;

	public abstract String getMemberContentDown(User mUser, int contentId) throws Exception;

	public abstract String getMemberCommentAdd(User mUser, int contentId,
											   String text) throws Exception;

	public abstract String getMemberCommentReplyAdd(User mUser,
													int replyToCommentId, String username, int type,
													String text) throws Exception;

	public abstract String getSearchCreativeList(int pageNo, int pageSize,
												 String mKeyword) throws Exception;

	public abstract String getUserCreativeList(int pageNo, int pageSize,
											   User mUser) throws Exception;

	public abstract String getUserAttentionCreativeList(int pageNo, int pageSize, User mUser) throws Exception;

	public abstract String deleteUserAttentionCreative(int contentId, User mUser) throws Exception;

}