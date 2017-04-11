/**
 * 
 */
package com.xtec.locki.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

import static android.content.ContentValues.TAG;

public class AppUtil {
    
	/**
     * 获取屏幕分辨率
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
		int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
		int result[] = { width, height };
		return result;
	}

	/**
	 * 检测辅助功能是否开启<br>
	 * 方 法 名：isAccessibilitySettingsOn <br>
	 * 创 建 人 <br>
	 * 创建时间：2016-6-22 下午2:29:24 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param mContext
	 * @return boolean
	 */
	public static boolean isAccessibilitySettingsOn(Context mContext,Class<?> serviceClass) {
		int accessibilityEnabled = 0;
		// TestService为对应的服务
		final String service = mContext.getPackageName() + "/" + serviceClass.getCanonicalName();
		L.i(TAG, "service:" + service);
		// com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			L.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (Settings.SettingNotFoundException e) {
			L.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

		if (accessibilityEnabled == 1) {
			L.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			// com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
			if (settingValue != null) {
				mStringColonSplitter.setString(settingValue);
				while (mStringColonSplitter.hasNext()) {
					String accessibilityService = mStringColonSplitter.next();

					L.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
					if (accessibilityService.equalsIgnoreCase(service)) {
						L.v(TAG, "We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			L.v(TAG, "***ACCESSIBILITY IS DISABLED***");
		}
		return false;
	}


	/**
	 * 判断当前程序是否运行在前台
	 *
	 * @return
	 */
	public static boolean isRunningForeground(Context context,String packageName) {
		String topActivityClassName = getTopActivityName(context);
		L.e("gesture",packageName+"--->"+topActivityClassName);
		if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(topActivityClassName)
				&& topActivityClassName.startsWith(packageName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		String pkName = context.getPackageName();
		return pkName;
	}

	/**
	 * 获得栈顶的activity名称
	 * @param context
	 * @return
	 */
	public static String getTopActivityName(Context context) {
		String topActivityClassName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			topActivityClassName = f.getClassName();
		}
		return topActivityClassName;
	}


}
