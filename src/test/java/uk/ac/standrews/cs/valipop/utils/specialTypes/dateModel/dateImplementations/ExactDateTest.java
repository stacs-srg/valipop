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
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.time.DateTimeException;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ExactDateTest {

    // Test initialisation with values
    @Test(expected = DateTimeException.class)
    public void initZeroMonthTime() {
        ExactDate t = new ExactDate(1, 0, 1);
    }

    @Test(expected = DateTimeException.class)
    public void initNegativeMonthTime() {
        ExactDate t = new ExactDate(1, -4, 1);
    }


    @Test(expected = DateTimeException.class)
    public void initZeroDayTime() {
        ExactDate t = new ExactDate(0, 6, 1);
    }

    @Test(expected = DateTimeException.class)
    public void initNegativeDayTime() {
        ExactDate t = new ExactDate(-12, 6, 1);
    }

    @Test
    public void initCorrectTime() {
        ExactDate t = new ExactDate(1, 6, 1);
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    // check leap years
    @Test(expected = DateTimeException.class)
    public void init29FebNormalYearTime() {
        ExactDate t = new ExactDate(30, 2, 1905);
    }

    @Test
    public void init29FebLeapYearTime() {
        ExactDate t = new ExactDate(29, 2, 1904);
        assertEquals(29, t.getDay());
        assertEquals(2, t.getMonth());
        assertEquals(1904, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void init31NovTime() {
        ExactDate t = new ExactDate(31, 11, 1904);
    }

    @Test
    public void init30NovTime() {
        ExactDate t = new ExactDate(30, 11, 1904);
        assertEquals(30, t.getDay());
        assertEquals(11, t.getMonth());
        assertEquals(1904, t.getYear());
    }


    @Test(expected = DateTimeException.class)
    public void init13MonthTime() {
        ExactDate t = new ExactDate(1, 13, 1);
    }

    // Test initialisation with String
    @Test(expected = DateTimeException.class)
    public void initStringZeroMonthTime() {
        ExactDate t = new ExactDate("1/0/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeMonthTime() {
        ExactDate t = new ExactDate("1/-10/0");
    }

    @Test
    public void initStringCorrectMonthTime() {
        ExactDate t = new ExactDate("1/6/0");
        assertEquals(1, t.getDay());
        assertEquals(6, t.getMonth());
        assertEquals(0, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString13MonthTime() {
        ExactDate t = new ExactDate("1/13/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringZeroDayTime() {
        ExactDate t = new ExactDate("0/6/1");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeDayTime() {
        ExactDate t = new ExactDate("-12/6/1");
    }

    @Test
    public void initStringCorrectTime() {
        ExactDate t = new ExactDate("1/6/1");
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    // check leap years
    @Test(expected = DateTimeException.class)
    public void initString29FebNormalYearTime() {
        ExactDate t = new ExactDate("29/2/1905");
    }

    @Test
    public void initString29FebLeapYearTime() {
        ExactDate t = new ExactDate("29/2/1904");
        assertEquals(29, t.getDay());
        assertEquals(2, t.getMonth());
        assertEquals(1904, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString31NovTime() {
        ExactDate t = new ExactDate("31/11/1904");
    }

    @Test
    public void initString30NovTime() {
        ExactDate t = new ExactDate("30/11/1904");
        assertEquals(30, t.getDay());
        assertEquals(11, t.getMonth());
        assertEquals(1904, t.getYear());
    }

}
