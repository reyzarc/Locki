package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;
import com.xtec.locki.widget.PayPwdEditText;
import com.xtec.locki.widget.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/4/13.
 * Description:设置数字密码界面
 */

public class CreateNumberPwdActivity extends BaseActivity {

    @BindView(R.id.password)
    PayPwdEditText password;
    @BindView(R.id.password_confirm)
    PayPwdEditText passwordConfirm;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.topbar)
    Topbar topbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_number_password);
        ButterKnife.bind(this);
        initTopbar(this, topbar);
        initView();
    }

    private void initView() {
        password.initStyle(R.drawable.edit_num_bg, 4, 0.33f, R.color.color999999, R.color.color999999, 20);
        passwordConfirm.initStyle(R.drawable.edit_num_bg, 4, 0.33f, R.color.color999999, R.color.color999999, 20);
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        if (TextUtils.isEmpty(password.getPwdText())) {
            T.showShort(this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(passwordConfirm.getPwdText())) {
            T.showShort(this, "请输入确认密码");
            return;
        }
        if (!TextUtils.equals(password.getPwdText(), passwordConfirm.getPwdText())) {
            T.showShort(this, "两次输入的密码不一致,请重试");
            password.clearText();
            passwordConfirm.clearText();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("status", "success");
        setResult(Constant.RESULT_NUMBER, intent);
        PreferenceUtils.putString(this, Constant.NUMBER_PASSWORD, password.getPwdText());
        finish();
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
        setResult(Constant.RESULT_NUMBER, intent);
        finish();
    }
}
