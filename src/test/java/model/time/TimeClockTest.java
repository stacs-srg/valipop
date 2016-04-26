package model.time;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.DateTimeException;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TimeClockTest {

    // Test initialisation with values
    @Test(expected = DateTimeException.class)
    public void initZeroMonthTime() {
        TimeClock t = new TimeClock(0, 1);
    }

    @Test
    public void initCorrectMonthTime() {
        TimeClock t = new TimeClock(6, 1);
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void init13MonthTime() {
        TimeClock t = new TimeClock(13, 1);
    }

    // Test initialisation with String
    @Test(expected = DateTimeException.class)
    public void initStringZeroMonthTime() {
        TimeClock t = new TimeClock("1/0/0");
    }

    @Test
    public void initStringCorrectMonthTime() {
        TimeClock t = new TimeClock("1/6/0");
        assertEquals(6, t.getMonth());
        assertEquals(0, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString13MonthTime() {
        TimeClock t = new TimeClock("1/13/0");
    }


    // Test advancing time
    @Test
    public void advanceTimeByMonthsWithinYear() {
        TimeClock t = new TimeClock(1, 1);
        t = t.advanceTime(3, TimeUnit.MONTH);
        assertEquals(4, t.getMonth());
        assertEquals(1, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYear() {
        TimeClock t = new TimeClock(1, 1);
        t = t.advanceTime(13, TimeUnit.MONTH);
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByMonthsOutwithYearUsingCompoundTimeUnit() {
        TimeClock t = new TimeClock(1, 1);
        t = t.advanceTime(new CompoundTimeUnit(13, TimeUnit.MONTH));
        assertEquals(2, t.getMonth());
        assertEquals(2, t.getYear());
    }

    @Test
    public void advanceTimeByYears() {
        TimeClock t = new TimeClock(1, 1);
        t = t.advanceTime(3, TimeUnit.YEAR);
        assertEquals(1, t.getMonth());
        assertEquals(4, t.getYear());
    }

}
