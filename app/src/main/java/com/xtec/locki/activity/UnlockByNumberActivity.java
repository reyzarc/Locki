package com.xtec.locki.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
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
import com.xtec.locki.widget.FastDialog;

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
    private FingerprintManagerCompat mFingerprintManager = FingerprintManagerCompat.from(this);
    private static final int HTTP_LOGIN_OUT = 21;
    private CancellationSignal mCancellationSignal;
    private Vibrator mVibrator;

    private FastDialog mNoPwdDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_by_number_password);
        ButterKnife.bind(this);
        blurView.setBlurredLevel(100);
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        initView();
        if (supportFingerprint()) {//支持指纹解锁
            unlockByFingerprint();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkHasPwd();
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
                if (i == 9) {//无按键
                    return;
                }
                if (i == 11) {//点击了删除按钮
                    if (count != 0) {
                        count--;
                        //判断回退了几个
                        if (mPwd == null) {
                            return;
                        }
                        mPwd =  mPwd.deleteCharAt(count % 4);
//                        mPwd.substring(0, count % 4);
                        L.e("reyzarc","psw is ----->"+mPwd);
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
                            case 4:
                                checkbox4.setChecked(false);
                                checkbox3.setChecked(false);
                                checkbox2.setChecked(false);
                                checkbox1.setChecked(false);
                                break;
                        }
                    } else {
                        mPwd.setLength(0);
                        L.e("reyzarc","clean psw is ----->"+mPwd);
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
        L.e("reyzarc", "输入的密码是---->" + pwd);
        if (TextUtils.isEmpty(PreferenceUtils.getString(this, Constant.NUMBER_PASSWORD))) {//未设置数字密码
            if (TextUtils.equals("1234", pwd)) {
                //发送认证成功的广播
                Intent intent = new Intent();
                intent.setAction(Constant.ACTION_UNLOCK_SUCCESS);
                intent.putExtra(Constant.PACKAGE_NAME, packageName);
                sendBroadcast(intent);
                finish();
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
            } else {
                T.showShort(this, "初始密码1234,请尽快更改!");
            }
        } else if (TextUtils.equals(pwd, PreferenceUtils.getString(this, Constant.NUMBER_PASSWORD))) {
            //发送认证成功的广播
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_UNLOCK_SUCCESS);
            intent.putExtra(Constant.PACKAGE_NAME, packageName);
            sendBroadcast(intent);
            finish();
            overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
        } else {
            T.showShort(this, "密码错误!");
        }
        //还原输入状态
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
            overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
//            AndroidUtils.Toast(UnlockByFingerprintAct.this, "指纹认证失败,请重试");
        }
    }

    /**
     * 检查是否设置数字密码
     */
    private void checkHasPwd() {
        if (TextUtils.isEmpty(PreferenceUtils.getString(this, Constant.NUMBER_PASSWORD))) {
            if (mNoPwdDialog!=null&&mNoPwdDialog.isShowing()){
                return;
            }
            mNoPwdDialog = new FastDialog(this)
                    .setTitle("警告")
                    .setContent("检测到您还未设置数字密码,初始密码为1234,请尽快更改数字密码或解锁方式,以保证安全!")
                    .setNegativeButton("下次再说", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("立即更改", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {
                            Intent intent = new Intent();
                            intent.setClass(UnlockByNumberActivity.this, CreateNumberPwdActivity.class);
                            startActivity(intent);
                        }
                    }).create();
            mNoPwdDialog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVibrator.cancel();
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }
}
