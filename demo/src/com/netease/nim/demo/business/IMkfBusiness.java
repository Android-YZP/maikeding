package com.netease.nim.demo.business;

public interface IMkfBusiness {

	public abstract String getArticlePage(int pageNo, int pageSize, int channelId) throws Exception;

	public abstract String getArticleDetail(int id) throws Exception;

}