package utils.time;

import java.time.DateTimeException;
import java.util.Calendar;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class DateInstant implements Date {

    private static final int[] DAYS_IN_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int DAYS_IN_LEAP_FEB = 29;
    private static final int FEB = 2;

    private final int year;
    private final int month;
    private final int day;

    public DateInstant(int day, int month, int year) {
        if (month <= 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        if (day <= 0 || day > DAYS_IN_MONTH[month - 1]) {
            if (month != FEB || (isLeapYear(year) && day != DAYS_IN_LEAP_FEB) || (!isLeapYear(year) && day >= DAYS_IN_LEAP_FEB)) {
                throw new DateTimeException("Days should be indexed between 1 and the number of days in the given month inclusive.");
            }
        }

        this.day = day;
        this.month = month;
        this.year = year;
    }

    public DateInstant(String ddmmyyyy) {
        String[] split = ddmmyyyy.split("/");

        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);

        if (month <= 0 || month > 12) {
            month = 0;
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        if (day <= 0 || day > DAYS_IN_MONTH[month - 1]) {
            if (month != FEB || (isLeapYear(year) && day != DAYS_IN_LEAP_FEB || (!isLeapYear(year) && day >= DAYS_IN_LEAP_FEB))) {
                throw new DateTimeException("Days should be indexed between 1 and the number of days in the given month inclusive.");
            }
        }

        this.day = day;
        this.month = month;
        this.year = year;

    }

    private static boolean isLeapYear(int year) {

        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return year % 400 == 0;
            } else {
                return true;
            }
        } else {
            return false;
        }
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
        return day;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }

    @Override
    public java.util.Date getDate() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTime();
    }

    @Override
    public DateInstant getInstant() {
        return this;
    }

    @Override
    public YearDate getYearDate() {
        return new YearDate(year);
    }

    @Override
    public DateClock getDateClock() throws UnsupportedDateConversion {
        throw new UnsupportedDateConversion("Cannot convert from DateInstant to DateClock due to the resulting loss information regarding the day of the month");
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
