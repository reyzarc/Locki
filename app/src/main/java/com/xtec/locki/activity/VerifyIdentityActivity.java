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
import com.xtec.locki.utils.DateUtils;
import com.xtec.locki.utils.L;
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
    //锁定时间,单位秒
    private int seconds = 600;
    private Timer timer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    seconds--;
                    if (seconds != 0) {
                        tvVerifyTips.setText(String.format(getResources().getString(R.string.verify_time), DateUtils.FormatStringTimeMS(seconds)));
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
        long time = PreferenceUtils.getLong(this,Constant.VERIFY_TIME);
        long difTime = time-System.currentTimeMillis();
        L.e("reyzarc",time+"时间差是----->"+difTime+"秒=====>"+difTime/1000);
        if(difTime > 0){//还未过冻结时间
            btnConfirm.setEnabled(false);
            seconds = (int) (difTime/1000);
            startTimer();
        }else{//已经过了冻结时间
            btnConfirm.setEnabled(true);
            seconds = 600;
            count = 3;
            if(timer!=null){
                timer.cancel();
                timer = null;
            }
            tvVerifyTips.setText("请输入密码验证身份");
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
            //退出时保存冻结倒计时结束时间
            PreferenceUtils.putLong(this,Constant.VERIFY_TIME,System.currentTimeMillis()+seconds*1000);
        }
    }
}
