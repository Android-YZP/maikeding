package com.mcwonders.mkd.main.checkupdate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.mcwonders.mkd.config.CommonConstants;
import com.mcwonders.mkd.exception.ServiceException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by qtfreet on 2016/1/5.
 */
public class CheckUpdate {

    //单例化检查更新类
    private CheckUpdate() {
    }

    private Context mcontext;
    private static CheckUpdate checkUpdate = null;

    public static CheckUpdate getInstance() {
        if (checkUpdate == null) {
            checkUpdate = new CheckUpdate();
        }
        return checkUpdate;
    }


    public void startCheck(Context context) {
        mcontext = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("=======>result", "12344");
//                URL url;
//                InputStream is = null;
//                HttpURLConnection conn = null;
//                try {
//                    //获取服务器端内容，检查是否存在更新，自己开发的话可以采用volley来检查更新
//                    url = new URL(CommonConstants.CHECK_UPDATE);
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(5000);
//                    conn.setRequestMethod("GET");
//                    is = conn.getInputStream();
//                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line);
//                    }
////                    LogUtils.e(sb);
//                    br.close();
//                    is.close();
//                    Message message = new Message();
//                    message.what = 0;
//                    message.obj = sb.toString();
//                    mhanler.sendMessage(message);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (conn != null) {
//                        conn.disconnect();
//                    }
//                }


                String result = "";
                InputStream in = null;
                URL url = null;
                HttpURLConnection urlConnection = null;
                OutputStream out = null;
                byte[] data = null;


                try {
                    JSONObject _json_args = new JSONObject();
                    _json_args.put("name", "麦可信");
                    String jsonargs = _json_args.toString();

                    data = jsonargs.getBytes();
                    url = new URL(CommonConstants.CHECK_UPDATE);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    //设置可以读取数据
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    urlConnection.setRequestProperty("verifyCode", "6fb89b57");
                    urlConnection.connect();
                    out = urlConnection.getOutputStream();
                    out.write(data);
                    out.flush();

                    int statusCode = urlConnection.getResponseCode();
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_url_status", url + ",_status = " + statusCode);
                    if (statusCode != HttpURLConnection.HTTP_OK) {
                        throw new ServiceException("服务器错误");
                    }
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    result = getStrFromInputSteam(in);
                    Log.d("=======>result", result + "12344");
                    if (!TextUtils.isEmpty(result)){
                        Message message = new Message();
                        message.what = 0;
                        message.obj = result;
                        mhanler.sendMessage(message);
                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                    try {
                        throw new ServiceException("连接出错，请检查您的网络");
                    } catch (ServiceException e1) {
                        e1.printStackTrace();
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    try {
                        throw new ServiceException("连接超时，请检查您的网络...");
                    } catch (ServiceException e1) {
                        e1.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    try {
                        throw new ServiceException("服务器响应超时...");
                    } catch (ServiceException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("服务器错误")) {
                        try {
                            throw new ServiceException(e.getMessage());
                        } catch (ServiceException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            throw new ServiceException("查询出错");
                        } catch (ServiceException e1) {
                            e1.printStackTrace();
                        }
                    }

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (urlConnection != null) {
                        urlConnection.disconnect();

                    }
                }

            }
        }).start();
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


    private Handler mhanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    JSONObject js = null;
                    try {
                        js = new JSONObject(msg.obj.toString());
                        String version = js.getString("version");
                        String intro = js.getString("introduction");
                        String apkurl = js.getString("url");
                        compareVersion(Integer.parseInt(version), intro, apkurl); //与本地版本进行比较
//                        LogUtils.e(apkurl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void compareVersion(int newVersion, String intro, final String url) {
        int versionCode = getVerCode(mcontext);
//        LogUtils.e(versionCode);
//        LogUtils.e(intro);
//        LogUtils.e(url);
        if (newVersion > versionCode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            builder.setTitle("发现更新");
            builder.setMessage(intro);
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mcontext, DownloadService.class);
                    intent.putExtra("url", url);
                    mcontext.startService(intent);
                }
            });
            builder.setNegativeButton("退出", null);
            builder.show();
        } else {
            return;
        }
    }


    private int getVerCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }

}
