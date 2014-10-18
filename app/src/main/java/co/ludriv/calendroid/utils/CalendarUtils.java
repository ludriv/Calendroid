package co.ludriv.calendroid.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ludovic on 14/10/2014.
 */
public final class CalendarUtils
{
    private static Calendar CALENDAR = Calendar.getInstance(Locale.FRANCE);


    public static Calendar getReusableCalendar()
    {
        CALENDAR.clear();
        return CALENDAR;
    }

    public static int getNumberOfWeeksInMonth(int year, int month)
    {
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.set(year, month, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    public static int[] getYearMonthFromEraMonthIndex(int eraMonthIndex)
    {
        int[] result = new int[2];

        result[0] = (int) Math.floor(eraMonthIndex / 12); // year
        result[1] = (eraMonthIndex % 12); // month

        return result;
    }

    public static int getEraMonthIndexFromYearMonth(int year, int month)
    {
        return year * 12 + month;
    }

}
