package co.ludriv.calendroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import co.ludriv.calendroid.interfaces.MonthCalendarViewListener;
import co.ludriv.calendroid.model.CalendarEvent;
import co.ludriv.calendroid.model.Day;
import co.ludriv.calendroid.model.DayRegion;
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

    private HashMap<Integer, DayRegion> mDayRegions;
    private MonthCalendarViewListener   mTouchListener;
    private ArrayList<CalendarEvent>    mEvents;
    private ArrayList<CalendarEvent>    mTempEvents;
    private ArrayList<Day>              mSelectedDays;

    private Day mMinimumSelectDay = null;
    private Day mMaximumSelectDay = null;


    // configurable
    private boolean          mIsSelectToday      = false;
    private Selection.Shape  mDefaultShape       = Selection.Shape.CIRCLE;
    private int              mEventPointSize     = 10;
    private Selection.Shape  mSelectTodayShape   = Selection.Shape.CIRCLE;
    private boolean          mEnableDayTouches   = false;
    private DaySelectionType mDaySelectionType   = DaySelectionType.DAY_RECT;
    //
    private int              mFirstDayOfWeek     = Calendar.MONDAY;
    private int              mLastDayOfWeek      = Calendar.SUNDAY;
    private int              mMaxDayEventCount   = 1;
    //
    private float            mDayTitleLineHeight = 1;
    private float            mDayTitleHeight     = 30;
    private float            mDayTextPadding     = 20;
    private float            mDayEventPadding    = 10;
    private float            mWeekLineHeight     = 1;
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
    private Paint mSelectTodayTextPaint;
    private Paint mSelectedDayPaint;
    private Paint mDisabledDayPaint;
    //
    private RectF mCanvasRect;
    private Rect  mCachedRect;
    private RectF mCachedRectF;
    //


    public enum PaintType
    {
        DAY_TITLE,              // day title in the header eq. SUN, MON, TUE, WED, ...
        DAY_TITLE_LINE,         // line in top and bottom of day titles

        DAY_CURRENT_MONTH,      // background style of block of day used when drawn day is in current month
        DAY_TEXT_CURRENT_MONTH, // text style of block of day used when drawn day is in current month

        DAY_OTHER_MONTH,        // background style of block of day used when drawn day is not the current month
        DAY_TEXT_OTHER_MONTH,   // text style of block of day used when drawn day is not in the current month

        DAY_SEPARATOR,          // day vertical separator
        WEEK_SEPARATOR,         // style of week separator

        SELECT_TODAY,           // background style of today if `mIsSelectToday` equals true
        SELECT_TODAY_TEXT,      // text of today if `mIsSelectToday` equals true

        SELECTED_DAY,           // selected day
        DISABLED_DAY            // disabled day
    }

    public enum DaySelectionType
    {
        FILL_RECT,              // as default: fill whole day block
        DAY_RECT,               // draw a square around the text of day
        DAY_ROUND               // draw a circle around the text of day
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
        mCurrentMonthCalendar = Calendar.getInstance();
        mCurrentMonthCalendar.set(Calendar.DATE, 1);
        mCurrentMonthCalendar.set(Calendar.HOUR, 0);
        mCurrentMonthCalendar.set(Calendar.MINUTE, 0);
        mCurrentMonthCalendar.set(Calendar.SECOND, 0);

        mCurrentYear = mCurrentMonthCalendar.get(Calendar.YEAR);
        mCurrentMonth = mCurrentMonthCalendar.get(Calendar.MONTH);

        mTodayCalendar = Calendar.getInstance(Locale.FRANCE);
        mTempCalendar = Calendar.getInstance(Locale.FRANCE);

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

        mDayRegions = new HashMap<Integer, DayRegion>();
        mTouchListener = null;
        mEvents = new ArrayList<CalendarEvent>();
        mTempEvents = new ArrayList<CalendarEvent>();
        mSelectedDays = new ArrayList<Day>();

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

        mSelectTodayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectTodayTextPaint.setStyle(Paint.Style.FILL);
        mSelectTodayTextPaint.setTextSize(20);
        mSelectTodayTextPaint.setColor(0xffffffff);

        mSelectedDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedDayPaint.setStyle(Paint.Style.FILL);
        mSelectedDayPaint.setColor(0x2600aeef); //15% alpha

        mDisabledDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDisabledDayPaint.setStyle(Paint.Style.FILL);
        mDisabledDayPaint.setColor(0xffcccccc);
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
    }

    private void update()
    {
        // foo!

        mRowCount = CalendarUtils.getNumberOfWeeksInMonth(mCurrentYear, mCurrentMonth);

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

        if (mDayRegions.isEmpty())
        {
            for (int i = startDayDiff; i <= (lastDayOfMonth + endDayDiff + 1); i++)
            {
                if (i == 0)
                {
                    continue;
                }

                mDayRegions.put(Integer.valueOf(i), new DayRegion());
            }
        }

        int k = 0;
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
            mDayRegions.get(i).getRectF().set(dayX, dayY, dayX + dayWidth, dayY + dayHeight);
            mDayRegions.get(i).setDay(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DATE));
            //

            // draw disabled block:
            // if current day is before miminum selectable date or if current day is after maximum selectable date
            if ((mMinimumSelectDay != null && mMinimumSelectDay.after(mDayRegions.get(i).getDay())) || (mMaximumSelectDay != null && mMaximumSelectDay.before(mDayRegions.get(i).getDay())))
            {
                canvas.drawRect(dayX, dayY, dayX + dayWidth, dayY + dayHeight, mDisabledDayPaint);
            }
            //

            String dayText = String.valueOf(mTempCalendar.get(Calendar.DATE));

            // draw 'event' points
            ArrayList<CalendarEvent> dayEvents = getEventsByDay(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DATE));
            if (!dayEvents.isEmpty())
            {
                int dayEventsCount = dayEvents.size();

                float eventSpace = (dayWidth - 2 * mDayEventPadding - dayEventsCount * mEventPointSize) / (dayEventsCount + 1);
                float eventX = dayX + mDayEventPadding + eventSpace;
                float eventY = dayY + dayHeight - mDayEventPadding - mEventPointSize;

                for (CalendarEvent calendarEvent : dayEvents)
                {
                    Selection.Shape eventShape = ((calendarEvent.getShape() != null && calendarEvent.getShape() != Selection.Shape.INHERIT) ? calendarEvent.getShape() : mDefaultShape);

                    if (eventShape == Selection.Shape.SQUARE)
                    {
                        canvas.drawRect(eventX, eventY, eventX + mEventPointSize, eventY + mEventPointSize, calendarEvent.getPaint());
                    }
                    else if (eventShape == Selection.Shape.CIRCLE)
                    {
                        canvas.drawCircle(eventX + mEventPointSize / 2, eventY + mEventPointSize / 2, mEventPointSize / 2, calendarEvent.getPaint());
                    }

                    eventX += eventSpace + mEventPointSize;
                }
            }
            //

            // prepare day text
            mCachedRect.setEmpty();
            (dayInCurrentMonth ? mDayTextCurrentMonthPaint : mDayTextOtherMonthPaint).getTextBounds(dayText, 0, dayText.length(), mCachedRect);

            float textOriginX = dayX + dayWidth - mCachedRect.width() - mDayTextPadding;
            float textOriginY = dayY + mCachedRect.height() + mDayTextPadding;

            float textRectLeft = textOriginX;
            float textRectTop = textOriginY - mCachedRect.height();
            float textRectRight = textRectLeft + mCachedRect.width();
            float textRectBottom = textOriginY;

            Paint dayTextPaint = mDayTextCurrentMonthPaint;
            if (!dayInCurrentMonth)
            {
                dayTextPaint = mDayTextOtherMonthPaint;
            }
            //


            // draw block selection
            if (mSelectedDays.contains(mDayRegions.get(i).getDay()))
            {
                float todayBoxWidth = Math.max(textRectRight - textRectLeft, textRectBottom - textRectTop);
                
                if (mDaySelectionType == DaySelectionType.FILL_RECT)
                {
                    canvas.drawRect(dayX, dayY, dayX + dayWidth, dayY + dayHeight, mSelectedDayPaint);
                }
                else if (mDaySelectionType == DaySelectionType.DAY_RECT)
                {
                    canvas.drawRect(textRectLeft - todayBoxWidth / 2, textRectTop - todayBoxWidth / 2, textRectRight + todayBoxWidth / 2, textRectBottom + todayBoxWidth / 2, mSelectedDayPaint);
                }
                else if (mDaySelectionType == DaySelectionType.DAY_ROUND)
                {
                    canvas.drawCircle((textRectLeft + textRectRight) / 2, (textRectTop + textRectBottom) / 2, todayBoxWidth, mSelectedDayPaint);
                }
            }
            //


            // draw highlight shape if needed
            if (mIsSelectToday && (mTempCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR) &&
                    mTempCalendar.get(Calendar.MONTH) == mTodayCalendar.get(Calendar.MONTH) &&
                    mTempCalendar.get(Calendar.DATE) == mTodayCalendar.get(Calendar.DATE)))
            {
                float todayBoxWidth = Math.max(textRectRight - textRectLeft, textRectBottom - textRectTop);

                float todayRectCenterX = (textRectLeft + textRectRight) / 2;
                float todayRectCenterY = (textRectTop + textRectBottom) / 2;

                if (mSelectTodayShape == Selection.Shape.SQUARE)
                {
                    canvas.drawRect(todayRectCenterX - todayBoxWidth, todayRectCenterY - todayBoxWidth, todayRectCenterX + todayBoxWidth, todayRectCenterY + todayBoxWidth, mSelectTodayPaint);
                }
                else if (mSelectTodayShape == Selection.Shape.CIRCLE)
                {
                    canvas.drawCircle(todayRectCenterX, todayRectCenterY, todayBoxWidth, mSelectTodayPaint);
                }

                dayTextPaint = mSelectTodayTextPaint;
            }
            //

            // draw day text
            canvas.drawText(dayText, dayX + dayWidth - mCachedRect.width() - mDayTextPadding, dayY + mCachedRect.height() + mDayTextPadding, dayTextPaint);
            //

            // draw day line separator
            canvas.drawLine(dayX, dayY, dayX, dayY + dayHeight, mDaySeparatorPaint);
            //

            // calculate next position
            dayX += dayWidth;
            ++k;

            if (k % mDayNames.length == 0)
            {
                canvas.drawLine(left, dayY, right, dayY + (mWeekLineHeight - 1), mWeekLinePaint);

                dayX = left;
                dayY += dayHeight + mWeekLineHeight;
            }

            mTempCalendar.add(Calendar.DATE, 1);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mEnableDayTouches)
        {
            for (Integer key : mDayRegions.keySet())
            {
                DayRegion dayRegion = mDayRegions.get(key);

                if (dayRegion.getRectF().contains((int) event.getX(), (int) event.getY()) && event.getAction() == MotionEvent.ACTION_UP)
                {
                    // ignore selection if not in range
                    if ((mMinimumSelectDay != null && mMinimumSelectDay.after(dayRegion.getDay())) || (mMaximumSelectDay != null && mMaximumSelectDay.before(dayRegion.getDay())))
                    {
                        return true;
                    }

                    if (mSelectedDays.contains(dayRegion.getDay()))
                    {
                        mSelectedDays.remove(dayRegion.getDay());
                        if (mTouchListener != null)
                        {
                            mTouchListener.onDayDeselected(dayRegion.getDay());
                        }
                    }
                    else
                    {
                        mSelectedDays.add(dayRegion.getDay());

                        if (mTouchListener != null)
                        {
                            mTouchListener.onDaySelected(dayRegion.getDay());
                        }
                    }

                    repaint();
                    return true;
                }

            }

            return true;
        }

        return super.onTouchEvent(event);
    }


    /**
     * Private methods
     */

    private ArrayList<CalendarEvent> getEventsByDay(int year, int month, int day)
    {
        mTempEvents.clear();
        Calendar tempCal = Calendar.getInstance();

        for (CalendarEvent calendarEvent : mEvents)
        {
            tempCal.setTime(calendarEvent.getDate());

            if (tempCal.get(Calendar.YEAR) == year && tempCal.get(Calendar.MONTH) == month && tempCal.get(Calendar.DATE) == day)
            {
                mTempEvents.add(calendarEvent);

                if (mTempEvents.size() == mMaxDayEventCount)
                {
                    break;
                }
            }
        }
        return mTempEvents;
    }

    /**
     * Public methods
     */

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

            case SELECT_TODAY_TEXT:
                return mSelectTodayTextPaint;

            case SELECTED_DAY:
                return mSelectedDayPaint;

            case DISABLED_DAY:
                return mDisabledDayPaint;
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

    public void enableDayTouchListener(MonthCalendarViewListener listener)
    {
        mEnableDayTouches = true;
        mTouchListener = listener;
    }

    public void disableDayTouchListener()
    {
        mEnableDayTouches = false;
        mTouchListener = null;
    }

    public void clearEvents()
    {
        mEvents.clear();
    }

    public void addAllEvents(List<CalendarEvent> events)
    {
        mEvents.addAll(events);
    }

    public void addEvent(CalendarEvent event)
    {
        mEvents.add(event);
    }

    public void setMaxDayEventCount(int maxDayEventCount)
    {
        mMaxDayEventCount = maxDayEventCount;
    }

    public void clearSelectedDates()
    {
        mSelectedDays.clear();
        repaint();
    }

    public void restoreSelectedDays(List<Day> days)
    {
        mSelectedDays.clear();
        mSelectedDays.addAll(days);
        repaint();
    }

    public void setMinimumSelectDay(Day minimumSelectDay)
    {
        mMinimumSelectDay = minimumSelectDay;
    }

    public void setMaximumSelectDay(Day maximumSelectDay)
    {
        mMaximumSelectDay = maximumSelectDay;
    }

    public void setDaySelectionType(DaySelectionType daySelectionType)
    {
        mDaySelectionType = daySelectionType;
        repaint();
    }

}
