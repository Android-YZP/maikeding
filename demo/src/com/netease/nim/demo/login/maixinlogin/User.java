package com.netease.nim.demo.login.maixinlogin;
/**
 * 已登录用户信息
 * @author JLJ
 *
 */
public class User {
	//登录时获取
	private String account;
	private String password;
	private int id;
	private String userImg;
	private String username;
	private String mobile;
	private String verifyCode;
	//创意数量
	private int creatives;
	private int designs;
	private int fans;
	private String userSignature;
	private boolean hasMobile;
	private boolean hasEmail;
	//未读消息数量
	private int unreadMsgCount;
	
	//加入时间
	private String registerTime;
	
	private String realname;
	public User() {
		super();
	}
	
	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public int getCreatives() {
		return creatives;
	}

	public void setCreatives(int creatives) {
		this.creatives = creatives;
	}

	public int getDesigns() {
		return designs;
	}

	public void setDesigns(int designs) {
		this.designs = designs;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public String getUserSignature() {
		return userSignature;
	}

	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}

	public boolean isHasMobile() {
		return hasMobile;
	}

	public void setHasMobile(boolean hasMobile) {
		this.hasMobile = hasMobile;
	}

	public boolean isHasEmail() {
		return hasEmail;
	}

	public void setHasEmail(boolean hasEmail) {
		this.hasEmail = hasEmail;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	
	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}
	
	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public User(String account, String password, int id, String userImg,
				String username, String mobile, String verifyCode, int creatives,
				int designs, int fans, String userSignature, boolean hasMobile,
				boolean hasEmail, int unreadMsgCount, String registerTime) {
		super();
		this.account = account;
		this.password = password;
		this.id = id;
		this.userImg = userImg;
		this.username = username;
		this.mobile = mobile;
		this.verifyCode = verifyCode;
		this.creatives = creatives;
		this.designs = designs;
		this.fans = fans;
		this.userSignature = userSignature;
		this.hasMobile = hasMobile;
		this.hasEmail = hasEmail;
		this.unreadMsgCount = unreadMsgCount;
		this.registerTime = registerTime;
	}

	@Override
	public String toString() {
		return "User [account=" + account + ", password=" + password + ", id="
				+ id + ", userImg=" + userImg + ", username=" + username
				+ ", mobile=" + mobile + ", verifyCode=" + verifyCode
				+ ", creatives=" + creatives + ", designs=" + designs
				+ ", fans=" + fans + ", userSignature=" + userSignature
				+ ", hasMobile=" + hasMobile + ", hasEmail=" + hasEmail + ", unreadMsgCount=" + unreadMsgCount+ ", registerTime=" + registerTime + "]";
	}
	
}
