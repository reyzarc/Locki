package com.xtec.locki.activity;

import android.content.Intent;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/4/13.
 * Description:设置密码保护页面
 */

public class SafeguardActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd_confirm)
    EditText etPwdConfirm;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safeguard);
        ButterKnife.bind(this);
        initToolBar(toolbar,true);
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        if (TextUtils.isEmpty(etPwd.getText().toString())) {
            T.showShort(this, "请输入密码");
            return;
        }
        if (etPwd.getText().toString().length()<6) {
            T.showShort(this, "密码格式不正确,请重新输入");
            return;
        }
        if (TextUtils.isEmpty(etPwdConfirm.getText().toString())) {
            T.showShort(this, "请输入确认密码");
            return;
        }
        if (!TextUtils.equals(etPwd.getText().toString(), etPwdConfirm.getText().toString())) {
            T.showShort(this, "两次输入的密码不一致,请重试");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("status", "success");
        setResult(Constant.RESULT_SAFEGUARD, intent);
        PreferenceUtils.putString(this, Constant.SAFEGUARD_PASSWORD, etPwd.getText().toString());
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
        setResult(Constant.RESULT_SAFEGUARD, intent);
        finish();
    }
}
