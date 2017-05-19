package com.xtec.locki.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtec.locki.R;
import com.xtec.locki.widget.RoundImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 武昌丶鱼 on 2017/5/19.
 * Description:
 */

public class DialogActivity extends AppCompatActivity {

    @BindView(R.id.iv)
    RoundImageView iv;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_give_up)
    Button btnGiveUp;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ButterKnife.bind(this);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRoot.getLayoutParams();
        params.width = (int) (getWindowManager().getDefaultDisplay().getWidth()*0.8);
        llRoot.setLayoutParams(params);
    }

    @OnClick({R.id.btn_start, R.id.btn_give_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start://开始,暂停按钮
                if (TextUtils.equals("开始", btnStart.getText()) || TextUtils.equals("继续", btnStart.getText())) {
                    btnStart.setText("暂停");
                    if (isFirst) {
                        btnGiveUp.setBackgroundResource(R.drawable.btn_stop);
                        btnGiveUp.setText("结束");
                        isFirst = false;
                    }
                } else if (TextUtils.equals("暂停", btnStart.getText())) {
                    btnStart.setText("继续");
                }

                break;
            case R.id.btn_give_up://放弃,结束按钮
                if (TextUtils.equals("放弃", btnGiveUp.getText())) {
                    finish();
                } else if (TextUtils.equals("结束", btnGiveUp.getText())) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
