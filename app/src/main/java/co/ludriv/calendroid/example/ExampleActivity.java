package co.ludriv.calendroid.example;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import co.ludriv.calendroid.R;
import co.ludriv.calendroid.interfaces.YearCalendarTouchListener;
import co.ludriv.calendroid.model.Selection;
import co.ludriv.calendroid.model.YearCalendarSelection;
import co.ludriv.calendroid.view.YearCalendarView;


public class ExampleActivity extends ActionBarActivity
{

    private TextView         mYearText;
    private YearCalendarView mYearCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mYearCalendar = (YearCalendarView) findViewById(R.id.year_calendar);
        mYearCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mYearCalendar.setMonthSpacing(20);
        mYearCalendar.setSelectToday(true);
        mYearCalendar.setYearCalendarTouchListener(new YearCalendarTouchListener()
        {
            @Override
            public void onMonthTap(int year, int month)
            {
                System.out.println(month + "/" + year);
            }
        });

        mYearCalendar.setSelectionShape(Selection.Shape.CIRCLE);

        Paint monthTitlePaint = mYearCalendar.getPaint(YearCalendarView.PaintType.MONTH_TITLE);
        monthTitlePaint.setColor(Color.parseColor("#00aeef"));

        Paint monthBarPaint = mYearCalendar.getPaint(YearCalendarView.PaintType.MONTH_BAR);
        monthBarPaint.setColor(Color.parseColor("#c6c5c5"));

        Paint dayTitlePaint = mYearCalendar.getPaint(YearCalendarView.PaintType.DAY_TITLE);
        dayTitlePaint.setColor(Color.parseColor("#8b8b8b"));

        Paint dayPaint = mYearCalendar.getPaint(YearCalendarView.PaintType.DAY);
        dayPaint.setColor(Color.parseColor("#353434"));

        Paint todayPaint = mYearCalendar.getPaint(YearCalendarView.PaintType.TODAY);
        todayPaint.setColor(Color.parseColor("#00aeef"));

        Paint todayTextPaint = mYearCalendar.getPaint(YearCalendarView.PaintType.TODAY_TEXT);
        todayTextPaint.setColor(Color.WHITE);

        mYearCalendar.repaint();

        mYearText = (TextView) findViewById(R.id.year);
        mYearText.setText(String.valueOf(mYearCalendar.getYear()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPreviousYear(View view)
    {
        mYearCalendar.showPreviousYear();
        mYearText.setText(String.valueOf(mYearCalendar.getYear()));
    }

    public void showNextYear(View view)
    {
        mYearCalendar.showNextYear();
        mYearText.setText(String.valueOf(mYearCalendar.getYear()));
    }
}
