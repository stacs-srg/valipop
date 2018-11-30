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

import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.MonthDate;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtilsTest {

    @Test
    public void calculateExactDate() {

        ValipopDate janDate = new MonthDate(1, 2015);
        ValipopDate marDate = new MonthDate(3, 2015);
        ValipopDate decDate = new MonthDate(12, 2015);

        ValipopDate febDate = new MonthDate(2, 2015);
        ValipopDate febDateLeapYear = new MonthDate(2, 2016);

        CompoundTimeUnit oneMonth = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit sixMonth = new CompoundTimeUnit(6, TimeUnit.MONTH);

        CompoundTimeUnit oneYear = new CompoundTimeUnit(1, TimeUnit.YEAR);

        CompoundTimeUnit minusOneMonth = new CompoundTimeUnit(-1, TimeUnit.MONTH);
        CompoundTimeUnit minusSixMonth = new CompoundTimeUnit(-6, TimeUnit.MONTH);

        CompoundTimeUnit minusOneYear = new CompoundTimeUnit(-1, TimeUnit.YEAR);

        calcExactDateHelper(janDate, oneMonth);

        calcExactDateHelper(febDateLeapYear, oneMonth);

        calcExactDateHelper(febDate, sixMonth);

        calcExactDateHelper(febDate, oneYear);

        calcExactDateHelper(febDateLeapYear, oneYear);

        calcExactDateHelper(febDateLeapYear, 28, 29, 2, febDateLeapYear.getYear());

        calcExactDateHelper(febDateLeapYear, 0, febDateLeapYear.getDay(), febDateLeapYear.getMonth(), febDateLeapYear.getYear());

        calcExactDateHelper(febDateLeapYear, 1, febDateLeapYear.getDay() + 1, febDateLeapYear.getMonth(), febDateLeapYear.getYear());

        calcExactDateHelper(febDateLeapYear, 13, 14, 2, febDateLeapYear.getYear());

        calcExactDateHelper(febDateLeapYear, 72, 13, 4, febDateLeapYear.getYear());

        calcExactDateHelper(janDate, 12154, 11, 4, 2048);


        calcExactDateHelper(marDate, minusOneMonth);

        calcExactDateHelper(febDateLeapYear, minusOneMonth);

        calcExactDateHelper(decDate, minusSixMonth);

        calcExactDateHelper(febDate, minusOneYear);

        calcExactDateHelper(febDateLeapYear, minusOneYear);

        calcExactDateHelper(janDate, 0, 1,1,2015);
        calcExactDateHelper(janDate, 364, 31,12,2015);
        calcExactDateHelper(janDate, 365, 1,1,2016);

        calcExactDateHelper(febDate, -31, 1, 1, febDate.getYear());
        calcExactDateHelper(janDate, -12154, 22, 9, 1981);

    }

    private void calcExactDateHelper(ValipopDate tDate, CompoundTimeUnit tTimeUnit) {

        ExactDate d = DateUtils.calculateExactDate(tDate, DateUtils.getDaysInTimePeriod(tDate, tTimeUnit));

        Assert.assertEquals(tDate.getDay(),d.getDay());

        if(tTimeUnit.getUnit() == TimeUnit.MONTH) {
            Assert.assertEquals(tDate.getMonth() + tTimeUnit.getCount(), d.getMonth());
        } else {
            Assert.assertEquals(tDate.getMonth(), d.getMonth());
        }
        if(tTimeUnit.getUnit() == TimeUnit.YEAR) {
            Assert.assertEquals(tDate.getYear()+tTimeUnit.getCount(), d.getYear());
        } else {
            Assert.assertEquals(tDate.getYear(), d.getYear());
        }

    }

    private void calcExactDateHelper(ValipopDate tDate, int days, int exD, int exM, int exY) {

        ExactDate d = DateUtils.calculateExactDate(tDate, days);
        Assert.assertEquals(exD, d.getDay());
        Assert.assertEquals(exM, d.getMonth());
        Assert.assertEquals(exY, d.getYear());

    }

    @Test
    public void differenceInDays() {

        ValipopDate a = new ExactDate(1, 1, 2000);
        ValipopDate b = new ExactDate(2, 1, 2000);

        Assert.assertEquals(1, DateUtils.differenceInDays(a, b));
        Assert.assertEquals(-1, DateUtils.differenceInDays(b, a));

        ValipopDate c = new ExactDate(1, 2, 2000);

        Assert.assertEquals(31, DateUtils.differenceInDays(a, c));

        ValipopDate d = new ExactDate(1, 1, 2016);
        ValipopDate e = new ExactDate(29, 2, 2016);
        ValipopDate f = new ExactDate(1, 3, 2016);

        Assert.assertEquals(59, DateUtils.differenceInDays(d, e));
        Assert.assertEquals(60, DateUtils.differenceInDays(d, f));

        ValipopDate h = new ExactDate(1, 1, 2001);
        Assert.assertEquals(366, DateUtils.differenceInDays(a, h));

        ValipopDate g = new ExactDate(24, 1, 2034);

        Assert.assertEquals(6598, DateUtils.differenceInDays(d, g));

        ValipopDate i = new ExactDate(31, 12, 2000);
        ValipopDate j = new ExactDate(2, 1, 2001);


        Assert.assertEquals(1, DateUtils.differenceInDays(e, f));

        Assert.assertEquals(2, DateUtils.differenceInDays(i, j));

        Assert.assertEquals(-1, DateUtils.differenceInDays(h, i));
        Assert.assertEquals(1, DateUtils.differenceInDays(i, h));

    }

    @Test
    public void getDaysInTimePeriod() {

        ValipopDate janDate = new MonthDate(1, 2015);
        ValipopDate janDateLeapYear = new MonthDate(1, 2016);
        ValipopDate janDatePostLeapYear = new MonthDate(1, 2017);

        ValipopDate febDate = new MonthDate(2, 2015);
        ValipopDate febDateLeapYear = new MonthDate(2, 2016);

        ValipopDate marDate = new MonthDate(3, 2015);
        ValipopDate marDateLeapYear = new MonthDate(3, 2016);

        ValipopDate offJanDate = new ExactDate(15, 1, 2015);
        ValipopDate offJanDateLeapYear = new ExactDate(15, 1, 2016);
        ValipopDate offJanDatePostLeapYear = new ExactDate(15, 1, 2017);

        ValipopDate offFebDate = new ExactDate(15, 2, 2015);
        ValipopDate offFebDateLeapYear = new ExactDate(15, 2, 2016);

        ValipopDate leapMarA = new ExactDate(29, 3, 2016);
        ValipopDate leapMarB = new ExactDate(31, 3, 2016);

        ValipopDate leapFeb = new ExactDate(29, 2, 2016);

        ValipopDate leapJan = new ExactDate(31, 1, 2016);
        ValipopDate postLeapJan = new ExactDate(31, 1, 2017);

        ValipopDate postLeapApr = new ExactDate(30, 4, 2017);

        CompoundTimeUnit oneMonth = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit twoMonth = new CompoundTimeUnit(2, TimeUnit.MONTH);
        CompoundTimeUnit threeMonth = new CompoundTimeUnit(3, TimeUnit.MONTH);
        CompoundTimeUnit sixMonth = new CompoundTimeUnit(6, TimeUnit.MONTH);
        CompoundTimeUnit elevenMonth = new CompoundTimeUnit(11, TimeUnit.MONTH);
        CompoundTimeUnit twelveMonth = new CompoundTimeUnit(12, TimeUnit.MONTH);
        CompoundTimeUnit twentyFourMonth = new CompoundTimeUnit(24, TimeUnit.MONTH);
        CompoundTimeUnit thirtySixMonth = new CompoundTimeUnit(36, TimeUnit.MONTH);

        CompoundTimeUnit oneYear = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit twoYear = new CompoundTimeUnit(2, TimeUnit.YEAR);
        CompoundTimeUnit threeYear = new CompoundTimeUnit(3, TimeUnit.YEAR);
        CompoundTimeUnit sevenYear = new CompoundTimeUnit(7, TimeUnit.YEAR);

        CompoundTimeUnit minusOneMonth = new CompoundTimeUnit(-1, TimeUnit.MONTH);
        CompoundTimeUnit minusTwoMonth = new CompoundTimeUnit(-2, TimeUnit.MONTH);
        CompoundTimeUnit minusSixMonth = new CompoundTimeUnit(-6, TimeUnit.MONTH);
        CompoundTimeUnit minusElevenMonth = new CompoundTimeUnit(-11, TimeUnit.MONTH);
        CompoundTimeUnit minusTwelveMonth = new CompoundTimeUnit(-12, TimeUnit.MONTH);
        CompoundTimeUnit minusTwentyFourMonth = new CompoundTimeUnit(-24, TimeUnit.MONTH);
        CompoundTimeUnit minusThirtySixMonth = new CompoundTimeUnit(-36, TimeUnit.MONTH);

        CompoundTimeUnit minusOneYear = new CompoundTimeUnit(-1, TimeUnit.YEAR);
        CompoundTimeUnit minusTwoYear = new CompoundTimeUnit(-2, TimeUnit.YEAR);
        CompoundTimeUnit minusThreeYear = new CompoundTimeUnit(-3, TimeUnit.YEAR);
        CompoundTimeUnit minusSevenYear = new CompoundTimeUnit(-7, TimeUnit.YEAR);

        // Reverse month checks

        Assert.assertEquals(-31, DateUtils.getDaysInTimePeriod(febDate, minusOneMonth));
        Assert.assertEquals(-28, DateUtils.getDaysInTimePeriod(marDate, minusOneMonth));
        Assert.assertEquals(-29, DateUtils.getDaysInTimePeriod(marDateLeapYear, minusOneMonth));

        Assert.assertEquals(-181, DateUtils.getDaysInTimePeriod(marDate, minusSixMonth));
        Assert.assertEquals(-182, DateUtils.getDaysInTimePeriod(marDateLeapYear, minusSixMonth));

        Assert.assertEquals(-334, DateUtils.getDaysInTimePeriod(marDate, minusElevenMonth));
        Assert.assertEquals(-335, DateUtils.getDaysInTimePeriod(marDateLeapYear, minusElevenMonth));

        Assert.assertEquals(-365, DateUtils.getDaysInTimePeriod(janDate, minusTwelveMonth));
        Assert.assertEquals(-366, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, minusTwelveMonth));

        // Reverse year checks

        Assert.assertEquals(-365, DateUtils.getDaysInTimePeriod(janDate, minusOneYear));
        Assert.assertEquals(-366, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, minusOneYear));

        Assert.assertEquals(-2557, DateUtils.getDaysInTimePeriod(janDate, minusSevenYear));

        // Reverse checks with complex date cases

        Assert.assertEquals(-31, DateUtils.getDaysInTimePeriod(leapMarB, minusOneMonth));
        Assert.assertEquals(-29, DateUtils.getDaysInTimePeriod(leapMarA, minusOneMonth));

        Assert.assertEquals(-62, DateUtils.getDaysInTimePeriod(leapJan, minusTwoMonth));

        Assert.assertEquals(-337, DateUtils.getDaysInTimePeriod(leapJan, minusElevenMonth));
        Assert.assertEquals(-337, DateUtils.getDaysInTimePeriod(postLeapJan, minusElevenMonth));

        Assert.assertEquals(-29, DateUtils.getDaysInTimePeriod(leapFeb, minusOneMonth));

        Assert.assertEquals(-365, DateUtils.getDaysInTimePeriod(febDateLeapYear, minusOneYear));

        // Forward Month Checks

        Assert.assertEquals(31, DateUtils.getDaysInTimePeriod(janDate, oneMonth));
        Assert.assertEquals(28, DateUtils.getDaysInTimePeriod(febDate, oneMonth));
        Assert.assertEquals(29, DateUtils.getDaysInTimePeriod(febDateLeapYear, oneMonth));

        Assert.assertEquals(181, DateUtils.getDaysInTimePeriod(janDate, sixMonth));
        Assert.assertEquals(182, DateUtils.getDaysInTimePeriod(janDateLeapYear, sixMonth));

        Assert.assertEquals(334, DateUtils.getDaysInTimePeriod(febDate, elevenMonth));
        Assert.assertEquals(335, DateUtils.getDaysInTimePeriod(febDateLeapYear, elevenMonth));

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(janDate, twelveMonth));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(janDateLeapYear, twelveMonth));

        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, thirtySixMonth));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDate, twentyFourMonth));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDateLeapYear, twentyFourMonth));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, twentyFourMonth));

        // Forward year checks

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(janDate, oneYear));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(janDateLeapYear, oneYear));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDate, twoYear));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDateLeapYear, twoYear));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, twoYear));

        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, threeYear));

        Assert.assertEquals(2557, DateUtils.getDaysInTimePeriod(janDate, sevenYear));


        Assert.assertEquals(31, DateUtils.getDaysInTimePeriod(offJanDate, oneMonth));
        Assert.assertEquals(28, DateUtils.getDaysInTimePeriod(offFebDate, oneMonth));
        Assert.assertEquals(29, DateUtils.getDaysInTimePeriod(offFebDateLeapYear, oneMonth));

        Assert.assertEquals(181, DateUtils.getDaysInTimePeriod(offJanDate, sixMonth));
        Assert.assertEquals(182, DateUtils.getDaysInTimePeriod(offJanDateLeapYear, sixMonth));

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(offJanDate, oneYear));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(offJanDateLeapYear, oneYear));

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(offJanDate, twelveMonth));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(offJanDateLeapYear, twelveMonth));

        Assert.assertEquals(334, DateUtils.getDaysInTimePeriod(offFebDate, elevenMonth));
        Assert.assertEquals(335, DateUtils.getDaysInTimePeriod(offFebDateLeapYear, elevenMonth));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(offJanDate, twoYear));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(offJanDateLeapYear, twoYear));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(offJanDatePostLeapYear, twoYear));

        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(offJanDatePostLeapYear, threeYear));
        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(offJanDatePostLeapYear, thirtySixMonth));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(offJanDate, twentyFourMonth));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(offJanDateLeapYear, twentyFourMonth));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(offJanDatePostLeapYear, twentyFourMonth));

        Assert.assertEquals(2557, DateUtils.getDaysInTimePeriod(offJanDate, sevenYear));

        // Forward checks with complex date cases

        Assert.assertEquals(30, DateUtils.getDaysInTimePeriod(leapMarB, oneMonth));
        Assert.assertEquals(31, DateUtils.getDaysInTimePeriod(leapMarA, oneMonth));

        Assert.assertEquals(60, DateUtils.getDaysInTimePeriod(leapJan, twoMonth));

        Assert.assertEquals(335, DateUtils.getDaysInTimePeriod(leapJan, elevenMonth));
        Assert.assertEquals(334, DateUtils.getDaysInTimePeriod(postLeapJan, elevenMonth));

        Assert.assertEquals(29, DateUtils.getDaysInTimePeriod(leapFeb, oneMonth));


    }

    @Test
    public void checkMonthsMethod() {
        // minus test
        MonthDate t = new MonthDate(6, 1900);

        assertEquals(21, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(21, TimeUnit.MONTH))).getCount());
        assertEquals(21, DateUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(21, TimeUnit.MONTH)), t).getCount());

        // 0 test
        assertEquals(0, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(0, TimeUnit.MONTH))).getCount());
        assertEquals(0, DateUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(0, TimeUnit.MONTH)), t).getCount());

        assertEquals(12, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(12, TimeUnit.MONTH))).getCount());
        assertEquals(12, DateUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(12, TimeUnit.MONTH)), t).getCount());

        // plus test
        assertEquals(15, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(15, TimeUnit.MONTH))).getCount());
        assertEquals(15, DateUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(15, TimeUnit.MONTH)), t).getCount());

        MonthDate t2 = new MonthDate(12, 2018);
        assertEquals(3, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(3, TimeUnit.MONTH))).getCount());
        assertEquals(3, DateUtils.differenceInMonths(t2, t2.advanceTime(new CompoundTimeUnit(3, TimeUnit.MONTH))).getCount());

    }

    @Test
    public void checkYearsMethod() {

        MonthDate t = new MonthDate(6, 1900);

        MonthDate t1 = new MonthDate(1, 2015);
        MonthDate t2 = new MonthDate(12, 2015);

        ExactDate d1 = new ExactDate(11, 5, 1900);
        ExactDate d2 = new ExactDate(10, 5, 1901);
        ExactDate d3 = new ExactDate(11, 5, 1901);
        ExactDate d4 = new ExactDate(12, 5, 1901);

        assertEquals(0, DateUtils.differenceInYears(d1, d2).getCount());
        assertEquals(1, DateUtils.differenceInYears(d1, d3).getCount());
        assertEquals(1, DateUtils.differenceInYears(d1, d4).getCount());

        assertEquals(0, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(0, TimeUnit.YEAR))).getCount());
        assertEquals(0, DateUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(0, TimeUnit.YEAR)), t).getCount());

        assertEquals(0, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(3, TimeUnit.MONTH))).getCount());
        assertEquals(0, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(6, TimeUnit.MONTH))).getCount());
        assertEquals(0, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(9, TimeUnit.MONTH))).getCount());

        assertEquals(0, DateUtils.differenceInYears(t1, t1.advanceTime(new CompoundTimeUnit(11, TimeUnit.MONTH))).getCount());
        assertEquals(1, DateUtils.differenceInYears(t2, t2.advanceTime(new CompoundTimeUnit(12, TimeUnit.MONTH))).getCount());
        assertEquals(0, DateUtils.differenceInYears(t2, t2.advanceTime(new CompoundTimeUnit(1, TimeUnit.MONTH))).getCount());

        assertEquals(7, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(7, TimeUnit.YEAR))).getCount());
        assertEquals(7, DateUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(7, TimeUnit.YEAR)), t).getCount());


        assertEquals(7, DateUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(-7, TimeUnit.YEAR))).getCount());
        assertEquals(7, DateUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(-7, TimeUnit.YEAR)), t).getCount());

    }

    @Test
    public void checkBeforeDateOfEqualMethod() {

        MonthDate a3 = new MonthDate(12, 2014);
        MonthDate a = new MonthDate(3, 2015);
        MonthDate a2 = new MonthDate(6, 2015);

        ExactDate b = new ExactDate(15, 3, 2015);

        assertTrue(DateUtils.dateBeforeOrEqual(a, b));
        assertFalse(DateUtils.dateBeforeOrEqual(b, a));

        assertTrue(DateUtils.dateBeforeOrEqual(b, b));

        assertTrue(DateUtils.dateBeforeOrEqual(a3, a));
        assertTrue(DateUtils.dateBeforeOrEqual(a, a2));
        assertTrue(DateUtils.dateBeforeOrEqual(a3, a2));

        assertFalse(DateUtils.dateBeforeOrEqual(a, a3));
        assertFalse(DateUtils.dateBeforeOrEqual(a2, a));
        assertFalse(DateUtils.dateBeforeOrEqual(a2, a3));

        YearDate y = new YearDate(1900);

        ExactDate e1 = new ExactDate(31,12,1899);
        ExactDate e2 = new ExactDate(1,1,1900);
        ExactDate e3 = new ExactDate(2,1,1900);

        assertTrue(DateUtils.dateBeforeOrEqual(e1, y));
        assertTrue(DateUtils.dateBeforeOrEqual(e2, y));
        assertFalse(DateUtils.dateBeforeOrEqual(e3, y));

    }

    @Test
    public void checkBeforeDateMethod() {

        MonthDate a3 = new MonthDate(12, 2014);
        MonthDate a = new MonthDate(3, 2015);
        MonthDate a2 = new MonthDate(6, 2015);

        ExactDate b = new ExactDate(15, 3, 2015);

        assertTrue(DateUtils.dateBefore(a, b));
        assertFalse(DateUtils.dateBefore(b, a));

        assertFalse(DateUtils.dateBefore(b, b));

        assertTrue(DateUtils.dateBefore(a3, a));
        assertTrue(DateUtils.dateBefore(a, a2));
        assertTrue(DateUtils.dateBefore(a3, a2));

        assertFalse(DateUtils.dateBefore(a, a3));
        assertFalse(DateUtils.dateBefore(a2, a));
        assertFalse(DateUtils.dateBefore(a2, a3));

        YearDate y = new YearDate(1900);

        ExactDate e1 = new ExactDate(31,12,1899);
        ExactDate e2 = new ExactDate(1,1,1900);
        ExactDate e3 = new ExactDate(2,1,1900);

        assertTrue(DateUtils.dateBefore(e1, y));
        assertFalse(DateUtils.dateBefore(e2, y));
        assertFalse(DateUtils.dateBefore(e3, y));

    }

    @Test
    public void checkMatchesIntervalMethod() {

        MonthDate s1 = new MonthDate(1,0);
        MonthDate s2 = new MonthDate(2,0);


        MonthDate a = new MonthDate(1, 10);
        MonthDate b = new MonthDate(8, 13);
        MonthDate c = new MonthDate(1, 2017);
        MonthDate d = new MonthDate(7, 2017);

        CompoundTimeUnit monthly = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit quarterly = new CompoundTimeUnit(3, TimeUnit.MONTH);
        CompoundTimeUnit annually = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit biannually = new CompoundTimeUnit(2, TimeUnit.YEAR);

        assertTrue(DateUtils.matchesInterval(a, monthly, s1));
        assertTrue(DateUtils.matchesInterval(a, quarterly, s1));
        assertTrue(DateUtils.matchesInterval(a, annually, s1));
        assertTrue(DateUtils.matchesInterval(a, biannually, s1));

        assertTrue(DateUtils.matchesInterval(b, monthly, s2));
        assertFalse(DateUtils.matchesInterval(b, quarterly, s1));
        assertTrue(DateUtils.matchesInterval(b, quarterly, s2));
        assertFalse(DateUtils.matchesInterval(b, annually, s2));
        assertFalse(DateUtils.matchesInterval(b, biannually, s2));

        assertTrue(DateUtils.matchesInterval(c, monthly, s1));
        assertTrue(DateUtils.matchesInterval(c, quarterly, s1));
        assertTrue(DateUtils.matchesInterval(c, annually, s1));
        assertFalse(DateUtils.matchesInterval(c, biannually, s1));

        assertTrue(DateUtils.matchesInterval(d, monthly, s1));
        assertTrue(DateUtils.matchesInterval(d, quarterly, s1));
        assertFalse(DateUtils.matchesInterval(d, annually, s1));
        assertFalse(DateUtils.matchesInterval(d, biannually, s1));

    }

    @Test
    public void subTimeUnitsInTimeUnitTests() {
        CompoundTimeUnit monthly = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit quarterly = new CompoundTimeUnit(3, TimeUnit.MONTH);
        CompoundTimeUnit annually = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit biannually = new CompoundTimeUnit(2, TimeUnit.YEAR);

        Assert.assertEquals(12, DateUtils.calcSubTimeUnitsInTimeUnit(monthly, annually));
        Assert.assertEquals(4, DateUtils.calcSubTimeUnitsInTimeUnit(quarterly, annually));
        Assert.assertEquals(1, DateUtils.calcSubTimeUnitsInTimeUnit(annually, annually));
        Assert.assertEquals(-1, DateUtils.calcSubTimeUnitsInTimeUnit(biannually, annually));


    }

    @Test
    public void dateInYearTests() {

        YearDate y1 = new YearDate(2000);
        YearDate y2 = new YearDate(2001);
        YearDate y3 = new YearDate(2002);

        MonthDate m1 = new MonthDate(6, 2000);
        MonthDate m2 = new MonthDate(12, 2000);
        MonthDate m3 = new MonthDate(1, 2001);
        MonthDate m4 = new MonthDate(6, 2001);
        MonthDate m5 = new MonthDate(12, 2001);
        MonthDate m6 = new MonthDate(1, 2002);
        MonthDate m7 = new MonthDate(6, 2002);

        ExactDate e1 = new ExactDate(1,1,2001);
        ExactDate e2 = new ExactDate(2,1,2001);
        ExactDate e3 = new ExactDate(5,7,2001);
        ExactDate e4 = new ExactDate(30,12,2001);
        ExactDate e5 = new ExactDate(31,12,2001);

        Assert.assertTrue(DateUtils.dateInYear(e1, y2));
        Assert.assertTrue(DateUtils.dateInYear(e2, y2));
        Assert.assertTrue(DateUtils.dateInYear(e3, y2));
        Assert.assertTrue(DateUtils.dateInYear(e4, y2));
        Assert.assertTrue(DateUtils.dateInYear(e5, y2));

        Assert.assertFalse(DateUtils.dateInYear(e1, y1));
        Assert.assertFalse(DateUtils.dateInYear(e2, y1));

        Assert.assertFalse(DateUtils.dateInYear(e4, y3));
        Assert.assertFalse(DateUtils.dateInYear(e5, y3));

        Assert.assertTrue(DateUtils.dateInYear(m3, y2));
        Assert.assertTrue(DateUtils.dateInYear(m4, y2));
        Assert.assertTrue(DateUtils.dateInYear(m5, y2));

        Assert.assertFalse(DateUtils.dateInYear(m1, y2));
        Assert.assertFalse(DateUtils.dateInYear(m2, y2));

        Assert.assertFalse(DateUtils.dateInYear(m6, y2));
        Assert.assertFalse(DateUtils.dateInYear(m7, y2));

    }

}
