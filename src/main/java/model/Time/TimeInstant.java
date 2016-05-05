package model.time;

import java.time.DateTimeException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class TimeInstant {

    private static final int MONTHS_IN_YEAR = 12;

    private final int year;
    private final int month;
    private static final int DAY = 1;

    public TimeInstant(int month, int year) {
        if(month == 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        this.month = month;
        this.year = year;
    }

    public TimeInstant(String ddmmyyyy) {
        String[] split = ddmmyyyy.split("/");
        month = Integer.parseInt(split[1]);
        if(month == 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        year = Integer.parseInt(split[2]);
    }

    public TimeInstant advanceTime(int numberOf, TimeUnit unit) {
        int m = month;
        int y = year;

        switch (unit) {
            case MONTH:
                m += numberOf;
                while(m > MONTHS_IN_YEAR) {
                    y++;
                    m -= MONTHS_IN_YEAR;
                }
                break;
            case YEAR:
                y += numberOf;
                break;
        }
        return new TimeInstant(m, y);
    }

    public TimeInstant advanceTime(CompoundTimeUnit timeStep) {
        return advanceTime(timeStep.getCount(), timeStep.getUnit());
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

}
