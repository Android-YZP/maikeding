package com.mcwonders.mkd.business.imp;

import android.util.Log;


import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.business.ICreativeBusiness;
import com.mcwonders.mkd.utils.ConvertUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class CreativeBusinessImp implements ICreativeBusiness {
	/**
	 * 以post方式发送url请求
	 */
	public String getResultFromUrlConnection(String urlconn,String jsonargs,String sVerifyCode) throws Exception{
		String result=null;
		
		InputStream in=null;
		URL url = null;
		HttpURLConnection urlConnection = null;
		OutputStream out = null;
		byte[] data = null;
		
		
		try {
			data = jsonargs.getBytes();
			url = new URL(urlconn);
			urlConnection = (HttpURLConnection)url.openConnection();
			//设置可以读取数据
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			urlConnection.setRequestProperty("verifyCode", sVerifyCode);
			urlConnection.connect();
			out = urlConnection.getOutputStream();
			out.write(data);
			out.flush();
			
			int statusCode =urlConnection.getResponseCode();
			Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", urlconn + ",_status = "+statusCode);
			if(statusCode != HttpURLConnection.HTTP_OK){
				throw new ServiceException("服务器错误");
			}
			in = new BufferedInputStream(urlConnection.getInputStream());
			result = getStrFromInputSteam(in);
			
		} catch(ConnectException e){
			e.printStackTrace();
			throw new ServiceException("连接出错，请检查您的网络");
		}catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new ServiceException("连接超时，请检查您的网络...");
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new ServiceException("服务器响应超时...");
		}catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().equals("服务器错误")){
				throw new ServiceException(e.getMessage());
			}else{
				throw new ServiceException("查询出错");
			}
			
		} finally{
			if(out!=null){
				out.close();
			}
			
			if(in!=null){
				in.close();
			}
			
			if(urlConnection!=null){
				urlConnection.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * 以get方式发送url请求
	 */
	public String getResultFromUrlConnectionWithGet(String urlconn,String jsonargs,String sVerifyCode) throws Exception{
		String result=null;
		
		InputStream in=null;
		URL url = null;
		HttpURLConnection urlConnection = null;
		String _urlconn = urlconn + "?jsonString=" + jsonargs;
		
		try {
			url = new URL(_urlconn);
			urlConnection = (HttpURLConnection)url.openConnection();
			//设置可以读取数据
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			urlConnection.setRequestProperty("verifyCode", sVerifyCode);
			urlConnection.connect();
			
			int statusCode =urlConnection.getResponseCode();
			Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", _urlconn + ",_status = "+statusCode);
			if(statusCode != HttpURLConnection.HTTP_OK){
				throw new ServiceException("服务器错误");
			}
			in = new BufferedInputStream(urlConnection.getInputStream());
			result = getStrFromInputSteam(in);
			
		} catch(ConnectException e){
			e.printStackTrace();
			throw new ServiceException("连接出错，请检查您的网络");
		}catch (ConnectTimeoutException e) {
			e.printStackTrace();
			throw new ServiceException("连接超时，请检查您的网络...");
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new ServiceException("服务器响应超时...");
		}catch(Exception e){
			e.printStackTrace();
			if(e.getMessage().equals("服务器错误")){
				throw new ServiceException(e.getMessage());
			}else{
				throw new ServiceException("查询出错");
			}
			
		} finally{
			
			if(in!=null){
				in.close();
			}
			
			if(urlConnection!=null){
				urlConnection.disconnect();
			}
		}
		return result;
	}
	
	public String getStrFromInputSteam(InputStream in) throws Exception{  
	     BufferedReader bf=new BufferedReader(new InputStreamReader(in,"UTF-8"));  
	     //最好在将字节流转换为字符流的时候 进行转码  
	     StringBuffer buffer=new StringBuffer();  
	     String line="";  
	     while((line=bf.readLine())!=null){  
	         buffer.append(line);  
	     }  
	       
	    return buffer.toString();  
	}

	@Override
	public String getHomeHotCreativeList() throws Exception {
		return getContentPage("1","10","0");
	}

	@Override
	public String getHomeHotDesignList() throws Exception {
		return getContentPage("1","5","100");
	}
	
	public String getContentPage(String pageNo,String pageSize,String designStatus) throws Exception{
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("designStatus", designStatus);//设计状态 	0：未设计、1：设计中、2：已设计、100：设计中及已设计 
		_json_args.put("titleLen", "14");
		_json_args.put("descLen", "30");
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_creative_home_getContentPage", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
//		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_md5value", _md5_value.substring(0,8));
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.CONTENT_PAGE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
	@Override
	public String getFindCreativeList(int pageNo,int pageSize,int channelId) throws Exception {
		return getContentPage(String.valueOf(pageNo),String.valueOf(pageSize),"0",String.valueOf(channelId));
	}
	
	public String getContentPage(String pageNo,String pageSize,String designStatus,String channelId) throws Exception{
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("designStatus", designStatus);//设计状态 	0：未设计、1：设计中、2：已设计、100：设计中及已设计 
		_json_args.put("titleLen", "14");
		_json_args.put("descLen", "30");
		if (channelId!=null&&!channelId.equals("0")){
			_json_args.put("channelId", channelId);
		}else{
			_json_args.put("channelId", "");
		}

		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_creative_cate_getContentPage", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
//		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_md5value", _md5_value.substring(0,8));
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.CONTENT_PAGE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
	@Override
	public String getCreativeDetail(String id,String userId) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("id", id);
		if(userId!=null&&!userId.equals("")){
			_json_args.put("userId", userId);
		}
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getCreativeDetail", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.CONTENT_DETAIL, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
	@Override
	public String getCreativeDetailBannerList() throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("positionId", "1");
		_json_args.put("count", "5");
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getCreativeDetailBannerList", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
//		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_md5value is ", _md5_value.substring(0,8));
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.BANNER_LIST, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
	
	@Override
	public String getCreativeDetailCommentList(String contentId,String count,String orderBy) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		if(count!=null&&!count.equals("")){
			_json_args.put("count", count);
		}
		if(count!=null&&!count.equals("")){
			_json_args.put("orderBy", orderBy);
		}
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getCreativeDetailCommentList", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.COMMENT_LIST, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}

	@Override
	public String getMemberAttentionSave(User mUser, int userId)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("friendId", userId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberAttentionSave", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_ATTENTION_SAVE, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberAttentionDelete(User mUser, int userId)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("friendId", userId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberAttentionDelete", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_ATTENTION_DELETE, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberContentCollect(User mUser, int contentId)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberContentCollect", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnection(CommonConstants.MEMBER_CONTENT_COLLECT, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberContentUp(User mUser, int contentId)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberContentUp", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnection(CommonConstants.MEMBER_CONTENT_UP, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberContentDown(User mUser, int contentId)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberContentDown", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnection(CommonConstants.MEMBER_CONTENT_DOWN, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberCommentAdd(User mUser, int contentId, String text)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		_json_args.put("text", text);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberCommentAdd", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnection(CommonConstants.MEMBER_COMMENT_ADD, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getMemberCommentReplyAdd(User mUser, int replyToCommentId,
			String username, int type, String text) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("commentId", replyToCommentId);
		_json_args.put("username", username);
		_json_args.put("type", type);
		_json_args.put("text", text);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getMemberCommentReplyAdd", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnection(CommonConstants.MEMBER_COMMENT_REPLY_ADD, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getSearchCreativeList(int pageNo, int pageSize,
			String mKeyword) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("q", mKeyword);
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("descLen", "30");
		
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getSearchCreativeList", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = this.getResultFromUrlConnection(CommonConstants.SEARCH_PAGE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}

	@Override
	public String getUserCreativeList(int pageNo, int pageSize, User mUser)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("descLen", "30");
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getUserCreativeList", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_CONTRIBUTE_PAGE, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String getUserAttentionCreativeList(int pageNo, int pageSize,
			User mUser) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("descLen", "30");
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getUserAttentionCreativeList", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_COLLECTION_PAGE, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}

	@Override
	public String deleteUserAttentionCreative(int contentId, User mUser)
			throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("contentId", contentId);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_deleteUserAttentionCreative", _json_args.toString());
		//头信息-verifyCode保存在用户信息中
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_COLLECTION_DELETE, _json_args.toString(), mUser.getVerifyCode());
		return _result;
	}
}
