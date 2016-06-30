package utils.time;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtils {

    protected static final int[] DAYS_IN_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    protected static final int DAYS_IN_LEAP_FEB = 29;
    protected static final int FEB = 2;
    protected static final int MONTHS_IN_YEAR = 12;

    protected static final int DAYS_IN_YEAR = 365;
    protected static final int DAYS_IN_LEAP_YEAR = 366;



    public static CompoundTimeUnit differenceInYears(Date a, Date b) {

        int months = differenceInMonths(a, b).getCount();

        return new CompoundTimeUnit(months / MONTHS_IN_YEAR, TimeUnit.YEAR);
    }

    public static CompoundTimeUnit differenceInMonths(Date a, Date b) {

        if (dateBefore(b, a)) {
            Date temp = a;
            a = b;
            b = temp;
        }

        int yearDiffInMonths = MONTHS_IN_YEAR * Math.abs(a.getYear() - b.getYear());

        return new CompoundTimeUnit(yearDiffInMonths + (b.getMonth() - a.getMonth()), TimeUnit.MONTH);
    }


    public static boolean dateBefore(Date a, Date b) {

        if (a.getYear() < b.getYear()) {
            return true;
        } else if (a.getYear() == b.getYear()) {
            if (a.getMonth() < b.getMonth()) {
                return true;
            } else if (a.getMonth() == b.getMonth()) {
                return a.getDay() <= b.getDay();
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

    public static boolean matchesInterval(Date date, CompoundTimeUnit interval) {

        // If year interval and first month of year
        if (interval.getUnit() == TimeUnit.YEAR) {
            if (date.getMonth() == 1) {
                // Checks year based on remainder from the year 0
                if (date.getYear() % interval.getCount() == 0) {
                    return true;
                }
            }
        }

        // If month interval
        if (interval.getUnit() == TimeUnit.MONTH) {
            // Offset by one to make month 1, 4, 7, 10 the months for the quarterly interval
            if ((date.getMonth() - 1) % interval.getCount() == 0) {
                return true;
            }
        }

        return false;

    }

    public static Date getEarliestDate(Date startDate, Date startDate1) {
        if (dateBefore(startDate, startDate1)) {
            return startDate;
        } else {
            return startDate1;
        }
    }

    public static Date getLatestDate(Date startDate, Date startDate1) {
        if (dateBefore(startDate, startDate1)) {
            return startDate1;
        } else {
            return startDate;
        }
    }

    public static boolean isLeapYear(int year) {

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

    /**
     * Counts the number of days in the following time period given the starting date of the time period. The returned
     * count is inclusive the first date given.
     *
     * @param date the day to count from
     * @param consideredTimePeriod the number of months/years to count days for
     * @return The number of days, inclusive of the starting day
     */
    public static int getDaysInTimePeriod(Date date, CompoundTimeUnit consideredTimePeriod) {

        if(consideredTimePeriod.getCount() < 0) {
                return getDaysInNegativeTimePeriod(date, consideredTimePeriod);
        } else {
            return getDaysInPositiveTimePeriod(date, consideredTimePeriod);
        }

    }


    private static int getDaysInPositiveTimePeriod(Date startingDate, CompoundTimeUnit consideredTimePeriod) {

        int days = 0;

        switch (consideredTimePeriod.getUnit()) {

            case MONTH:

                for(int i = 0; i < consideredTimePeriod.getCount(); i++) {

                    int year = startingDate.getYear();
                    int month = startingDate.getMonth() + i;

                    if(month > MONTHS_IN_YEAR) {
                        int y = (month - 1) / MONTHS_IN_YEAR;
                        year += y;
                        month -= y * MONTHS_IN_YEAR;
                    }


                    if(month == FEB) {

                        if(isLeapYear(year)) {
                            days += DAYS_IN_LEAP_FEB;
                        } else {
                            days += DAYS_IN_MONTH[FEB - 1];
                        }

                    } else {
                        days += DAYS_IN_MONTH[month - 1];
                    }

                }

                break;
            case YEAR:

                for(int i = 0; i < consideredTimePeriod.getCount(); i++) {

                    int year = startingDate.getYear() + i;

                    // Does the year stradle the potential leap day
                    if(startingDate.getMonth() == FEB && startingDate.getDay() == DAYS_IN_LEAP_FEB
                            || startingDate.getMonth() > FEB) {
                        year++;
                    }

                    if(isLeapYear(year)) {
                        days += DAYS_IN_LEAP_YEAR;
                    } else {
                        days += DAYS_IN_YEAR;
                    }

                }

                break;

        }


        return days;
    }

    private static int getDaysInNegativeTimePeriod(Date startingDate, CompoundTimeUnit consideredTimePeriod) {

        int days = 0;

        switch (consideredTimePeriod.getUnit()) {

            case MONTH:

                for(int i = 0; i < Math.abs(consideredTimePeriod.getCount()); i++) {

                    int year = startingDate.getYear();
                    int month = startingDate.getMonth() - i;


                    if(month < 1) {
                        int y =  month / MONTHS_IN_YEAR - 1;
                        year += y;
                        month += MONTHS_IN_YEAR;
                    }


                    if(month - 1 == FEB) {

                        if(isLeapYear(year)) {
                            days += DAYS_IN_LEAP_FEB;
                        } else {
                            days += DAYS_IN_MONTH[FEB - 1];
                        }

                    } else {
                        if(month == 1) {
                            days += DAYS_IN_MONTH[11];
                        } else {
                            days += DAYS_IN_MONTH[month - 2];
                        }
                    }

                }

                break;
            case YEAR:

                for(int i = 0; i < Math.abs(consideredTimePeriod.getCount()); i++) {

                    int year = startingDate.getYear() - i;

                    // Does the year stradle the potential leap day
                    if(startingDate.getMonth() == FEB && startingDate.getDay() == DAYS_IN_LEAP_FEB
                            || startingDate.getMonth() < FEB) {
                        year--;
                    }

                    if(isLeapYear(year)) {
                        days += DAYS_IN_LEAP_YEAR;
                    } else {
                        days += DAYS_IN_YEAR;
                    }

                }

                break;

        }


        return days;
    }

    public static DateInstant calculateDateInstant(Date date, int chosenDay) {

        if(chosenDay < 0) {
            return calculateBWDateInstant(date, Math.abs(chosenDay));
        } else {
            return calculateFWDateInstant(date, chosenDay);
        }


    }

    public static DateInstant calculateFWDateInstant(Date startingDate, int chosenDay) {

        int day = startingDate.getDay();
        int month = startingDate.getMonth();
        int year = startingDate.getYear();


        while (chosenDay != 0) {

            int daysLeft;

            // get days left in current month
            if(month == FEB) {
                if(isLeapYear(year)) {
                    daysLeft = DAYS_IN_LEAP_FEB - day;
                } else {
                    daysLeft = DAYS_IN_MONTH[month-1] - day;
                }
            } else {
                daysLeft = DAYS_IN_MONTH[month-1] - day;
            }

            if(chosenDay < daysLeft) {
                day += chosenDay;
                chosenDay = 0;
            } else {
                chosenDay -= daysLeft;
                day = 1;
                if(chosenDay != 0) {
                    chosenDay--;
                }
                month++;
                if(month > 12) {
                    month = 1;
                    year++;
                }
            }
        }

        return new DateInstant(day, month, year);
    }

    public static DateInstant calculateBWDateInstant(Date endingDate, int chosenDay) {

        int day = endingDate.getDay();
        int month = endingDate.getMonth();
        int year = endingDate.getYear();


        while (chosenDay != 0) {

            int daysLeft = day;

            // get days left in current month

            if(chosenDay < daysLeft) {
                day -= chosenDay;
                chosenDay = 0;
            } else {
                chosenDay -= daysLeft;

                month--;
                if(month < 1) {
                    month = 12;
                    year--;
                }

                if(month == FEB) {
                    if(isLeapYear(year)) {
                        day = DAYS_IN_LEAP_FEB;
                    } else {
                        day = DAYS_IN_MONTH[month-1];
                    }
                } else {
                    day = DAYS_IN_MONTH[month-1];
                }

//                if(chosenDay != 0) {
//                    chosenDay--;
//                }
            }
        }

        return new DateInstant(day, month, year);
    }
}
