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

import java.text.SimpleDateFormat;
import java.time.DateTimeException;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ExactDateTest {

    @Test(expected = DateTimeException.class)
    public void negativeMonth() {
        assertIllegal(1,-10,0);
    }

    @Test(expected = DateTimeException.class)
    public void zeroDay() {
        assertIllegal(0, 6, 1);
    }

    @Test(expected = DateTimeException.class)
    public void negativeDay() {
        assertIllegal(-12, 6, 1);
    }

    @Test(expected = DateTimeException.class)
    public void Nov31st() {
        assertIllegal(31, 11, 1904);
    }

    @Test(expected = DateTimeException.class)
    public void monthTooLarge() {
        assertIllegal(1, 13, 1);
    }

    @Test(expected = DateTimeException.class)
    public void Feb29thNonLeapYear() {
        assertIllegal(29, 2, 1905);
    }

    @Test(expected = DateTimeException.class)
    public void zeroMonth() {
        assertIllegal(1, 0, 1);
    }

    @Test
    public void June1st() {

        assertValid(1, 6, 2017);
    }

    @Test
    public void Feb29thInLeapYear() {

        assertValid(29, 2, 1904);
    }

    @Test
    public void November30th() {

        assertValid(30, 11, 1904);
    }

    @Test
    public void zeroYear() {

        assertValid(1,6,0);
    }

    private void checkDay(int day, ExactDate d) {

        assertEquals(day, d.getDay());
    }

    private void checkMonth(int month, ExactDate d) {

        assertEquals(month, d.getMonth());
    }

    private void checkYear(int year, ExactDate d) {

        assertEquals(year, d.getYear());
    }

    private void assertValid(int day, int month, int year) {

        ExactDate t = new ExactDate(day, month, year);
        checkDay(day, t);
        checkMonth(month, t);
        checkYear(year, t);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");

        assertEquals(ExactDate.makeDateString(day, month, year), formatter.format(t.getDate()));
    }

    private void assertIllegal(int day, int month, int year) {

        new ExactDate(day, month, year);
    }
}
