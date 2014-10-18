package co.ludriv.calendroid.example;

import android.app.Activity;
import android.os.Bundle;

import java.util.Date;

import co.ludriv.calendroid.R;
import co.ludriv.calendroid.interfaces.MonthCalendarWidgetListener;
import co.ludriv.calendroid.model.Day;
import co.ludriv.calendroid.widget.MonthCalendarWidget;

/**
 * Created by Ludovic on 14/10/2014.
 */
public class MonthSampleActivity extends Activity implements MonthCalendarWidgetListener
{
    private MonthCalendarWidget mMonthCalendarWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        mMonthCalendarWidget = (MonthCalendarWidget) findViewById(R.id.sample_month_widget);
        mMonthCalendarWidget.setWidgetListener(this);
    }

    @Override
    public void onMonthChanged(int year, int month)
    {
        //System.out.println("MonthSampleActivity.onMonthChanged");
        //System.out.println("year = [" + year + "], month = [" + month + "]");
    }

    @Override
    public void onDaySelected(Day day)
    {
        //System.out.println("MonthSampleActivity.onDaySelected");
        //System.out.println("date = [" + date + "]");
    }

    @Override
    public void onDayDeselected(Day day)
    {
        //System.out.println("MonthSampleActivity.onDayDeselected");
        //System.out.println("date = [" + date + "]");
    }
}
