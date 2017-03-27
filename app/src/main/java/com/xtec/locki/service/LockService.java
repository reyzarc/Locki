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

import rx.subscriptions.CompositeSubscription;

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

    private CompositeSubscription mCompositeSubscription;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type=event.getEventType();
        switch (type){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName();
                mCurrentPackage = event.getPackageName()==null?"":event.getPackageName().toString();
                Log.e("reyzarc","------------->"+mCurrentPackage);
                if(TextUtils.equals("com.xtec.timeline",mCurrentPackage)&&!PreferenceUtils.getBoolean(this,Constant.UNLOCK_SUCCESS,true,false)){
                    Intent intent = new Intent();
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.PACKAGE_NAME,"com.xtec.timeline");
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
        Log.e("reyzarc","onCreate is running....");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UNLOCK_SUCCESS);
        registerReceiver(new MyBroadcastReceiver(),filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("reyzarc","onStartCommand is running....");


        return super.onStartCommand(intent, flags, startId);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action)&&TextUtils.equals(action,Constant.ACTION_UNLOCK_SUCCESS)){
                Log.e("reyzarc","解锁成功....."+intent.getStringExtra(Constant.PACKAGE_NAME));
                PreferenceUtils.putBoolean(LockService.this,Constant.UNLOCK_SUCCESS,true,true);
            }
        }
    }
}
