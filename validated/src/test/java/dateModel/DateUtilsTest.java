package dateModel;

import dateModel.dateImplementations.MonthDate;
import org.junit.Assert;
import org.junit.Test;
import dateModel.dateImplementations.ExactDate;
import dateModel.timeSteps.CompoundTimeUnit;
import dateModel.timeSteps.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtilsTest {

    @Test
    public void calculateExactDate() throws Exception {

        Date janDate = new MonthDate(1, 2015);
        Date marDate = new MonthDate(3, 2015);
        Date decDate = new MonthDate(12, 2015);

        Date febDate = new MonthDate(2, 2015);
        Date febDateLeapYear = new MonthDate(2, 2016);

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

        calcExactDateHelper(febDateLeapYear, 28, 1, 3, febDateLeapYear.getYear());

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


        calcExactDateHelper(febDate, -31, 1, 1, febDate.getYear());
        calcExactDateHelper(janDate, -12154, 22, 9, 1981);

    }

    private void calcExactDateHelper(Date tDate, CompoundTimeUnit tTimeUnit) {

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

    private void calcExactDateHelper(Date tDate, int days, int exD, int exM, int exY) {

        ExactDate d = DateUtils.calculateExactDate(tDate, days);
        Assert.assertEquals(exD, d.getDay());
        Assert.assertEquals(exM, d.getMonth());
        Assert.assertEquals(exY, d.getYear());

    }

    @Test
    public void differenceInDays() {

        Date a = new ExactDate(1, 1, 2000);
        Date b = new ExactDate(2, 1, 2000);

        Assert.assertEquals(1, DateUtils.differenceInDays(a, b));
        Assert.assertEquals(1, DateUtils.differenceInDays(b, a));


        Date c = new ExactDate(1, 2, 2000);

        Assert.assertEquals(31, DateUtils.differenceInDays(a, c));

        Date d = new ExactDate(1, 1, 2016);
        Date e = new ExactDate(29, 2, 2016);
        Date f = new ExactDate(1, 3, 2016);

        Assert.assertEquals(59, DateUtils.differenceInDays(d, e));
        Assert.assertEquals(60, DateUtils.differenceInDays(d, f));

        Date h = new ExactDate(1, 1, 2001);
        Assert.assertEquals(366, DateUtils.differenceInDays(a, h));

        Date g = new ExactDate(24, 1, 2034);

        Assert.assertEquals(6598, DateUtils.differenceInDays(d, g));

    }

    @Test
    public void getDaysInTimePeriod() throws Exception {

        Date janDate = new MonthDate(1, 2015);
        Date janDateLeapYear = new MonthDate(1, 2016);
        Date janDatePostLeapYear = new MonthDate(1, 2017);

        Date febDate = new MonthDate(2, 2015);
        Date febDateLeapYear = new MonthDate(2, 2016);

        Date marDate = new MonthDate(3, 2015);
        Date marDateLeapYear = new MonthDate(3, 2016);

        Date offJanDate = new ExactDate(15, 1, 2015);
        Date offJanDateLeapYear = new ExactDate(15, 1, 2016);
        Date offJanDatePostLeapYear = new ExactDate(15, 1, 2017);

        Date offFebDate = new ExactDate(15, 2, 2015);
        Date offFebDateLeapYear = new ExactDate(15, 2, 2016);

        Date leapMarA = new ExactDate(29, 3, 2016);
        Date leapMarB = new ExactDate(31, 3, 2016);

        Date leapFeb = new ExactDate(29, 2, 2016);

        Date leapJan = new ExactDate(31, 1, 2016);
        Date postLeapJan = new ExactDate(31, 1, 2017);

        Date postLeapApr = new ExactDate(30, 4, 2017);

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
    public void checkBeforeDateMethod() {

        MonthDate a3 = new MonthDate(12, 2014);
        MonthDate a = new MonthDate(3, 2015);
        MonthDate a2 = new MonthDate(6, 2015);

        ExactDate b = new ExactDate(15, 3, 2015);

        assertTrue(DateUtils.dateBefore(a, b));
        assertFalse(DateUtils.dateBefore(b, a));

        assertTrue(DateUtils.dateBefore(b, b));

        assertTrue(DateUtils.dateBefore(a3, a));
        assertTrue(DateUtils.dateBefore(a, a2));
        assertTrue(DateUtils.dateBefore(a3, a2));

        assertFalse(DateUtils.dateBefore(a, a3));
        assertFalse(DateUtils.dateBefore(a2, a));
        assertFalse(DateUtils.dateBefore(a2, a3));

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

}
