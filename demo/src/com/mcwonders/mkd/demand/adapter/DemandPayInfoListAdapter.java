package com.mcwonders.mkd.demand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mcwonders.mkd.demand.bean.DemandPayInfo;

import java.util.List;

/**
 * 需求支付信息列表
 */
public class DemandPayInfoListAdapter extends BaseAdapter implements ListAdapter {
    private List<DemandPayInfo> mPayInfoList;
    private Context mContext;

    public DemandPayInfoListAdapter() {
    }

    public DemandPayInfoListAdapter(Context context, List<DemandPayInfo> payInfoList) {
        this.mContext = context;
        this.mPayInfoList = payInfoList;
    }

    @Override
    public int getCount() {
        return mPayInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPayInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        DemandPayInfo demandPayInfo = mPayInfoList.get(position);
        view = LayoutInflater.from(mContext).inflate(com.mcwonders.mkd.R.layout.list_item_demand_pay_info, null);
        View ivLine = (View) view.findViewById(com.mcwonders.mkd.R.id.line_list_item_demand_pay_info);
        TextView tvName = (TextView) view.findViewById(com.mcwonders.mkd.R.id.tv_list_item_demand_pay_info_name);
        TextView tvIsDeliver = (TextView) view.findViewById(com.mcwonders.mkd.R.id.tv_list_item_demand_pay_info_require);
        TextView tvScale = (TextView) view.findViewById(com.mcwonders.mkd.R.id.tv_list_item_demand_pay_info_scale);
        TextView tvMoney = (TextView) view.findViewById(com.mcwonders.mkd.R.id.tv_list_item_demand_pay_info_money);

        switch (demandPayInfo.getPayType()) {
            case 1:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color1));
                break;
            case 2:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color2));
                break;
            case 3:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color3));
                break;
            case 4:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color1));
                break;
            case 5:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color2));
                break;
            case 6:
                ivLine.setBackgroundColor(mContext.getResources().getColor(com.mcwonders.mkd.R.color.demand_pay_info_line_color3));
                break;
        }
        tvName.setText(demandPayInfo.getPayName());
        if (demandPayInfo.isDeliver()) {
            tvIsDeliver.setText("交付要求： 有交付物");
        } else {
            tvIsDeliver.setText("交付要求： 无交付物");
        }
        tvScale.setText("付款比例： " + demandPayInfo.getPayScale() + "%");
        tvMoney.setText("付款金额： " + demandPayInfo.getPayMoney() + "元");
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public List GetList() {
        return mPayInfoList;
    }
}
