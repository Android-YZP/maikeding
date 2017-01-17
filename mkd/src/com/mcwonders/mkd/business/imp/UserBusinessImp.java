package com.mcwonders.mkd.business.imp;

import android.util.Log;

import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.business.IUserBusiness;
import com.mcwonders.mkd.utils.ConvertUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserBusinessImp implements IUserBusiness {

    /**
     * 上传照片
     *
     * @param urlconn
     * @param jsonargs
     * @param sVerifyCode
     * @param mFile
     * @return
     * @throws Exception
     */
    private String getResultFromUrlConnectionWithPhoto(
            String urlconn, String jsonargs,
            String sVerifyCode, File mFile) throws Exception {
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        byte[] data = null;
        String BOUNDARY = "|"; // request头和上传文件内容分隔符
        DataInputStream in1 = null;
        try {
            data = jsonargs.getBytes();
            url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            // 设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            urlConnection.setRequestProperty("sVerifyCode", sVerifyCode);
            urlConnection.connect();

            out = new DataOutputStream(urlConnection.getOutputStream());
            in1 = new DataInputStream(new FileInputStream(mFile));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in1.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in1.close();
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();


            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", urlconn + ",status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("照片出错，出现异常错误");
            }

        } finally {
            if (out != null) {
                out.close();
            }

            if (in1 != null) {
                in1.close();
            }

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }


    /**
     * 以post方式发送url请求
     */
    public String getResultFromUrlConnection(String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        OutputStream out = null;
        byte[] data = null;


        try {
            data = jsonargs.getBytes();
            url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
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

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("查询出错");
            }

        } finally {
            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 以get方式发送url请求
     */
    public String getResultFromUrlConnectionWithGet(String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        String _urlconn = urlconn;
        if (jsonargs != null && !jsonargs.equals("")) {
            _urlconn = urlconn + "?jsonString=" + jsonargs;
        }
        Log.d("URL", _urlconn);
        try {
            url = new URL(_urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("verifyCode", sVerifyCode);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", _urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("查询出错");
            }

        } finally {

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public String getStrFromInputSteam(InputStream in) throws Exception {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        //最好在将字节流转换为字符流的时候 进行转码
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = bf.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    private Map<String, String> getResultFromUrlConnectionWithGetReturnSessionId(
            String urlconn, String jsonargs, String sVerifyCode) throws Exception {
        Map<String, String> resultMap = new HashMap<String, String>();
        String result = null;

        InputStream in = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        String _urlconn = urlconn;
        if (jsonargs != null && !jsonargs.equals("")) {
            _urlconn = urlconn + "?jsonString=" + jsonargs;
        }

        try {
            url = new URL(_urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("verifyCode", sVerifyCode);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", _urlconn + ",_status = " + statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("服务器错误");
            }

            String sessionId = this.getCookieValue(urlConnection);
            resultMap.put("sessionId", sessionId);

            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);
            resultMap.put("result", result);

        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ServiceException("连接出错，请检查您的网络");
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("连接超时，请检查您的网络...");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ServiceException("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("服务器错误")) {
                throw new ServiceException(e.getMessage());
            } else {
                throw new ServiceException("查询出错");
            }

        } finally {

            if (in != null) {
                in.close();
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return resultMap;
    }

    public String getCookieValue(HttpURLConnection urlConnection) {
        String cookieskey = "Set-Cookie";
        Map<String, List<String>> maps = urlConnection.getHeaderFields();
        List<String> coolist = maps.get(cookieskey);
        Iterator<String> it = coolist.iterator();
        StringBuffer sbu = new StringBuffer();
        //	    sbu.append("eos_style_cookie=default; ");
        while (it.hasNext()) {
            String _cookie = it.next();
            if (_cookie.contains("JSESSIONID")) {
                sbu.append(_cookie.subSequence(0, _cookie.indexOf(";") + 1));
                break;
            }
            //	    	sbu.append(_cookie);
        }
        //	    sbu.append("appWapLogin=true;");
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "sessionid=", "is " + sbu.toString());
        return sbu.toString();
    }


    @Override
    public String getHomeBannerList() throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("positionId", "1");
        _json_args.put("count", "5");
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
//		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_json_getHomeBannerList", _json_args.toString());
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
//		Log.d(CommonConstants.LOGCAT_TAG_NAME + "_md5value is ", _md5_value.substring(0,8));
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.BANNER_LIST, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    @Override
    public String getMobilemsgRegister(String phone) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", phone);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.MOBILEMSG_REGISTER, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }

    /**
     * 获取需求详情
     */
    @Override
    public String getDemandDetail(int demandId) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("id", demandId);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.GET_DEMAND_DETAIL, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }

    @Override
    public String getMobilemsgValidate(String phone, String smsCode)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", phone);
        _json_args.put("captcha", smsCode);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.MOBILEMSG_VALIDATE, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    @Override
    public String getUserRegister(String mPhone, String mCode, String username,
                                  String password) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("username", username);
        _json_args.put("mobile", mPhone);
        _json_args.put("password", password);
        _json_args.put("captcha", mCode);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.USER_REGISTER, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    @Override
    public String getUserLogin(String pPhone, String pPassword)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("account", pPhone);
        _json_args.put("password", pPassword);

        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        Log.d("参数", _json_args.toString() + ",头信息=====>(verifyCode)" + _md5_value.substring(0, 8));
        _result = this.getResultFromUrlConnection(CommonConstants.USER_LOGIN, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }

    /**
     * 设置个人信息
     *
     * @throws Exception String jsonargs = "{account:'18795618280',value:'女',type:'2'}";//性别
     *                   //		String jsonargs = "{account:'18795618280',value:'2008-08-08',type:'3'}";// 生日
     *                   //		String jsonargs = "{account:'18795618280',value:'13291218351',type:'4'}"; //手机
     *                   //		String jsonargs = "{account:'18795618280',value:'签名嗯哼',type:'6'}"; //签名
     *                   <p>
     *                   //jc_user
     *                   //		String jsonargs = "{account:'18795618280',value:'我就看看昵称存进去没有',type:'1'}"; //昵称
     *                   String jsonargs = "{account:'18795618280',value:'666@qq.com',type:'5'}"; //邮箱
     */
    @Override
    public String setUserInfo(String account, String value, String type) throws Exception {

        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("account", account);
        _json_args.put("value", value);
        _json_args.put("type", type);
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        Log.d("参数", _json_args.toString() + ",头信息=====>(verifyCode)" + _md5_value.substring(0, 8));
        _result = this.getResultFromUrlConnection(CommonConstants.UPDATE_USER_INFO, _json_args.toString(), _md5_value.substring(0, 8));
        Log.d("参数", _result + "123");
        return _result;
    }


    /**
     * 的得到个人信息
     */
    @Override
    public String getUserInfo(String account)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("account", account);
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        Log.d("参数", _json_args.toString() + ",头信息=====>(verifyCode)" + _md5_value.substring(0, 8));
        _result = this.getResultFromUrlConnection(CommonConstants.USER_INFO, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }

    public String getUserForgotPasswordOne(String phone) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", phone);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.USER_FORGOT_PASSWORD_ONE, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    @Override
    public String getUserForgotPasswordTwo(String mPhone, String smsCode,
                                           String newpwd, String newpwdAgain) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", mPhone);
        _json_args.put("newPwd", newpwd);
        _json_args.put("password", newpwdAgain);
        _json_args.put("captcha", smsCode);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnection(CommonConstants.USER_FORGOT_PASSWORD_TWO, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    @Override
    public String getUserDetail(int userid) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("id", userid);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.USER_DETAIL, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }


    public String getUserAttentionUserList(int pageNo, int pageSize, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("pageNo", pageNo);
        _json_args.put("pageSize", pageSize);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_ATTENTION_PAGE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String deleteUserAttentionUser(int friendId, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("friendId", friendId);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_ATTENTION_DELETE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserFansList(int pageNo, int pageSize, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("pageNo", pageNo);
        _json_args.put("pageSize", pageSize);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_FANS_PAGE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String deleteUserFans(int friendId, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("friendId", friendId);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_FANS_DELETE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserCommentsList(int pageNo, int pageSize, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("pageNo", pageNo);
        _json_args.put("pageSize", pageSize);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_COMMENTS_REPAGE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String deleteUserComment(int commentId, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("commentId", commentId);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_COMMENT_DELETE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserSelfCommentsList(int pageNo, int pageSize, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("pageNo", pageNo);
        _json_args.put("pageSize", pageSize);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_COMMENTS_PAGE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserMessagesList(int pageNo, int pageSize, User mUser,
                                      int type) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("pageNo", pageNo);
        _json_args.put("pageSize", pageSize);
        _json_args.put("type", type);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.MEMBER_MESSAGE_PAGE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String deleteUserMessage(int messageId, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("idsstr", String.valueOf(messageId));
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_MESSAGE_DELETE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserNoticeDetail(int mNoticeId, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("id", mNoticeId);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_MESSAGE_READ, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String userCommitCreative(String mTitle, String mIntro,
                                     String mSource, String mDetail, boolean draft, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("title", mTitle);
        _json_args.put("desc", mIntro);
        _json_args.put("txt", mSource);
        _json_args.put("txt1", mDetail);
        _json_args.put("draft", draft);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_CONTRIBUTE_SAVE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String updateUserPwd(String oldpwd, String newpwd, String againpwd,
                                User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("oldpwd", oldpwd);
        _json_args.put("pwd", newpwd);
        _json_args.put("repwd", againpwd);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_PWD, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserSettingSecurityCode(String phone, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", phone);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_SECURITY_MOBILE_CAPTCHA, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserSettingSecurityPhoneUpdate(String mPhone,
                                                    String smsCode, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("mobile", mPhone);
        _json_args.put("captcha", smsCode);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_SECURITY_MOBILE_BIND, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    @Override
    public Map<String, String> startBusinessUserCheckLogin(String verifyCode)
            throws Exception {
        //头信息-verifyCode保存在用户信息中
        Map<String, String> _result = this.getResultFromUrlConnectionWithGetReturnSessionId(CommonConstants.START_BUSINESS_CHECK_LOGIN + verifyCode, null, null);
        return _result;
    }


    public String getUserSettingSecurityEmailCode(String pEmail, User mUser)
            throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("email", pEmail);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_SECURITY_EMAIL_CAPTCHA, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String getUserSettingSecurityEmailUpdate(String mEmail,
                                                    String emailCode, User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("email", mEmail);
        _json_args.put("captcha", emailCode);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_SECURITY_EMAIL_BIND, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    public String updateUserSettingPsInfo(String _usersign,
                                          User mUser) throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("userSignature", _usersign);
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //头信息-verifyCode保存在用户信息中
        _result = this.getResultFromUrlConnection(CommonConstants.MEMBER_PROFILE_UPDATE, _json_args.toString(), mUser.getVerifyCode());
        return _result;
    }


    @Override
    public String checkAppUpdate() throws Exception {
        String _result = null;
        //封装成json数据
        JSONObject _json_args = new JSONObject();
        _json_args.put("clientVersion", "1.0");
        _json_args.put("clientType", "Phone");
        //添加头信息
        String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
        _result = this.getResultFromUrlConnectionWithGet(CommonConstants.CHECK_APP_VERSION, _json_args.toString(), _md5_value.substring(0, 8));
        return _result;
    }
}
