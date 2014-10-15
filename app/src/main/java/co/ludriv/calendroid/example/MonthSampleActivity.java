package co.ludriv.calendroid.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import co.ludriv.calendroid.R;
import co.ludriv.calendroid.utils.CalendarUtils;
import co.ludriv.calendroid.view.MonthCalendarView;

/**
 * Created by Ludovic on 14/10/2014.
 */
public class MonthSampleActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener
{
    private LayoutInflater mInflater;

    private Button   mPrevMonthButton;
    private Button   mNextMonthButton;
    private TextView mCurrentMonthTextView;
    private TextView mCurrentYearTextView;

    private ViewPager mPager;
    private Adapter   mAdapter;

    private int mYear;
    private int mMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        mInflater = LayoutInflater.from(this);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);

        calendar.add(Calendar.MONTH, -1);
        int previousMonth = calendar.get(Calendar.MONTH);

        calendar.add(Calendar.MONTH, +2);
        int nextMonth = calendar.get(Calendar.MONTH);

        String[] months = DateFormatSymbols.getInstance().getShortMonths();

        mPrevMonthButton = (Button) findViewById(R.id.prev_month_button);
        mPrevMonthButton.setText(months[previousMonth]);
        mPrevMonthButton.setOnClickListener(this);

        mNextMonthButton = (Button) findViewById(R.id.next_month_button);
        mNextMonthButton.setText(months[nextMonth]);
        mNextMonthButton.setOnClickListener(this);

        mCurrentMonthTextView = (TextView) findViewById(R.id.current_month_textview);
        mCurrentMonthTextView.setText(months[mMonth]);

        mCurrentYearTextView = (TextView) findViewById(R.id.current_year_textview);
        mCurrentYearTextView.setText(String.valueOf(mYear));

        mAdapter = new Adapter();

        mPager = (ViewPager) findViewById(R.id.months_pager);
        mPager.setPageTransformer(true, new VerticalPageTransformer());
        mPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(CalendarUtils.getEraMonthIndexFromYearMonth(mYear, mMonth));
        mPager.setOnPageChangeListener(this);
        mPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //swap the x and y coords of the touch event
                event.setLocation(event.getY(), event.getX());
                return mPager.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if (mPrevMonthButton.equals(v))
        {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
        }
        else if (mNextMonthButton.equals(v))
        {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2)
    {

    }

    @Override
    public void onPageSelected(int i)
    {
        String[] months = DateFormatSymbols.getInstance().getShortMonths();

        int[] yearMonth = CalendarUtils.getYearMonthFromEraMonthIndex(i);
        mCurrentMonthTextView.setText(months[yearMonth[1]]);
        mCurrentYearTextView.setText(String.valueOf(yearMonth[0]));

        Calendar calendar = Calendar.getInstance();
        calendar.set(yearMonth[0], yearMonth[1], 1, 0, 0, 0);
        calendar.add(Calendar.MONTH, -1);
        mPrevMonthButton.setText(months[calendar.get(Calendar.MONTH)]);

        calendar.add(Calendar.MONTH, +2);
        mNextMonthButton.setText(months[calendar.get(Calendar.MONTH)]);
    }

    @Override
    public void onPageScrollStateChanged(int i)
    {

    }

    class Adapter extends PagerAdapter
    {
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = mInflater.inflate(R.layout.layout_month_widget, null);

            int[] yearMonth = CalendarUtils.getYearMonthFromEraMonthIndex(position);
            //System.out.println("yearMonth= " + yearMonth[0] + " / " + yearMonth[1]);
            ((MonthCalendarView) view).setYearMonth(yearMonth[0], yearMonth[1]);

            mPager.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view)
        {
            ((ViewPager) container).removeView((View) view);
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
}
