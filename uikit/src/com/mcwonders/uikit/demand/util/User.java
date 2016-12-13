package com.mcwonders.uikit.demand.util;
/**
 * 已登录用户信息
 * @author JLJ
 *
 */
public class User {

	/**
	 * token : 158dfeda02c685b2a59c5d8f08d26cdc
	 * account : 天外飞仙
	 * password : 111111
	 * id : 21
	 * userImg : http://img.maikejia.com/u/cms/www/201511/23121345w7yj.jpg
	 * username : 天外飞仙
	 * mobile : 1
	 * verifyCode : 6160d1ab21
	 * success : true
	 */

	private String token;
	private String account;
	private String password;
	private int id;
	private String userImg;
	private String username;
	private String mobile;
	private String verifyCode;
	private boolean success;

	public void setToken(String token) {
		this.token = token;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getToken() {
		return token;
	}

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public int getId() {
		return id;
	}

	public String getUserImg() {
		return userImg;
	}

	public String getUsername() {
		return username;
	}

	public String getMobile() {
		return mobile;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public boolean getSuccess() {
		return success;
	}
}
