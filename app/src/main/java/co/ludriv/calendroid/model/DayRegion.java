package co.ludriv.calendroid.model;

import android.graphics.RectF;

/**
 * Created by Ludovic on 18/10/2014.
 */
public class DayRegion
{
    private RectF mRectF;
    private Day   mDay;

    public DayRegion()
    {
        mDay = new Day();
        mRectF = new RectF();
    }

    public RectF getRectF()
    {
        return mRectF;
    }

    public void setDay(int year, int month, int day)
    {
        mDay.setYear(year);
        mDay.setMonth(month);
        mDay.setDay(day);
    }

    public void setDay(Day day)
    {
        mDay = day;
    }

    public Day getDay()
    {
        return mDay;
    }

}
