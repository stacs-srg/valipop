package utils.time;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateUtilsTest {

    @Test
    public void getDaysInTimePeriod() throws Exception {

        Date janDate = new DateClock(1, 2015);
        Date janDateLeapYear = new DateClock(1, 2016);
        Date janDatePostLeapYear = new DateClock(1, 2017);

        Date febDate = new DateClock(2, 2015);
        Date febDateLeapYear = new DateClock(2, 2016);


        CompoundTimeUnit oneMonth = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit sixMonth = new CompoundTimeUnit(6, TimeUnit.MONTH);
        CompoundTimeUnit elevenMonth = new CompoundTimeUnit(11, TimeUnit.MONTH);
        CompoundTimeUnit twelveMonth = new CompoundTimeUnit(12, TimeUnit.MONTH);
        CompoundTimeUnit twentyFourMonth = new CompoundTimeUnit(24, TimeUnit.MONTH);
        CompoundTimeUnit thirtySixMonth = new CompoundTimeUnit(36, TimeUnit.MONTH);

        CompoundTimeUnit oneYear = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit twoYear = new CompoundTimeUnit(2, TimeUnit.YEAR);
        CompoundTimeUnit threeYear = new CompoundTimeUnit(3, TimeUnit.YEAR);
        CompoundTimeUnit sevenYear = new CompoundTimeUnit(7, TimeUnit.YEAR);


        Assert.assertEquals(31, DateUtils.getDaysInTimePeriod(janDate, oneMonth));
        Assert.assertEquals(28, DateUtils.getDaysInTimePeriod(febDate, oneMonth));
        Assert.assertEquals(29, DateUtils.getDaysInTimePeriod(febDateLeapYear, oneMonth));

        Assert.assertEquals(181, DateUtils.getDaysInTimePeriod(janDate, sixMonth));
        Assert.assertEquals(182, DateUtils.getDaysInTimePeriod(janDateLeapYear, sixMonth));

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(janDate, oneYear));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(janDateLeapYear, oneYear));

        Assert.assertEquals(365, DateUtils.getDaysInTimePeriod(janDate, twelveMonth));
        Assert.assertEquals(366, DateUtils.getDaysInTimePeriod(janDateLeapYear, twelveMonth));

        Assert.assertEquals(334, DateUtils.getDaysInTimePeriod(febDate, elevenMonth));
        Assert.assertEquals(335, DateUtils.getDaysInTimePeriod(febDateLeapYear, elevenMonth));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDate, twoYear));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDateLeapYear, twoYear));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, twoYear));

        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, threeYear));
        Assert.assertEquals(1095, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, thirtySixMonth));

        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDate, twentyFourMonth));
        Assert.assertEquals(731, DateUtils.getDaysInTimePeriod(janDateLeapYear, twentyFourMonth));
        Assert.assertEquals(730, DateUtils.getDaysInTimePeriod(janDatePostLeapYear, twentyFourMonth));

        Assert.assertEquals(2557, DateUtils.getDaysInTimePeriod(janDate, sevenYear));




    }

    @Test
    public void checkMonthsMethod() {
        // minus test
        DateClock t = new DateClock(6, 1900);

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

        DateClock t2 = new DateClock(12, 2018);
        assertEquals(3, DateUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(3, TimeUnit.MONTH))).getCount());
        assertEquals(3, DateUtils.differenceInMonths(t2, t2.advanceTime(new CompoundTimeUnit(3, TimeUnit.MONTH))).getCount());



    }

    @Test
    public void checkYearsMethod() {

        DateClock t = new DateClock(6, 1900);

        DateClock t1 = new DateClock(1, 2015);
        DateClock t2 = new DateClock(12, 2015);

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

        DateClock a3 = new DateClock(12, 2014);
        DateClock a = new DateClock(3, 2015);
        DateClock a2 = new DateClock(6, 2015);

        DateInstant b = new DateInstant(15, 3, 2015);

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

        DateClock a = new DateClock(1, 10);
        DateClock b = new DateClock(8, 13);
        DateClock c = new DateClock(1, 2017);
        DateClock d = new DateClock(7, 2017);

        CompoundTimeUnit monthly = new CompoundTimeUnit(1, TimeUnit.MONTH);
        CompoundTimeUnit quarterly = new CompoundTimeUnit(3, TimeUnit.MONTH);
        CompoundTimeUnit annually = new CompoundTimeUnit(1, TimeUnit.YEAR);
        CompoundTimeUnit biannually = new CompoundTimeUnit(2, TimeUnit.YEAR);

        assertTrue(DateUtils.matchesInterval(a, monthly));
        assertTrue(DateUtils.matchesInterval(a, quarterly));
        assertTrue(DateUtils.matchesInterval(a, annually));
        assertTrue(DateUtils.matchesInterval(a, biannually));

        assertTrue(DateUtils.matchesInterval(b, monthly));
        assertFalse(DateUtils.matchesInterval(b, quarterly));
        assertFalse(DateUtils.matchesInterval(b, annually));
        assertFalse(DateUtils.matchesInterval(b, biannually));

        assertTrue(DateUtils.matchesInterval(c, monthly));
        assertTrue(DateUtils.matchesInterval(c, quarterly));
        assertTrue(DateUtils.matchesInterval(c, annually));
        assertFalse(DateUtils.matchesInterval(c, biannually));

        assertTrue(DateUtils.matchesInterval(d, monthly));
        assertTrue(DateUtils.matchesInterval(d, quarterly));
        assertFalse(DateUtils.matchesInterval(d, annually));
        assertFalse(DateUtils.matchesInterval(d, biannually));

    }

}
