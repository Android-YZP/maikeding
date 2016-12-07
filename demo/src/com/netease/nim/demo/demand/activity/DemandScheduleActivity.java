package com.netease.nim.demo.demand.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.demand.bean.Demand;
import com.netease.nim.demo.demand.util.DBHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

/**
 * 需求进度页面
 * Created by ZJ on 2016/11/30.
 */
public class DemandScheduleActivity extends UI {

    private TextView mTvDemandId, mTvReleaseTime;
    private ImageView mIvRelease, mIvEnroll, mIvConfirm, mIvEnd;
    private int demandId;
    private DbManager dbManager;//数据库管理对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demand_schedule_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.demand_schedule;
        setToolBar(R.id.toolbar, options, R.id.toolbar_demand_schedule_title);
        findViews();
        initDada();
    }

    private void findViews() {
        mTvDemandId = findView(R.id.tv_demand_schedule_number);
        mTvReleaseTime = findView(R.id.tv_demand_schedule_release_time);
        mIvRelease = findView(R.id.iv_demand_schedule_release);
        mIvEnroll = findView(R.id.iv_demand_schedule_enroll);
        mIvConfirm = findView(R.id.iv_demand_schedule_confirm);
        mIvEnd = findView(R.id.iv_demand_schedule_end);
    }

    private void initDada() {
        dbManager = DBHelper.getDbManager();
        Bundle _bundle = getIntent().getExtras();
        if (_bundle != null) {
            demandId = _bundle.getInt("demandId");
            mTvDemandId.setText(String.valueOf(demandId));
            try {
                Demand _demand = dbManager.selector(Demand.class).where("demand_id", "=", demandId).findFirst();
                if (_demand != null) {
                    mTvReleaseTime.setText(_demand.getReleaseTime());
                    switch (_demand.getStep()) {
                        case 1:
                            mIvRelease.setImageResource(R.drawable.finish);
                            mIvEnroll.setImageResource(R.drawable.unfinish);
                            mIvConfirm.setImageResource(R.drawable.unfinish);
                            mIvEnd.setImageResource(R.drawable.unfinish);
                            break;
                        case 2:
                            mIvRelease.setImageResource(R.drawable.finish);
                            mIvEnroll.setImageResource(R.drawable.finish);
                            mIvConfirm.setImageResource(R.drawable.unfinish);
                            mIvEnd.setImageResource(R.drawable.unfinish);
                            break;
                        case 3:
                            mIvRelease.setImageResource(R.drawable.finish);
                            mIvEnroll.setImageResource(R.drawable.finish);
                            mIvConfirm.setImageResource(R.drawable.finish);
                            mIvEnd.setImageResource(R.drawable.unfinish);
                            break;
                        case 4:
                            mIvRelease.setImageResource(R.drawable.finish);
                            mIvEnroll.setImageResource(R.drawable.finish);
                            mIvConfirm.setImageResource(R.drawable.finish);
                            mIvEnd.setImageResource(R.drawable.unfinish);
                            break;
                        case 5:
                            mIvRelease.setImageResource(R.drawable.finish);
                            mIvEnroll.setImageResource(R.drawable.finish);
                            mIvConfirm.setImageResource(R.drawable.finish);
                            mIvEnd.setImageResource(R.drawable.finish);
                            break;
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }
}
