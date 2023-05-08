package hr.tvz.groops.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}

