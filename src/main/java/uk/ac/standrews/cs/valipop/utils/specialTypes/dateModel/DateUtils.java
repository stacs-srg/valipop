/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel;

import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.time.DateTimeException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtils {

    private static final int[] DAYS_IN_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int DAYS_IN_LEAP_FEB = 29;
    public static final int FEB = 2;
    public static final int MONTHS_IN_YEAR = 12;

    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_LEAP_YEAR = 366;

    public static int getDaysInMonthNonLeapYear(int month) {
        // Jan 1 > Dec 12

        if(month < 1 || month > 12) {
            throw new DateTimeException("Month number invalid");
        } else {
            return DAYS_IN_MONTH[month - 1];
        }

    }


    public static CompoundTimeUnit differenceInYears(Date a, Date b) {

        int months = differenceInMonths(a, b).getCount();

        return new CompoundTimeUnit(months / MONTHS_IN_YEAR, TimeUnit.YEAR);
    }

    public static CompoundTimeUnit differenceInMonths(Date a, Date b) {

        if (dateBeforeOrEqual(b, a)) {
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

    public static boolean datesEqual(Date a, Date b) {
        return a.getDay() == b.getDay() && a. getMonth() == b.getMonth() && a.getYear() == b.getYear();
    }

    /**
     * If the dates represent the same day in time then the return value is true.
     *
     * @param a The first date
     * @param b The second date
     * @return returns true if date a is before day b (or is the same as date b) else returns false
     */
    public static boolean dateBeforeOrEqual(Date a, Date b) {

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

    public static boolean dateBefore(Date a, Date b) {

        if (a.getYear() < b.getYear()) {
            return true;
        } else if (a.getYear() == b.getYear()) {
            if (a.getMonth() < b.getMonth()) {
                return true;
            } else if (a.getMonth() == b.getMonth()) {
                return a.getDay() < b.getDay();
            } else {
                return false;
            }
        } else {
            return false;
        }


    }




    public static boolean matchesInterval(Date currentDate, CompoundTimeUnit interval, Date startDate) {

        int dM = differenceInMonths(startDate, currentDate).getCount();

        return dM % monthsInTimeUnit(interval) == 0;

    }

    public static AdvancableDate getEarliestDate(AdvancableDate startDate, AdvancableDate startDate1) {
        if (dateBeforeOrEqual(startDate, startDate1)) {
            return startDate;
        } else {
            return startDate1;
        }
    }

    public static Date getLatestDate(Date startDate, Date startDate1) {
        if (dateBeforeOrEqual(startDate, startDate1)) {
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

        if(dateBeforeOrEqual(latestDate, birthDate)) {
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

    public static double stepsInYear(CompoundTimeUnit timeStep) {

        if(timeStep.getUnit() == TimeUnit.YEAR) {
            return 1 / (double) timeStep.getCount();
        } else { // unit == MONTH
            return MONTHS_IN_YEAR / (double) timeStep.getCount();
        }

    }

    public static int calcSubTimeUnitsInTimeUnit(CompoundTimeUnit subTimeUnit, CompoundTimeUnit timeUnit) {

        // div by 0?
        double n = monthsInTimeUnit(timeUnit) / (double) monthsInTimeUnit(subTimeUnit);

        if(n % 1 == 0) {
            return (int) Math.floor(n);
        }

        return -1;

    }

    public static int monthsInTimeUnit(CompoundTimeUnit timeUnit) {
        if(timeUnit.getUnit() == TimeUnit.MONTH) {
            return timeUnit.getCount();
        } else {
            return timeUnit.getCount() * MONTHS_IN_YEAR;
        }
    }

    public static boolean dateInYear(Date date, YearDate year) {

        return !DateUtils.dateBefore(date, year) && DateUtils.dateBefore(date, year.advanceTime(1, TimeUnit.YEAR));

    }
}
