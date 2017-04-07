/**
 * 
 */
package com.xtec.locki.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

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
		Log.i(TAG, "service:" + service);
		// com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (Settings.SettingNotFoundException e) {
			Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

		if (accessibilityEnabled == 1) {
			Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			// com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
			if (settingValue != null) {
				mStringColonSplitter.setString(settingValue);
				while (mStringColonSplitter.hasNext()) {
					String accessibilityService = mStringColonSplitter.next();

					Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
					if (accessibilityService.equalsIgnoreCase(service)) {
						Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
		}
		return false;
	}
    
}
