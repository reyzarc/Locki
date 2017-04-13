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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);
        ButterKnife.bind(this);
        initToolBar(toolbar, true);
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
        if(TextUtils.equals(pwd,PreferenceUtils.getString(this,Constant.SAFEGUARD_PASSWORD))){
            Intent intent = new Intent();
            intent.putExtra("status", "success");
            setResult(Constant.RESULT_VERIFY, intent);
            finish();
        }else{
            T.showShort(this, "密码错误,请重试");
        }
    }
}
