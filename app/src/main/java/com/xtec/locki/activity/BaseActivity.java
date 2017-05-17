package com.xtec.locki.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xtec.locki.utils.UIUtils;
import com.xtec.locki.widget.swipeBackLayout.SwipeBackActivity;
import com.xtec.locki.widget.swipeBackLayout.SwipeBackLayout;

/**
 * Created by 武昌丶鱼 on 2017/4/13.
 * Description:
 */

public class BaseActivity extends SwipeBackActivity {

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置是否开启手势滑动返回
//        getSwipeBackLayout().setEnableGesture(true);
        //设置是否全屏滑动返回
//        getSwipeBackLayout().setSwipeMode(SwipeBackLayout.FULL_SCREEN_LEFT);
        //设置滑动返回触发位置
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
//            getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏和导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN);
        }
    }

//    public void initToolBar(Toolbar toolbar, boolean isShowBackIcon) {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(isShowBackIcon);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * 进度框,可根据需求自定义
     * @return
     */
    public ProgressDialog getProgressDialog(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        return mProgressDialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    /**
     * 初始化topbar
     * @param context 上下文
     * @param topbar
     */
    public void initTopbar(Context context,View topbar){
        UIUtils.initTopbarForActivity(context,topbar);
    }

    /**
     * 初始化topbar
     * @param context 上下文
     * @param topbar
     * @param isEnableBack 是否激活返回按钮
     */
    public void initTopbar(Context context, View topbar, boolean isEnableBack){
        UIUtils.initTopbarForActivity(context,topbar,isEnableBack);
    }

    /**
     * 初始化topbar,带topbar的颜色
     * @param context 上下文
     * @param topbar
     * @param isEnableBack 是否激活返回按钮
     */
    public void initTopbar(Context context,View topbar,int color,boolean isEnableBack){
        UIUtils.initTopbarForActivity(context,topbar,color,isEnableBack);
    }


    /**
     * 实现点击空白处，软键盘消失事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
