package com.dftaihua.dfth_threeinone.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MathUtils {
    // LE 表示字节顺序， le == true 表示低字节在前， 否则表示高字节在前
    // short
    private static long SENDTIME = 0;

    public static short bytes2short(byte b[], int offset, boolean le) {
        short s = 0;
        short s0 = (short) (b[offset + 0] & 0xff);// 最低位
        short s1 = (short) (b[offset + 1] & 0xff);
        if (le == true) {
            s1 <<= 8;
            s = (short) (s0 | s1);
        } else {
            s0 <<= 8;
            s = (short) (s0 | s1);
        }
        return s;
    }

    public static short bytes2short(byte b[], boolean le) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        if (le == true)
            s1 <<= 8;
        else
            s0 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static void short2bytes(short s, byte[] b, int offset, boolean le) {
        if (le == true) {
            b[offset + 0] = (byte) (s & 0xFF);
            b[offset + 1] = (byte) ((s >> 8) & 0xFF);
        } else {
            b[offset + 1] = (byte) (s & 0xFF);
            b[offset + 0] = (byte) ((s >> 8) & 0xFF);
        }
    }

    public static byte[] short2bytes(short s, boolean le) {
        byte[] b = new byte[2];
        if (le == true) {
            b[0] = (byte) (s & 0xFF);
            b[1] = (byte) ((s >> 8) & 0xFF);
        } else {
            b[1] = (byte) (s & 0xFF);
            b[0] = (byte) ((s >> 8) & 0xFF);
        }
        return b;
    }

    public static short[] compress_bytes2short(byte b[], int offset, boolean le) {
        short[] s = new short[2];
        short s0 = (short) (b[offset + 0] & 0xff);// 最低位
        short s1 = (short) (b[offset + 1] & 0xff); //
        short s2 = (short) (b[offset + 2] & 0xff);//
        if (le == true) {
            short t = (short) (s1 & 0xf0);
            t <<= 4;
            s[0] = (short) (s0 | t);
            if (s[0] > 2048)
                s[0] -= 4096;
            t = (short) (s1 & 0x0f);
            t <<= 8;
            s[1] = (short) (s2 | t);
            if (s[1] > 2048)
                s[1] -= 4096;
        } else {
            short t = (short) (s1 & 0xf0);
            s0 <<= 4;
            s[0] = 0;// (short)(s0 | t);
            t = (short) (s1 & 0x0f);
            t <<= 8;
            s[1] = 0;// (short)(s2 | s1);
        }
        return s;
    }

    public static short[] ti_compress_bytes2short(byte b[], int offset,
                                                  boolean le) {
        short[] s = new short[2];
        short s0 = (short) (b[offset + 0] & 0xff);//
        short s1 = (short) (b[offset + 1] & 0xff); //
        short s2 = (short) (b[offset + 2] & 0xff);//
        short s3 = (short) (b[offset + 3] & 0xff);//
        short s4 = (short) (b[offset + 4] & 0xff); //
        short s5 = (short) (b[offset + 5] & 0xff);//
        if (le == true) {
            s[0] = (short) (s0 * 256 + s1);
            s[1] = (short) (s3 * 256 + s4);
        } else {
            short t = (short) (s1 & 0xf0);
            s0 <<= 4;
            s[0] = 0;// (short)(s0 | t);
            t = (short) (s1 & 0x0f);
            t <<= 8;
            s[1] = 0;// (short)(s2 | s1);
        }
        return s;
    }

    // int
    // 字节数组到整数的转换
    public static byte[] int2bytes(int n, boolean le) {
        byte[] ab = new byte[4];
        if (le == true) {
            ab[0] = (byte) (0xff & n);
            ab[1] = (byte) ((0xff00 & n) >> 8);
            ab[2] = (byte) ((0xff0000 & n) >> 16);
            ab[3] = (byte) ((0xff000000 & n) >> 24);
        } else {
            ab[3] = (byte) (0xff & n);
            ab[2] = (byte) ((0xff00 & n) >> 8);
            ab[1] = (byte) ((0xff0000 & n) >> 16);
            ab[0] = (byte) ((0xff000000 & n) >> 24);
        }
        return ab;
    }

    public static void int2bytes(int n, byte[] b, int offset, boolean le) {
        if (le == true) {
            b[offset + 0] = (byte) (0xff & n);
            b[offset + 1] = (byte) ((0xff00 & n) >> 8);
            b[offset + 2] = (byte) ((0xff0000 & n) >> 16);
            b[offset + 3] = (byte) ((0xff000000 & n) >> 24);
        } else {
            b[offset + 3] = (byte) (0xff & n);
            b[offset + 2] = (byte) ((0xff00 & n) >> 8);
            b[offset + 1] = (byte) ((0xff0000 & n) >> 16);
            b[offset + 0] = (byte) ((0xff000000 & n) >> 24);
        }

    }

    public static int bytes2int(byte b[], boolean le) {
        int s = 0;
        if (le == true) {
            s = ((((b[3] & 0xff) << 8 | (b[2] & 0xff)) << 8) | (b[1] & 0xff)) << 8
                    | (b[0] & 0xff);
        } else {
            s = ((((b[0] & 0xff) << 8 | (b[1] & 0xff)) << 8) | (b[2] & 0xff)) << 8
                    | (b[3] & 0xff);
        }
        return s;
    }

    public static int bytes2int(byte b[], int offset, boolean le) {
        int s = 0;
        if (le == true) {
            s = ((((b[offset + 3] & 0xff) << 8 | (b[offset + 2] & 0xff)) << 8) | (b[offset + 1] & 0xff)) << 8
                    | (b[offset + 0] & 0xff);
        } else {
            s = ((((b[offset + 0] & 0xff) << 8 | (b[offset + 1] & 0xff)) << 8) | (b[offset + 2] & 0xff)) << 8
                    | (b[offset + 3] & 0xff);
        }
        return s;
    }

    // float
    public static float getFloat(byte[] b, int index, boolean le)
            throws Exception {
        int i = 0;
        if (le == true) {
            i = ((((b[index + 3] & 0xff) << 8 | (b[index + 2] & 0xff)) << 8) | (b[index + 1] & 0xff)) << 8
                    | (b[index + 0] & 0xff);
        } else {
            i = ((((b[index + 0] & 0xff) << 8 | (b[index + 1] & 0xff)) << 8) | (b[index + 2] & 0xff)) << 8
                    | (b[index + 3] & 0xff);
        }
        return Float.intBitsToFloat(i);
    }

    public static float bytes2float(byte b[], int offset, boolean le) {
        float f = 0f;
        try {
            f = getFloat(b, offset, le);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }

    public static float bytes2float(byte b[], boolean le) {
        float f = 0f;
        try {
            f = getFloat(b, 0, le);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }

    // 字节转换到字符
    public static char byte2char(byte b) {
        return (char) b;
    }

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从字节数组到十六进制字符串转换
    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static byte HexString2Byte(String hexstr) {
        byte b;
        int j = 0;
        char c0 = hexstr.charAt(j++);
        char c1 = hexstr.charAt(j++);
        b = (byte) ((parse(c0) << 4) | parse(c1));
        return b;
    }

    // 获取当前时间
    public static String getDateString(Date d) {
        String str = Integer.toString(d.getYear() + 1900);
        str += String.format("%02d", d.getMonth() + 1);
        str += String.format("%02d", d.getDate());
        return str;
    }

    // 获取当前时间
    public static String getTimeString(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        String str = df.format(d);
        return str;
    }

    // 获取当前时间
    public static String getTimeSecondString(long mmsec) {
        long sec = mmsec / 1000;
        long s = sec % 60;
        long min = sec / 60;
        long m = min % 60;
        long h = min / 60;
        String str = String.format("%02d:%02d:%02d", h, m, s);
        return str;
    }

    public static String getTimeString(long mmsec) {
        long sec = mmsec / 1000;
        long s = sec % 60;
        long min = sec / 60;
        long m = min % 60;
        long hour = (min / 60) + 8;
        long h = hour % 24;
        String str = String.format("%02d:%02d:%02d", h, m, s);
        return str;
    }

    // 获取当前时间
    public static String getSampingTimeString(int pos, int rate) {
        int sec = pos / rate;
        long s = sec % 60;
        long min = sec / 60;
        long m = min % 60;
        long h = min / 60;
        String str = String.format("%02d:%02d:%02d", h, m, s);
        return str;
    }

    public static String getTotalTimeString(long mmsecond) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(mmsecond);
        String str = df.format(date);
        return str;
    }

    public static void float2bytes(float f, byte[] b, int offset, boolean le) {
        int i = Float.floatToIntBits(f);
        int2bytes(i, b, offset, le);
    }

    public static byte[] float2bytes(float f, boolean le) {
        int i = Float.floatToIntBits(f);
        return int2bytes(i, le);
    }

    public static byte[] long2bytes(long n, boolean le) {
        byte[] bits = new byte[8];
        if (le == true) {
            for (int i = 0; i < 8; i++)
                bits[i] = (byte) (n >> (56 - 8 * i));
        } else {
            for (int i = 0; i < 8; i++)
                bits[7 - i] = (byte) (n >> (56 - 8 * i));
        }
        return bits;

    }

    public static long bytes2long(byte[] bits, int offset, boolean le) {
        long l = 0;
        if (le == true) {
            for (int i = 0; i < 8; i++)
                l |= ((long) (bits[offset + i]) << (56 - 8 * i));
        } else {
            for (int i = 0; i < 8; i++)
                l |= ((long) (bits[offset + 7 - i]) << (56 - 8 * i));
        }
        return l;

    }

    /**
     * 判断一个字符串是否全是数字
     */

    public static boolean isNumber(String str) {
        if (str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static byte xorAdd(byte[] bytes) {
        byte b = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            b ^= bytes[i];
        }
        return b;
    }

    public static float getFactor(float startx, float starty, float stopx, float stopy) {
        return (stopy - starty) / (starty - startx);
    }

    /**
     * 获取指定格式的时间数据
     *
     * @param time    毫秒级的时间
     * @param formate 定义的时间格式
     * @return
     */
    public static String getStrTimeByLong(long time, String formate) {
        if (time == 0)
            return "";
        try {
            SimpleDateFormat df = new SimpleDateFormat(formate);
            Date date = new Date(time);
            String str = df.format(date);
            return str;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取格式为"MM/dd/HH:mm"的时间
     *
     * @param time
     * @return
     */
    public static String getStartEndTimeByLong(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);

        Date date = new Date(year - 1900, month, day, hh, mm);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String str = df.format(date);
        return str;
    }

    /**
     * 获取时间点以小时为单位
     *
     * @param time 秒级的时间数据
     * @return
     */
    public static float getDrawTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time * 1000L));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);
        return hh + ((float) mm) / 60;
    }

    /**
     * 获取日期格式为"MM/dd"
     *
     * @param time 秒级的时间数据
     * @return
     */
    public static String getMouthAndDay(long time) {
        String formate = "MM/dd";
        time = time * 1000;
        SimpleDateFormat df = new SimpleDateFormat(formate);
        Date date = new Date(time);
        String str = df.format(date);
        return str;
    }

    /**
     * 获取long的数据 通过 如"yyyyMMdd"格式的
     *
     * @param time
     * @return
     */
    public static long getLongTimeByStr(String time, String formate) {
        long timelong = 0;
        SimpleDateFormat df = new SimpleDateFormat(formate);
        try {
            Date date = df.parse(time);
            timelong = date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timelong;
    }

    public static void setSendTime(long time) {
        SENDTIME = time;
    }

    public static long getHandleTime() {
        return System.currentTimeMillis() - SENDTIME;
    }

    /**
     * 用于提醒用户下次测量时间的时间显示格式00:00:00
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);
        int ss = c.get(Calendar.SECOND);
        return parseZero(hh) + ":" + parseZero(mm) + ":" + parseZero(ss);
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

    /**
     * 获取一天的开始时间 00:00:00
     *
     * @param time
     * @return
     */
    public static int getDayStartTime(int time) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time * 1000L));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Date date = new Date(year - 1900, month, day);
        int currenttime = (int) (date.getTime() / 1000);
        return currenttime;
    }


    public static long getTimeBystr(int year, int month, int day) {
        Date date = new Date(year - 1900, month, day);
        long currenttime = date.getTime();
        return currenttime;
    }

    public static long getTimeByStr2(int year, int month, int day, int hour, int minute) {
        Date date = new Date(year - 1900, month, day, hour, minute);
        long currenttime = date.getTime();
        return currenttime;
    }

    public static float mmHgToKpa(int mmhg) {
        if (mmhg > 0) {
            float value = (float) (mmhg / 7.5) * 10;
            return (((int) value) / (float) 10.0);
        }
        return 0;
    }

    public static int KpaToMmHg(float kpa) {
        if (kpa > 0) {
            return (int) (kpa * 7.5);
        }
        return 0;
    }

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, 3);
        long time = c.getTimeInMillis();
        System.out.println("time=" + time);
    }

    public static long getLongTime(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTimeInMillis();
    }


    public static String getStringToHex(byte[] buffer) {
        StringBuffer buffer2 = new StringBuffer();
        for (int i = 0; i < buffer.length; i++) {
            char c1 = (char) (0x0f & (buffer[i] >> 4));
            char c2 = (char) (0x0f & (buffer[i]));
            if (c1 >= 0 && c1 <= 9) {
                c1 += 48;
            } else {
                c1 += 87;
            }
            if (c2 >= 0 && c2 <= 9) {
                c2 += 48;
            } else {
                c2 += 87;
            }
            buffer2.append(c1).append(c2);
        }
        return buffer2.toString();
    }


/*    public static String getDeadLineTime(long time) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= time) {
            return DfthClinicApplication.getStringRes(R.string.device_over_time);
        }
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = dateFormat.format(date);
        str = DfthClinicApplication.getStringRes(R.string.over_time_to) + str + DfthClinicApplication.getStringRes(R.string.over_time_end);
        return str;
    }*/

    public static String getStringTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = dateFormat.format(date);
        return str;
    }

    public static boolean checkBoolsIsSame(boolean[] bools, boolean is) {
        for (int i = 0; i < bools.length; i++) {
            if (bools[i] != is) {
                return false;
            }
        }
        return true;
    }

    public static int search(boolean [] bools, boolean key){
        for(int i = 0; i < bools.length; i++){
            if(bools[i] == key)
                return i;
        }
        return -1;
    }

    public static boolean search(boolean [] bools, boolean key,int num){
        int number = 0;
        for(int i = 0; i < bools.length; i++){
            number = bools[i] == key ? number  + 1 : number;
        }
        return number == num;
    }


    public static int binarySearch(boolean[] array, boolean value) {
        for(int i = 0; i < array.length; i++){
            if(array[i] == value){
                return i;
            }
        }
        return -1;
    }

    public static boolean judgeStringVersionBig(String version1 , String version2) throws Exception{
        String[] version1s = version1.substring(1, version1.length()).split("\\.");
        String[] version2s = version2.substring(1, version2.length()).split("\\.");
        int i = 0 , s1 = 0 , s2 = 0;;
        while(i < version1s.length && i < version2s.length){
            s1 = Integer.parseInt(version1s[i]);
            s2 = Integer.parseInt(version2s[i]);
            if(s1 == s2) {
                i++;
                continue;
            }
            if(s1 > s2){
                return true;
            }
            if(s1 < s2){
                return false;
            }
        }
        s1 = Integer.parseInt(version1s[version1s.length - 1]);
        return (version1s.length > version2s.length && s1 != 0)? true : false;
    }
}
