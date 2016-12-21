package com.mcwonders.mkd.contact.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcwonders.mkd.business.IUserBusiness;
import com.mcwonders.mkd.business.imp.UserBusinessImp;
import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.contact.helper.UserUpdateHelper;
import com.mcwonders.mkd.exception.ServiceException;
import com.mcwonders.mkd.login.maixinlogin.User;
import com.mcwonders.mkd.login.maixinlogin.UserLoginActivity;
import com.mcwonders.mkd.utils.CommonUtil;
import com.mcwonders.uikit.common.util.sys.NetworkUtil;
import com.mcwonders.uikit.model.ToolBarOptions;
import com.mcwonders.mkd.contact.constant.UserConstant;
import com.mcwonders.uikit.cache.FriendDataCache;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.common.ui.dialog.DialogMaker;
import com.mcwonders.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.mcwonders.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/14.
 */
public class UserProfileEditItemActivity extends UI implements View.OnClickListener {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    // data
    private int key;
    private String data;
    private int birthYear = 1990;
    private int birthMonth = 10;
    private int birthDay = 10;
    private Map<Integer, UserInfoFieldEnum> fieldMap;

    // VIEW
    private ClearableEditTextWithIcon editText;

    // gender layout
    private RelativeLayout maleLayout;
    private RelativeLayout femaleLayout;
    private RelativeLayout otherLayout;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private ImageView otherCheck;

    // birth layout
    private RelativeLayout birthPickerLayout;
    private TextView birthText;
    private int gender;
    private User mUserInfo;


    public static final void startActivity(Context context, int key, String data) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileEditItemActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        mUserInfo = CommonUtil.getUserInfo(UserProfileEditItemActivity.this);
        if (key == UserConstant.KEY_NICKNAME || key == UserConstant.KEY_PHONE || key == UserConstant.KEY_EMAIL
                || key == UserConstant.KEY_SIGNATURE || key == UserConstant.KEY_ALIAS) {
            setContentView(com.mcwonders.mkd.R.layout.user_profile_edittext_layout);
            findEditText();
        } else if (key == UserConstant.KEY_GENDER) {
            setContentView(com.mcwonders.mkd.R.layout.user_profile_gender_layout);
            findGenderViews();
        } else if (key == UserConstant.KEY_BIRTH) {
            setContentView(com.mcwonders.mkd.R.layout.user_profile_birth_layout);
            findBirthViews();
        }
        ToolBarOptions options = new ToolBarOptions();
        setToolBar(com.mcwonders.mkd.R.id.toolbar, options, com.mcwonders.mkd.R.id.toolbar_user_profile_edittext_title);
        initActionbar();
        setTitles();
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }

    private void parseIntent() {
        key = getIntent().getIntExtra(EXTRA_KEY, -1);
        data = getIntent().getStringExtra(EXTRA_DATA);
    }

    /**
     * key值代表着是设置哪个类型
     */
    private void setTitles() {
        switch (key) {
            case UserConstant.KEY_NICKNAME:
                setTitle(com.mcwonders.mkd.R.string.nickname);
                break;
            case UserConstant.KEY_PHONE:
                setTitle(com.mcwonders.mkd.R.string.phone_number);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UserConstant.KEY_EMAIL:
                setTitle(com.mcwonders.mkd.R.string.email);
                break;
            case UserConstant.KEY_SIGNATURE:
                setTitle(com.mcwonders.mkd.R.string.signature);
                break;
            case UserConstant.KEY_GENDER:
                setTitle(com.mcwonders.mkd.R.string.gender);
                break;
            case UserConstant.KEY_BIRTH:
                setTitle(com.mcwonders.mkd.R.string.birthday);
                break;
            case UserConstant.KEY_ALIAS:
                setTitle(com.mcwonders.mkd.R.string.alias);
                break;
        }
    }

    private void findEditText() {
        editText = findView(com.mcwonders.mkd.R.id.edittext);
        if (key == UserConstant.KEY_NICKNAME) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else if (key == UserConstant.KEY_PHONE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        } else if (key == UserConstant.KEY_EMAIL || key == UserConstant.KEY_SIGNATURE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        } else if (key == UserConstant.KEY_ALIAS) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }
        if (key == UserConstant.KEY_ALIAS) {
            Friend friend = FriendDataCache.getInstance().getFriendByAccount(data);
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                editText.setText(friend.getAlias());
            } else {
                editText.setHint("请输入备注名...");
            }
        } else {
            Log.d("YZP=========>", data);
            editText.setText(data);
        }
        editText.setDeleteImage(com.mcwonders.mkd.R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {
        TextView toolbarView = findView(com.mcwonders.mkd.R.id.action_bar_right_clickable_textview);
        toolbarView.setText(com.mcwonders.mkd.R.string.save);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetAvailable(UserProfileEditItemActivity.this)) {
                    Toast.makeText(UserProfileEditItemActivity.this, com.mcwonders.mkd.R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_NICKNAME && TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(UserProfileEditItemActivity.this, com.mcwonders.mkd.R.string.nickname_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_BIRTH) {
                    /********************************这里会得到一个生日数据数据.同步到本地服务器成功之后再同步云信服务器************/
                    Log.d("YZP=========>KEY_BIRTH", birthText.getText().toString());

                    setMKJPerData(mUserInfo.getMobile(), birthText.getText().toString(), "3");
                    update(birthText.getText().toString());


                } else if (key == UserConstant.KEY_GENDER) {
                    /********************************这里会得到一个性别数据数据.同步到本地服务器成功之后再同步云信服务器************/
                    Log.d("YZP=========>KEY_GENDER", Integer.valueOf(gender) + "");
                    String sax;
                    if (Integer.valueOf(gender) == 1) {
                        sax = "男";
                    } else {
                        sax = "女";
                    }
                    setMKJPerData(mUserInfo.getMobile(), sax, "2");
                    update(Integer.valueOf(gender));
                } else if (key == UserConstant.KEY_NICKNAME) {
                    /********************************这里会得到一个昵称数据.同步到本地服务器成功之后再同步云信服务器************/

                    Log.d("YZP=========>NICKNAME", editText.getText().toString().trim());
                    setMKJPerData(mUserInfo.getMobile(), editText.getText().toString(), "1");
                    update(editText.getText().toString().trim());
                } else if (key == UserConstant.KEY_EMAIL) {
                    /********************************这里会得到一个EMAIL数据.同步到本地服务器成功之后再同步云信服务器************/

                    Log.d("YZP=========>KEY_EMAIL", editText.getText().toString().trim());
                    setMKJPerData(mUserInfo.getMobile(), editText.getText().toString(), "5");
                    update(editText.getText().toString().trim());
                } else if (key == UserConstant.KEY_PHONE) {
                    /********************************这里会得到一个KEY_PHONE数据.同步到本地服务器成功之后再同步云信服务器************/

                    Log.d("YZP=========>KEY_PHONE", editText.getText().toString().trim());
                    setMKJPerData(mUserInfo.getMobile(), editText.getText().toString(), "4");
                    update(editText.getText().toString().trim());
                } else if (key == UserConstant.KEY_SIGNATURE) {
                    /********************************这里会得到一个KEY_SIGNATURE数据.同步到本地服务器成功之后再同步云信服务器************/

                    Log.d("YZP=========>SIGNATURE", editText.getText().toString().trim());
                    setMKJPerData(mUserInfo.getMobile(), editText.getText().toString(), "6");
                    update(editText.getText().toString().trim());
                }
            }
        });
    }

    private void findGenderViews() {
        maleLayout = findView(com.mcwonders.mkd.R.id.male_layout);
        femaleLayout = findView(com.mcwonders.mkd.R.id.female_layout);
        otherLayout = findView(com.mcwonders.mkd.R.id.other_layout);

        maleCheck = findView(com.mcwonders.mkd.R.id.male_check);
        femaleCheck = findView(com.mcwonders.mkd.R.id.female_check);
        otherCheck = findView(com.mcwonders.mkd.R.id.other_check);

        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

        initGender();
    }

    private void initGender() {
        gender = Integer.parseInt(data);
        genderCheck(gender);
    }

    private void findBirthViews() {
        birthPickerLayout = findView(com.mcwonders.mkd.R.id.birth_picker_layout);
        birthText = findView(com.mcwonders.mkd.R.id.birth_value);

        birthPickerLayout.setOnClickListener(this);
        birthText.setText(data);

        if (!TextUtils.isEmpty(data)) {
            Date date = TimeUtil.getDateFromFormatString(data);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (date != null) {
                birthYear = cal.get(Calendar.YEAR);
                birthMonth = cal.get(Calendar.MONTH);
                birthDay = cal.get(Calendar.DATE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.mcwonders.mkd.R.id.male_layout:
                gender = GenderEnum.MALE.getValue();
                genderCheck(gender);
                break;
            case com.mcwonders.mkd.R.id.female_layout:
                gender = GenderEnum.FEMALE.getValue();
                genderCheck(gender);
                break;
            case com.mcwonders.mkd.R.id.other_layout:
                gender = GenderEnum.UNKNOWN.getValue();
                genderCheck(gender);
                break;
            case com.mcwonders.mkd.R.id.birth_picker_layout:
                openTimePicker();
                break;
        }
    }


    /**
     * 从麦客加接口得到个人信息
     */
    private void setMKJPerData(final String account, final String value, final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.setUserInfo(account, value, type);

                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        Log.d("YZP=========>", "修改成功");
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT, handler);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT, handler);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    //what = 0;sendmsg 0;
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_GET_ERROR, handler);
                }
            }
        }).start();

    }


    private MyHandler handler = new MyHandler(UserProfileEditItemActivity.this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;


        public MyHandler(UserProfileEditItemActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((UserProfileEditItemActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS:

                    break;
                default:
                    break;
            }
        }
    }

    private void genderCheck(int selected) {
        otherCheck.setBackgroundResource(selected == GenderEnum.UNKNOWN.getValue() ? com.mcwonders.mkd.R.drawable.nim_contact_checkbox_checked_red : com.mcwonders.mkd.R.drawable.nim_checkbox_not_checked);
        maleCheck.setBackgroundResource(selected == GenderEnum.MALE.getValue() ? com.mcwonders.mkd.R.drawable.nim_contact_checkbox_checked_red : com.mcwonders.mkd.R.drawable.nim_checkbox_not_checked);
        femaleCheck.setBackgroundResource(selected == GenderEnum.FEMALE.getValue() ? com.mcwonders.mkd.R.drawable.nim_contact_checkbox_checked_red : com.mcwonders.mkd.R.drawable.nim_checkbox_not_checked);
    }

    private void openTimePicker() {
        MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthYear = year;
                birthMonth = monthOfYear;
                birthDay = dayOfMonth;
                updateDate();

            }
        }, birthYear, birthMonth, birthDay);
        datePickerDialog.show();
    }

    private void updateDate() {
        birthText.setText(TimeUtil.getFormatDatetime(birthYear, birthMonth, birthDay));
    }

    private void update(Serializable content) {
        RequestCallbackWrapper callback = new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                DialogMaker.dismissProgressDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                    onUpdateCompleted();
                } else if (code == ResponseCode.RES_ETIMEOUT) {
                    Toast.makeText(UserProfileEditItemActivity.this, com.mcwonders.mkd.R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (key == UserConstant.KEY_ALIAS) {
            DialogMaker.showProgressDialog(this, null, true);
            Map<FriendFieldEnum, Object> map = new HashMap<>();
            map.put(FriendFieldEnum.ALIAS, content);
            NIMClient.getService(FriendService.class).updateFriendFields(data, map).setCallback(callback);
        } else {
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                fieldMap.put(UserConstant.KEY_NICKNAME, UserInfoFieldEnum.Name);
                fieldMap.put(UserConstant.KEY_PHONE, UserInfoFieldEnum.MOBILE);
                fieldMap.put(UserConstant.KEY_SIGNATURE, UserInfoFieldEnum.SIGNATURE);
                fieldMap.put(UserConstant.KEY_EMAIL, UserInfoFieldEnum.EMAIL);
                fieldMap.put(UserConstant.KEY_BIRTH, UserInfoFieldEnum.BIRTHDAY);
                fieldMap.put(UserConstant.KEY_GENDER, UserInfoFieldEnum.GENDER);
            }
            DialogMaker.showProgressDialog(this, null, true);
            UserUpdateHelper.update(fieldMap.get(key), content, callback);
        }
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        Toast.makeText(UserProfileEditItemActivity.this, com.mcwonders.mkd.R.string.user_info_update_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class MyDatePickerDialog extends DatePickerDialog {
        private int maxYear = 2015;
        private int minYear = 1900;
        private int currYear;
        private int currMonthOfYear;
        private int currDayOfMonth;

        public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            currYear = year;
            currMonthOfYear = monthOfYear;
            currDayOfMonth = dayOfMonth;
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            if (year >= minYear && year <= maxYear) {
                currYear = year;
                currMonthOfYear = month;
                currDayOfMonth = day;
            } else {
                if (currYear > maxYear) {
                    currYear = maxYear;
                } else if (currYear < minYear) {
                    currYear = minYear;
                }
                updateDate(currYear, currMonthOfYear, currDayOfMonth);
            }
        }

        public void setMaxYear(int year) {
            maxYear = year;
        }

        public void setMinYear(int year) {
            minYear = year;
        }

        public void setTitle(CharSequence title) {
            super.setTitle("生 日");
        }
    }
}
