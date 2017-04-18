package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.utils.L;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.widget.gesture.GestureContentView;
import com.xtec.locki.widget.gesture.GestureDrawline;
import com.xtec.locki.widget.gesture.LockIndicator;

/**
 * Created by 武昌丶鱼 on 2017/3/27.
 * Description:设置手势密码页面
 */

public class CreateGestureActivity extends AppCompatActivity implements View.OnClickListener {
    /** 手机号码*/
    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
    /** 意图 */
    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
    /** 首次提示绘制手势密码，可以选择跳过 */
    public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";
    private TextView mTextTitle;
    private TextView mTextCancel;
    private LockIndicator mLockIndicator;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextReset;
    private String mParamSetUpcode = null;
    private String mParamPhoneNumber;
    private boolean mIsFirstInput = true;
    private String mFirstPassword = null;
    private String mConfirmPassword = null;
    private int mParamIntentCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gesture);

        setUpViews();
        setUpListeners();
    }

    private void setUpViews() {
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextCancel = (TextView) findViewById(R.id.text_cancel);
        mTextReset = (TextView) findViewById(R.id.text_reset);
        mTextReset.setClickable(false);
        mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
        mTextTip = (TextView) findViewById(R.id.text_tip);
        mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, false, "", new GestureDrawline.GestureCallBack() {
            @Override
            public void onGestureCodeInput(String inputCode) {
                if (!isInputPassValidate(inputCode)) {
                    mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
                    mGestureContentView.clearDrawlineState(0L);
                    return;
                }
                if (mIsFirstInput) {
                    mFirstPassword = inputCode;
                    updateCodeList(inputCode);
                    mGestureContentView.clearDrawlineState(0L);
                    mTextReset.setClickable(true);
                    mTextReset.setText(getString(R.string.reset_gesture_code));
                } else {
                    if (inputCode.equals(mFirstPassword)) {
                        Toast.makeText(CreateGestureActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        L.e("reyzarc","手势密码是---->"+inputCode);
                        //保存设置的手势
                        PreferenceUtils.putString(CreateGestureActivity.this,Constant.GESTURE_PASSWORD,inputCode);
                        mGestureContentView.clearDrawlineState(0L);
                        //保存选中的状态
                        PreferenceUtils.putString(CreateGestureActivity.this, Constant.LOCK_METHOD, Constant.GESTURE);
                        Intent intent = new Intent();
                        intent.putExtra("status","success");
                        setResult(Constant.RESULT_GESTURE,intent);
                        finish();
                    } else {
                        mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(CreateGestureActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                        // 保持绘制的线，1.5秒后清除
                        mGestureContentView.clearDrawlineState(1300L);
                    }
                }
                mIsFirstInput = false;
            }

            @Override
            public void checkedSuccess() {

            }

            @Override
            public void checkedFail() {

            }
        });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
        updateCodeList("");
    }

    private void setUpListeners() {
        mTextCancel.setOnClickListener(this);
        mTextReset.setOnClickListener(this);
    }

    private void updateCodeList(String inputCode) {
        // 更新选择的图案
        mLockIndicator.setPath(inputCode);
    }

    private boolean isInputPassValidate(String inputPassword) {
        if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                Intent intent = new Intent();
                intent.putExtra("status","cancel");
                setResult(Constant.RESULT_GESTURE,intent);
                this.finish();
                break;
            case R.id.text_reset:
                mIsFirstInput = true;
                updateCodeList("");
                mTextTip.setText(getString(R.string.set_gesture_pattern));
                break;
            default:
                break;
        }
    }
}
