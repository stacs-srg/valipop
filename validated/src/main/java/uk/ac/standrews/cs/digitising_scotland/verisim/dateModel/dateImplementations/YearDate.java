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
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;

import java.util.Calendar;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class YearDate implements AdvancableDate {

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
    public MonthDate getMonthDate() {
        return new MonthDate(MONTH, year);
    }

    @Override
    public int hashCode() {
        return year * 100 + MONTH;
    }

    @Override
    public boolean equals(Object obj) {
        AdvancableDate date = (AdvancableDate) obj;
        return this.year == date.getYear() && this.getMonth() == date.getMonth();
    }

    @Override
    public String toOrderableString() {
        return year + "_" + MONTH + "_" + DAY;
    }

    @Override
    public int compareTo(Date o) {
        if(equals(o)) {
            return 0;
        } else if (DateUtils.dateBeforeOrEqual(this, o)) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public AdvancableDate advanceTime(int numberOf, TimeUnit unit) {
        return getMonthDate().advanceTime(numberOf, unit);
    }

    @Override
    public AdvancableDate advanceTime(CompoundTimeUnit timeStep) {
        return getMonthDate().advanceTime(timeStep);
    }
}
