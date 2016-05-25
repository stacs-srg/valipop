package utils.time;

import java.util.Calendar;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class YearDate implements Date {

    private static final int DAY = 1;
    private final int year;
    private final int MONTH = 1;

    public YearDate(int year) {
        this.year = year;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getMonth() {
        return MONTH;
    }

    @Override
    public int getDay() {
        return DAY;
    }

    @Override
    public java.util.Date getDate() {
        Calendar c = Calendar.getInstance();
        c.set(year, MONTH, DAY);
        return c.getTime();
    }

    @Override
    public DateInstant getInstant() {
        return new DateInstant(DAY, MONTH, year);
    }

    @Override
    public YearDate getYearDate() {
        return this;
    }

    @Override
    public int hashCode() {
        return year;
    }

    @Override
    public boolean equals(Object obj) {
        return this.year == ((YearDate) obj).getYear();
    }
}
