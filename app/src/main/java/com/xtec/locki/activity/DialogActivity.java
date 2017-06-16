package com.xtec.locki.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.model.PlanInfoModel;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.widget.RoundImageView;

import java.io.IOException;
import java.util.List;

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
    private PlanInfoModel model;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private List<PlanInfoModel> mPlanList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ButterKnife.bind(this);

        model = getIntent().getParcelableExtra("model");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRoot.getLayoutParams();
        params.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        llRoot.setLayoutParams(params);

        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);//获取当前手机模式
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT://静音
                //do sth
                break;
            case AudioManager.RINGER_MODE_NORMAL://响铃
                //do sth
                //响铃并震动
                ringAndVibrate(true, true);
                break;
            case AudioManager.RINGER_MODE_VIBRATE://震动
                //do sth
                ringAndVibrate(false, true);
                break;
        }
    }

    private void ringAndVibrate(boolean isRing, boolean isVibrate) {
        if (isRing) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));//这里我用的通知声音，还有其他的，大家可以点进去看
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        if (isVibrate) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
            //第二个参数为重复次数，-1为不重复，0为一直震动
            vibrator.vibrate(new long[]{500, 1000}, 0);
        }
    }

    @OnClick({R.id.btn_start, R.id.btn_give_up})
    public void onViewClicked(View view) {
        //停止震动和响铃
        stopRingAndVibrate();
        switch (view.getId()) {
            case R.id.btn_start://开始,暂停按钮
                if (TextUtils.equals("开始", btnStart.getText()) || TextUtils.equals("继续", btnStart.getText())) {
                    model.setStatus("1");

                    btnStart.setText("暂停");
                    if (isFirst) {
                        btnGiveUp.setBackgroundResource(R.drawable.btn_stop);
                        btnGiveUp.setText("结束");
                        isFirst = false;
                    }
                } else if (TextUtils.equals("暂停", btnStart.getText())) {
                    btnStart.setText("继续");
                    model.setStatus("0");
                }

                break;
            case R.id.btn_give_up://放弃,结束按钮
                model.setStatus("2");
                if (TextUtils.equals("放弃", btnGiveUp.getText())) {
                    finish();
                } else if (TextUtils.equals("结束", btnGiveUp.getText())) {
                    finish();
                }
                break;
        }
    }

    private void stopRingAndVibrate() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        //保存数据
        saveData();
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_UPDATE_PLAN_STATUS);
        sendBroadcast(intent);
    }

    private void saveData() {
        //获取计划列表
        String str = PreferenceUtils.getString(this, Constant.PLAN_LIST);
        mPlanList = new Gson().fromJson(str, new TypeToken<List<PlanInfoModel>>() {
        }.getType());

        if (!mPlanList.isEmpty()) {
            for (int i = 0; i < mPlanList.size(); i++) {
                PlanInfoModel temp = mPlanList.get(i);
                if(temp.getId().equals(model.getId())){
                    temp.setStatus(model.getStatus());
                }
            }

            Gson gson = new Gson();
            String s = gson.toJson(mPlanList);
            if (!TextUtils.isEmpty(s)) {
                PreferenceUtils.putString(this, Constant.PLAN_LIST, str);
            }
        }
    }
}
