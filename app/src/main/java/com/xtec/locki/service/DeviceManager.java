package com.xtec.locki.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 武昌丶鱼 on 2017/4/13.
 * Description:
 */

public class DeviceManager extends DeviceAdminReceiver{
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);

    }
}
