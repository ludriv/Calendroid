package co.ludriv.calendroid.model;

/**
 * Created by Ludovic on 18/10/2014.
 */
public class Month
{
    private int mYear;
    private int mMonth;

    public Month(int year, int month)
    {
        mYear = year;
        mMonth = month;
    }

    public void setYear(int year)
    {
        mYear = year;
    }

    public int getYear()
    {
        return mYear;
    }

    public void setMonth(int month)
    {
        mMonth = month;
    }

    public int getMonth()
    {
        return mMonth;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Month))
        {
            return false;
        }

        Month oDay = (Month) o;
        return (oDay.mYear == mYear && oDay.mMonth == mMonth);
    }
}
