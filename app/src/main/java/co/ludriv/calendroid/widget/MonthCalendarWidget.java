package co.ludriv.calendroid.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.util.Predicate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import co.ludriv.calendroid.R;
import co.ludriv.calendroid.interfaces.MonthCalendarViewListener;
import co.ludriv.calendroid.interfaces.MonthCalendarWidgetListener;
import co.ludriv.calendroid.model.CalendarEvent;
import co.ludriv.calendroid.model.Day;
import co.ludriv.calendroid.model.Selection;
import co.ludriv.calendroid.utils.CalendarUtils;
import co.ludriv.calendroid.view.MonthCalendarView;

/**
 * Created by Ludovic on 18/10/2014.
 */
public class MonthCalendarWidget extends LinearLayout implements View.OnClickListener, ViewPager.OnPageChangeListener, MonthCalendarViewListener
{
    private Button    mPreviousButton;
    private TextView  mMonthTextView;
    private TextView  mYearTextView;
    private Button    mNextButton;
    private ViewPager mPager;
    private Adapter   mPagerAdapter;

    private String[]                 mMonths;
    private int                      mYear;
    private int                      mMonth;
    private ArrayList<Day>           mSelectedDays;
    private ArrayList<CalendarEvent> mEvents;

    private MonthCalendarWidgetListener mWidgetListener;

    private boolean mEnableSelectDays = false;
    private Day     mMiminumSelectDay = null;
    private Day     mMaximumSelectDay = null;

    private Selection.Shape mTodayShape = Selection.Shape.CIRCLE;


    public MonthCalendarWidget(Context context)
    {
        super(context);
        init();
    }

    public MonthCalendarWidget(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MonthCalendarWidget(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.widget_month_calendar, this);

        mMonths = DateFormatSymbols.getInstance().getShortMonths();

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);

        calendar.add(Calendar.MONTH, -1);
        int previousMonth = calendar.get(Calendar.MONTH);

        calendar.add(Calendar.MONTH, +2);
        int nextMonth = calendar.get(Calendar.MONTH);

        mSelectedDays = new ArrayList<Day>();
        mEvents = new ArrayList<CalendarEvent>();

        mPreviousButton = (Button) findViewById(R.id.widget_month_previous_button);
        mPreviousButton.setOnClickListener(this);
        mPreviousButton.setText(mMonths[previousMonth]);

        mMonthTextView = (TextView) findViewById(R.id.widget_month_month_textview);
        mMonthTextView.setText(mMonths[mMonth]);

        mYearTextView = (TextView) findViewById(R.id.widget_month_year_textivew);
        mYearTextView.setText(String.valueOf(mYear));

        mNextButton = (Button) findViewById(R.id.widget_month_next_button);
        mNextButton.setOnClickListener(this);
        mNextButton.setText(mMonths[nextMonth]);


        mPagerAdapter = new Adapter();

        mPager = (ViewPager) findViewById(R.id.widget_month_pager);
        mPager.setPageTransformer(true, new VerticalPageTransformer());
        mPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(CalendarUtils.getEraMonthIndexFromYearMonth(mYear, mMonth));
        mPager.setOnPageChangeListener(this);
        mPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // swap the x and y coords of the touch event
                event.setLocation(event.getY(), event.getX());
                return mPager.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if (mPreviousButton.equals(v))
        {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
        }
        else if (mNextButton.equals(v))
        {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i)
    {
        int[] yearMonth = CalendarUtils.getYearMonthFromEraMonthIndex(i);

        mMonthTextView.setText(mMonths[yearMonth[1]]);
        mYearTextView.setText(String.valueOf(yearMonth[0]));

        Calendar calendar = Calendar.getInstance();
        calendar.set(yearMonth[0], yearMonth[1], 1, 0, 0, 0);
        calendar.add(Calendar.MONTH, -1);
        mPreviousButton.setText(mMonths[calendar.get(Calendar.MONTH)]);

        calendar.add(Calendar.MONTH, +2);
        mNextButton.setText(mMonths[calendar.get(Calendar.MONTH)]);

        if (mWidgetListener != null)
        {
            mWidgetListener.onMonthChanged(yearMonth[0], yearMonth[1]);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    @Override
    public void onDaySelected(Day day)
    {
        mSelectedDays.add(day);

        if (mWidgetListener != null)
        {
            mWidgetListener.onDaySelected(day);
        }
    }

    @Override
    public void onDayDeselected(Day day)
    {
        mSelectedDays.remove(day);

        if (mWidgetListener != null)
        {
            mWidgetListener.onDayDeselected(day);
        }
    }

    class Adapter extends PagerAdapter
    {
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            int[] yearMonth = CalendarUtils.getYearMonthFromEraMonthIndex(position);

            MonthCalendarView view = new MonthCalendarView(getContext());
            view.setYearMonth(yearMonth[0], yearMonth[1]);


            /*
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 2);
            CalendarEvent event = new CalendarEvent(calendar.getTime(), "My event");
            event.setShape(Selection.Shape.SQUARE);
            event.getPaint().setColor(Color.RED);
            event.getPaint().setStyle(Paint.Style.FILL);
            view.addEvent(event);

            calendar.set(Calendar.DAY_OF_MONTH, 19);
            CalendarEvent event2 = new CalendarEvent(calendar.getTime(), "My party");
            event2.setShape(Selection.Shape.CIRCLE);
            event2.getPaint().setColor(Color.BLUE);
            event2.getPaint().setStyle(Paint.Style.FILL);
            view.addEvent(event2);
            */

            //view.restoreSelectedDates(getSelectedDatesInMonth(yearMonth[0], yearMonth[1]));

            mPager.addView(view);
            return view;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object)
        {
            super.setPrimaryItem(container, position, object);

            MonthCalendarView calendarView = (MonthCalendarView) object;

            calendarView.setSelectToday(true);
            calendarView.setTodaySelectionShape(mTodayShape);
            calendarView.addAllEvents(mEvents);

            if (mEnableSelectDays)
            {
                calendarView.enableDayTouchListener(MonthCalendarWidget.this);

                calendarView.setMinimumSelectDay(mMiminumSelectDay);
                calendarView.setMaximumSelectDay(mMaximumSelectDay);
            }

            calendarView.clearSelectedDates();
            calendarView.restoreSelectedDays(getSelectedDays());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view)
        {
            mPager.removeView((View) view);
        }

        @Override
        public int getCount()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o)
        {
            return (view == o);
        }
    }

    //
    // thanks to: http://stackoverflow.com/questions/13477820/android-vertical-viewpager
    //
    private class VerticalPageTransformer implements ViewPager.PageTransformer
    {
        @Override
        public void transformPage(View view, float position)
        {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1)
            {
                // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            }
            else if (position <= 1)
            {
                // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                //set Y position to swipe in from top
                float yPosition = position * pageHeight;
                view.setTranslationY(yPosition);
            }
            else
            {
                // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private HashSet<Date> getSelectedDatesInMonth(final int year, final int month)
    {
        Predicate<Day> predicate = new Predicate<Day>()
        {
            @Override
            public boolean apply(Day day)
            {
                return (day.getYear() == year && day.getMonth() == month);
            }
        };

        HashSet hashSet = new HashSet();
        for (Day day : mSelectedDays)
        {
            if (predicate.apply(day))
            {
                hashSet.add(day);
            }
        }
        return hashSet;
    }

    public void setWidgetListener(MonthCalendarWidgetListener listener)
    {
        mWidgetListener = listener;
    }

    public void showTodayMonth(boolean smoothScroll)
    {
        mPager.setCurrentItem(CalendarUtils.getEraMonthIndexFromYearMonth(mYear, mMonth), smoothScroll);
    }

    public List<Day> getSelectedDays()
    {
        return mSelectedDays;
    }

    public void enableDaySelection()
    {
        mEnableSelectDays = true;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void disableDaySelection()
    {
        mEnableSelectDays = false;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setMinimumSelectDay(Day minimumDay)
    {
        mMiminumSelectDay = minimumDay;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setMaximumSelectDay(Day maximumDay)
    {
        mMaximumSelectDay = maximumDay;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void enableDaySelection(Day minimumDay, Day maximumDay)
    {
        mEnableSelectDays = true;
        mMiminumSelectDay = minimumDay;
        mMaximumSelectDay = maximumDay;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setTodayShape(Selection.Shape shape)
    {
        mTodayShape = shape;
    }

    public void setEvents(ArrayList<CalendarEvent> events)
    {
        mEvents = events;
        mPagerAdapter.notifyDataSetChanged();
    }

}
