package com.xtec.locki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.xtec.locki.R;
import com.xtec.locki.model.PlanInfoModel;
import com.xtec.locki.utils.T;

import java.util.List;


/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:计划列表
 */

public class PlanListAdapter extends BaseSwipeAdapter {
    private Context mContext;
    private List<PlanInfoModel> mList;

    public PlanListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<PlanInfoModel> planList) {
        mList = planList;
        notifyDataSetChanged();
    }

    public void addData(List<PlanInfoModel> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_plan_list, null);
        SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setClickToClose(true);
        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
        TextView tvStartTime = (TextView) convertView.findViewById(R.id.tv_start_time);

        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
        TextView tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);

        PlanInfoModel model = mList.get(position);
        if (model != null) {
            tvTitle.setText(model.getPlanTitle());
            tvDuration.setText("持续时间:"+model.getDuration()+"分钟");
            tvStartTime.setText("开始时间:"+model.getStartTime());

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    T.showShort(mContext,position+"被删除了");
                }
            });

            tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    T.showShort(mContext,"暂停or继续");
                }
            });
        }
    }

}
