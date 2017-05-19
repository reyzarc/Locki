package com.xtec.locki.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtec.locki.Constant;
import com.xtec.locki.activity.DialogActivity;
import com.xtec.locki.activity.UnlockByFingerprintActivity;
import com.xtec.locki.activity.UnlockByGestureActivity;
import com.xtec.locki.activity.UnlockByNumberActivity;
import com.xtec.locki.utils.L;
import com.xtec.locki.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:
 */

public class LockService extends AccessibilityService {
    private CharSequence mWindowClassName;
    /**
     * 超时时间
     */
    private static final int Timeout = 5 * 60 * 1000;
    /**
     * 当前应用包名
     */
    private String mCurrentPackage;
    /**
     * 需要验证的应用包名
     */
    private String mTargetPackage;
    private MyBroadcastReceiver mReceiver;
    /**
     * 不加锁的应用列表
     */
//    private String[] mFilterPackage = new String[]{"com.google.android.googlequicksearchbox", "com.android.systemui", "com.xtec.locki", "com.cyou.privacysecurity"};
    private String[] mFilterPackage = new String[]{ "com.xtec.locki","com.google.android.inputmethod.pinyin","com.android.systemui","com.iflytek.inputmethod","com.google.android.packageinstaller"};
    private List<String> mLockList = new ArrayList<>();

    // 定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName();
                mCurrentPackage = event.getPackageName() == null ? "" : event.getPackageName().toString();
                L.e("reyzarc", mCurrentPackage + "目标包名是---->" + mTargetPackage);


                //过滤掉像系统锁屏界面/launcher,输入法等
                if(mCurrentPackage.contains("inputmethod")){
                    return;
                }
                for (String filterPackage : mFilterPackage) {
                    if (TextUtils.equals(mCurrentPackage, filterPackage)) {
                        return;
                    }
                }

                //判断当前将要打开的应用是否跟之前的应用包名不一样,如果不一样,则将之前的应用锁重置
                //如果应用包名一样,再判断锁是否超时,如果超时,则也要将应用锁重置,超时时间为5分钟
                if (!TextUtils.equals(mCurrentPackage, mTargetPackage)) {
                    PreferenceUtils.putBoolean(this, mTargetPackage, false);
                } else if (System.currentTimeMillis() - PreferenceUtils.getLong(this, mTargetPackage + "time") > Timeout) {
                    PreferenceUtils.putBoolean(this, mTargetPackage, false);
                    PreferenceUtils.putLong(this, mTargetPackage + "time", System.currentTimeMillis());
                }

                //如果不在加锁列表中,也不需要处理
                if (!checkInList(mCurrentPackage)) {
                    return;
                }
                mTargetPackage = mCurrentPackage;
                checkLockStatus(mTargetPackage);

                break;
            case TYPE_VIEW_CLICKED:
            case TYPE_VIEW_LONG_CLICKED:
                break;
        }
    }

    private void checkLockStatus(String targetPackage) {
        if (!PreferenceUtils.getBoolean(this, targetPackage)) {
            PreferenceUtils.putString(this,Constant.PACKAGE_NAME,targetPackage);
            Intent intent = new Intent();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            intent.putExtra(Constant.PACKAGE_NAME, targetPackage);
            switch (PreferenceUtils.getString(this, Constant.LOCK_METHOD)) {
                case Constant.FINGERPRINT://指纹
                    intent.setClass(this, UnlockByFingerprintActivity.class);
                    break;
                case Constant.GESTURE://手势
                    intent.setClass(this, UnlockByGestureActivity.class);
                    break;
                case Constant.NUMBER://密码
                default:
                    intent.setClass(this, UnlockByNumberActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取保存的加锁列表
        getLockList();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_UNLOCK_SUCCESS);
        filter.addAction(Constant.ACTION_UPDATE_UNLOCK_LIST);
        mReceiver = new MyBroadcastReceiver();
        registerReceiver(mReceiver, filter);

        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(mScreenOReceiver, mScreenOnFilter);

        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenOReceiver, mScreenOffFilter);

//        checkWindowPermission();

//        //创建悬浮窗
//        createFloatView();

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                createFloatView();
            }
        },0,50000);

    }


    //以悬浮窗的形式显示dialog,没有阴影,遂放弃
    private void checkWindowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            } else {
                //绘ui代码, 这里说明6.0系统已经有权限了
                createFloatView();
            }
        } else {
            //绘ui代码,这里android6.0以下的系统直接绘出即可
            createFloatView();
        }
    }

    private void createFloatView() {

        Intent intent = new Intent(this,DialogActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_NO_USER_ACTION);
        startActivity(intent);



//        wmParams = new WindowManager.LayoutParams();
//        // 设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
//        // wmParams.type = LayoutParams.TYPE_PHONE;
//        // wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//        // 设置图片格式，效果为背景透明
//        wmParams.format = PixelFormat.RGBA_8888;
//        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        // wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
//        // 设置可以显示在状态栏上
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//
//
//
//
//        // 悬浮窗默认显示以左上角为起始坐标
//        wmParams.gravity = Gravity.CENTER;
//
//        // 以屏幕右上角为原点，设置x、y初始值，确定显示窗口的起始位置
//        wmParams.x = 0;
//        wmParams.y = 0;
//        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//
//        // 设置悬浮窗口长宽数据
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
////        wmParams.width = (int) (mWindowManager.getDefaultDisplay().getWidth()*0.8);
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        // 获取浮动窗口视图所在布局
//        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.floating_view, null);
//        // 添加悬浮窗的视图
//        mWindowManager.addView(mFloatLayout, wmParams);

        /**
         * 设置悬浮窗的点击、滑动事件
         */
//        ImageButton sampleFloat = (ImageButton) mFloatLayout.findViewById(R.id.float_button_id);

//        /**
//         * 设置有无反馈
//         */
//        MyOnGestureListener listener = new MyOnGestureListener();
//        @SuppressWarnings("deprecation")
//        final GestureDetector mGestureDetector = new GestureDetector(listener);
//        sampleFloat.setOnTouchListener(new MyOnTouchListener(mGestureDetector));

    }

    private void getLockList() {
        String str = PreferenceUtils.getString(this, Constant.LOCK_LIST);
        if (!TextUtils.isEmpty(str)) {
            Gson gson = new Gson();
            mLockList = gson.fromJson(str, new TypeToken<List<String>>() {
            }.getType());
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getStringExtra(Constant.PACKAGE_NAME);
            if (!TextUtils.isEmpty(action) && TextUtils.equals(action, Constant.ACTION_UNLOCK_SUCCESS)) {//解锁成功
                L.e("reyzarc", "解锁成功....." + packageName);
                PreferenceUtils.putBoolean(LockService.this, packageName, true);
                //保存时间,以当前加锁应用的包名为key
                PreferenceUtils.putLong(LockService.this,packageName+"time", System.currentTimeMillis());
            } else if (!TextUtils.isEmpty(action) && TextUtils.equals(action, Constant.ACTION_UPDATE_UNLOCK_LIST)) {//更新加锁列表
                getLockList();
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
                L.e("reyzarc", "—— SCREEN_ON ——");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {//熄屏
                //将锁重置为未解锁状态
                L.e("reyzarc", "—— SCREEN_OFF ——" + mTargetPackage);
                if (!TextUtils.isEmpty(mTargetPackage)) {
                    PreferenceUtils.putBoolean(LockService.this, mTargetPackage, false);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 判断是否在加锁列表中
     *
     * @param packageName
     * @return
     */
    public boolean checkInList(String packageName) {
        if (!mLockList.isEmpty()) {
            for (int i = 0; i < mLockList.size(); i++) {
                if (TextUtils.equals(mLockList.get(i), packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


//    /**
//     * @author:Jack Tony
//     * @tips  :设置触摸监听器，处理触摸的事件
//     * @date  :2014-8-13
//     */
//    private class MyOnTouchListener implements View.OnTouchListener {
//        private GestureDetector mGestureDetector;
//
//        public MyOnTouchListener(GestureDetector mGestureDetector) {
//            this.mGestureDetector = mGestureDetector;
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    v.setBackgroundColor(Color.parseColor("#ffd060"));
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                }else if(event.getAction()==MotionEvent.ACTION_MOVE){
//                    moveViewWithFinger(v,event.getRawX(), event.getRawY());
//                }
//
//
//            return mGestureDetector.onTouchEvent(event);
//        }
//    }

    /**
     * 设置View的布局属性，使得view随着手指移动 注意：view所在的布局必须使用RelativeLayout 而且不得设置居中等样式
     *
     * @param view
     * @param rawX
     * @param rawY
     */
    private void moveViewWithFinger(View view, float rawX, float rawY) {
        int left = (int) (rawX - view.getWidth() / 2);
        int top = (int) (rawY  - view.getHeight() / 2);
        int width = left + view.getWidth();
        int height = top + view.getHeight();
        view.layout(left, top, width, height);
    }

//    /**
//     * @author:金凯
//     * @tips :自己定义的手势监听类，设置悬浮窗上下左右滑动、双击的动作
//     * @date :2014-3-29
//     */
//    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
//
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            T.showShort(LockService.this,"双击啦...");
//            return false;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            super.onLongPress(e);
//            T.showShort(LockService.this,"长按");
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                               float velocityY) {
//
//            int dy = (int) (e2.getY() - e1.getY()); // 计算滑动的距离,纵向操作
//            int dx = (int) (e2.getX() - e1.getX());
//
//            if (dy < -20 && Math.abs(velocityY) > Math.abs(velocityX)) {
//                Log.i("sample", "向上");
//            }
//
//            if (dy > 20 && Math.abs(velocityY) > Math.abs(velocityX)) {
//                Log.i("sample", "向下");
//            }
//
//            if (dx > 20 && Math.abs(velocityX) > Math.abs(velocityY)) {
//                Log.i("sample", "向右");
//            }
//            if (dx < -20 && Math.abs(velocityX) > Math.abs(velocityY)) {
//                Log.i("sample", "向左");
//
//
//            }
//            return false;
//        }
//
//    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        createFloatView();

        return super.onStartCommand(intent, flags, startId);
    }
}
