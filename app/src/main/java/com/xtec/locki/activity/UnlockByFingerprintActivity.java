package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtec.locki.R;
import com.xtec.locki.widget.FastDialog;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:指纹解锁页面
 */

public class UnlockByFingerprintActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UnlockByFingerprintAct";

    private FingerprintManagerCompat mFingerprintManager = FingerprintManagerCompat.from(this);
    private static final int HTTP_LOGIN_OUT = 21;
    private CancellationSignal mCancellationSignal;

    private LinearLayout llFingerprint;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mCancellationSignal = new CancellationSignal();
            mFingerprintManager.authenticate(null, 0, mCancellationSignal, new MyCallBack(), null);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_by_fingerprint);

        TextView tvLoginByPwd = (TextView) findViewById(R.id.tv_login_by_pwd);
        llFingerprint = (LinearLayout) findViewById(R.id.ll_fingerprint);


        tvLoginByPwd.setOnClickListener(this);
        llFingerprint.setOnClickListener(this);
        showFingerprintDialog();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_fingerprint://指纹登陆
                showFingerprintDialog();

                break;
            case R.id.tv_login_by_pwd://用账号密码登录
                break;
        }
    }

    private void showFingerprintDialog() {

        //判断是否添加过指纹
        if (!mFingerprintManager.hasEnrolledFingerprints()) {//没有添加过指纹
            new FastDialog(this)
                    .setContent("你尚未设置过指纹,请前往手机系统\n'设置'>指纹功能中添加指纹")
                    .setPositiveButton("确定", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {

                        }
                    })
                    .setNegativeButton("取消", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {

                        }
                    }).create().show();

            return;
        }

        new FastDialog(this)
                .setContent("请通过指纹识别器验证\n已录入的指纹")
                .setPositiveButton("确定", new FastDialog.OnClickListener() {
                    @Override
                    public void onClick(FastDialog dialog) {

                    }
                })
                .setNegativeButton("取消", new FastDialog.OnClickListener() {
                    @Override
                    public void onClick(FastDialog dialog) {
                        if(mCancellationSignal!=null){
                            mCancellationSignal.cancel();
                        }
                    }
                }).create().show();

        mCancellationSignal = new CancellationSignal();
        mFingerprintManager.authenticate(null, 0, mCancellationSignal, new MyCallBack(), null);

    }

    private class MyCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        private static final String TAG = "MyCallBack";

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
//            AndroidUtils.Toast(this, ""+errString);
//            handler.sendMessageDelayed(new Message(), 1000 * 30);
//            logout();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);

            finish();
            overridePendingTransition(R.anim.enter_hold_view, R.anim.exit_slidedown);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
//            AndroidUtils.Toast(UnlockByFingerprintAct.this, "指纹认证失败,请重试");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==111){
            if(data.getStringExtra("result").equals("login_success")){
                //登陆成功
                gotoMainActivity();
            }
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_hold_view, R.anim.exit_slidedown);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
