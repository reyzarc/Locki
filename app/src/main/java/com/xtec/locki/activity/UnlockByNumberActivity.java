package com.xtec.locki.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.NumberKeyboardAdapter;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:数字密码解锁
 */

public class UnlockByNumberActivity extends AppCompatActivity {
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.checkbox_1)
    CheckBox checkbox1;
    @BindView(R.id.checkbox_2)
    CheckBox checkbox2;
    @BindView(R.id.checkbox_3)
    CheckBox checkbox3;
    @BindView(R.id.checkbox_4)
    CheckBox checkbox4;
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.tv_emergency)
    TextView tvEmergency;
    @BindView(R.id.tv_delete)
    TextView tvDelete;

    private ArrayList<Map<String, String>> mKeysList = new ArrayList<>();
    private int count = 0;//记录输入的次数
    private StringBuffer mPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_by_number_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mPwd = new StringBuffer();
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {//按键1-9
                map.put("num", String.valueOf(i));
            } else if (i == 11) {//按键0
                map.put("num", String.valueOf(0));
            } else if (i == 12) {
                map.put("num", "删除");
            } else {
                map.put("num", "");
            }
            mKeysList.add(map);
        }

        NumberKeyboardAdapter mAdapter = new NumberKeyboardAdapter(this, mKeysList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("reyzarc", "点击了----->" + mKeysList.get(i).get("num"));
                if (i == 11) {//点击了删除按钮
                    if (count != 0) {
                        count--;
                        //判断回退了几个
                        if (mPwd == null) {
                            return;
                        }
                        mPwd.substring(0, count % 4);
                        switch (4 - count % 4) {
                            case 1:
                                checkbox4.setChecked(false);
                                break;
                            case 2:
                                checkbox4.setChecked(false);
                                checkbox3.setChecked(false);
                                break;
                            case 3:
                                checkbox4.setChecked(false);
                                checkbox3.setChecked(false);
                                checkbox2.setChecked(false);
                                break;
                            case 0:
                                checkbox4.setChecked(false);
                                checkbox3.setChecked(false);
                                checkbox2.setChecked(false);
                                checkbox1.setChecked(false);
                                break;
                        }
                    } else {
                        mPwd.setLength(0);
                    }
                } else {
                    count++;
                    mPwd.append(mKeysList.get(i).get("num"));
                    switch (count % 4) {
                        case 1:
                            checkbox1.setChecked(true);
                            break;
                        case 2:
                            checkbox2.setChecked(true);
                            break;
                        case 3:
                            checkbox3.setChecked(true);
                            break;
                        case 0:
                            checkbox4.setChecked(true);
                            //输入满4位密码则进行验证
                            checkPwd(mPwd.toString().trim());
                            break;
                    }
                }
            }
        });
    }

    private void checkPwd(String pwd) {
        if (TextUtils.equals(pwd, PreferenceUtils.getString(this, Constant.NUMBER_PASSWORD))) {
            T.showShort(this, "解锁成功...");
        } else {
            T.showShort(this, "密码错误!");
        }
        mPwd.setLength(0);
        count=0;
        checkbox4.setChecked(false);
        checkbox3.setChecked(false);
        checkbox2.setChecked(false);
        checkbox1.setChecked(false);
    }

    @OnClick({R.id.tv_emergency, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_emergency://紧急呼叫
                break;
            case R.id.tv_delete://删除
                break;
        }
    }
}
