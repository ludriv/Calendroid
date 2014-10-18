package co.ludriv.calendroid.model;

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
