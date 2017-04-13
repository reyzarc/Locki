package com.xtec.locki.utils;

/**
 * Created by 武昌丶鱼 on 2017/3/24.
 * Description:
 */

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_MULTI_PROCESS;

/**
 * Description: SharedPreferences 工具类
 * Created by 冯浩 on 2016/11/30.
 */
public  class PreferenceUtils {

    public static String PREFERENCE_NAME = "locki";

    private PreferenceUtils() {
        throw new AssertionError();
    }



//    public static boolean putString(Context context, String key, String value) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(key, value);
//        return editor.commit();
//    }
//
//
//    public static String getString(Context context, String key) {
//        return getString(context, key, null);
//    }
//
//
//    public static String getString(Context context, String key, String defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        return settings.getString(key, defaultValue);
//    }
//
//
//    public static boolean putInt(Context context, String key, int value) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(key, value);
//        return editor.commit();
//    }
//
//    public static int getInt(Context context, String key) {
//        return getInt(context, key, -1);
//    }
//
//
//    public static int getInt(Context context, String key, int defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        return settings.getInt(key, defaultValue);
//    }
//
//
//    public static boolean putLong(Context context, String key, long value) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putLong(key, value);
//        return editor.commit();
//    }
//
//
//    public static long getLong(Context context, String key) {
//        return getLong(context, key, -1);
//    }
//
//    public static long getLong(Context context, String key, long defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        return settings.getLong(key, defaultValue);
//    }
//
//
//
//    public static boolean putFloat(Context context, String key, float value) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putFloat(key, value);
//        return editor.commit();
//    }
//
//
//    public static float getFloat(Context context, String key) {
//        return getFloat(context, key, -1);
//    }
//
//
//    public static float getFloat(Context context, String key, float defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        return settings.getFloat(key, defaultValue);
//    }
//
//
//    public static boolean putBoolean(Context context, String key, boolean value) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean(key, value);
//        return editor.commit();
//    }
//
//
//    public static boolean getBoolean(Context context, String key) {
//        return getBoolean(context, key, false);
//    }
//
//
//    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        return settings.getBoolean(key, defaultValue);
//    }
//
//    public static void putObject(Context context,String key,Object object){
//        String str = new Gson().toJson(object);
//        putString(context,key,str);
//    }
//
//    public static <T>T getObject(Context context,String key,Class<T> clazz){
//
//        return new Gson().fromJson(getString(context,key),clazz);
//    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }


    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        return settings.getString(key, defaultValue);
    }


    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     *
     * @param context
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    public static boolean getBoolean(Context context, String key,boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        return settings.getBoolean(key,defaultValue);
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context,key,false);
    }

    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        return settings.getLong(key, defaultValue);
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }


    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
        return settings.getInt(key, defaultValue);
    }

}
