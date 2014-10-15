package co.ludriv.calendroid.model;

import android.graphics.Paint;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ludovic on 13/10/2014.
 */
public class YearCalendarSelection extends Selection
{

    private Date            mDate;
    private Selection.Shape mShape;
    private Paint           mPaint;

    public YearCalendarSelection(int year, int month, int day, Shape shape)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        mDate = calendar.getTime();
        mShape = shape;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public YearCalendarSelection(int year, int month, int day, Shape shape, Paint paint)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        mDate = calendar.getTime();
        mShape = shape;
        mPaint = paint;
    }

    public YearCalendarSelection(Date date, Shape shape)
    {
        this(date, shape, new Paint(Paint.ANTI_ALIAS_FLAG));
    }

    public YearCalendarSelection(Date date, Shape shape, Paint paint)
    {
        mDate = date;
        mShape = shape;
        mPaint = paint;
    }

    public Date getDate()
    {
        return mDate;
    }

    public Shape getShape()
    {
        return mShape;
    }

    public Paint getPaint()
    {
        return mPaint;
    }
}
