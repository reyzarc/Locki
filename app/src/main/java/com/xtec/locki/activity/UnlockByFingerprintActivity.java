package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.widget.FastDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:指纹解锁页面
 */

public class UnlockByFingerprintActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UnlockByFingerprintAct";
    @BindView(R.id.ll_fingerprint)
    LinearLayout llFingerprint;
    @BindView(R.id.tv_login_by_pwd)
    TextView tvLoginByPwd;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;

    private FingerprintManagerCompat mFingerprintManager = FingerprintManagerCompat.from(this);
    private CancellationSignal mCancellationSignal;

    private String packageName;
    private Vibrator mVibrator;

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
        mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        packageName = getIntent().getStringExtra(Constant.PACKAGE_NAME);
        packageName = PreferenceUtils.getString(this,Constant.PACKAGE_NAME);
        if(mCancellationSignal==null){
            mCancellationSignal = new CancellationSignal();
            mFingerprintManager.authenticate(null, 0, mCancellationSignal, new MyCallBack(), null);
        }
    }

    @OnClick({R.id.rl_root})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_root:
                showFingerprintDialog();
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
                        if (mCancellationSignal != null) {
                            mCancellationSignal.cancel();
                        }
                    }
                }).create().show();

        if (mCancellationSignal == null) {
            mCancellationSignal = new CancellationSignal();
        }
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
            Log.e("reyzarc", "指纹认证成功....");
            mVibrator.vibrate(new long[]{0, 30, 0, 0}, -1);
            //发送认证成功的广播
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_UNLOCK_SUCCESS);
            intent.putExtra(Constant.PACKAGE_NAME, packageName);
            sendBroadcast(intent);
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
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
        mVibrator.cancel();
    }
}
