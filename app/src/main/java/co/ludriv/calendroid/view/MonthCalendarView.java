package co.ludriv.calendroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;

import co.ludriv.calendroid.model.Selection;
import co.ludriv.calendroid.utils.CalendarUtils;

/**
 * Created by Ludovic on 14/10/2014.
 */
public class MonthCalendarView extends View
{
    private Context mContext;
    private int     mWidth;
    private int     mHeight;
    private int     mRowCount; // aka. number of weeks in month
    //

    //
    private Calendar mCurrentMonthCalendar;
    private int      mCurrentMonth;
    private int      mCurrentYear;
    private String[] mDayNames;
    private Calendar mTodayCalendar;
    private Calendar mTempCalendar;
    //

    private HashMap<String, Region> mDayRegions;

    // configurable
    private boolean         mIsSelectToday    = false;
    private Selection.Shape mSelectTodayShape = Selection.Shape.CIRCLE;
    //
    private int   mFirstDayOfWeek     = Calendar.MONDAY; //Calendar.MONDAY;
    private int   mLastDayOfWeek      = Calendar.SUNDAY; //Calendar.SUNDAY;
    //
    private float mDayTitleLineHeight = 1;
    private float mDayTitleHeight     = 30;
    private float mDayTextPadding     = 20;
    private float mWeekLineHeight     = 1;
    //

    //
    // for drawing
    //
    private Paint mDayTitlePaint;
    private Paint mDayTitleLinePaint;
    private Paint mDaySeparatorPaint;
    private Paint mDayCurrentMonthPaint;
    private Paint mDayTextCurrentMonthPaint;
    private Paint mDayOtherMonthPaint;
    private Paint mDayTextOtherMonthPaint;
    private Paint mWeekLinePaint;
    private Paint mSelectTodayPaint;
    private Paint mHightlightDayPaint;
    //
    private RectF mCanvasRect;
    private Rect  mCachedRect;
    private RectF mCachedRectF;
    //


    public enum PaintType
    {
        DAY_TITLE, DAY_TITLE_LINE, DAY_SEPARATOR, DAY_CURRENT_MONTH, DAY_TEXT_CURRENT_MONTH, DAY_OTHER_MONTH, DAY_TEXT_OTHER_MONTH,
        WEEK_SEPARATOR,
        SELECT_TODAY,
        HIGHLIGHT_DAY
    }


    public MonthCalendarView(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    public MonthCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MonthCalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init()
    {
        //
        mCurrentMonthCalendar = Calendar.getInstance();
        mCurrentMonthCalendar.set(Calendar.DATE, 1);
        mCurrentMonthCalendar.set(Calendar.HOUR, 0);
        mCurrentMonthCalendar.set(Calendar.MINUTE, 0);
        mCurrentMonthCalendar.set(Calendar.SECOND, 0);

        mCurrentYear = mCurrentMonthCalendar.get(Calendar.YEAR);
        mCurrentMonth = mCurrentMonthCalendar.get(Calendar.MONTH);

        mTodayCalendar = Calendar.getInstance();
        mTempCalendar = Calendar.getInstance();

        //
        // cache month/day names
        //
        DateFormatSymbols symbols = DateFormatSymbols.getInstance();

        String[] weekdays = symbols.getWeekdays();
        mDayNames = new String[7];
        int j = 0;
        for (int i = 0; i < weekdays.length; i++)
        {
            if (weekdays[i].length() > 0)
            {
                // we get only the 3 first characters
                mDayNames[j] = weekdays[i].substring(0, 3).toUpperCase() + ".";
                ++j;
            }
        }

        mDayRegions = new HashMap<String, Region>();

        updateData();


        //
        // for drawing
        //
        mDayTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayTitlePaint.setStyle(Paint.Style.FILL);
        mDayTitlePaint.setColor(0xff8b8b8b);
        mDayTitlePaint.setTextSize(14);

        mDayTitleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayTitleLinePaint.setStyle(Paint.Style.FILL);
        mDayTitleLinePaint.setColor(0x40353434); // 25% alpha

        mDaySeparatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDaySeparatorPaint.setStyle(Paint.Style.FILL);
        mDaySeparatorPaint.setColor(Color.WHITE);

        mDayCurrentMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayCurrentMonthPaint.setStyle(Paint.Style.FILL);
        mDayCurrentMonthPaint.setColor(Color.WHITE);

        mDayTextCurrentMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayTextCurrentMonthPaint.setStyle(Paint.Style.FILL);
        mDayTextCurrentMonthPaint.setTextSize(20);
        mDayTextCurrentMonthPaint.setColor(0xff353434);

        mDayOtherMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayOtherMonthPaint.setStyle(Paint.Style.FILL);
        mDayOtherMonthPaint.setColor(0xfff4f4f4);

        mDayTextOtherMonthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayTextOtherMonthPaint.setStyle(Paint.Style.FILL);
        mDayTextOtherMonthPaint.setTextSize(20);
        mDayTextOtherMonthPaint.setColor(0xff999999);

        mWeekLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWeekLinePaint.setStyle(Paint.Style.FILL);
        mWeekLinePaint.setColor(Color.DKGRAY);

        mSelectTodayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectTodayPaint.setStyle(Paint.Style.FILL);
        mSelectTodayPaint.setColor(0xff00aeef);

        mHightlightDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHightlightDayPaint.setStyle(Paint.Style.FILL);
        mHightlightDayPaint.setColor(0x2600aeef); //15% alpha
        //
        mCanvasRect = new RectF();
        mCachedRect = new Rect();
        mCachedRectF = new RectF();
        //
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        setMeasuredDimension(mWidth, mHeight);

        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = mWidth - getPaddingRight();
        float bottom = mHeight - getPaddingBottom();

        mCanvasRect.set(left, top, right, bottom);


    }

    private float measureNeededHeight()
    {
        return 0;
    }

    private void updateData()
    {
        mRowCount = CalendarUtils.getNumberOfWeeksInMonth(mCurrentYear, mCurrentMonth);
        System.out.println("mRowCount= " + mRowCount);
    }

    private void update()
    {
        // foo!

        mRowCount = CalendarUtils.getNumberOfWeeksInMonth(mCurrentYear, mCurrentMonth);
        System.out.println("mRowCount= " + mRowCount);

        repaint();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float left = mCanvasRect.left, top = mCanvasRect.top, right = mCanvasRect.right, bottom = 0;

        float dayWidth = mCanvasRect.width() / mDayNames.length;

        // draw top line of day title
        canvas.drawLine(left, top, right, top, mDayTitleLinePaint);
        top += mDayTitleLineHeight;
        //


        float dayX = left;
        float dayY = top;

        int dayNamesDrawn = 0;
        int dayIndex = mFirstDayOfWeek - 1;

        // draw day names
        while (dayNamesDrawn < mDayNames.length)
        {
            if (dayIndex > mDayNames.length - 1)
            {
                dayIndex = 0;
            }

            dayX = left + dayNamesDrawn * dayWidth;

            String dayTitle = mDayNames[dayIndex];

            mCachedRectF.setEmpty();
            mDayTitlePaint.getTextBounds(dayTitle, 0, dayTitle.length(), mCachedRect);
            canvas.drawText(dayTitle, dayX + (dayWidth - mCachedRect.width()) / 2, dayY + mDayTitleHeight - (mDayTitleHeight - mCachedRect.height()) / 2, mDayTitlePaint);

            ++dayIndex;
            ++dayNamesDrawn;
        }
        top += mDayTitleHeight;
        //

        // draw bottom line of day title
        canvas.drawLine(left, top, right, top, mDayTitleLinePaint);
        top += mDayTitleLineHeight;
        //


        // draw day view
        float dayHeight = (mCanvasRect.height() - top - (mRowCount * mWeekLineHeight)) / mRowCount;
        dayY = top;
        dayX = left;

        int firstDayOfMonth = mCurrentMonthCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDayOfMonth = mCurrentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // calculate difference of day between first day of current month displayed and mFirstDayOfWeek day
        int startDayDiff = 0;
        int endDayDiff = 0;

        // set first day of current month
        mTempCalendar.set(mCurrentYear, mCurrentMonth, firstDayOfMonth, 0, 0, 0);
        int firstDayNameType = mTempCalendar.get(Calendar.DAY_OF_WEEK); // sunday, monday, ...

        if (firstDayNameType != mFirstDayOfWeek)
        {
            if (firstDayNameType > mFirstDayOfWeek)
            {
                startDayDiff = -Math.abs(firstDayNameType - mFirstDayOfWeek);
            }
            else
            {
                startDayDiff = -(Math.abs(mDayNames.length - Math.abs(firstDayNameType - mFirstDayOfWeek)));
            }
        }

        mTempCalendar.set(mCurrentYear, mCurrentMonth, lastDayOfMonth, 0, 0, 0);
        int lastDayNameType = mTempCalendar.get(Calendar.DAY_OF_WEEK); // sunday, monday, ...

        if (lastDayNameType != mLastDayOfWeek)
        {
            if (lastDayNameType < mLastDayOfWeek)
            {
                endDayDiff = Math.abs(lastDayNameType - mLastDayOfWeek);
            }
            else
            {
                endDayDiff = Math.abs(Math.abs(lastDayNameType - mLastDayOfWeek) - mDayNames.length);
            }
        }

        dayX = left;

        mTempCalendar.set(mCurrentYear, mCurrentMonth, 1, 0, 0, 0);
        mTempCalendar.add(Calendar.DATE, startDayDiff);

        for (int i = startDayDiff; i <= (lastDayOfMonth + endDayDiff + 1); i++)
        {
            // skip index 0
            if (i == 0)
            {
                continue;
            }

            // adjust paint according state of day (belongs to current month or not)
            boolean dayInCurrentMonth = true;
            if (mTempCalendar.get(Calendar.MONTH) != mCurrentMonth)
            {
                dayInCurrentMonth = false;
            }

            canvas.drawRect(dayX, dayY, dayX + dayWidth, dayY + dayHeight, (dayInCurrentMonth ? mDayCurrentMonthPaint : mDayOtherMonthPaint));
            //

            String dayText = String.valueOf(mTempCalendar.get(Calendar.DATE));

            // draw selection
            //

            // draw day text
            mCachedRect.setEmpty();
            (dayInCurrentMonth ? mDayTextCurrentMonthPaint : mDayTextOtherMonthPaint).getTextBounds(dayText, 0, dayText.length(), mCachedRect);

            // draw highlight shape if needed
            if (mIsSelectToday && (mTempCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR) &&
                    mTempCalendar.get(Calendar.MONTH) == mTodayCalendar.get(Calendar.MONTH) &&
                    mTempCalendar.get(Calendar.DATE) == mTodayCalendar.get(Calendar.DATE)))
            {
                float sideWidth = (dayWidth - 2 * mDayTextPadding);
                mCachedRectF.setEmpty();
                mCachedRectF.set(dayX + mDayTextPadding * 1.5f, dayY + mDayTextPadding/2, (int) (dayX + mDayTextPadding * 1.5) + sideWidth, dayY + mDayTextPadding/2 + sideWidth);

                if (mSelectTodayShape == Selection.Shape.SQUARE)
                {
                    canvas.drawRect(mCachedRectF, mSelectTodayPaint);
                }
                else if (mSelectTodayShape == Selection.Shape.CIRCLE)
                {
                    canvas.drawCircle(mCachedRectF.centerX(), mCachedRectF.centerY(), sideWidth/2, mSelectTodayPaint);
                }
            }
            //

            canvas.drawText(dayText, dayX + dayWidth - mCachedRect.width() - mDayTextPadding, dayY + mCachedRect.height() + mDayTextPadding, (dayInCurrentMonth ? mDayTextCurrentMonthPaint : mDayTextOtherMonthPaint));
            //

            // draw day line separator
            canvas.drawLine(dayX, dayY, dayX, dayY + dayHeight, mDaySeparatorPaint);
            //

            // calculate next position
            dayX += dayWidth;
            if (dayX == right)
            {
                canvas.drawLine(left, dayY, right, dayY + (mWeekLineHeight - 1), mWeekLinePaint);

                dayX = left;
                dayY += dayHeight + mWeekLineHeight;
            }

            mTempCalendar.add(Calendar.DATE, 1);
        }


    }

    public void repaint()
    {
        postInvalidate();
    }

    public void setYearMonth(int year, int month)
    {
        mCurrentYear = year;
        mCurrentMonth = month;
        update();
    }

    public Paint getPaint(PaintType paintType)
    {
        switch (paintType)
        {
            case DAY_TITLE:
                return mDayTitlePaint;

            case DAY_TITLE_LINE:
                return mDayTitleLinePaint;

            case DAY_SEPARATOR:
                return mDaySeparatorPaint;

            case DAY_CURRENT_MONTH:
                return mDayCurrentMonthPaint;

            case DAY_TEXT_CURRENT_MONTH:
                return mDayTextCurrentMonthPaint;

            case DAY_OTHER_MONTH:
                return mDayOtherMonthPaint;

            case DAY_TEXT_OTHER_MONTH:
                return mDayTextOtherMonthPaint;

            case WEEK_SEPARATOR:
                return mWeekLinePaint;

            case SELECT_TODAY:
                return mSelectTodayPaint;

            case HIGHLIGHT_DAY:
                return mHightlightDayPaint;
        }
        return null;
    }

    public void setSelectToday(boolean isSelectToday)
    {
        mIsSelectToday = isSelectToday;
        repaint();
    }

    public void setTodaySelectionShape(Selection.Shape shape)
    {
        mSelectTodayShape = shape;
        repaint();
    }

}
