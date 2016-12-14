package com.mcwonders.uikit.demand.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcwonders.uikit.R;
import com.mcwonders.uikit.common.activity.UI;
import com.mcwonders.uikit.demand.bean.Demand;
import com.mcwonders.uikit.demand.bean.DemandPayInfo;
import com.mcwonders.uikit.demand.util.CommonConstants;
import com.mcwonders.uikit.demand.util.CommonUtil;
import com.mcwonders.uikit.demand.util.DBHelper;
import com.mcwonders.uikit.demand.util.IUserBusiness;
import com.mcwonders.uikit.demand.util.JsonUtils;
import com.mcwonders.uikit.demand.util.UserBusinessImp;
import com.mcwonders.uikit.model.ToolBarOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 需求详情页面
 * Created by ZJ on 2016/11/30.
 */
public class DemandDetailActivity extends UI {

    private LinearLayout mLayoutSchedule;
    private LinearLayout mLayoutPay;
    private int demandId, step;
    private String mDemandName, mDemandPrice, mDemandDescribtion;
    private boolean isPerson, isMKJ, isLYBZ, isSDCY;
    private TextView mTvDemandName, mTvDemandPrice, mTvDemandId, mTvDemandSchedule, mTvDemandDescribtion;
    private ImageView mIvPerson, mIvMKJ, mIvLYBZ, mIvSDCY;
    //业务层
    private static ProgressDialog mProgressDialog = null;
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private DbManager dbManager;//数据库管理对象
    private static final int FLAG_GET_DEMAND_DETAIL_SUCCESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demand_detail_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.demand_detail;
        setToolBar(R.id.toolbar, options, R.id.toolbar_demand_detail_title);
        findViews();
        initDada();
    }

    private void findViews() {
        dbManager = DBHelper.getDbManager();
        mLayoutSchedule = findView(R.id.layout_demand_detail_schedule);
        mLayoutPay = findView(R.id.layout_demand_detail_pay);
        mTvDemandName = findView(R.id.tv_demand_detail_name);
        mTvDemandPrice = findView(R.id.tv_demand_detail_price);
        mTvDemandId = findView(R.id.tv_demand_detail_number);
        mTvDemandSchedule = findView(R.id.tv_demand_detail_schedule);
        mIvPerson = findView(R.id.iv_demand_detail_certification);
        mIvMKJ = findView(R.id.iv_demand_detail_maikejia);
        mIvLYBZ = findView(R.id.iv_demand_detail_performance);
        mIvSDCY = findView(R.id.iv_demand_detail_field);
        mTvDemandDescribtion = findView(R.id.tv_demand_detail_describe);
        mLayoutSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(DemandDetailActivity.this, DemandScheduleActivity.class);
                _intent.putExtra("demandId", demandId);
                startActivity(_intent);
            }
        });
        mLayoutPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(DemandDetailActivity.this, DemandPayActivity.class);
                _intent.putExtra("demandId", demandId);
                startActivity(_intent);
            }
        });
    }

    private void initDada() {
        Bundle _bundle = getIntent().getExtras();
        if (_bundle != null) {
            demandId = _bundle.getInt("demandId");
            mTvDemandId.setText(String.valueOf(demandId));
            try {
                Demand _demand = dbManager.selector(Demand.class).where("demand_id", "=", demandId).findFirst();
                if (_demand != null) {
                    mDemandName = _demand.getDemandName();
                    mDemandPrice = _demand.getDemandPrice();
                    step = _demand.getStep();
                    isPerson = _demand.isVerifyPersonOrCompany();
                    isMKJ = _demand.isVerifyMKJ();
                    isLYBZ = _demand.isVerifyLYBZ();
                    isSDCY = _demand.isVerifySDCY();
                    mDemandDescribtion = _demand.getDescription();
                    updateDemandDetail();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        getDemandDetailFromNet(demandId);
    }

    /**
     * 获取需求详情
     */
    private void getDemandDetailFromNet(final int id) {
        //弹出加载进度条
        mProgressDialog = ProgressDialog.show(DemandDetailActivity.this, "请稍等", "正在加载中...", true, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.getDemandDetail(id);
                    Log.d("zzz--------result", result.toString());
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        JSONObject datas = jsonObj.getJSONObject("datas");
                        Demand demand = new Demand();
                        demand.setDemandID(JsonUtils.getInt(datas, "id"));
                        mDemandName = JsonUtils.getString(datas, "title");
                        demand.setDemandName(mDemandName);
                        mDemandPrice = JsonUtils.getString(datas, "priceStr");
                        demand.setDemandPrice(mDemandPrice);
                        step = JsonUtils.getInt(datas, "step");
                        demand.setStep(step);
                        isPerson = JsonUtils.getBoolean(datas, "verifypersonorcompany");
                        demand.setVerifyPersonOrCompany(isPerson);
                        isMKJ = JsonUtils.getBoolean(datas, "verifymkj");
                        demand.setVerifyMKJ(isMKJ);
                        isLYBZ = JsonUtils.getBoolean(datas, "verifylybz");
                        demand.setVerifyLYBZ(isLYBZ);
                        isSDCY = JsonUtils.getBoolean(datas, "verifysdcy");
                        demand.setVerifySDCY(isSDCY);
                        JSONArray needs = JsonUtils.getArray(datas, "needs");
                        mDemandDescribtion = "";
                        for (int i = 0; i < needs.length(); i++) {
                            JSONObject jobj = needs.getJSONObject(i);
                            String str = JsonUtils.getString(jobj, "description");
                            Pattern p = Pattern.compile("<(\\w+)([^<>]*)>([^<>]+)</\\1>");
                            Matcher m = p.matcher(str);
                            while (m.find()) {
                                mDemandDescribtion += m.group(3);
                            }
                        }
                        demand.setDescription(mDemandDescribtion);
                        JSONObject jobjTime = datas.getJSONObject("releasetime");
                        int year = JsonUtils.getInt(jobjTime, "year") + 1900;
                        String time = year + "-" + JsonUtils.getInt(jobjTime, "month") + "-" + JsonUtils.getInt(jobjTime, "day") + "    " + JsonUtils.getInt(jobjTime, "hours") + ":" + JsonUtils.getInt(jobjTime, "minutes");
                        demand.setReleaseTime(time);
                        //存储需求详情到数据库
                        Demand _demand = dbManager.selector(Demand.class).where("demand_id", "=", JsonUtils.getInt(datas, "id")).findFirst();
                        if (_demand != null) {
                            //有就更改他的字段
                            _demand.setDemandID(JsonUtils.getInt(datas, "id"));
                            _demand.setDemandName(JsonUtils.getString(datas, "title"));
                            _demand.setDemandPrice(JsonUtils.getString(datas, "priceStr"));
                            _demand.setStep(JsonUtils.getInt(datas, "step"));
                            _demand.setVerifyPersonOrCompany(JsonUtils.getBoolean(datas, "verifypersonorcompany"));
                            _demand.setVerifyMKJ(JsonUtils.getBoolean(datas, "verifymkj"));
                            _demand.setVerifyLYBZ(JsonUtils.getBoolean(datas, "verifylybz"));
                            _demand.setVerifySDCY(JsonUtils.getBoolean(datas, "verifysdcy"));
                            _demand.setDescription(mDemandDescribtion);
                            _demand.setReleaseTime(time);
                            dbManager.saveOrUpdate(_demand);
                        } else {
                            Demand _demand_tab = new Demand(JsonUtils.getInt(datas, "id"), JsonUtils.getString(datas, "title"), JsonUtils.getString(datas, "priceStr"), JsonUtils.getBoolean(datas, "verifypersonorcompany"), JsonUtils.getBoolean(datas, "verifymkj"), JsonUtils.getBoolean(datas, "verifylybz"), JsonUtils.getBoolean(datas, "verifysdcy"), mDemandDescribtion, JsonUtils.getInt(datas, "step"), time);
                            dbManager.save(_demand_tab);
                        }
                        //存储需求付款信息到数据库
                        JSONArray payInfos = JsonUtils.getArray(datas, "prices");
                        for (int i = 0; i < payInfos.length(); i++) {
                            JSONObject jobj = payInfos.getJSONObject(i);
                            DemandPayInfo _demandPayInfo = dbManager.selector(DemandPayInfo.class).where("demand_id", "=", demandId).and("pay_type", "=", JsonUtils.getInt(jobj, "type")).findFirst();
                            if (_demandPayInfo != null) {
                                //有就更改他的字段
                                _demandPayInfo.setDemandID(JsonUtils.getInt(datas, "id"));
                                _demandPayInfo.setPayName(JsonUtils.getString(jobj, "name"));
                                _demandPayInfo.setPayType(JsonUtils.getInt(jobj, "type"));
                                _demandPayInfo.setDeliver(JsonUtils.getBoolean(jobj, "deliver"));
                                _demandPayInfo.setPayScale(JsonUtils.getInt(jobj, "scale"));
                                _demandPayInfo.setPayMoney(JsonUtils.getInt(jobj, "price"));
                                dbManager.saveOrUpdate(_demandPayInfo);
                            } else {
                                DemandPayInfo _demandPayInfo_tab = new DemandPayInfo(JsonUtils.getInt(datas, "id"), JsonUtils.getString(jobj, "name"), JsonUtils.getInt(jobj, "type"), JsonUtils.getBoolean(jobj, "deliver"), JsonUtils.getInt(jobj, "scale"), JsonUtils.getInt(jobj, "price"));
                                dbManager.save(_demandPayInfo_tab);
                            }
                        }
                        handler.sendEmptyMessage(FLAG_GET_DEMAND_DETAIL_SUCCESS);
                    } else {
                        String errorMsg = jsonObj.getString("errorMsg");
                        CommonUtil.sendErrorMessage(errorMsg, handler);
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT, handler);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(DemandDetailActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((DemandDetailActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case FLAG_GET_DEMAND_DETAIL_SUCCESS:
                    //获取成功
                    ((DemandDetailActivity) mActivity.get()).updateDemandDetail();
                    break;
                default:
                    break;
            }
        }

    }

    private void updateDemandDetail() {
        mTvDemandName.setText(mDemandName);
        mTvDemandPrice.setText(mDemandPrice);
        switch (step) {
            case 1:
                mTvDemandSchedule.setText("需求发布中");
                break;
            case 2:
                mTvDemandSchedule.setText("需求报名中");
                break;
            case 3:
                mTvDemandSchedule.setText("确认中选中");
                break;
            case 4:
                mTvDemandSchedule.setText("确认中选中");
                break;
            case 5:
                mTvDemandSchedule.setText("需求结束");
                break;
        }
        if (isPerson == true) {
            mIvPerson.setImageResource(R.drawable.certification_nor);
        } else {
            mIvPerson.setImageResource(R.drawable.certification_grey);
        }
        if (isMKJ == true) {
            mIvMKJ.setImageResource(R.drawable.maikejia_attestation_nor);
        } else {
            mIvMKJ.setImageResource(R.drawable.maikejia_attestation_grey);
        }
        if (isLYBZ == true) {
            mIvLYBZ.setImageResource(R.drawable.performance_bond_nor);
        } else {
            mIvLYBZ.setImageResource(R.drawable.performance_bond_grey);
        }
        if (isSDCY == true) {
            mIvSDCY.setImageResource(R.drawable.field_testing_nor);
        } else {
            mIvSDCY.setImageResource(R.drawable.field_testing_grey);
        }
        mTvDemandDescribtion.setText(mDemandDescribtion);
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
