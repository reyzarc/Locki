package com.xtec.locki.model;

/**
 * Created by 武昌丶鱼 on 2017/4/1.
 * Description:
 */


import android.content.Intent;
import android.graphics.drawable.Drawable;
//Model类,用来存储应用程序信息
public class AppInfo {
    public  static final int ITEM =0;
    public static final int SECTION =1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String appLabel;    //应用程序标签
    private Drawable appIcon ;  //应用程序图像
    private Intent intent ;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
    private String pkgName ;    //应用程序所对应的包名

    public AppInfo(){}

    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public Intent getIntent() {
        return intent;
    }
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }
}