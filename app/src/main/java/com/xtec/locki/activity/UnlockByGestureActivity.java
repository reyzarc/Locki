package com.xtec.locki.activity;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.widget.gesture.GestureContentView;
import com.xtec.locki.widget.gesture.GestureDrawline;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:
 */

public class UnlockByGestureActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 手机号码
     */
    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
    /**
     * 意图
     */
    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
    private RelativeLayout mTopLayout;
    private TextView mTextTitle;
    private TextView mTextCancel;
    private ImageView mImgUserLogo;
    private TextView mTextPhoneNumber;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextForget;
    private TextView mTextOther;
    private String mParamPhoneNumber;
    private long mExitTime = 0;
    private int mParamIntentCode;
    private String packageName;

    private FingerprintManagerCompat mFingerprintManager = FingerprintManagerCompat.from(this);
    private static final int HTTP_LOGIN_OUT = 21;
    private CancellationSignal mCancellationSignal;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_by_gesture);
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        ObtainExtraData();
        setUpViews();
        setUpListeners();
        if (supportFingerprint()) {//支持指纹解锁
            unlockByFingerprint();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        packageName = PreferenceUtils.getString(this,Constant.PACKAGE_NAME);
    }

    //判断手机是否支持指纹解锁
    private boolean supportFingerprint() {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
        if (!fingerprintManager.isHardwareDetected()) {//不支持指纹
            return false;
        } else {//支持指纹识别
            return true;
        }
    }

    private void unlockByFingerprint() {
        //判断是否添加过指纹
//        if (!mFingerprintManager.hasEnrolledFingerprints()) {//没有添加过指纹
//            new FastDialog(this)
//                    .setContent("你尚未设置过指纹,请前往手机系统\n'设置'>指纹功能中添加指纹")
//                    .setPositiveButton("确定", new FastDialog.OnClickListener() {
//                        @Override
//                        public void onClick(FastDialog dialog) {
//
//                        }
//                    })
//                    .setNegativeButton("取消", new FastDialog.OnClickListener() {
//                        @Override
//                        public void onClick(FastDialog dialog) {
//
//                        }
//                    }).create().show();
//
//            return;
//        }

//        new FastDialog(this)
//                .setContent("请通过指纹识别器验证\n已录入的指纹")
//                .setPositiveButton("确定", new FastDialog.OnClickListener() {
//                    @Override
//                    public void onClick(FastDialog dialog) {
//
//                    }
//                })
//                .setNegativeButton("取消", new FastDialog.OnClickListener() {
//                    @Override
//                    public void onClick(FastDialog dialog) {
//                        if(mCancellationSignal!=null){
//                            mCancellationSignal.cancel();
//                        }
//                    }
//                }).create().show();
        if (mCancellationSignal != null)
            mCancellationSignal.cancel();
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
            //停止1秒，开启震动10秒，然后又停止1秒，又开启震动10秒，不重复.
            if (mVibrator == null) {
                mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            }
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

    private void ObtainExtraData() {
        mParamPhoneNumber = getIntent().getStringExtra(PARAM_PHONE_NUMBER);
        mParamIntentCode = getIntent().getIntExtra(PARAM_INTENT_CODE, 0);
    }

    private void setUpViews() {
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextCancel = (TextView) findViewById(R.id.text_cancel);
        mImgUserLogo = (ImageView) findViewById(R.id.user_logo);
        mTextPhoneNumber = (TextView) findViewById(R.id.text_phone_number);
        mTextTip = (TextView) findViewById(R.id.text_tip);
        mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
        mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
        mTextOther = (TextView) findViewById(R.id.text_other_account);


        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, true, "1235789",
                new GestureDrawline.GestureCallBack() {

                    @Override
                    public void onGestureCodeInput(String inputCode) {

                    }

                    @Override
                    public void checkedSuccess() {
                        mGestureContentView.clearDrawlineState(0L);
//                        Toast.makeText(UnlockByGestureActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                        //发送认证成功的广播
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_UNLOCK_SUCCESS);
                        intent.putExtra(Constant.PACKAGE_NAME, packageName);
                        sendBroadcast(intent);
                        finish();
                        overridePendingTransition(R.anim.enter_hold_view, R.anim.exit_slidedown);
                    }

                    @Override
                    public void checkedFail() {
                        mGestureContentView.clearDrawlineState(1300L);
                        mTextTip.setVisibility(View.VISIBLE);
                        mTextTip.setText(Html
                                .fromHtml("<font color='#c70c1e'>密码错误</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(UnlockByGestureActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                    }
                });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
    }

    private void setUpListeners() {
        mTextCancel.setOnClickListener(this);
        mTextForget.setOnClickListener(this);
        mTextOther.setOnClickListener(this);
    }

    private String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append("****");
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mVibrator.cancel();
    }
}
