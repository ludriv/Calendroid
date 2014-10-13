package co.ludriv.calendroid.model;

import android.graphics.Paint;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ludovic on 13/10/2014.
 */
public class YearCalendarSelection
{
    public enum Type
    {
        SQUARE, CIRCLE
    }

    private Date  mDate;
    private Type  mType;
    private Paint mPaint;

    public YearCalendarSelection(int year, int month, int day, Type type)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        mDate = calendar.getTime();
        mType = type;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public YearCalendarSelection(int year, int month, int day, Type type, Paint paint)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        mDate = calendar.getTime();
        mType = type;
        mPaint = paint;
    }

    public YearCalendarSelection(Date date, Type type)
    {
        this(date, type, new Paint(Paint.ANTI_ALIAS_FLAG));
    }

    public YearCalendarSelection(Date date, Type type, Paint paint)
    {
        mDate = date;
        mType = type;
        mPaint = paint;
    }

    public Date getDate()
    {
        return mDate;
    }

    public Type getType()
    {
        return mType;
    }

    public Paint getPaint()
    {
        return mPaint;
    }
}
