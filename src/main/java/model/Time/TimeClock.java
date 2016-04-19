package model.Time;

import java.time.DateTimeException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TimeClock {

    private static final int MONTHS_IN_YEAR = 12;

    private int year = 0;
    private int month = 1;
    private static final int DAY = 1;

    public static void main(String[] args) {

        TimeClock time = new TimeClock(1, 1540);
        time.advanceTime(10, TimeUnit.YEAR);
        System.out.println(time.toString());
        time.advanceTime(10, TimeUnit.MONTH);
        System.out.println(time.toString());
        time.advanceTime(2, TimeUnit.MONTH);
        System.out.println(time.toString());

        System.out.println(time.getMonth());
        System.out.println(time.getYear());


    }

    public TimeClock(int month, int year) {
        if(month == 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        this.month = month;
        this.year = year;
    }

    public void advanceTime(int numberOf, TimeUnit unit) {
        switch (unit) {
            case MONTH:
                month += numberOf;
                checkMonth();
                break;
            case YEAR:
                year += numberOf;
                break;
        }
    }

    public void checkMonth() {
        if(month > MONTHS_IN_YEAR) {
            month = month - MONTHS_IN_YEAR;
            year += 1;
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String toString() {
        return DAY + "/" + month + "/" + year;
    }

    public boolean isLeapYear() {

        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if(year % 400 == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

}
