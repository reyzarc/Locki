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
     * ��ȡ��Ļ�ֱ���
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// �ֻ���Ļ�Ŀ��
		int height = windowManager.getDefaultDisplay().getHeight();// �ֻ���Ļ�ĸ߶�
		int result[] = { width, height };
		return result;
	}

	/**
	 * ��⸨�������Ƿ���<br>
	 * �� �� ����isAccessibilitySettingsOn <br>
	 * �� �� �� <br>
	 * ����ʱ�䣺2016-6-22 ����2:29:24 <br>
	 * �� �� �ˣ� <br>
	 * �޸����ڣ� <br>
	 * @param mContext
	 * @return boolean
	 */
	public static boolean isAccessibilitySettingsOn(Context mContext,Class<?> serviceClass) {
		int accessibilityEnabled = 0;
		// TestServiceΪ��Ӧ�ķ���
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
	 * �жϵ�ǰ�����Ƿ�������ǰ̨
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
	 * ��ð���
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		String pkName = context.getPackageName();
		return pkName;
	}

	/**
	 * ���ջ����activity����
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
