package com.xtec.locki.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.BrowseApplicationInfoAdapter;
import com.xtec.locki.model.AppInfo;
import com.xtec.locki.service.LockService;
import com.xtec.locki.utils.AppUtil;
import com.xtec.locki.utils.L;
import com.xtec.locki.utils.PreferenceUtils;
import com.xtec.locki.widget.FastDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.halfbit.pinnedsection.PinnedSectionListView;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {

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

    private List<AppInfo> mListAppInfo = null;
    private PackageManager pm;

    private List<String> mLockList = new ArrayList<>();
    private boolean hasList;

    private FastDialog mDialog;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT);
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

    @Override
    protected void onResume() {
        super.onResume();
        //检查辅助功能中的服务是否开启
        checkServiceEnable();
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
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT);
                break;
            case R.id.rb_gesture_pwd://手势密码解锁
                startActivity(new Intent(this, CreateGestureActivity.class));
//                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.GESTURE, true);
                break;
            case R.id.rb_number_pwd://数字密码解锁
            default:
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.NUMBER);
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
}
