package com.xtec.locki.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.model.PlanInfoModel;
import com.xtec.locki.utils.DateUtils;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.RxBus;
import com.xtec.locki.utils.T;
import com.xtec.locki.widget.Topbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/5/19.
 * Description:计划详情页面
 * flag: view-查看 add-添加 edit-编辑
 */

public class PlanDetailActivity extends BaseActivity {
    @BindView(R.id.topbar)
    Topbar topbar;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.rl_plan_type)
    RelativeLayout rlPlanType;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.rl_start_time)
    RelativeLayout rlStartTime;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.rl_duration)
    RelativeLayout rlDuration;
    @BindView(R.id.tv_repeat)
    TextView tvRepeat;
    @BindView(R.id.rl_repeat)
    RelativeLayout rlRepeat;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.rl_plan_title)
    RelativeLayout rlPlanTitle;

    private String flag;
    private List<String> planType;
    private List<String> repeatModel;
    private List<String> durationHour;
    private List<String> durationMinute = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);
        initTopbar(this, topbar);
        flag = getIntent().getStringExtra("flag");
        if (!TextUtils.isEmpty(flag)) {
            switch (flag) {
                case "add":
                    topbar.setTitle("添加计划");
                    break;
                case "edit":
                    topbar.setTitle("修改计划");
                    break;
                case "view":
                    topbar.setTitle("计划详情");
                    break;
            }
        }

        String[] arr = getResources().getStringArray(R.array.plan_type);
        String[] repeat = getResources().getStringArray(R.array.repeat_type);
        String[] hour = getResources().getStringArray(R.array.duration_hour);
        planType = Arrays.asList(arr);
        repeatModel = Arrays.asList(repeat);
        durationHour = Arrays.asList(hour);

        for (int i = 0; i < 12; i++) {
            durationMinute.add(i, i * 5 + "");
        }
    }

    @OnClick({R.id.btn_confirm, R.id.rl_plan_type, R.id.rl_start_time, R.id.rl_duration, R.id.rl_repeat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_plan_type://计划类型
                OptionsPickerView pickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        String s = planType.get(options1);
                        tvType.setText(s);
                    }
                }).build();
                pickerView.setPicker(planType);
                pickerView.show();
                break;
            case R.id.rl_start_time://开始时间
                TimePickerView timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tvStartTime.setText(DateUtils.getMDHM(date));
                    }
                })
                        .setType(new boolean[]{false, true, true, true, true, false})
                        .setLabel("", "月", "日", "点", "分", "")
                        .build();
                timePickerView.show();

                break;
            case R.id.rl_duration://持续时间
                TimePickerView timePickerView1 = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tvDuration.setText(DateUtils.getHM(date));
                    }
                })
                        .setType(new boolean[]{false, false, false, true, true, false})
                        .setLabel("", "", "", "小时", "分", "")
                        .build();
                timePickerView1.show();

//                OptionsPickerView pickerView1 = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
//                    @Override
//                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                        String hour = durationHour.get(options1);
//                        String minute = durationMinute.get(options2);
//                        tvRepeat.setText(hour+"小时"+minute);
//                    }
//                })
//                        .setLinkage(false)
//                        .setLabels("小时", "分钟", "").build();
//                pickerView1.setPicker(durationHour, durationMinute);
//                pickerView1.show();

                break;
            case R.id.rl_repeat://是否重复或重复周期
                OptionsPickerView pickerView2 = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        String s = repeatModel.get(options1);
                        tvRepeat.setText(s);
                    }
                }).build();
                pickerView2.setPicker(repeatModel);
                pickerView2.show();
                break;
            case R.id.btn_confirm://确定
                if(TextUtils.isEmpty(etTitle.getText())){
                    T.showShort(this,"请填写标题");
                    return;
                }

                if (TextUtils.isEmpty(tvType.getText())) {
                    T.showShort(this, "请选择类别");
                    return;
                }

                if (TextUtils.isEmpty(tvStartTime.getText())) {
                    T.showShort(this, "请选择开始时间");
                    return;
                }

                if (TextUtils.isEmpty(tvDuration.getText())) {
                    T.showShort(this, "请选择持续时间");
                    return;
                }

                if (TextUtils.isEmpty(tvRepeat.getText())) {
                    T.showShort(this, "请选择重复周期");
                    return;
                }

                PlanInfoModel model = new PlanInfoModel();
                model.setType(tvType.getText().toString().trim());
                model.setStartTime(tvStartTime.getText().toString().trim());
                model.setDuration(tvDuration.getText().toString().trim());
                model.setRepeat(tvRepeat.getText().toString().trim());
                model.setPlanTitle(etTitle.getText().toString());
                model.setStatus("0");
                model.setId(UUID.randomUUID().toString());

                PreferenceUtils.putObject(this, Constant.PLAN_SINGLE_RECORD,model);
                RxBus.getInstance().postEvent(model);
                getProgressDialog().setMessage("处理中...");
                getProgressDialog().show();
                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        getProgressDialog().dismiss();
                        finish();
                    }
                };
                timer.start();
                break;
        }
    }
}
