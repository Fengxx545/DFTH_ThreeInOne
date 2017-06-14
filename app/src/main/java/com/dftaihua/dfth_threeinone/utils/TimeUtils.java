package com.dftaihua.dfth_threeinone.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static final long ONE_MILLISECOND = 1;
    public static final long ONE_SECOND = 1000 * ONE_MILLISECOND;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    @SuppressLint("SimpleDateFormat")
    public static String getString(long m, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(m);
        return dateFormat.format(date);
    }

    public static long getTimeByTimeStr(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date1 = dateFormat.parse(date);
            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getMAS(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
        min %= 60;
        s %= 60;
        hour %= 24;
        String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hh = hour > 0 ? hour >= 10 ? String.valueOf(hour) : "0" + hour : "";
        return ("".equals(hh) ? "" : hh + ":") + ms + ":" + ss;
    }

    // 获取心电实时时间时间
    public static String getSampingTimeString(long startMeasureTime,int pos, int rate) {
        int sec = pos / rate;
        int time = (int) (startMeasureTime + sec);
        long s = time % 60;
        long min = time / 60;
        long m = min % 60;
        long h = min / 60 >= 24 ? (min / 60 - 24) : min / 60;
        String str = String.format("%02d:%02d:%02d", h, m, s);
        return str;
    }

    public static String getMAShms(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
        int count = hour / 24;
        min %= 60;
        s %= 60;
        hour %= 24;
        hour = count * 24 + hour;
//		String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ms = min > 0 ? min >= 10 ? String.valueOf(min) : "0" + min : "00";
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hh = hour > 0 ? hour >= 10 ? String.valueOf(hour) : "0" + hour : "";
        ms = "".equals(hh) && "00".equals(ms) ? "" : ms;
        return ("".equals(hh) ? "" : hh + "h ") + ("".equals(ms) ? "" : ms + "m ") + ss + "s";
    }

    public static String getHAMAS(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
        hour %= 24;
        min %= 60;
        s %= 60;
        String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hs = hour >= 10 ? String.valueOf(hour) : "0" + hour;
        return hs + ":" + ms + ":" + ss;
    }

    public static String getMeasureTime(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
//		hour %= 24;
        min %= 60;
        s %= 60;
        String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hs = hour >= 10 ? String.valueOf(hour) : "0" + hour;
        return hs + ":" + ms + ":" + ss;
    }

    public static String getIntervalTimeString(long start, long end) {
        long m = end - start;
        int SSS = (int) m % 1000;
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
//		hour %= 24;
        min %= 60;
        s %= 60;
        String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hs = hour >= 10 ? String.valueOf(hour) : "0" + hour;
        return hs + ":" + ms + ":" + ss + "." + SSS + " => " + m + "ms";
    }

    public static int getDayByMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
                || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            return 29;
        }
        return 28;
    }

    /**
     * 获取指定类型的时间数据
     */
    public static int getDateAndType(long birth, int type) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(birth);
        return c.get(type);
    }

    public static boolean isToday(long time) {
        return isToday(time, System.currentTimeMillis());
    }

    public static boolean isToday(long time, long time1) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        int d = c1.get(Calendar.DAY_OF_YEAR);
        int d1 = c.get(Calendar.DAY_OF_YEAR);
        return c1.get(Calendar.YEAR) == c.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR);
    }

    public static long generateTimeByYMD(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND));
        return c.getTimeInMillis();
    }

    public static long getZeroTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c = Calendar.getInstance();
        c.set(year, month, day, 0, 0, 0);
        return c.getTimeInMillis() / 1000 * 1000;
    }

    public static int getDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int getAge(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
    }

    public static boolean isBeforeToday(String date) {
        try {
            java.util.Date nowdate = new java.util.Date();
//		String myString = "2008/09/02";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date d = null;
            d = sdf.parse(date);
            boolean flag = d.before(nowdate);
            if (!flag || isToday(getTimeByStr(date, "yyyy-MM-dd"))) {
//			System.out.print("早于今天") ;
                return false;
            } else {
                return true;
//			System.out.print("晚于今天") ;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getTimeByStr(String time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
//            Logger.e(e, null);
        }
        return date.getTime();
    }

    public static String getTimeStr(long time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    public static long getOneDayStartTime(Calendar currentDate) {
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime().getTime();
    }

    public static long getOneDayEndTime(Calendar currentDate) {
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTime().getTime();
    }

    public static Calendar stringToCalendar(String str) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前时间的前一天时间
     *
     * @param cl
     * @return
     */
    public static Calendar getBeforeDay(Calendar cl) {
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day - 1);
        return cl;
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    public static Calendar getAfterDay(Calendar cl) {
        //使用roll方法进行回滚到后一天的时间
        //cl.roll(Calendar.DATE, 1);
        //使用set方法直接设置时间值
        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day + 1);
        return cl;
    }

    /**
     * 获取当前时间的前一个月时间
     *
     * @param day
     * @return
     */
    public static String getBeforeMonth(String day) {
        Calendar calendar = Calendar.getInstance();
        Date date = DateUtils.stringToDate(day);
        calendar.setTime(date);
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - 1);
        Date beforeMonth = calendar.getTime();
        return DateUtils.dateToString(beforeMonth);
    }

    /**
     * 获取当前时间的前一个月时间
     *
     * @param day
     * @return
     */
    public static String getAfterMonth(String day) {
        Calendar calendar = Calendar.getInstance();
        Date date = DateUtils.stringToDate(day);
        calendar.setTime(date);
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if(dayOfMonth == 31){
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth - 4);
        }
        calendar.set(Calendar.MONTH, month + 1);
        Date nextMonth = calendar.getTime();
        return DateUtils.dateToString(nextMonth);
    }

    public static String getCurrentDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        return df.format(new Date());
    }

    /**
     * 用于提醒用户下次测量时间的时间显示格式00:00:00
     *
     * @param time
     * @return
     */
    public static String getCountTime(long time) {
        int t = (int) (time / 1000);
        int minute = t / 60;
        int h = minute / 60;
        minute = minute % 60;
        int s = t % 60;
        return parseZero(h) + ":" + parseZero(minute) + ":" + parseZero(s);
    }

    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = new SimpleDateFormat(formatType).format(dateOld); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.parse(strTime);
    }

    private static String parseZero(int time) {
        String name = null;
        if (time < 10) {
            name = "0" + String.valueOf(time);
        } else {
            name = String.valueOf(time);
        }
        return name;
    }

    public static void main(String arg[]) {
        System.out.println(getMAShms(171649269L));
        System.out.println(getString(1489744008120L, "yyyy:MM:dd HH:mm:ss"));
        System.out.println(getString(1495528585533L, "HH:mm:ss"));
        System.out.println(getTimeStr(1495528585533L, "yyyy:MM:dd HH:mm:ss"));
//		System.out.println(getMAShms(171649269));
    }
}
