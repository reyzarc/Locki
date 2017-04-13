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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    private List<AppInfo> mListAppInfo = null;
    private PackageManager pm;

    private List<String> mLockList = new ArrayList<>();
    private boolean hasList;

    private FastDialog mDialog;
    private Gson mGson;
    private final int REQUEST_GESTURE = 1;
    private final int REQUEST_NUMBER = 2;
    private final int REQUEST_SAFEGUARD = 3;
    private final int REQUEST_VERIFY = 4;
    private String mLockMethod;

    private DevicePolicyManager devicePolicyManager;
    public ComponentName componentName;//权限监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar(toolbar, false);
        //判断是否是第一次打开应用,如果是第一次,则引导用户设置保护密码
        if (PreferenceUtils.getBoolean(this, Constant.IS_FIRST, true)) {//第一次
            startActivityForResult(new Intent(this, SafeguardActivity.class), REQUEST_SAFEGUARD);
        } else {//不是第一次,则需要验证身份才能进入应用
            startActivityForResult(new Intent(this, VerifyIdentityActivity.class), REQUEST_VERIFY);
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
        if (!TextUtils.isEmpty(str)) {
            hasList = true;
            mLockList = mGson.fromJson(str, new TypeToken<List<String>>() {
            }.getType());
            L.e("reyzarc", "lock list is---->" + mLockList.toString());
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

    private void showEnableDialog() {
        new FastDialog(this)
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
                }).create().show();
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
            if (mDialog != null && mDialog.isShowing()) {
                return;
            }
            mDialog = new FastDialog(this)
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
            mDialog.show();
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
        L.e("reyzarc", "=======>" + mLockList.toString());
        String str = mGson.toJson(mLockList);
        L.e("reyzarc", "数据是---->" + str);
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
                            PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.GESTURE);
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
                            PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.NUMBER);
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
                            PreferenceUtils.putBoolean(this,Constant.IS_FIRST,false);
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
}
