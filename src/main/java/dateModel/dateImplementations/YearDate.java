package dateModel.dateImplementations;

import dateModel.Date;
import dateModel.DateUtils;
import dateModel.exceptions.UnsupportedDateConversion;

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

    public String toString() {
        return "1/1/" + year;
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
    public ExactDate getExactDate() {
        return new ExactDate(DAY, MONTH, year);
    }

    @Override
    public YearDate getYearDate() {
        return this;
    }

    @Override
    public DateClock getDateClock() throws UnsupportedDateConversion {
        return new DateClock(MONTH, year);
    }

    @Override
    public DateClock getDateClock(boolean force) throws UnsupportedDateConversion {
        return getDateClock();
    }

    @Override
    public int hashCode() {
        return year;
    }

    @Override
    public boolean equals(Object obj) {
        return this.year == ((YearDate) obj).getYear();
    }

    @Override
    public String toOrderableString() {
        return year + "_" + MONTH + "_" + DAY;
    }

    @Override
    public int compareTo(Date o) {
        if (DateUtils.dateBefore(this, o)) {
            return -1;
        } else {
            return 1;
        }
    }
}
