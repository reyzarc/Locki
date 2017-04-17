package com.xtec.locki.utils;

import android.content.Context;

import com.xtec.locki.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by 武昌丶鱼 on 2017/4/11.
 * Description:
 */

public class DateUtils {

    /**
     * 时间类型转换
     *
     * @return
     */
    public static String FormatStringTimeHM(long time) {
        Date date = new Date(time);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * 时间类型转换
     *
     * @return
     */
    public static String FormatStringTimeMD(long time) {
        Date date = new Date(time);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }


    public static String getDate(Context context) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String weekDay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(weekDay)) {
            weekDay = "天";
        } else if ("2".equals(weekDay)) {
            weekDay = "一";
        } else if ("3".equals(weekDay)) {
            weekDay = "二";
        } else if ("4".equals(weekDay)) {
            weekDay = "三";
        } else if ("5".equals(weekDay)) {
            weekDay = "四";
        } else if ("6".equals(weekDay)) {
            weekDay = "五";
        } else if ("7".equals(weekDay)) {
            weekDay = "六";
        }
        return String.format(context.getResources().getString(R.string.date_and_week), month, day, weekDay);

    }

    /**
     * 将时间差(s)转换成x分y秒,例如 125s->2min5s
     *
     * @return
     */
    public static String FormatStringTimeMS(int time) {
        int min;
        int sec;
        if (time > 0) {
            min = time / 60;
            sec = time % 60;
            if (min > 0) {
                return min +"分" + sec + "秒";
            } else {
                return sec + "秒";
            }
        }
        return "";
    }
}
