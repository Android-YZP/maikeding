package com.mcwonders.mkd.contact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mcwonders.mkd.R;
import com.mcwonders.mkd.business.IUserBusiness;
import com.mcwonders.mkd.business.imp.UserBusinessImp;
import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.contact.constant.UserConstant;
import com.mcwonders.mkd.contact.helper.UserUpdateHelper;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.mkd.login.maixinlogin.MKJUserInfo;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.main.model.Extras;
import com.mcwonders.mkd.utils.CommonUtil;
import com.mcwonders.mkd.utils.ConvertUtil;
import com.mcwonders.mkd.utils.JsonUtils;
import com.mcwonders.mkd.utils.NetWorkUtil;
import com.mcwonders.uikit.cache.NimUserInfoCache;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.common.media.picker.PickImageHelper;
import com.mcwonders.uikit.common.ui.dialog.DialogMaker;
import com.mcwonders.uikit.common.ui.imageview.HeadImageView;
import com.mcwonders.uikit.common.util.log.LogUtil;
import com.mcwonders.uikit.model.ToolBarOptions;
import com.mcwonders.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileSettingActivity extends UI implements View.OnClickListener {
    private final String TAG = UserProfileSettingActivity.class.getSimpleName();

    // constant
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;

    private String account;

    // view
    private HeadImageView userHead;
    private RelativeLayout nickLayout;
    private RelativeLayout genderLayout;
    private RelativeLayout birthLayout;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout signatureLayout;

    private TextView nickText;
    private TextView genderText;
    private TextView birthText;
    private TextView phoneText;
    private TextView emailText;
    private TextView signatureText;
    private String mpicName = "touxiang.jpg";
    // data
    AbortableFuture<String> uploadAvatarFuture;
    private NimUserInfo userInfo;
    private User mUser;

    public static void start(Context context, String account) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileSettingActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mcwonders.mkd.R.layout.user_profile_set_activity);
        mUser = CommonUtil.getUserInfo(this);

        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options, R.id.toolbar_profile_set_title);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void findViews() {
        userHead = findView(com.mcwonders.mkd.R.id.user_head);
        nickLayout = findView(com.mcwonders.mkd.R.id.nick_layout);
        genderLayout = findView(com.mcwonders.mkd.R.id.gender_layout);
        birthLayout = findView(com.mcwonders.mkd.R.id.birth_layout);
        phoneLayout = findView(com.mcwonders.mkd.R.id.phone_layout);
        emailLayout = findView(com.mcwonders.mkd.R.id.email_layout);
        signatureLayout = findView(com.mcwonders.mkd.R.id.signature_layout);

        ((TextView) nickLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.nickname);
        ((TextView) genderLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.gender);
        ((TextView) birthLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.birthday);
        ((TextView) phoneLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.phone);
        ((TextView) emailLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.email);
        ((TextView) signatureLayout.findViewById(com.mcwonders.mkd.R.id.attribute)).setText(com.mcwonders.mkd.R.string.signature);

        nickText = (TextView) nickLayout.findViewById(com.mcwonders.mkd.R.id.value);
        genderText = (TextView) genderLayout.findViewById(com.mcwonders.mkd.R.id.value);
        birthText = (TextView) birthLayout.findViewById(com.mcwonders.mkd.R.id.value);
        phoneText = (TextView) phoneLayout.findViewById(com.mcwonders.mkd.R.id.value);
        emailText = (TextView) emailLayout.findViewById(com.mcwonders.mkd.R.id.value);
        signatureText = (TextView) signatureLayout.findViewById(com.mcwonders.mkd.R.id.value);

        findViewById(com.mcwonders.mkd.R.id.head_layout).setOnClickListener(this);
        nickLayout.setOnClickListener(this);
        genderLayout.setOnClickListener(this);
        birthLayout.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
        emailLayout.setOnClickListener(this);
        signatureLayout.setOnClickListener(this);
    }

    private void getUserInfo() {
        userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo param) {
                    userInfo = param;
                    updateUI();
                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(UserProfileSettingActivity.this, "getUserInfoFromRemote failed:" + code, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    Toast.makeText(UserProfileSettingActivity.this, "getUserInfoFromRemote exception:" + exception, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        userHead.loadBuddyAvatar(account);
        if (!TextUtils.isEmpty(userInfo.getName())) {
            nickText.setText(userInfo.getName());
        } else {
            nickText.setText("未设置");
        }

        if (userInfo.getGenderEnum() != null) {
            if (userInfo.getGenderEnum() == GenderEnum.MALE) {
                genderText.setText("男");
            } else if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
                genderText.setText("女");
            } else {
                genderText.setText("其他");
            }
        }
        if (!TextUtils.isEmpty(userInfo.getBirthday())) {
            birthText.setText(userInfo.getBirthday());
        } else {
            birthText.setText("未设置");
        }

        if (!TextUtils.isEmpty(userInfo.getMobile())) {
            phoneText.setText(userInfo.getMobile());
        } else {
            phoneText.setText("未设置");
        }
        if (!TextUtils.isEmpty(userInfo.getEmail())) {
            emailText.setText(userInfo.getEmail());
        } else {
            emailText.setText("未设置");
        }
        if (!TextUtils.isEmpty(userInfo.getSignature())) {
            signatureText.setText(userInfo.getSignature());
        } else {
            signatureText.setText("未设置");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.mcwonders.mkd.R.id.head_layout:
                PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
                option.titleResId = com.mcwonders.mkd.R.string.set_head_image;
                option.crop = true;
                option.multiSelect = false;
                option.cropOutputImageWidth = 720;
                option.cropOutputImageHeight = 720;
                PickImageHelper.pickImage(UserProfileSettingActivity.this, PICK_AVATAR_REQUEST, option);
                break;
            case com.mcwonders.mkd.R.id.nick_layout:
                if (nickText.getText().toString().equals("未设置")) nickText.setText("");
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_NICKNAME,
                        nickText.getText().toString());
                break;
            case com.mcwonders.mkd.R.id.gender_layout:
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_GENDER,
                        String.valueOf(userInfo.getGenderEnum().getValue()));
                break;
            case com.mcwonders.mkd.R.id.birth_layout:
                if (birthText.getText().toString().equals("未设置")) birthText.setText("");
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_BIRTH,
                        birthText.getText().toString());
                break;
            case com.mcwonders.mkd.R.id.phone_layout:
                if (phoneText.getText().toString().equals("未设置")) phoneText.setText("");
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_PHONE,
                        phoneText.getText().toString());
                break;
            case com.mcwonders.mkd.R.id.email_layout:
                if (emailText.getText().toString().equals("未设置")) emailText.setText("");
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_EMAIL,
                        emailText.getText().toString());
                break;
            case com.mcwonders.mkd.R.id.signature_layout:
                if (signatureText.getText().toString().equals("未设置")) signatureText.setText("");
                UserProfileEditItemActivity.startActivity(UserProfileSettingActivity.this, UserConstant.KEY_SIGNATURE,
                        signatureText.getText().toString());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            String path = data.getStringExtra(com.mcwonders.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            updateAvatar(path);
        }
    }

    /**
     * 更新头像
     */
    private void updateAvatar(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File file = new File(path);
        if (file == null) {
            return;
        }

        DialogMaker.showProgressDialog(this, null, null, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(com.mcwonders.mkd.R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(true);

        LogUtil.i(TAG, "start upload avatar, local file path=" + file.getAbsolutePath());
        new Handler().postDelayed(outimeTask, AVATAR_TIME_OUT);
        //上传服务器到麦客加服务器
        /**********************************************在这里会得到一个file头像文件，同步迈克家服务器，成功之后上传云信服务器*************************************************************/
        sendPicToServer(file);
        Log.d("YZP=======>", file.getName() + "");

        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload avatar success, url =" + url);

                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(UserProfileSettingActivity.this, com.mcwonders.mkd.R.string.head_update_success, Toast.LENGTH_SHORT).show();
                                onUpdateDone();
                            } else {
                                Toast.makeText(UserProfileSettingActivity.this, com.mcwonders.mkd.R.string.head_update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(UserProfileSettingActivity.this, com.mcwonders.mkd.R.string.user_info_update_failed, Toast
                            .LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    /**
     * 上传图片到服务器
     */
    private void sendPicToServer(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("==========>", mUser.getVerifyCode());
                    String _md5_value = ConvertUtil.getMD5("MAIKEJIA");
                    String _withPhoto = NetWorkUtil.getResultFromUrlConnectionWithPhoto(CommonConstants.UPLOAD_IMAGE,
                            null, file.getName(), _md5_value.substring(0, 8), file, mUser.getMobile());
                    //解析出上传图片的地址
                    Log.d("==========>", _withPhoto + "");
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("上传图片失败，数据异常", handler);
                }
            }
        }).start();
    }

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(UserProfileSettingActivity.this, resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(com.mcwonders.mkd.R.string.user_info_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
        getUserInfo();
    }


    //处理消息队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    try {
                        if (errorMsg != null && errorMsg.equals("执行成功!")) {
                            errorMsg = "上传成功";
                        }
                        Toast.makeText(UserProfileSettingActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_SET_TARGET_SUCCESS:
                    //   setTargetCompleted();//操作完成
                    Toast.makeText(UserProfileSettingActivity.this, "同步完成", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
