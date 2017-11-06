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

import org.junit.Test;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import java.time.DateTimeException;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MonthDateTest {

    // Test initialisation with values
    @Test(expected = DateTimeException.class)
    public void initZeroMonthTime() {
        MonthDate t = new MonthDate(0, 1);
    }

    @Test
    public void initCorrectMonthTime() {
        MonthDate t = new MonthDate(6, 1);
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void init13MonthTime() {
        MonthDate t = new MonthDate(13, 1);
    }

    // Test initialisation with String
    @Test(expected = DateTimeException.class)
    public void initStringZeroMonthTime() {
        MonthDate t = new MonthDate("1/0/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeMonthTime() {
        MonthDate t = new MonthDate("1/-10/0");
    }

    @Test
    public void initStringCorrectMonthTime() {
        MonthDate t = new MonthDate("1/6/0");
        assertEquals(6, t.getMonth());
        assertEquals(0, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString13MonthTime() {
        MonthDate t = new MonthDate("1/13/0");
    }


    // Test advancing utils.time
    @Test
    public void advanceTimeByMonthsWithinYear() {
        MonthDate t = new MonthDate(1, 1);
        t = t.advanceTime(3, TimeUnit.MONTH);
        assertEquals(4, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYear() {
        MonthDate t = new MonthDate(1, 1);
        t = t.advanceTime(13, TimeUnit.MONTH);
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYearUsingCompoundTimeUnit() {
        MonthDate t = new MonthDate(1, 1);
        t = t.advanceTime(new CompoundTimeUnit(13, TimeUnit.MONTH));
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByYears() {
        MonthDate t = new MonthDate(1, 1);
        t = t.advanceTime(3, TimeUnit.YEAR);
        assertEquals(1, t.getMonth());
        assertEquals(4, t.getYear());
    }

}
