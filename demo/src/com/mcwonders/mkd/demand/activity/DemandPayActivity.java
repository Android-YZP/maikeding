package com.mcwonders.mkd.demand.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.mcwonders.mkd.demand.util.DBHelper;
import com.mcwonders.uikit.model.ToolBarOptions;
import com.mcwonders.mkd.R;
import com.mcwonders.mkd.demand.adapter.DemandPayInfoListAdapter;
import com.mcwonders.mkd.demand.bean.DemandPayInfo;
import com.mcwonders.uikit.common.activity.UI;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 付款信息页面
 * Created by ZJ on 2016/11/30.
 */
public class DemandPayActivity extends UI {

    private TextView mTvDemandId;
    private ListView mListPayInfo;
    private DemandPayInfoListAdapter mAdapter;
    private DbManager dbManager;//数据库管理对象
    private int demandId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demand_pay_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.demand_pay;
        setToolBar(R.id.toolbar, options, R.id.toolbar_demand_pay_title);
        findViews();
        initDada();
    }

    private void findViews() {
        mTvDemandId = findView(R.id.tv_demand_pay_id);
        mListPayInfo = findView(R.id.list_demand_pay_info);
    }

    private void initDada() {
        dbManager = DBHelper.getDbManager();
        Bundle _bundle = getIntent().getExtras();
        if (_bundle != null) {
            demandId = _bundle.getInt("demandId");
            mTvDemandId.setText(String.valueOf(demandId));
            try {
                List<DemandPayInfo> demandPayInfos = dbManager.selector(DemandPayInfo.class).where("demand_id", "=", demandId).findAll();
                mAdapter = new DemandPayInfoListAdapter(this, demandPayInfos);
                mListPayInfo.setAdapter(mAdapter);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }
}
