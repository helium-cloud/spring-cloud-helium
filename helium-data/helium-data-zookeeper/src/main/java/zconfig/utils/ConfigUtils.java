package zconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by liufeng on 2017/8/10.
 */
public class ConfigUtils {
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
        String currentTime = df.format(new Date());
        return currentTime;
    }

    public static String getCurrentTime(SimpleDateFormat df) {
        String currentTime = df.format(new Date());
        return currentTime;
    }

    public static Date increaseTime(Date date, int calendarMode, int increasing) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarMode, increasing);

        return cal.getTime();
    }

    public static Date convertStringToDate(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = sdf.parse(strDate);

            return date;
        } catch (Exception ex) {
            return new Date();
        }
    }

    public static Date convertStringToDate(String strDate, SimpleDateFormat sdf) {

        try {

            Date date = sdf.parse(strDate);

            return date;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String convertDateToString(Date date, SimpleDateFormat sdf) {

        try {
            String currentTime = sdf.format(date);

            return currentTime;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
        String currentTime = df.format(date);
        return currentTime;
    }

    public static int compareDate(String date1, String date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);

            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception ex) {
        }

        return 0;
    }

    public static int compareDate(Date date1, Date date2) {
        try {
            if (date1.getTime() > date2.getTime()) {
                return 1;
            } else if (date1.getTime() < date2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception ex) {
        }

        return 0;
    }
}
