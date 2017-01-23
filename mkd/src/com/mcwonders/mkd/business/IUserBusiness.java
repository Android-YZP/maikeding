package com.mcwonders.mkd.business;


import com.mcwonders.mkd.login.maixinlogin.User;

import java.util.Map;

public interface IUserBusiness {

//	/**
//	 * 用户登录
//	 */
//	public abstract String userlogin(User registerUser) throws Exception;
//
//	/**
//	 * 用户上传头像接口
//	 */
//	public abstract String userUploadHeader(User registerUser, String loginname)
//			throws Exception;
//
//	/**
//	 * 上传文件
//	 */
//	public abstract String uploadUserPhoto(File mFile) throws Exception;

    /**
     * 获取首页轮播列表
     *
     * @return
     */
    public abstract String getHomeBannerList() throws Exception;

    /**
     * 获取手机短信验证码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public abstract String getMobilemsgRegister(String phone) throws Exception;

    String getUserID(String phone) throws Exception;

    String getDemandDetail(int demandId) throws Exception;

    /**
     * 获取手机短信验证码验证信息
     *
     * @param phone   手机号
     * @param smsCode 验证码
     * @return
     * @throws Exception
     */
    public abstract String getMobilemsgValidate(String phone, String smsCode) throws Exception;

    /**
     * 用户注册
     *
     * @param mPhone
     * @param mCode
     * @param username
     * @param password
     * @return
     */
    public abstract String getUserRegister(String mPhone, String mCode,
                                           String username, String password) throws Exception;

    /**
     * 用户登录
     *
     * @param pPhone
     * @param pPassword
     * @return
     * @throws Exception
     */
    public abstract String getUserLogin(String pPhone, String pPassword) throws Exception;


    /**
     * 修改用户信息
     *
     * @return
     * @throws Exception
     */
    public abstract String setUserInfo(String account, String value, String type) throws Exception;

    /**
     * 得到用户信息
     *
     * @return
     * @throws Exception
     */
    public abstract String getUserInfo(String account) throws Exception;

    /**
     * 用户忘记密码第一步
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public abstract String getUserForgotPasswordOne(String phone) throws Exception;

    /**
     * 用户忘记密码第二步，重置密码
     *
     * @param mPhone
     * @param smsCode
     * @param newpwd
     * @param newpwdAgain
     * @return
     * @throws Exception
     */
    public abstract String getUserForgotPasswordTwo(String mPhone,
                                                    String smsCode, String newpwd, String newpwdAgain) throws Exception;

    /**
     * 获取用户登录后的用户详情：如用户名、头像、创意设计粉丝的数量
     *
     * @param userid
     * @return
     * @throws Exception
     */
    public abstract String getUserDetail(int userid) throws Exception;

    /**
     * 获取我关注的人的列表
     *
     * @param pageNo
     * @param pageSize
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserAttentionUserList(int pageNo, int pageSize,
                                                    User mUser) throws Exception;

    /**
     * 取消我关注的人
     *
     * @param friendId
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String deleteUserAttentionUser(int friendId, User mUser) throws Exception;

    /**
     * 获取我的粉丝
     *
     * @param pageNo
     * @param pageSize
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserFansList(int pageNo, int pageSize, User mUser) throws Exception;

    /**
     * 移除粉丝
     *
     * @param friendId
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String deleteUserFans(int friendId, User mUser) throws Exception;

    /**
     * 获取用户评论
     *
     * @param pageNo
     * @param pageSize
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserCommentsList(int pageNo, int pageSize,
                                               User mUser) throws Exception;

    /**
     * 移除评论
     *
     * @param commentId
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String deleteUserComment(int commentId, User mUser) throws Exception;

    /**
     * 我的评论
     *
     * @param pageNo
     * @param pageSize
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserSelfCommentsList(int pageNo, int pageSize,
                                                   User mUser) throws Exception;

    /**
     * 我的通知
     *
     * @param pageNo
     * @param pageSize
     * @param mUser
     * @param type
     * @return
     * @throws Exception
     */
    public abstract String getUserMessagesList(int pageNo, int pageSize,
                                               User mUser, int type) throws Exception;

    /**
     * 删除通知
     *
     * @param messageId
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String deleteUserMessage(int messageId, User mUser) throws Exception;

    /**
     * 获取通知详情
     *
     * @param mNoticeId
     * @return
     * @throws Exception
     */
    public abstract String getUserNoticeDetail(int mNoticeId, User mUser) throws Exception;

    /**
     * 用户提交创意
     *
     * @param mTitle
     * @param mIntro
     * @param mSource
     * @param mDetail
     * @param draft
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String userCommitCreative(String mTitle, String mIntro,
                                              String mSource, String mDetail, boolean draft, User mUser) throws Exception;

    /**
     * 用户修改密码
     *
     * @param oldpwd
     * @param newpwd
     * @param againpwd
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String updateUserPwd(String oldpwd, String newpwd,
                                         String againpwd, User mUser) throws Exception;

    /**
     * 获取安全中心-验证码
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public abstract String getUserSettingSecurityCode(String phone, User mUser) throws Exception;

    /**
     * 用户设置-安全中心修改绑定手机
     *
     * @param mPhone
     * @param smsCode
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserSettingSecurityPhoneUpdate(String mPhone,
                                                             String smsCode, User mUser) throws Exception;

    /**
     * 验证创业大赛是否登录
     *
     * @param verifyCode
     * @return
     * @throws Exception
     */
    public abstract Map<String, String> startBusinessUserCheckLogin(String verifyCode) throws Exception;

    /**
     * 用户设置-安全中心-获取邮箱验证码
     *
     * @param pEmail
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserSettingSecurityEmailCode(String pEmail,
                                                           User mUser) throws Exception;

    /**
     * 用户设置-安全中心-验证邮箱验证码
     *
     * @param mEmail
     * @param emailCode
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String getUserSettingSecurityEmailUpdate(String mEmail,
                                                             String emailCode, User mUser) throws Exception;

    /**
     * 用户设置-修改用户信息
     *
     * @param _usersign
     * @param mUser
     * @return
     * @throws Exception
     */
    public abstract String updateUserSettingPsInfo(
            String _usersign, User mUser) throws Exception;

    public abstract String checkAppUpdate() throws Exception;

}