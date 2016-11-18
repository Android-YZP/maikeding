package com.netease.nim.demo.login.maixinlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.login.maixinreg.UserRegPhoneActivity;
import com.netease.nim.demo.main.activity.MainActivity;

public class UserLoginActivity extends AppCompatActivity {

    private EditText mEtAccount;//用户名
    private EditText mEtPassword;//密码

    private Button mBtnLogin;//登录按钮
    private TextView mTvGoRegister;//去注册
    private TextView mTvGoForgot;//去忘记密码
    private Button mBtnVisitByEasy;//随便看看

    private static ProgressDialog mProgressDialog = null;

    //	private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mEtAccount = (EditText) findViewById(R.id.et_user_login_account);
        mEtPassword = (EditText) findViewById(R.id.et_user_login_password);

        mBtnLogin = (Button) findViewById(R.id.btn_user_login_commit);

        mTvGoRegister = (TextView) findViewById(R.id.tv_user_login_reg);
        mTvGoForgot = (TextView) findViewById(R.id.tv_user_login_forget);

        mBtnVisitByEasy = (Button) findViewById(R.id.btn_user_login_visit_by_easy);
    }

    private void initData() {

    }

    private void setListener() {
        //忘记密码
        mTvGoForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this,UserForgotPhoneActivity.class));
            }
        });
        //注册
        mTvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this,UserRegPhoneActivity.class));
            }
        });
        //登录
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this,MainActivity.class));
            }
        });
    }


//	private class UserLoginOnClickListener implements OnClickListener{
//
//		@Override
//		public void onClick(View view) {
//			Intent _intent = null;
//			switch (view.getId()) {
//			case R.id.btn_user_login_visit_by_easy:
//				UserLoginActivity.this.finish();
//				break;
//			case R.id.btn_user_login_commit:
//
//
//				String account = mEtAccount.getText().toString();
//				String password = mEtPassword.getText().toString();
//
//				if(account==null||account.equals("")){
//					Toast.makeText(UserLoginActivity.this, "您未填写用户名", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if(password==null||password.equals("")){
//					Toast.makeText(UserLoginActivity.this, "您未填写密码", Toast.LENGTH_SHORT).show();
//					return;
//				}
//
//				//弹出加载进度条
//				mProgressDialog = ProgressDialog.show(UserLoginActivity.this, "请稍等", "正在玩命登录中...",true,true);
//				//开启副线程-发起登录
//				userLoginFromNet(account,password);
//
//				break;
//			case R.id.tv_user_login_reg:
//				_intent = new Intent(UserLoginActivity.this,UserRegPhoneActivity.class);
//				startActivity(_intent);
//				break;
//			case R.id.tv_user_login_forget:
//				_intent = new Intent(UserLoginActivity.this,UserForgotPhoneActivity.class);
//				startActivity(_intent);
//				break;
//			default:
//				break;
//			}
//		}

//		private void userLoginFromNet(final String account, final String password) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						String result = mUserBusiness.getUserLogin(account,password);
//						Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_user_login_getUserLogin", result);
//						JSONObject jsonObj = new JSONObject(result);
//						boolean Success = jsonObj.getBoolean("success");
//						if(Success){
//							//填充用户信息
//							fullUserInfo(jsonObj);
//							//获取成功
//							handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS);
//						}else{
//							//获取错误代码，并查询出错误文字
//							String errorMsg = jsonObj.getString("errorMsg");
//							CommonUtil.sendErrorMessage(errorMsg,handler);
//						}
//					} catch (ConnectTimeoutException e) {
//						e.printStackTrace();
//						CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT,handler);
//					}catch (SocketTimeoutException e) {
//						e.printStackTrace();
//						CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT,handler);
//					}
//					catch (ServiceException e) {
//						e.printStackTrace();
//						CommonUtil.sendErrorMessage(e.getMessage(),handler);
//					} catch (Exception e) {
//						//what = 0;sendmsg 0;
//						CommonUtil.sendErrorMessage("注册-用户登录："+CommonConstants.MSG_GET_ERROR,handler);
//					}
//				}
//				/**
//				 * 填充用户信息
//				 * @param jsonObj
//				 */
//				private void fullUserInfo(JSONObject jsonObj) {
//					mUser = new User();
//					mUser.setAccount(JsonUtils.getString(jsonObj, "account"));
//					mUser.setPassword(JsonUtils.getString(jsonObj, "password"));
//					mUser.setId(JsonUtils.getInt(jsonObj, "id"));
//					mUser.setUserImg(JsonUtils.getString(jsonObj, "userImg"));
//					mUser.setUsername(JsonUtils.getString(jsonObj, "username"));
//					mUser.setMobile(JsonUtils.getString(jsonObj, "mobile"));
//					mUser.setVerifyCode(JsonUtils.getString(jsonObj, "verifyCode"));
//				}
//			}).start();
//		}
//
////	}
}
