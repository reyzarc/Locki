package com.xtec.locki.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.xtec.locki.Constant;
import com.xtec.locki.activity.UnlockByFingerprintActivity;
import com.xtec.locki.activity.UnlockByGestureActivity;
import com.xtec.locki.activity.UnlockByNumberActivity;
import com.xtec.locki.utils.PreferenceUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:
 */

public class LockService extends AccessibilityService {
    private CharSequence mWindowClassName;
    private String mCurrentPackage;
    private MyBroadcastReceiver mReceiver;
    private String mTargetPackage = "com.tencent.mm";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type=event.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName();
                mCurrentPackage = event.getPackageName()==null?"":event.getPackageName().toString();
                //判断包名是否在加锁列表里,如果在,则继续判断是否已经解锁,如果锁过期,也需要重新解锁,失效时间为2分钟
                Long currentTime = System.currentTimeMillis();
                Log.e("reyzarc","时间差为---->"+currentTime+"------->"+PreferenceUtils.getLong(this,mCurrentPackage));
//                if(TextUtils.equals(mTargetPackage,mCurrentPackage)&&!PreferenceUtils.getBoolean(this,Constant.UNLOCK_SUCCESS,true,false)&& System.currentTimeMillis()-PreferenceUtils.getLong(this,mCurrentPackage)>2*60*1000){
                if(TextUtils.equals(mTargetPackage,mCurrentPackage)&&!PreferenceUtils.getBoolean(this,Constant.UNLOCK_SUCCESS,true,false)){
                    Intent intent = new Intent();
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.PACKAGE_NAME,mTargetPackage);
                    switch (PreferenceUtils.getString(this, Constant.LOCK_METHOD,true)){
                        case Constant.FINGERPRINT://指纹
                            intent.setClass(this,UnlockByFingerprintActivity.class);
                            break;
                        case Constant.GESTURE://手势
                            intent.setClass(this,UnlockByGestureActivity.class);
                            break;
                        case Constant.NUMBER://密码
                        default:
                            intent.setClass(this,UnlockByNumberActivity.class);
                            break;
                    }
                    startActivity(intent);
                }

                break;
            case TYPE_VIEW_CLICKED:
            case TYPE_VIEW_LONG_CLICKED:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UNLOCK_SUCCESS);
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver,filter);

        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(mScreenOReceiver, mScreenOnFilter);

        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenOReceiver, mScreenOffFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName  = intent.getStringExtra(Constant.PACKAGE_NAME);
            if(!TextUtils.isEmpty(action)&&TextUtils.equals(action,Constant.ACTION_UNLOCK_SUCCESS)){
                Log.e("reyzarc","解锁成功....."+packageName);
                PreferenceUtils.putBoolean(LockService.this,Constant.UNLOCK_SUCCESS,true,true);
                //保存时间,以当前加锁应用的包名为key
                PreferenceUtils.putLong(LockService.this,packageName, System.currentTimeMillis());
            }
        }
    }

    /**
     * 锁屏的管理类叫KeyguardManager，
     * 通过调用其内部类KeyguardLockmKeyguardLock的对象的disableKeyguard方法可以取消系统锁屏，
     * newKeyguardLock的参数用于标识是谁隐藏了系统锁屏
     */
    private BroadcastReceiver mScreenOReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("android.intent.action.SCREEN_ON")) {//亮屏
                Log.e("reyzarc","—— SCREEN_ON ——");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {//熄屏
                //将锁重置为未解锁状态
                PreferenceUtils.putBoolean(LockService.this,Constant.UNLOCK_SUCCESS,false,true);
                Log.e("reyzarc","—— SCREEN_OFF ——");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
