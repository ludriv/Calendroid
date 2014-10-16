package co.ludriv.calendroid.model;

import android.graphics.Paint;

import java.util.Date;

/**
 * Created by Ludovic on 16/10/2014.
 */
public class CalendarEvent
{
    /**
     * Date of event
     */
    private Date mDate;

    /**
     * Summary of event
     *
     * Default is empty string
     */
    private String mText = null;

    /**
     * <p>
     * @see co.ludriv.calendroid.model.Selection.Shape
     * <p/>
     * Default is INHERIT
     */
    private Selection.Shape mShape = Selection.Shape.INHERIT;

    /**
     * Paint object for styling representation
     */
    private Paint mPaint;

    /**
     * First arbitrary object
     * Use it as you want
     *
     * Default is null
     */
    private Object mObject = null;

    /**
     * Second arbitrary object
     * Use it as you want
     *
     * Default is null
     */
    private Object mObject2 = null;


    public CalendarEvent(Date date, String text)
    {
        mDate = date;
        mText = text;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public CalendarEvent(Date date, String text, Selection.Shape shape)
    {
        this(date, text);
        mShape = shape;
    }

    public CalendarEvent(Date date, String text, Selection.Shape shape, Object object, Object object2)
    {
        this(date, text, shape);
        mObject = object;
        mObject2 = object2;
    }


    /**
     * Setters
     */

    public void setShape(Selection.Shape shape)
    {
        mShape = shape;
    }

    public void setObject(Object object)
    {
        mObject = object;
    }

    public void setObject2(Object object2)
    {
        mObject2 = object2;
    }


    /**
     * Getters
     */

    public Date getDate()
    {
        return mDate;
    }

    public String getText()
    {
        return mText;
    }

    public Selection.Shape getShape()
    {
        return mShape;
    }

    public Paint getPaint()
    {
        return mPaint;
    }

    public Object getObject()
    {
        return mObject;
    }

    public Object getObject2()
    {
        return mObject2;
    }

}
