package com.faridfaharaj.profitable.util;

/**
 * Utility for time handling in the plugin.
 * Uses real-world system time (milliseconds since epoch).
 * "Day" is defined as 2 hours (7200000 ms). Week = 7 days. Month = 30 days.
 */
public class TimeUtil {

//    public static final long DAY_MS = 2L * 60L * 60L * 1000L; // 2 hours
    public static final long DAY_MS = 5L * 60L * 1000L; // 5 min
    public static final long WEEK_MS = DAY_MS * 7L;
    public static final long MONTH_MS = DAY_MS * 30L;

    public static final long[] INTERVALS = {DAY_MS, WEEK_MS, MONTH_MS};

    public static long getNowMillis(){
        return System.currentTimeMillis();
    }

    public static long roundToInterval(long timeMillis, int intervalIndex){
        long interval = INTERVALS[intervalIndex];
        return (timeMillis / interval) * interval;
    }

    public static long roundDay(long timeMillis){
        return roundToInterval(timeMillis, 0);
    }

}

