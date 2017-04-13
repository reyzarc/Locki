package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/4/13.
 * Description:身份认证页面
 */

public class VerifyIdentityActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_verify_tips)
    TextView tvVerifyTips;
    //密码验证次数
    private int count = 3;
    //锁定时间
    private int seconds = 600;
    private Timer timer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    seconds--;
                    if (seconds != 0) {
                        tvVerifyTips.setText(String.format(getResources().getString(R.string.verify_time),seconds));
                    } else {
                        btnConfirm.setEnabled(true);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);
        ButterKnife.bind(this);
        initToolBar(toolbar, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int time = PreferenceUtils.getInt(this,Constant.VERIFY_TIME);
        if(time>0){
            btnConfirm.setEnabled(false);
            seconds = time;
            startTimer();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("status", "cancel");
        setResult(Constant.RESULT_VERIFY, intent);
        finish();
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            T.showShort(this, "请输入密码");
            return;
        }
        if (TextUtils.equals(pwd, PreferenceUtils.getString(this, Constant.SAFEGUARD_PASSWORD))) {
            Intent intent = new Intent();
            intent.putExtra("status", "success");
            setResult(Constant.RESULT_VERIFY, intent);
            finish();
        } else {
            if(count<=0){
                T.showShort(this, "密码错误次数太多,请10分钟后再试");
                btnConfirm.setEnabled(false);
                startTimer();
            }else{
                T.showShort(this, "密码错误,请重试");
                tvVerifyTips.setText(String.format(getResources().getString(R.string.verify_tips),count--));
            }
        }
    }

    private void startTimer() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(seconds>0){
            PreferenceUtils.putInt(this,Constant.VERIFY_TIME,seconds);
        }
    }
}
