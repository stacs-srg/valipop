package utils.time;

import org.junit.Test;

import java.time.DateTimeException;

import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateInstantTest {

    // Test initialisation with values
    @Test(expected = DateTimeException.class)
    public void initZeroMonthTime() {
        DateInstant t = new DateInstant(1, 0, 1);
    }

    @Test(expected = DateTimeException.class)
    public void initNegativeMonthTime() {
        DateInstant t = new DateInstant(1, -4, 1);
    }


    @Test(expected = DateTimeException.class)
    public void initZeroDayTime() {
        DateInstant t = new DateInstant(0, 6, 1);
    }

    @Test(expected = DateTimeException.class)
    public void initNegativeDayTime() {
        DateInstant t = new DateInstant(-12, 6, 1);
    }

    @Test
    public void initCorrectTime() {
        DateInstant t = new DateInstant(1, 6, 1);
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    // check leap years
    @Test(expected = DateTimeException.class)
    public void init29FebNormalYearTime() {
        DateInstant t = new DateInstant(30, 2, 1905);
    }

    @Test
    public void init29FebLeapYearTime() {
        DateInstant t = new DateInstant(29, 2, 1904);
        assertEquals(29, t.getDay());
        assertEquals(2, t.getMonth());
        assertEquals(1904, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void init31NovTime() {
        DateInstant t = new DateInstant(31, 11, 1904);
    }

    @Test
    public void init30NovTime() {
        DateInstant t = new DateInstant(30, 11, 1904);
        assertEquals(30, t.getDay());
        assertEquals(11, t.getMonth());
        assertEquals(1904, t.getYear());
    }


    @Test(expected = DateTimeException.class)
    public void init13MonthTime() {
        DateInstant t = new DateInstant(1, 13, 1);
    }

    // Test initialisation with String
    @Test(expected = DateTimeException.class)
    public void initStringZeroMonthTime() {
        DateInstant t = new DateInstant("1/0/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeMonthTime() {
        DateInstant t = new DateInstant("1/-10/0");
    }

    @Test
    public void initStringCorrectMonthTime() {
        DateInstant t = new DateInstant("1/6/0");
        assertEquals(1, t.getDay());
        assertEquals(6, t.getMonth());
        assertEquals(0, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString13MonthTime() {
        DateInstant t = new DateInstant("1/13/0");
    }

    @Test(expected = DateTimeException.class)
    public void initStringZeroDayTime() {
        DateInstant t = new DateInstant("0/6/1");
    }

    @Test(expected = DateTimeException.class)
    public void initStringNegativeDayTime() {
        DateInstant t = new DateInstant("-12/6/1");
    }

    @Test
    public void initStringCorrectTime() {
        DateInstant t = new DateInstant("1/6/1");
        assertEquals(6, t.getMonth());
        assertEquals(1, t.getYear());
    }

    // check leap years
    @Test(expected = DateTimeException.class)
    public void initString29FebNormalYearTime() {
        DateInstant t = new DateInstant("29/2/1905");
    }

    @Test
    public void initString29FebLeapYearTime() {
        DateInstant t = new DateInstant("29/2/1904");
        assertEquals(29, t.getDay());
        assertEquals(2, t.getMonth());
        assertEquals(1904, t.getYear());
    }

    @Test(expected = DateTimeException.class)
    public void initString31NovTime() {
        DateInstant t = new DateInstant("31/11/1904");
    }

    @Test
    public void initString30NovTime() {
        DateInstant t = new DateInstant("30/11/1904");
        assertEquals(30, t.getDay());
        assertEquals(11, t.getMonth());
        assertEquals(1904, t.getYear());
    }

}
