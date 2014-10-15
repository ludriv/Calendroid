package co.ludriv.calendroid.utils;

import java.util.Calendar;

/**
 * Created by Ludovic on 14/10/2014.
 */
public final class CalendarUtils
{

    public static int getNumberOfWeeksInMonth(int year, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1, 0, 0, 0);
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