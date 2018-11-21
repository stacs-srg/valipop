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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations;

import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.ValipopDate;

import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;

import static uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils.getDaysInMonth;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class ExactDate implements ValipopDate {

    private final int year;
    private final int month;
    private final int day;

    private final String representation;
    private final Date real_date;

    private static final Calendar CALENDAR = Calendar.getInstance();

    public ExactDate(int day, int month, int year) {

        this.day = day;
        this.month = month;
        this.year = year;

        checkBounds(day, month, year);
        real_date = makeDate(day, month, year);
        representation = makeDateString(day, month, year);
    }

    public ExactDate(ValipopDate date) {
        this(date.getDay(), date.getMonth(), date.getYear());
    }

    public ExactDate advanceTime(int numberOfDays) {

        int day = this.day;
        int month = this.month;
        int year = this.year;

        while (numberOfDays >= 0) {

            int daysLeftInMonth = getDaysInMonth(month, year) - day;

            if (daysLeftInMonth > numberOfDays) {
                return new ExactDate(day + numberOfDays, month, year);

            } else {
                numberOfDays -= daysLeftInMonth;
                day = 1;

                if (month == 12) {
                    month = 1;
                    year += 1;
                } else {
                    month += 1;
                }
            }
        }

        throw new IllegalArgumentException("Advancing of time failed - for days - numberOfDays = " + numberOfDays);
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
        return representation;
    }

    @Override
    public Date getDate() {

        return real_date;
    }

    @Override
    public ExactDate getExactDate() {
        return this;
    }

    @Override
    public YearDate getYearDate() {
        return new YearDate(year);
    }

    @Override
    public MonthDate getMonthDate() {
        return new MonthDate(month, year);
    }

    @Override
    public int compareTo(ValipopDate other) {

        if (DateUtils.dateBefore(this, other)) return -1;
        if (DateUtils.datesEqual(this, other)) return 0;

        return 1;
    }

    private static synchronized Date makeDate(int day, int month, int year) {

        CALENDAR.clear();
        CALENDAR.set(year, month - 1, day, 12, 0, 0);
        return CALENDAR.getTime();
    }

    private void checkBounds(int day, int month, int year) {

        if (day < 1) throw new DateTimeException("illegal day: " + day);
        if (month < 1) throw new DateTimeException("illegal month: " + month);
        if (month > 12) throw new DateTimeException("illegal month: " + month);

        if (day > getDaysInMonth(month, year)) throw new DateTimeException("too many days in month: " + day);
    }

    static String makeDateString(int day, int month, int year) {

        return pad(day, 2) + "/" + pad(month, 2) + "/" + pad(year, 4);
    }

    private static String pad(int i, int width) {

        String result = String.valueOf(i);
        while (result.length() < width) {
            result = "0" + result;
        }
        return result;
    }
}
