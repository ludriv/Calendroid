package co.ludriv.calendroid.model;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ludovic on 18/10/2014.
 */
public class Day
{
    private int mYear;
    private int mMonth;
    private int mDay;

    public Day()
    {
        mYear = mMonth = mDay = 0;
    }

    public Day(int year, int month, int day)
    {
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public void setYear(int year)
    {
        mYear = year;
    }

    public int getYear()
    {
        return mYear;
    }

    public void setMonth(int month)
    {
        mMonth = month;
    }

    public int getMonth()
    {
        return mMonth;
    }

    public void setDay(int day)
    {
        mDay = day;
    }

    public int getDay()
    {
        return mDay;
    }


    private static Calendar _calendar = Calendar.getInstance(Locale.FRANCE);

    public boolean after(Day otherDay)
    {
        _calendar.clear();
        _calendar.set(mYear, mMonth, mDay, 0, 0, 0);
        long time1 = _calendar.getTimeInMillis();

        _calendar.clear();
        _calendar.set(otherDay.mYear, otherDay.mMonth, otherDay.mDay, 0, 0, 0);
        long time2 = _calendar.getTimeInMillis();

        return time1 > time2;
    }

    public boolean before(Day otherDay)
    {
        _calendar.clear();
        _calendar.set(mYear, mMonth, mDay, 0, 0, 0);
        long time1 = _calendar.getTimeInMillis();

        _calendar.clear();
        _calendar.set(otherDay.mYear, otherDay.mMonth, otherDay.mDay, 0, 0, 0);
        long time2 = _calendar.getTimeInMillis();

        return time1 < time2;
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Day))
        {
            return false;
        }

        Day oDay = (Day) o;
        return (oDay.mYear == mYear && oDay.mMonth == mMonth && oDay.mDay == mDay);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public String toString()
    {
        return mYear + "-" + mMonth + "-" + mDay;
    }
}
