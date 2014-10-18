package co.ludriv.calendroid.interfaces;

import java.util.Date;

import co.ludriv.calendroid.model.Day;

/**
 * Created by Ludovic on 14/10/2014.
 */
public interface MonthCalendarWidgetListener
{
    public void onMonthChanged(int year, int month);

    public void onDaySelected(Day day);

    public void onDayDeselected(Day day);
}
