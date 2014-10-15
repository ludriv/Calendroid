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

import co.ludriv.calendroid.interfaces.YearCalendarTouchListener;
import co.ludriv.calendroid.model.YearCalendarSelection;

/**
 * Created by Ludovic on 09/10/2014.
 */
public class YearCalendarView extends View
{
    private Context mContext;
    private int     mWidth;
    private int     mHeight;
    private RectF   mCanvasRect;
    private float   mMonthWidth;
    private float   mMonthHeight;
    private int     mRowCount;

    private HashMap<Integer, RectF>          mMonthRegions;
    private ArrayList<YearCalendarSelection> mSelections;


    private int      mCurrentYear;
    private String[] mMonthNames;
    private String[] mDayNames;
    private Calendar mCurrentYearCalendar;
    private Calendar mTodayCalendar;
    private Calendar mTempCalendar;


    private static final int COLUMN_COUNT = 3;


    // configurable
    private boolean                    mIsSelectToday     = false;
    //
    private int                        mFirstDayOfWeek    = Calendar.SUNDAY;
    //
    private int                        mMonthSpacing      = 10;
    private int                        mPaddingTopDayName = 20;
    private int                        mPaddingTopDay     = 10;
    private YearCalendarSelection.Type mSelectionType     = YearCalendarSelection.Type.SQUARE;
    //

    //
    // for drawing
    //
    private Paint mMonthTitlePaint;
    private Paint mMonthBarPaint;
    private Paint mDayTitlePaint;
    private Paint mDayPaint;
    private Paint mTodayPaint;
    private Paint mTodayTextPaint;
    private Rect  mCachedRect;
    private RectF mCachedRectF;
    private float mMonthTitleMaxHeight;
    //

    public enum PaintType
    {
        MONTH_TITLE, MONTH_BAR,
        DAY_TITLE, DAY,
        TODAY, TODAY_TEXT
    }


    //
    private YearCalendarTouchListener mTouchListener;
    //

    public YearCalendarView(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    public YearCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }

    public YearCalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init()
    {
        mTouchListener = null;

        mMonthRegions = new HashMap<Integer, RectF>();
        mSelections = new ArrayList<YearCalendarSelection>();

        mCurrentYearCalendar = Calendar.getInstance();
        mCurrentYearCalendar.set(mCurrentYearCalendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
        mCurrentYear = mCurrentYearCalendar.get(Calendar.YEAR);

        mTodayCalendar = Calendar.getInstance();
        mTempCalendar = Calendar.getInstance();

        //
        // cache month/day names
        //
        DateFormatSymbols symbols = DateFormatSymbols.getInstance();

        String[] months = symbols.getMonths();
        mMonthNames = new String[months.length];
        int k = 0;
        for (String month : months)
        {
            mMonthNames[k] = month.substring(0, 1).toUpperCase() + month.substring(1, month.length());
            mMonthRegions.put(k, new RectF());
            ++k;
        }

        String[] weekdays = symbols.getWeekdays();
        mDayNames = new String[7];
        int j = 0;
        for (int i = 0; i < weekdays.length; i++)
        {
            if (weekdays[i].length() > 0)
            {
                // we get only the first character
                mDayNames[j] = weekdays[i].substring(0, 1).toUpperCase();
                ++j;
            }
        }

        //
        // for drawing
        //

        mMonthTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthTitlePaint.setColor(Color.BLUE);
        mMonthTitlePaint.setStyle(Paint.Style.FILL);
        mMonthTitlePaint.setTextSize(17);

        mMonthBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthBarPaint.setColor(Color.DKGRAY);
        mMonthBarPaint.setStyle(Paint.Style.STROKE);

        mDayTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayTitlePaint.setColor(Color.GRAY);
        mDayTitlePaint.setStyle(Paint.Style.FILL);
        mDayTitlePaint.setTextSize(13);

        mDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayPaint.setColor(Color.DKGRAY);
        mDayPaint.setStyle(Paint.Style.FILL);
        mDayPaint.setTextSize(13);

        mTodayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayPaint.setColor(Color.RED);
        mTodayPaint.setStyle(Paint.Style.FILL);

        mTodayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayTextPaint.setColor(Color.WHITE);
        mTodayTextPaint.setStyle(Paint.Style.FILL);

        mCanvasRect = new RectF();
        mCachedRect = new Rect();
        mCachedRectF = new RectF();

        mMonthTitleMaxHeight = 0;
        Rect textBounds = new Rect();
        for (int i = 0; i < mMonthNames.length; i++)
        {
            mMonthTitlePaint.getTextBounds(mMonthNames[i], 0, mMonthNames[i].length(), textBounds);
            mMonthTitleMaxHeight = Math.max(mMonthTitleMaxHeight, textBounds.height());
        }


        int minimumHeight = (int) measureNeededHeight();

        //minimumHeight += 50 * 4; //150 * mRowCount;

        //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minimumHeight, getResources().getDisplayMetrics());

        //System.out.println("minimumHeight= " + minimumHeight + " | height= " + height);

        setMinimumHeight(minimumHeight); //height);
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


        mMonthWidth = (mCanvasRect.width() - COLUMN_COUNT * (mMonthSpacing - 1)) / COLUMN_COUNT;

        mRowCount = (int) Math.ceil(12 / COLUMN_COUNT);
        mMonthHeight = (mCanvasRect.height() - mRowCount * (mMonthSpacing - 1)) / mRowCount;


    }

    private float measureNeededHeight()
    {
        float monthTitleMaxHeight = 0;
        Rect bounds = new Rect();
        for (int i = 0; i < mMonthNames.length; i++)
        {
            mMonthTitlePaint.getTextBounds(mMonthNames[i], 0, mMonthNames[i].length(), bounds);
            monthTitleMaxHeight = Math.max(monthTitleMaxHeight, bounds.height());
        }
        bounds.setEmpty();

        float monthBarHeight = 1;
        float dayTitleMaxHeight = 0;
        for (int i = 0; i < mDayNames.length; i++)
        {
            mDayTitlePaint.getTextBounds(mDayNames[i], 0, mDayNames[i].length(), bounds);
            dayTitleMaxHeight = Math.max(dayTitleMaxHeight, bounds.height());
        }
        dayTitleMaxHeight += mPaddingTopDayName;
        bounds.setEmpty();

        mDayPaint.getTextBounds("33", 0, 2, bounds);
        float dayNumMaxHeight = (bounds.height() + mPaddingTopDay) * 7; // 6 rows of week max

        float monthBlockHeight = monthTitleMaxHeight + monthBarHeight + dayTitleMaxHeight + dayNumMaxHeight;

        int rowCount = (int) Math.ceil(12 / COLUMN_COUNT);

        float optimalHeight = monthBlockHeight * rowCount + mMonthSpacing * (rowCount - 1);

        optimalHeight += (rowCount * 10);

        return optimalHeight;
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float left = mCanvasRect.left, top = mCanvasRect.top, right = 0, bottom = 0;

        for (int i = 0; i < 12; i++)
        {
            mCurrentYearCalendar.set(Calendar.MONTH, i);

            right = left + mMonthWidth;
            bottom = top + mMonthHeight;

            float innerTop = top + 10; // 10 = inner padding

            // draw month name
            mCachedRect.setEmpty();
            mMonthTitlePaint.getTextBounds(mMonthNames[i], 0, mMonthNames[i].length(), mCachedRect);
            canvas.drawText(mMonthNames[i], left + mMonthWidth / 2 - mCachedRect.width() / 2, innerTop + mMonthTitleMaxHeight / 2, mMonthTitlePaint);
            innerTop += mMonthTitleMaxHeight;
            //

            // draw bar below month name
            innerTop += 1;
            canvas.drawLine(left, innerTop, right, innerTop, mMonthBarPaint);
            //

            // draw day names
            innerTop += mPaddingTopDayName;
            float dayWidth = mMonthWidth / mDayNames.length;
            float dayX = left;
            float dayY = innerTop;
            int dayNamesDrawn = 0;
            int dayIndex = mFirstDayOfWeek - 1;
            while (dayNamesDrawn < mDayNames.length)
            {
                if (dayIndex > mDayNames.length - 1)
                {
                    dayIndex = 0;
                }

                dayX = left + dayNamesDrawn * dayWidth;

                mCachedRect.setEmpty();
                mDayTitlePaint.getTextBounds(mDayNames[dayIndex], 0, mDayNames[dayIndex].length(), mCachedRect);
                canvas.drawText(mDayNames[dayIndex], dayX + dayWidth / 2 - mCachedRect.width() / 2, dayY, mDayTitlePaint);
                ++dayIndex;
                ++dayNamesDrawn;
            }
            //

            // draw days
            innerTop += mCachedRect.height();
            dayX = left;
            dayY = innerTop + mPaddingTopDay;

            mCurrentYearCalendar.set(mCurrentYear, i, 1, 0, 0);
            int maxDayInMonth = mCurrentYearCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int j = 1; j <= maxDayInMonth; j++)
            {
                mCurrentYearCalendar.set(Calendar.DAY_OF_MONTH, j);
                String dayText = String.valueOf(j);

                int dayOfWeek = mCurrentYearCalendar.get(Calendar.DAY_OF_WEEK);

                if (dayOfWeek == mFirstDayOfWeek && j > 1)
                {
                    dayY += dayWidth;
                }

                if (dayOfWeek < mFirstDayOfWeek)
                {
                    dayX = left + (mDayNames.length * dayWidth) - (mFirstDayOfWeek - dayOfWeek) * dayWidth;
                }
                else
                {
                    dayX = left + (dayOfWeek - mFirstDayOfWeek) * dayWidth;
                }

                mCachedRect.setEmpty();
                mDayPaint.getTextBounds(dayText, 0, dayText.length(), mCachedRect);


                mCachedRectF.setEmpty();
                mCachedRectF.set(dayX, dayY - dayWidth / 2 - mPaddingTopDay / 2, dayX + dayWidth, dayY + dayWidth / 2 - mPaddingTopDay / 2);


                // draw selections if exists
                if (!mSelections.isEmpty())
                {
                    for (YearCalendarSelection selection : mSelections)
                    {
                        mTempCalendar.setTime(selection.getDate());

                        // if it draws same day
                        if (mCurrentYearCalendar.get(Calendar.YEAR) == mTempCalendar.get(Calendar.YEAR) &&
                                mCurrentYearCalendar.get(Calendar.MONTH) == mTempCalendar.get(Calendar.MONTH) &&
                                mCurrentYearCalendar.get(Calendar.DAY_OF_MONTH) == mTempCalendar.get(Calendar.DAY_OF_MONTH))
                        {
                            if (selection.getType() == YearCalendarSelection.Type.SQUARE)
                            {
                                canvas.drawRect(mCachedRectF, selection.getPaint());
                            }
                            else if (selection.getType() == YearCalendarSelection.Type.CIRCLE)
                            {
                                canvas.drawCircle(mCachedRectF.centerX(), mCachedRectF.centerY(), (mCachedRectF.width())/2, selection.getPaint());
                            }
                        }
                    }
                }
                //

                boolean todayAlreadyDrawn = false;

                // draw selection of today if needed
                if (mIsSelectToday)
                {
                    // if same day
                    if (mCurrentYearCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR) &&
                            mCurrentYearCalendar.get(Calendar.MONTH) == mTodayCalendar.get(Calendar.MONTH) &&
                            mCurrentYearCalendar.get(Calendar.DAY_OF_MONTH) == mTodayCalendar.get(Calendar.DAY_OF_MONTH))
                    {
                        if (mSelectionType == YearCalendarSelection.Type.SQUARE)
                        {
                            canvas.drawRect(mCachedRectF, mTodayPaint);
                        }
                        else if (mSelectionType == YearCalendarSelection.Type.CIRCLE)
                        {
                            canvas.drawCircle(mCachedRectF.centerX(), mCachedRectF.centerY(), mCachedRectF.width()/2, mTodayPaint);
                        }

                        canvas.drawText(dayText, dayX + dayWidth / 2 - mCachedRect.width() / 2, dayY, mTodayTextPaint);

                        todayAlreadyDrawn = true;
                    }
                }


                if (!todayAlreadyDrawn)
                {
                    // draw day number text
                    canvas.drawText(dayText, dayX + dayWidth / 2 - mCachedRect.width() / 2, dayY, mDayPaint);
                }
            }
            //

            mMonthRegions.get(i).set(left, top, right, bottom);

            left += mMonthWidth + mMonthSpacing;

            if ((i + 1) % COLUMN_COUNT == 0)
            {
                top += mMonthHeight + mPaddingTopDay;
                left = mCanvasRect.left;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        super.onTouchEvent(event);

        // on touch up
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            for (Integer monthIndex : mMonthRegions.keySet())
            {
                RectF monthRegion = mMonthRegions.get(monthIndex);

                if (monthRegion.contains(event.getX(), event.getY()))
                {
                    if (mTouchListener != null)
                    {
                        mTouchListener.onMonthTap(mCurrentYear, monthIndex);
                    }
                    break;
                }
            }
        }

        return true;
    }

    public void repaint()
    {
        postInvalidate();
    }

    public void setYear(int year)
    {
        Calendar calendar = Calendar.getInstance();
        int minYear = calendar.getActualMinimum(Calendar.YEAR);
        int maxYear = calendar.getActualMaximum(Calendar.YEAR);

        if (year < minYear)
        {
            year = minYear;
        }
        else if (year > maxYear)
        {
            year = maxYear;
        }

        mCurrentYear = year;
        mCurrentYearCalendar.set(mCurrentYear, Calendar.JANUARY, 1, 0, 0, 0);
        repaint();
    }

    public int getYear()
    {
        return mCurrentYear;
    }

    public void showNextYear()
    {
        setYear(++mCurrentYear);
    }

    public void showPreviousYear()
    {
        setYear(--mCurrentYear);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek)
    {
        if (firstDayOfWeek < Calendar.SUNDAY)
        {
            firstDayOfWeek = Calendar.SUNDAY;
        }
        else if (firstDayOfWeek > Calendar.SATURDAY)
        {
            firstDayOfWeek = Calendar.SATURDAY;
        }
        mFirstDayOfWeek = firstDayOfWeek;
        repaint();
    }

    public void setYearCalendarTouchListener(YearCalendarTouchListener listener)
    {
        mTouchListener = listener;
    }

    public Paint getPaint(PaintType paintType)
    {
        switch (paintType)
        {
            case MONTH_TITLE:
                return mMonthTitlePaint;

            case MONTH_BAR:
                return mMonthBarPaint;

            case DAY_TITLE:
                return mDayTitlePaint;

            case DAY:
                return mDayPaint;

            case TODAY:
                return mTodayPaint;

            case TODAY_TEXT:
                return mTodayTextPaint;

        }
        return null;
    }

    public void setMonthSpacing(int monthSpacingPx)
    {
        mMonthSpacing = monthSpacingPx;
        repaint();
    }

    public void setSelectionType(YearCalendarSelection.Type selectionType)
    {
        mSelectionType = selectionType;
        repaint();
    }

    public void setSelectToday(boolean isSelectToday)
    {
        mIsSelectToday = isSelectToday;
        repaint();
    }

    public void addSelection(YearCalendarSelection selection)
    {
        mSelections.add(selection);
    }

    public void clearSelections()
    {
        mSelections.clear();
        repaint();
    }
}
