package com.xtec.locki.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xtec.locki.Constant;
import com.xtec.locki.R;
import com.xtec.locki.adapter.BrowseApplicationInfoAdapter;
import com.xtec.locki.model.AppInfo;
import com.xtec.locki.service.LockService;
import com.xtec.locki.utils.PreferenceUtils;

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

    private List<AppInfo> mlistAppInfo = null;
    private PackageManager pm;

    private List<String> mLockList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        radioGroup.setOnCheckedChangeListener(this);
        PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT, true);
        startService(new Intent(this, LockService.class));

        mlistAppInfo = new ArrayList<AppInfo>();
//        queryAppInfo(); // 查询所有应用程序信息
//        queryAllAppInfo();//查询所有应用信息
        queryCustomAppInfo();//查询用户安装的应用
        BrowseApplicationInfoAdapter browseAppAdapter = new BrowseApplicationInfoAdapter(
                this, mlistAppInfo, new BrowseApplicationInfoAdapter.OnStatusChangedListener() {
            @Override
            public void onStatusChange(AppInfo appInfo) {
                if(appInfo.isOpened()){
                    mLockList.add(appInfo.getPkgName());
                }else{
                    mLockList.remove(appInfo.getPkgName());
                }
                for (int i = 0; i < mLockList.size(); i++) {
                    Log.e("reyzarc",i+"----->"+mLockList.get(i).toString());
                }
            }
        });
        lv.setAdapter(browseAppAdapter);
        lv.setOnItemClickListener(this);
    }

    private void queryCustomAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        mlistAppInfo.clear();
        AppInfo appTitle = new AppInfo();
        appTitle.setAppLabel("用户安装");
        appTitle.setType(AppInfo.SECTION);
        mlistAppInfo.add(appTitle);
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                mlistAppInfo.add(getAppInfo(app));
            }
        }

        //查询系统应用
        querySystemAppInfo();
    }

    private void querySystemAppInfo() {
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        AppInfo appTitle = new AppInfo();
        appTitle.setAppLabel("系统应用");
        appTitle.setType(AppInfo.SECTION);
        mlistAppInfo.add(appTitle);
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                mlistAppInfo.add(getAppInfo(app));
            }
        }
    }

    private void queryAllAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序

        mlistAppInfo.clear();
        for (ApplicationInfo app : listAppcations) {
            mlistAppInfo.add(getAppInfo(app));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.rb_fingerprint://指纹解锁
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.FINGERPRINT, true);
                break;
            case R.id.rb_gesture_pwd://手势密码解锁
                startActivity(new Intent(this, CreateGestureActivity.class));
//                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.GESTURE, true);
                break;
            case R.id.rb_number_pwd://数字密码解锁
            default:
                PreferenceUtils.putString(this, Constant.LOCK_METHOD, Constant.NUMBER, true);
                break;
        }
    }

    // 点击跳转至该应用程序
    public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long arg3) {
        // TODO Auto-generated method stub
        if(mlistAppInfo.get(position).getType()==AppInfo.SECTION){
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
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
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
                mlistAppInfo.add(appInfo); // 添加至列表中
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
        super.onStop();
        finish();
    }
}
