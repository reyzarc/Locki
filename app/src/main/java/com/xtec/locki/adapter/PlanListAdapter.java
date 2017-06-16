package com.xtec.locki.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.model.PlanInfoModel;
import com.xtec.locki.utils.DateUtils;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;
import com.xtec.locki.widget.FastDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
        sortAndSaveList();
        notifyDataSetChanged();
    }

    private void sortAndSaveList() {
        //对列表按照时间从近到远进行排序
        Collections.sort(mList, new Comparator<PlanInfoModel>() {
            //返回负数表示t0小于t1,返回0表示相等,返回正数表示t0>t1
            @Override
            public int compare(PlanInfoModel t0, PlanInfoModel t1) {
                //从近到远排序
                long diff = DateUtils.getTimestamp(t0.getStartTime()) - DateUtils.getTimestamp(t1.getStartTime());
                Log.e("reyzarc", "diff is ----->" + diff);
                if (diff > 0) {
                    return 1;
                }
                if (diff == 0) {
                    return 0;
                }
                return -1;
            }
        });

        Iterator<PlanInfoModel> it = mList.iterator();
        while (it.hasNext()) {
            PlanInfoModel model = it.next();
            if (DateUtils.getTimestamp(model.getStartTime()) < System.currentTimeMillis()) {
                it.remove();
            }
        }

        //保存排序后的列表
        Gson gson = new Gson();
        String str = gson.toJson(mList);
        Log.e("reyzarc","最终的数据是0----->"+str);
        if (!TextUtils.isEmpty(str)) {
            PreferenceUtils.putString(mContext, Constant.PLAN_LIST, str);
        }
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

        if (position == 0) {
            if (model != null && model.getStatus() != null) {
                switch (model.getStatus()) {
                    case "-1"://未开始
                        ivDot.setImageResource(R.drawable.dot_gray);
                        tvStatus.setText("未开始");
                        tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                        break;
                    case "0"://暂停
                        ivDot.setImageResource(R.drawable.dot_orange);
                        tvStatus.setText("暂停中");
                        tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.orange_normal));
                        break;
                    case "1"://执行
                        ivDot.setImageResource(R.drawable.dot_green);
                        tvStatus.setText("进行中");
                        tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.btn_theme_green));
                        break;
                    case "2"://放弃
                        ivDot.setImageResource(R.drawable.dot_red);
                        tvStatus.setText("已放弃");
                        tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.red));
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
                    new FastDialog(mContext)
                            .setTitle("提醒")
                            .setContent("确定删除这条计划?")
                            .setPositiveButton("确定", new FastDialog.OnClickListener() {
                                @Override
                                public void onClick(FastDialog dialog) {
                                    mList.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new FastDialog.OnClickListener() {
                                @Override
                                public void onClick(FastDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).create().show();
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
