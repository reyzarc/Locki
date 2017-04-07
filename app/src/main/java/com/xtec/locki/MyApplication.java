package com.xtec.locki;

import android.app.Application;
import android.util.Log;

/**
 * Created by 武昌丶鱼 on 2017/4/6.
 * Description:
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("reyzarc","application oncreate is running....");
        //初始化保存数据的provider
//        ProviderUtil.init(this);
    }

}
