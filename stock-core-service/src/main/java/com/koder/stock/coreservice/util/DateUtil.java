package com.koder.stock.coreservice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class DateUtil {

    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";


    public static String getYYYYMMDDFromInstant(Instant instant, String dateFormat) {
        Date tmpDate = Date.from(instant);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String str = formatter.format(tmpDate);
        return str;
    }

    //获取当天（按当前传入的时区）00:00:00所对应时刻的long型值
    public static long getStartTimeOfDay(long now) {
        String tz = "GMT+8";
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    public static Instant getFromString(String date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date retDate = null;
        try {
            retDate = formatter.parse(date);
        } catch (ParseException e) {
            log.warn("parse date error", e);
        }
        return Instant.ofEpochMilli(retDate.getTime());
    }

    public static Calendar getHKOpenTime() {
        Calendar openTime = Calendar.getInstance();
        openTime.set(Calendar.HOUR_OF_DAY, 9);
        openTime.set(Calendar.MINUTE, 30);
        return openTime;
    }

}
