package co.ludriv.calendroid.interfaces;

import co.ludriv.calendroid.model.Day;

/**
 * Created by Ludovic on 14/10/2014.
 */
public interface MonthCalendarViewListener
{
    public void onDaySelected(Day day);

    public void onDayDeselected(Day day);
}
