package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.NumberKeyboardAdapter;
import com.xtec.locki.utils.DateUtils;
import com.xtec.locki.utils.L;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;
import com.xtec.locki.widget.BlurredView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.blur_view)
    BlurredView blurView;

    private ArrayList<Map<String, String>> mKeysList = new ArrayList<>();
    private int count = 0;//记录输入的次数
    private StringBuffer mPwd;

    private String packageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_by_number_password);
        ButterKnife.bind(this);
        blurView.setBlurredLevel(100);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvTime.setText(DateUtils.FormatStringTimeHM(System.currentTimeMillis()));
        tvDate.setText(DateUtils.getDate(this));
        packageName = PreferenceUtils.getString(this, Constant.PACKAGE_NAME);
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
                L.e("reyzarc", "点击了----->" + mKeysList.get(i).get("num"));
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
                            blurView.setBlurredLevel(90);
                            break;
                        case 2:
                            checkbox2.setChecked(true);
                            blurView.setBlurredLevel(80);

                            break;
                        case 3:
                            checkbox3.setChecked(true);
                            blurView.setBlurredLevel(70);
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
        L.e("reyzarc","输入的密码是---->"+pwd);
//        if (TextUtils.equals(pwd, PreferenceUtils.getString(this, Constant.NUMBER_PASSWORD))) {
        if (TextUtils.equals(pwd, "4568")) {
            //发送认证成功的广播
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_UNLOCK_SUCCESS);
            intent.putExtra(Constant.PACKAGE_NAME, packageName);
            sendBroadcast(intent);
            finish();
            overridePendingTransition(R.anim.right_in,R.anim.right_out);
        } else {
            T.showShort(this, "密码错误!");
        }
        mPwd.setLength(0);
        count = 0;
        checkbox4.setChecked(false);
        checkbox3.setChecked(false);
        checkbox2.setChecked(false);
        checkbox1.setChecked(false);
        blurView.setBlurredLevel(100);
    }


    @Override
    public void onBackPressed() {
    }
}
