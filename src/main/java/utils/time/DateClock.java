package utils.time;

import datastructure.summativeStatistics.structure.IntegerRange;

import java.time.DateTimeException;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class DateClock implements Date {

    protected static final int MONTHS_IN_YEAR = 12;
    private static final int DAY = 1;
    private final int year;
    private final int month;

    public DateClock(int month, int year) {
        if (month <= 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        this.month = month;
        this.year = year;
    }

    public DateClock(String ddmmyyyy) {
        String[] split = ddmmyyyy.split("/");
        month = Integer.parseInt(split[1]);
        if (month <= 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        year = Integer.parseInt(split[2]);
    }

    public DateClock advanceTime(int numberOf, TimeUnit unit) {
        int m = month;
        int y = year;

        switch (unit) {
            case MONTH:
                m += numberOf;
                while (m > MONTHS_IN_YEAR) {
                    y++;
                    m -= MONTHS_IN_YEAR;
                }
                while (m <= 0) {
                    y--;
                    m += MONTHS_IN_YEAR;
                }
                break;
            case YEAR:
                y += numberOf;
                break;
        }
        return new DateClock(m, y);
    }

    public DateClock advanceTime(CompoundTimeUnit timeStep) {
        return advanceTime(timeStep.getCount(), timeStep.getUnit());
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public int getDay() {
        return DAY;
    }

    @Override
    public String toString() {
        return DAY + "/" + month + "/" + year;
    }

    @Override
    public java.util.Date getDate() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, DAY);
        return c.getTime();
    }

    @Override
    public DateInstant getInstant() {
        return new DateInstant(DAY, month, year);
    }

    @Override
    public YearDate getYearDate() {
        return new YearDate(year);
    }

    @Override
    public DateClock getDateClock() throws UnsupportedDateConversion {
        return this;
    }

    @Override
    public int compareTo(Date o) {
        if(DateUtils.dateBefore(this, o)) {
            return -1;
        } else {
            return 1;
        }
    }
}
