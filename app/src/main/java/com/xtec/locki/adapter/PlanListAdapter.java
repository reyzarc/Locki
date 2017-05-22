package com.xtec.locki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        mList.addAll(0, list);
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

        View header = convertView.findViewById(R.id.header);
        View footer = convertView.findViewById(R.id.footer);
        ImageView ivDot = (ImageView) convertView.findViewById(R.id.iv_dot);

        PlanInfoModel model = mList.get(position);

        if (mList.size() == 1) {//只有一条数据
            header.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
        } else {
            if (position == 0) {//第一条数据
                ivDot.setImageResource(R.drawable.dot_orange);
                header.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
            } else if (position == mList.size() - 1) {//最后一条数据
                ivDot.setImageResource(R.drawable.dot_gray);
                footer.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
            } else {
                ivDot.setImageResource(R.drawable.dot_gray);
                footer.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
            }
        }

        if(position==0){
            if(model!=null&&model.getStatus()!=null){
                switch (model.getStatus()){
                    case "0"://暂停
                        ivDot.setImageResource(R.drawable.dot_orange);
                        break;
                    case "1"://执行
                        ivDot.setImageResource(R.drawable.dot_green);
                        break;
                    case "2"://放弃
                        ivDot.setImageResource(R.drawable.dot_red);
                        break;
                }
            }
        }


        if (model != null) {
            tvTitle.setText(model.getPlanTitle());
            tvDuration.setText("持续时间:" + model.getDuration() + "分钟");
            tvStartTime.setText("开始时间:" + model.getStartTime());

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    T.showShort(mContext, position + "被删除了");
                }
            });

            tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    T.showShort(mContext, "暂停or继续");
                }
            });
        }
    }

}
