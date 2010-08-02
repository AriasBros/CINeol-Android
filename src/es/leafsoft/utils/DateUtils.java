package es.leafsoft.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static int firstDayOfWeek = Calendar.MONDAY;
	
	public static int getNumberOfWeekInYear(Date date) {
        Calendar calendar = Calendar.getInstance();   
        calendar.setFirstDayOfWeek(firstDayOfWeek);
       	calendar.setTime(date);
        
    	return calendar.get(Calendar.WEEK_OF_YEAR);
    }
    
    public static boolean isActualWeek(Date date) {
        return DateUtils.getNumberOfWeekInYear(date) == DateUtils.getNumberOfWeekInYear(new Date());
    }
}
