package com.netease.nim.demo.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.netease.nim.demo.login.maixinlogin.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by SunnyJiang on 2016/9/1.
 */
public class CommonUtil {

    /**
     * 保存用户信息
     *
     * @param user
     * @param mContext
     */
    public static void saveUserInfo(User user, Context mContext) {
        //构建对象
        Gson gson = new Gson();
        String gsonUser = gson.toJson(user);
        //获取指定Key的SharedPreferences对象
        SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
        //获取编辑
        SharedPreferences.Editor _Editor = _SP.edit();
        //按照指定Key放入数据
        _Editor.putString("user", gsonUser);
        //提交保存数据
        _Editor.commit();
    }

//    /**
//     * 保存未登录时的用户信息，包括是否首次安装、上次安装版本数量、生成短信验证码的tokenID令牌
//     *
//     * @param unLoginedUser
//     * @param mContext
//     */
//    public static void saveUnLoginedUser(UnLoginedUser unLoginedUser, Context mContext) {
//        //构建对象
//        Gson gson = new Gson();
//        String gsonSearchHistory = gson.toJson(unLoginedUser);
//        //获取指定Key的SharedPreferences对象
//        SharedPreferences _SP = mContext.getSharedPreferences("UnLoginedUser", mContext.MODE_PRIVATE);
//        //获取编辑
//        SharedPreferences.Editor _Editor = _SP.edit();
//        //按照指定Key放入数据
//        _Editor.putString("unLoginedUser", gsonSearchHistory);
//        //提交保存数据
//        _Editor.commit();
//    }


//    /**
//     * 获取未登录用户的信息，包括是否首次安装、上次安装版本数量、生成短信验证码的tokenID令牌
//     *
//     * @param mContext
//     * @return
//     */
//    public static UnLoginedUser getUnLoginedUser(Context mContext) {
//        SharedPreferences _SP = mContext.getSharedPreferences("UnLoginedUser", mContext.MODE_PRIVATE);
//        if (_SP == null) {
//            return null;
//        } else {
//            String jsonSearchHistory = _SP.getString("unLoginedUser", "");
//            Gson gson = new Gson();
//            UnLoginedUser unLoginedUser = gson.fromJson(jsonSearchHistory, UnLoginedUser.class);
//            return unLoginedUser;
//        }
//    }

    /**
     * 获取登录用户的信息
     *
     * @param mContext
     * @return
     */
    public static User getUserInfo(Context mContext) {
        SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
        if (_SP == null) {
            return null;
        } else {
            String jsonUser = _SP.getString("user", "");
            Gson gson = new Gson();
            User user = gson.fromJson(jsonUser, User.class);
            return user;
        }
    }

    /**
     * 清除用户信息
     *
     * @param mContext
     */
    public static void clearUserInfo(Context mContext) {
        SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
        if (_SP != null) {
            SharedPreferences.Editor _Editor = _SP.edit();
            _Editor.clear();
            _Editor.commit();
        }
    }

    /**
     * 是否需要隐藏输入法
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
//			if (event.getX() > left && event.getX() < right
//					&& event.getY() > top && event.getY() < bottom) {
            if (event.getX() > left
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 手动隐藏输入法
     */
    public static void hideInput(Context context, EditText mEtCommonText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mEtCommonText.getWindowToken(), 0);
        }
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isnetWorkAvilable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.e("FlyleafActivity", "couldn't get connectivity manager");
        } else {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null) {
                for (int i = 0, count = networkInfos.length; i < count; i++) {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取UUID
     *
     * @param context
     * @return
     */
    public static String getMyUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }

    /**
     * 设置listview的高度，以适应布局
     *
     * @param listView
     */
    public static void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 发送错误信息到消息队列
     *
     * @param errorMsg
     */
    public static void sendErrorMessage(String errorMsg, Handler handler) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("ErrorMsg", errorMsg);
        msg.setData(data);
        handler.sendMessage(msg);
    }

//    /**
//     * 获取app版本
//     *
//     * @param context
//     * @return
//     */
//    public static AppVersion getAppVersion(Context context) {
//        AppVersion version = null;
//
//        PackageManager pm = context.getPackageManager();
//        PackageInfo info = null;
//        if (pm != null) {
//            try {
//                info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (info != null) {
//            version = new AppVersion();
//            version.setVersionCode(info.versionCode);
//            version.setVersionName(info.versionName);
//        }
//        return version;
//    }

//    /**
//     * 创建luncher图标logo
//     */
//    public static void createShortCut(Context context) {
//        //创建快捷方式的Intent
//        Intent addShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        //不允许重复创建
//        addShortcut.putExtra("duplicate", false);
//        //<span><span class="comment">指定当前的Activity为快捷方式启动的对象: 如 com.android.music.</span>MusicBrowserActivity<span> </span></span>
//        //<span><span class="comment">注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序</span></span>
//        ComponentName comp = new ComponentName(context.getPackageName(), "."
//                + ((Activity) context).getLocalClassName());
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
//        //设置快捷方式的图标
//        Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(context,
//                R.drawable.ic_launcher);
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
//        // 设置快捷方式的名字
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
//        context.sendBroadcast(addShortcut);
//    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 计算出二个日期之间日期集合
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> dateSplit(Date startDate, Date endDate) {
        Long spi = endDate.getTime() - startDate.getTime();
        Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数

        List<Date> dateList = new ArrayList<Date>();
        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime()
                    - (24 * 60 * 60 * 1000)));// 比上一天减一
        }
        return dateList;
    }
//
//    /**
//     * 给定一个日期判断是周几
//     *
//     * @throws Exception
//     */
//    public static int dayForWeek(String pTime) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
//        Calendar c = Calendar.getInstance();
//        try {
//            c.setTime(format.parse(pTime));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        int dayForWeek = 0;
//        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
//            dayForWeek = 7;
//        } else {
//            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
//        }
//        return dayForWeek;
//    }

    /**
     * 计算出总时间
     * 输入时间String和次数
     *
     * @return
     */
    public static String TatloTimes(String Time, int Times) {
        SimpleDateFormat _SDF = new SimpleDateFormat("HH小时mm分钟");
        long hours = 0;
        long mins = 0;
        for (int i = 0; i < Times; i++) {
            try {
                Date _tiem = _SDF.parse(Time);
                long temp = _tiem.getSeconds();
                long hour = temp / 1000 / 3600;                //相加小时数
                long temp2 = temp % (1000 * 3600);
                long min = temp2 / 1000 / 60;                    //相加分钟数
                hours = hours + hour;
                mins = mins + min;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return hours + "小时" + mins + "分钟";

    }

    /**
     * 得到当前时间
     *
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return sDateFormat.format(new Date());
    }

    /**
     * 得到标签名称
     *
     * @param label 标签号
     * @return
     */
    public static String getLabelName(int label) {
        switch (label) {
            case 0:
                return "个人";
            case 1:
                return "工作";
            case 2:
                return "娱乐";
            case 3:
                return "重要";
            case 4:
                return "健康";
            case 5:
                return "其他";
        }
        return "";
    }


    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 创建文件夹
     */
//    public static void makeDri() {
//        File dirFirstFolder = new File(CommonConstants.YOU_PAN_PIC_PATH);// 方法一：直接使用字符串，如果是安装在存储卡上面，则需要使用sdcard2，但是需要确认是否有存储卡
//        if (!dirFirstFolder.exists()) { //如果该文件夹不存在，则进行创建
//            dirFirstFolder.mkdirs();//创建文件夹
//        }
//    }
//    /**
//     * 创建文件夹
//     */
//    public static void makeCollectDri() {
//        File dirFirstFolder = new File(CommonConstants.COLLECT_PATH);// 方法一：直接使用字符串，如果是安装在存储卡上面，则需要使用sdcard2，但是需要确认是否有存储卡
//        if (!dirFirstFolder.exists()) { //如果该文件夹不存在，则进行创建
//            dirFirstFolder.mkdirs();//创建文件夹
//        }
//    }

}
