package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.service.LockService;
import com.xtec.locki.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rb_fingerprint)
    RadioButton rbFingerprint;
    @BindView(R.id.rb_number_pwd)
    RadioButton rbNumberPwd;
    @BindView(R.id.rb_gesture_pwd)
    RadioButton rbGesturePwd;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startService(new Intent(this, LockService.class));
        radioGroup.setOnCheckedChangeListener(this);
        PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT, true);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.rb_fingerprint://指纹解锁
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT, true);
                break;
            case R.id.rb_gesture_pwd://手势密码解锁
                startActivity(new Intent(this,CreateGestureActivity.class));
//                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.GESTURE, true);
                break;
            case R.id.rb_number_pwd://数字密码解锁
            default:
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.NUMBER, true);
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
