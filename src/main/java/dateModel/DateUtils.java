package dateModel;

import dateModel.dateImplementations.AdvancableDate;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtils {

    public static final int[] DAYS_IN_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int DAYS_IN_LEAP_FEB = 29;
    public static final int FEB = 2;
    public static final int MONTHS_IN_YEAR = 12;

    public static final int DAYS_IN_YEAR = 365;
    public static final int DAYS_IN_LEAP_YEAR = 366;



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

        int months = yearDiffInMonths + (b.getMonth() - a.getMonth());

        if(a.getDay() > b.getDay()) {

            int daysInMonthA = getDaysInCurrentMonth(a.getMonth(), a.getYear());
            int daysInMonthB = getDaysInCurrentMonth(b.getMonth(), b.getYear());

            if(daysInMonthB > daysInMonthA && b.getDay() > daysInMonthA && a.getDay() == daysInMonthA) {

            } else {
                months--;
            }
        }

        return new CompoundTimeUnit( months, TimeUnit.MONTH);
    }


    /**
     * If the dates represent the same day in time then the return value is true.
     *
     * @param a The first date
     * @param b The second date
     * @return returns true if date a is before day b (or is the same as date b) else returns false
     */
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

    public static AdvancableDate getEarliestDate(AdvancableDate startDate, AdvancableDate startDate1) {
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
            return (-1) * getDaysInNegativeTimePeriod(date, consideredTimePeriod);
        } else {
            return getDaysInPositiveTimePeriod(date, consideredTimePeriod);
        }

    }


    private static int getDaysInPositiveTimePeriod(Date startingDate, CompoundTimeUnit consideredTimePeriod) {

        int days = 0;

        switch (consideredTimePeriod.getUnit()) {

            case MONTH:

                int year = startingDate.getYear();
                int month = startingDate.getMonth();
                int day = startingDate.getDay();

                for(int i = 0; i < consideredTimePeriod.getCount(); i++) {

                    if(day < getDaysInNextMonth(month, year)) {
                        days += getDaysInCurrentMonth(month, year);

                    } else {
                        int t = getDaysInNextMonth(month, year);
                        days += t;

                    }

                    month ++;
                    if(month >= 13) {
                        month = 1;
                        year++;
                    }
                }

                // ---------------------------------------------------------

//                for(int i = 0; i < consideredTimePeriod.getCount(); i++) {
//
//                    int year = startingDate.getYear();
//                    int month = startingDate.getMonth() + i;
//
//                    if(month > MONTHS_IN_YEAR) {
//                        int y = (month - 1) / MONTHS_IN_YEAR;
//                        year += y;
//                        month -= y * MONTHS_IN_YEAR;
//                    }
//
//
////                    if(month == FEB) {
////
////                        if(isLeapYear(year)) {
////                            days += DAYS_IN_LEAP_FEB;
////                        } else {
////                            days += DAYS_IN_MONTH[FEB - 1];
////                        }
////
////                    } else {
//                       int tMonth = month;
//                        if(month == 12) {
//                            tMonth = 0;
//                        }
//                        if(startingDate.getDay() > DAYS_IN_MONTH[tMonth]) {
//                            if(tMonth == 1) {
//                                if(isLeapYear(year)) {
//                                    days += DAYS_IN_LEAP_FEB;
//                                } else {
//                                    days += DAYS_IN_MONTH[tMonth];
//                                }
//                            } else {
//                                days += DAYS_IN_MONTH[tMonth];
//                            }
//                        } else {
//                            if(tMonth == 1) {
//                                if(isLeapYear(year)) {
//                                    days += DAYS_IN_LEAP_FEB;
//                                } else {
//                                    days += DAYS_IN_MONTH[tMonth];
//                                }
//                            } else {
//                                days += DAYS_IN_MONTH[month - 1];
//                            }
//                        }
////                    }
//
//                }

                break;
            case YEAR:

                for(int i = 0; i < consideredTimePeriod.getCount(); i++) {

                    year = startingDate.getYear() + i;

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

    private static int getDaysInCurrentMonth(int currentMonth, int year) {

        if (currentMonth == FEB && isLeapYear(year)) {
            return DAYS_IN_LEAP_FEB;
        } else {
            // We're making a transfer from months denoted from 1-12 to 0-11, hence the -1
            return DAYS_IN_MONTH[currentMonth - 1];
        }

    }

    private static int getDaysInNextMonth(int currentMonth, int year) {

        if (currentMonth + 1 == FEB && isLeapYear(year)) {
            return DAYS_IN_LEAP_FEB;
        } else {
            // We're making a transfer from months denoted from 1-12 to 0-11, so really the index is:
            // DAYS_IN_MONTH[ (currentMonth + 1) - 1 ]
            if(currentMonth == 12) {
                return DAYS_IN_MONTH[0];
            } else {
                return DAYS_IN_MONTH[currentMonth];
            }
        }
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
                            if(startingDate.getDay() > DAYS_IN_LEAP_FEB) {
                                days += DAYS_IN_MONTH[month - 1];
                            } else {
                                days += DAYS_IN_LEAP_FEB;
                            }
                        } else {
                            if(startingDate.getDay() > DAYS_IN_MONTH[FEB - 1]) {
                                days += DAYS_IN_MONTH[month - 1];
                            } else {
                                days += DAYS_IN_MONTH[month - 2];
                            }
                        }

                    } else {
                        if(month == 1) {
                            if(startingDate.getDay() >= DAYS_IN_MONTH[month - 1]) {
                                days += DAYS_IN_MONTH[month - 1];
                            } else {
                                days += DAYS_IN_MONTH[11];
                            }
                        } else if(month == FEB) {
                            if(startingDate.getDay() >= DAYS_IN_MONTH[month - 1]) {
                                if(isLeapYear(year)) {
                                    days += DAYS_IN_LEAP_FEB;
                                } else {
                                    days += DAYS_IN_MONTH[month - 1];
                                }
                            } else {
                                days += DAYS_IN_MONTH[month - 2];
                            }
                        } else {
                            if(startingDate.getDay() >= DAYS_IN_MONTH[month - 1]) {
                                days += DAYS_IN_MONTH[month - 1];
                            } else {
                                days += DAYS_IN_MONTH[month - 2];
                            }
                        }
                    }

                }

                break;
            case YEAR:

                for(int i = 0; i < Math.abs(consideredTimePeriod.getCount()); i++) {

                    int year = startingDate.getYear() - i;

                    // Does the year stradle the potential leap day
                    if(startingDate.getMonth() == FEB && startingDate.getDay() != DAYS_IN_LEAP_FEB
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

    public static ExactDate calculateExactDate(Date date, int chosenDay) {

        if(chosenDay < 0) {
            return calculateBWExactDate(date, Math.abs(chosenDay));
        } else {
            return calculateFWExactDate(date, chosenDay);
        }


    }

    private static ExactDate calculateFWExactDate(Date startingDate, int chosenDay) {

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

        return new ExactDate(day, month, year);
    }

    private static ExactDate calculateBWExactDate(Date endingDate, int chosenDay) {

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

        return new ExactDate(day, month, year);
    }

    public static int differenceInDays(Date birthDate, Date latestDate) {

        if(dateBefore(latestDate, birthDate)) {
            Date temp = latestDate;
            latestDate = birthDate;
            birthDate = temp;
        }

        int day = birthDate.getDay();
        int month = birthDate.getMonth();
        int year = birthDate.getYear();

        int counts = 0;

        if(day != 1) {
            counts += (getDaysInCurrentMonth(month, year) - day);
            month ++;
        }

        for(int y = year; y < latestDate.getYear(); y++) {

            for(int m = month; m <= MONTHS_IN_YEAR; m++) {
                counts += getDaysInCurrentMonth(m, y);
            }

            month = 1;

        }

        for(int m = month; m < latestDate.getMonth(); m++) {
            counts += getDaysInCurrentMonth(m, latestDate.getYear());
        }

        if(day != latestDate.getDay()) {
            counts += latestDate.getDay() - 1;
        }


        return counts;
    }

    public static int stepsInYear(CompoundTimeUnit timeStep) {

        if(timeStep.getUnit() == TimeUnit.YEAR) {
            return MONTHS_IN_YEAR * timeStep.getCount();
        } else { // unit == MONTH
            return MONTHS_IN_YEAR / timeStep.getCount();
        }

    }

    public static int calcSubTimeUnitsInTimeUnit(CompoundTimeUnit subTimeUnit, CompoundTimeUnit timeUnit){

        double n = monthsInTimeUnit(timeUnit) / (double) monthsInTimeUnit(subTimeUnit);

        if(n % 1 == 0) {
            return (int) Math.floor(n);
        }

        return -1;

    }

    private static int monthsInTimeUnit(CompoundTimeUnit timeUnit) {
        if(timeUnit.getUnit() == TimeUnit.MONTH) {
            return timeUnit.getCount();
        } else {
            return timeUnit.getCount() * MONTHS_IN_YEAR;
        }
    }
}
