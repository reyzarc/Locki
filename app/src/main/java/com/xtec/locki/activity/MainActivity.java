package com.xtec.locki.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.BrowseApplicationInfoAdapter;
import com.xtec.locki.model.AppInfo;
import com.xtec.locki.service.DeviceManager;
import com.xtec.locki.service.LockService;
import com.xtec.locki.utils.AppUtil;
import com.xtec.locki.utils.L;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.utils.T;
import com.xtec.locki.widget.FastDialog;
import com.xtec.locki.widget.MultiStateView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.halfbit.pinnedsection.PinnedSectionListView;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {

    @BindView(R.id.rb_fingerprint)
    RadioButton rbFingerprint;
    @BindView(R.id.rb_number_pwd)
    RadioButton rbNumberPwd;
    @BindView(R.id.rb_gesture_pwd)
    RadioButton rbGesturePwd;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.lv)
    PinnedSectionListView lv;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mv_state)
    MultiStateView mvState;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.rl_modify_app_pwd)
    RelativeLayout rlModifyAppPwd;
    @BindView(R.id.rl_modify_number_pwd)
    RelativeLayout rlModifyNumberPwd;
    @BindView(R.id.rl_add_plan)
    RelativeLayout rlAddPlan;

    private List<AppInfo> mListAppInfo = null;
    private PackageManager pm;

    private List<String> mLockList = new ArrayList<>();
    private boolean hasList;

    private FastDialog mServiceEnableDialog;
    private FastDialog mDeviceManageDialog;
    private FastDialog mTipsDialog;
    private Gson mGson;
    private final int REQUEST_GESTURE = 1;
    private final int REQUEST_NUMBER = 2;
    private final int REQUEST_SAFEGUARD = 3;
    private final int REQUEST_VERIFY = 4;
    private String mLockMethod;

    private DevicePolicyManager devicePolicyManager;
    public ComponentName componentName;//权限监听器

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSwipeBackLayout().setEnableGesture(false);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

//        initToolBar(toolbar, false);
        //判断是否是第一次打开应用,如果是第一次,则引导用户设置保护密码
        if (PreferenceUtils.getBoolean(MainActivity.this, Constant.IS_FIRST, true)) {//第一次
            showTipsDialog();
            startActivityForResult(new Intent(MainActivity.this, SafeguardActivity.class), REQUEST_SAFEGUARD);
        } else {//不是第一次,则需要验证身份才能进入应用
            startActivityForResult(new Intent(MainActivity.this, VerifyIdentityActivity.class), REQUEST_VERIFY);
        }

        //检查是否激活了设备管理器,防止应用被卸载
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, DeviceManager.class);//用广播接收器实例化一个系统组件

        mGson = new Gson();
        pm = getPackageManager();

        //判断手机是否支持指纹解锁
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
        if (!fingerprintManager.isHardwareDetected()) {//不支持指纹
            rbFingerprint.setVisibility(View.GONE);
        } else {//支持指纹识别
            rbFingerprint.setVisibility(View.VISIBLE);
        }

        mListAppInfo = new ArrayList<>();

        //获取之前保存的列表
        String str = PreferenceUtils.getString(this, Constant.LOCK_LIST);
        L.e("reyzarc", "sfafafafagst is---->" + str);
        if (!TextUtils.isEmpty(str)) {//这里两次判断是因为第一次str必为空,而如果有列表,然后再删除,则str为json字符串[]
            mLockList = mGson.fromJson(str, new TypeToken<List<String>>() {
            }.getType());
            L.e("reyzarc", "lock list is---->" + mLockList.toString());
            if (!mLockList.isEmpty()) {
                hasList = true;
                //将加锁的列表显示在最前端
                mListAppInfo.clear();
                AppInfo appTitle = new AppInfo();
                appTitle.setAppLabel("已加锁应用");
                appTitle.setType(AppInfo.SECTION);
                mListAppInfo.add(appTitle);
                for (int i = 0; i < mLockList.size(); i++) {
                    ApplicationInfo info = AppUtil.getAppInfoByPackageName(this, mLockList.get(i));
                    if (info != null) {
                        AppInfo appInfo = getAppInfo(info);
                        appInfo.setOpened(true);
                        mListAppInfo.add(appInfo);
                    }
                }
            } else {
                mListAppInfo.clear();
            }
        }


        radioGroup.setOnCheckedChangeListener(this);
        //获取保存的解锁方式,没有保存则默认数字
        mLockMethod = PreferenceUtils.getString(this, Constant.LOCK_METHOD, Constant.NUMBER);
        startService(new Intent(this, LockService.class));

//        queryAppInfo(); // 查询所有应用程序信息
//        queryAllAppInfo();//查询所有应用信息
        queryCustomAppInfo();//查询用户安装的应用
        BrowseApplicationInfoAdapter browseAppAdapter = new BrowseApplicationInfoAdapter(
                this, mListAppInfo, new BrowseApplicationInfoAdapter.OnStatusChangedListener() {
            @Override
            public void onStatusChange(AppInfo appInfo) {
                if (appInfo.isOpened()) {//开启锁
                    mLockList.add(appInfo.getPkgName());
                } else {//关闭锁
                    mLockList.remove(appInfo.getPkgName());
                }
            }
        });
        lv.setAdapter(browseAppAdapter);
        lv.setOnItemClickListener(this);
    }

    /**
     * 加入白名单dialog
     */
    private void showTipsDialog() {
        if (mTipsDialog != null && mTipsDialog.isShowing()) {
            return;
        }
        mTipsDialog = new FastDialog(this)
                .setTitle("提示")
                .setContent("为了保证应用锁能正常运行,请在多任务列表和清理软件中手动将本应用锁定(加入应用清理白名单)")
                .setSingleButton("知道了", new FastDialog.OnClickListener() {
                    @Override
                    public void onClick(FastDialog dialog) {
                        dialog.dismiss();
                    }
                }).create();
        mTipsDialog.show();
    }

    /**
     * 设备管理器dialog
     */
    private void showEnableDialog() {
        if (mDeviceManageDialog != null && mDeviceManageDialog.isShowing()) {
            return;
        }
        mDeviceManageDialog = new FastDialog(this)
                .setTitle("激活设备管理器")
                .setContent("使用锁屏需要激活设备管理器功能,请按提示操作")
                .setNegativeButton("取消", new FastDialog.OnClickListener() {
                    @Override
                    public void onClick(FastDialog dialog) {
                        finish();
                    }
                })
                .setPositiveButton("去激活", new FastDialog.OnClickListener() {
                    @Override
                    public void onClick(FastDialog dialog) {
                        Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);//激活系统设备管理器
                        i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);//注册系统组件
                        startActivity(i);
                    }
                }).create();
        mDeviceManageDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查辅助功能中的服务是否开启
        checkServiceEnable();
        checkDeviceEnable();
    }

    private void checkDeviceEnable() {
        boolean flagChanged = devicePolicyManager.isAdminActive(componentName);//判断这个应用是否激活了设备管理器
        if (flagChanged) {

        } else {
            showEnableDialog();
        }
    }

    private void checkServiceEnable() {
        if (!AppUtil.isAccessibilitySettingsOn(this, LockService.class)) {
            if (mServiceEnableDialog != null && mServiceEnableDialog.isShowing()) {
                return;
            }
            mServiceEnableDialog = new FastDialog(this)
                    .setTitle("提示")
                    .setContent("应用锁需要开启辅助功能才能正常运行,请前往设置->无障碍或者设置->高级->辅助功能中,找到LockService并开启)")
                    .setPositiveButton("前往设置", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {
                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("残忍拒绝", new FastDialog.OnClickListener() {
                        @Override
                        public void onClick(FastDialog dialog) {
                            finish();
                        }
                    }).create();
            mServiceEnableDialog.show();
        }
    }

    /**
     * 查询用户安装的第三方应用
     */
    private void queryCustomAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listApplications = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listApplications,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        if (!hasList) {
            mListAppInfo.clear();
        }
        AppInfo appTitle = new AppInfo();
        appTitle.setAppLabel("用户安装");
        appTitle.setType(AppInfo.SECTION);
        mListAppInfo.add(appTitle);
        for (ApplicationInfo app : listApplications) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                AppInfo appInfo = getAppInfo(app);
                boolean isLocked = false;
                if (hasList) {//有历史加锁列表
                    for (int i = 0; i < mLockList.size(); i++) {
                        if (TextUtils.equals(app.packageName, mLockList.get(i))) {
                            //当前应用已经在已加锁列表显示,则不再添加到下面
                            appInfo.setOpened(true);
                            isLocked = true;
                            break;
                        }
                    }
                    if (!isLocked) {
                        mListAppInfo.add(appInfo);
                    }
                } else {
                    mListAppInfo.add(appInfo);
                }
            }
        }

        //查询系统应用
        querySystemAppInfo();
    }

    /**
     * 查询系统自带应用
     */
    private void querySystemAppInfo() {
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        AppInfo appTitle = new AppInfo();
        appTitle.setAppLabel("系统应用");
        appTitle.setType(AppInfo.SECTION);
        mListAppInfo.add(appTitle);
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //判断以前是否有设置开启过
                AppInfo appInfo = getAppInfo(app);
                boolean isLocked = false;
                if (hasList) {
                    for (int i = 0; i < mLockList.size(); i++) {
                        if (TextUtils.equals(app.packageName, mLockList.get(i))) {
                            appInfo.setOpened(true);
                            isLocked = true;
                            break;
                        }
                    }
                    if (!isLocked) {
                        mListAppInfo.add(appInfo);
                    }
                } else {
                    mListAppInfo.add(appInfo);
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //显示内容
                mvState.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            }
        }, 5000);

    }

    /**
     * 查询所有应用
     */
    private void queryAllAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序

        mListAppInfo.clear();
        for (ApplicationInfo app : listAppcations) {
            mListAppInfo.add(getAppInfo(app));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.rb_fingerprint://指纹解锁
                mLockMethod = Constant.FINGERPRINT;
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT);
                break;
            case R.id.rb_gesture_pwd://手势密码解锁
                startActivityForResult(new Intent(this, CreateGestureActivity.class), REQUEST_GESTURE);
//                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.GESTURE, true);
                break;
            case R.id.rb_number_pwd://数字密码解锁
                startActivityForResult(new Intent(this, CreateNumberPwdActivity.class), REQUEST_NUMBER);
//                mLockMethod = Constant.NUMBER;
//                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.NUMBER);
                break;
        }
    }

    // 点击跳转至该应用程序
    public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long arg3) {
        // TODO Auto-generated method stub
        if (mListAppInfo.get(position).getType() == AppInfo.SECTION) {
            return;
        }
//        Intent intent = mlistAppInfo.get(position).getIntent();
//        startActivity(intent);
    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public void queryAppInfo() {
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mListAppInfo != null) {
            mListAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                mListAppInfo.add(appInfo); // 添加至列表中
            }
        }
    }

    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(ApplicationInfo app) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        appInfo.setType(AppInfo.ITEM);
        return appInfo;
    }


    @Override
    protected void onStop() {
        //保存解锁方式
        PreferenceUtils.putString(this, Constant.LOCK_METHOD, mLockMethod);
        //保存加锁列表
        String str = mGson.toJson(mLockList);
        if (!TextUtils.isEmpty(str)) {
            PreferenceUtils.putString(this, Constant.LOCK_LIST, str);
            sendBroadcast(new Intent(Constant.ACTION_UPDATE_UNLOCK_LIST));
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GESTURE && resultCode == Constant.RESULT_GESTURE) {
            if (data != null) {
                String str = data.getStringExtra("status");
                if (!TextUtils.isEmpty(str)) {
                    switch (str) {
                        case "cancel"://取消
                            //还原状态
                            switch (mLockMethod) {
                                case Constant.NUMBER:
                                    rbNumberPwd.setChecked(true);
                                    break;
                                case Constant.FINGERPRINT:
                                    rbFingerprint.setChecked(true);
                                    break;
                            }
                            break;
                        case "success"://成功
                            mLockMethod = Constant.GESTURE;
                            rbGesturePwd.setChecked(true);
                            break;
                    }
                }
            }
        } else if (requestCode == REQUEST_NUMBER && resultCode == Constant.RESULT_NUMBER) {
            if (data != null) {
                String str = data.getStringExtra("status");
                if (!TextUtils.isEmpty(str)) {
                    switch (str) {
                        case "cancel"://取消
                            //还原状态
                            switch (mLockMethod) {
                                case Constant.GESTURE:
                                    rbGesturePwd.setChecked(true);
                                    break;
                                case Constant.FINGERPRINT:
                                    rbFingerprint.setChecked(true);
                                    break;
                            }
                            break;
                        case "success"://成功
                            mLockMethod = Constant.NUMBER;
                            rbNumberPwd.setChecked(true);
                            break;
                    }
                }
            }
        } else if (requestCode == REQUEST_SAFEGUARD && resultCode == Constant.RESULT_SAFEGUARD) {
            if (data != null) {
                String str = data.getStringExtra("status");
                if (!TextUtils.isEmpty(str)) {
                    switch (str) {
                        case "cancel"://取消
                            finish();
                            break;
                        case "success"://成功
                            PreferenceUtils.putBoolean(this, Constant.IS_FIRST, false);
                            T.showShort(this, "设置成功");
                            break;
                    }
                }
            }
        } else if (requestCode == REQUEST_VERIFY && resultCode == Constant.RESULT_VERIFY) {
            if (data != null) {
                String str = data.getStringExtra("status");
                if (!TextUtils.isEmpty(str)) {
                    switch (str) {
                        case "cancel"://取消
                            finish();
                            break;
                        case "success"://成功
                            T.showShort(this, "验证成功");
                            break;
                    }
                }
            }
        }
    }

    @OnClick({R.id.rl_add_plan,R.id.toolbar_right, R.id.rl_modify_app_pwd, R.id.rl_modify_number_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_right://帮助
                showTipsDialog();
                break;
            case R.id.rl_modify_app_pwd://修改app验证密码
                drawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(this, SafeguardActivity.class));
                break;
            case R.id.rl_modify_number_pwd://修改数字密码
                drawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(this, CreateNumberPwdActivity.class));
                break;
            case R.id.rl_add_plan://添加计划
                drawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(this, TimePlanActivity.class));
                break;
        }
    }
}
