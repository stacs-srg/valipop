package utils.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.time.DateTimeException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateClockTest {

    // Test initialisation with values
    @Test(expected = DateTimeException.class)
    public void initZeroMonthTime() {
        DateClock t = new DateClock(0, 1);
    }

    @Test
    public void initCorrectMonthTime() {
        DateClock t = new DateClock(6, 1);
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void init13MonthTime() {
        DateClock t = new DateClock(13, 1);
    }

    // Test initialisation with String
    @Test(expected = DateTimeException.class)
    public void initStringZeroMonthTime() {
        DateClock t = new DateClock("1/0/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeMonthTime() {
        DateClock t = new DateClock("1/-10/0");
    }

    @Test
    public void initStringCorrectMonthTime() {
        DateClock t = new DateClock("1/6/0");
        assertEquals(6, t.getMonth());
        assertEquals(0, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString13MonthTime() {
        DateClock t = new DateClock("1/13/0");
    }


    // Test advancing utils.time
    @Test
    public void advanceTimeByMonthsWithinYear() {
        DateClock t = new DateClock(1, 1);
        t = t.advanceTime(3, TimeUnit.MONTH);
        assertEquals(4, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYear() {
        DateClock t = new DateClock(1, 1);
        t = t.advanceTime(13, TimeUnit.MONTH);
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYearUsingCompoundTimeUnit() {
        DateClock t = new DateClock(1, 1);
        t = t.advanceTime(new CompoundTimeUnit(13, TimeUnit.MONTH));
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByYears() {
        DateClock t = new DateClock(1, 1);
        t = t.advanceTime(3, TimeUnit.YEAR);
        assertEquals(1, t.getMonth());
        assertEquals(4, t.getYear());
    }

}
