package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.PlanListAdapter;
import com.xtec.locki.model.PlanInfoModel;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.RxBus;
import com.xtec.locki.widget.Topbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by 武昌丶鱼 on 2017/5/17.
 * Description:时间计划页面,提供的功能包括设置计划列表,到设定时间检查手机屏幕开关状态,
 * 如果是亮屏,则在屏幕上显示一方块,并随着时间的增加不断变大,此时用户可拖动方块,但不可消除,
 * 直到铺满整个屏幕(显示提示文字:保持专注),持续时间结束,则当前周期的这个计划完成.
 * 当计划时间启动时用户可长按方块5s强制取消当前计划(屏幕破碎效果),拟提供延期功能,弹窗提示整个延期还是只延期当前
 * 列表显示,侧滑弹出,编辑,删除
 */

public class TimePlanActivity extends BaseActivity {

    @BindView(R.id.topbar)
    Topbar topbar;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.lv_list)
    ListView lvList;
    @BindView(R.id.btn_add_plan)
    Button btnAddPlan;

    private List<PlanInfoModel> mPlanList;
    private PlanListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_plan);
        ButterKnife.bind(this);
        initTopbar(this, topbar);
        initView();
        initData();
        initRxBus();
    }

    private void initRxBus() {
        Subscription subscribe = RxBus.getInstance().toObservable(PlanInfoModel.class)
                .subscribe(new Action1<PlanInfoModel>() {
                    @Override
                    public void call(PlanInfoModel planInfoModel) {
                        if (planInfoModel != null) {

                            List<PlanInfoModel> list = new ArrayList<>();
                            list.add(0, planInfoModel);
                            mAdapter.addData(list);
                        }
                    }
                });
        addSubscription(subscribe);
    }

    private void initData() {
        //获取计划列表
        String str = PreferenceUtils.getString(this, Constant.PLAN_LIST);
        mPlanList = new Gson().fromJson(str, new TypeToken<List<PlanInfoModel>>() {
        }.getType());
        if (mPlanList == null || mPlanList.isEmpty()) {
            mPlanList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PlanInfoModel model = new PlanInfoModel();
                model.setDuration("" + 100 * i);
                model.setPlanTitle("跑步" + i);
                model.setStartTime("0" + (i + 1) + "-05 " + "18:00");
                model.setStartTime(i-1+"");
                mPlanList.add(model);
            }
        }
        mAdapter.setData(mPlanList);
    }

    private void initView() {

        mAdapter = new PlanListAdapter(this);
        lvList.setAdapter(mAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {//跳转到编辑银行卡界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                startActivity(new Intent(TimePlanActivity.this, PlanDetailActivity.class));

            }
        });
    }

    @OnClick(R.id.btn_add_plan)
    public void onViewClicked() {
        Intent intent = new Intent(this, PlanDetailActivity.class);
        intent.putExtra("flag", "add");
        startActivity(intent);
    }
}
