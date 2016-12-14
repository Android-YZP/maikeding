package com.mcwonders.mkd.business.imp;

import android.util.Log;


import com.mcwonders.mkd.business.IMkfBusiness;
import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.exception.ServiceException;
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

public class MkfBusinessImp implements IMkfBusiness {
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
	public String getArticlePage(int pageNo,int pageSize,int channelId) throws Exception{
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("channelId", channelId);
		_json_args.put("pageNo", pageNo);
		_json_args.put("pageSize", pageSize);
		_json_args.put("descLen", "30");
		
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_mkf_getArticlePage", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.ARTICLE_PAGE, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
	@Override
	public String getArticleDetail(int id) throws Exception {
		String _result = null;
		//封装成json数据
		JSONObject _json_args = new JSONObject();
		_json_args.put("id", id);
		_json_args.put("clientVersion", "1.0");
		_json_args.put("clientType", "Phone");
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getArticleDetail", _json_args.toString());
		//添加头信息
		String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
		_result = this.getResultFromUrlConnectionWithGet(CommonConstants.ARTICLE_DETAIL, _json_args.toString(), _md5_value.substring(0,8));
		return _result;
	}
	
}
