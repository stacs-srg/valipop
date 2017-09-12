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
package uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;

import java.time.DateTimeException;
import java.util.Calendar;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class ExactDate implements Date {



    private final int year;
    private final int month;
    private final int day;

    public ExactDate(int day, int month, int year) {
        if (month <= 0 || month > 12) {
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        if (day <= 0 || day > DateUtils.DAYS_IN_MONTH[month - 1]) {
            if (month != DateUtils.FEB || (DateUtils.isLeapYear(year) && day != DateUtils.DAYS_IN_LEAP_FEB) || (!DateUtils.isLeapYear(year) && day >= DateUtils.DAYS_IN_LEAP_FEB)) {
                throw new DateTimeException("Days should be indexed between 1 and the number of days in the given month inclusive.");
            }
        }

        this.day = day;
        this.month = month;
        this.year = year;
    }

    public ExactDate(String ddmmyyyy) {
        String[] split = ddmmyyyy.split("/");

        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);

        if (month <= 0 || month > 12) {
            month = 0;
            throw new DateTimeException("Months should be indexed between 1 and 12 inclusive.");
        }
        if (day <= 0 || day > DateUtils.DAYS_IN_MONTH[month - 1]) {
            if (month != DateUtils.FEB || (DateUtils.isLeapYear(year) && day != DateUtils.DAYS_IN_LEAP_FEB || (!DateUtils.isLeapYear(year) && day >= DateUtils.DAYS_IN_LEAP_FEB))) {
                throw new DateTimeException("Days should be indexed between 1 and the number of days in the given month inclusive.");
            }
        }

        this.day = day;
        this.month = month;
        this.year = year;

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
        c.set(year, month, day, 0, 0);
        return c.getTime();
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
    public String toOrderableString() {
        return year + "_" + month + "_" + day;
    }

    @Override
    public MonthDate getMonthDate() {
        return new MonthDate(month, year);
    }

    @Override
    public int compareTo(Date o) {
        if (DateUtils.dateBeforeOrEqual(this, o)) {
            return -1;
        } else {
            return 1;
        }
    }
}
