package model.time;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TimeUtilsTest {

    @Test
    public void checkMonthsMethod() {
        // minus test
        TimeInstant t = new TimeInstant(6, 1900);

        assertEquals(21, TimeUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(21, TimeUnit.MONTH))).getCount());
        assertEquals(21, TimeUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(21, TimeUnit.MONTH)), t).getCount());

        // 0 test
        assertEquals(0, TimeUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(0, TimeUnit.MONTH))).getCount());
        assertEquals(0, TimeUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(0, TimeUnit.MONTH)), t).getCount());

        assertEquals(12, TimeUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(12, TimeUnit.MONTH))).getCount());
        assertEquals(12, TimeUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(12, TimeUnit.MONTH)), t).getCount());

        // plus test
        assertEquals(15, TimeUtils.differenceInMonths(t, t.advanceTime(new CompoundTimeUnit(15, TimeUnit.MONTH))).getCount());
        assertEquals(15, TimeUtils.differenceInMonths(t.advanceTime(new CompoundTimeUnit(15, TimeUnit.MONTH)), t).getCount());

    }

    @Test
    public void checkYearsMethod() {

        TimeInstant t = new TimeInstant(6, 1900);

        assertEquals(0, TimeUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(0, TimeUnit.YEAR))).getCount());
        assertEquals(0, TimeUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(0, TimeUnit.YEAR)), t).getCount());

        assertEquals(7, TimeUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(7, TimeUnit.YEAR))).getCount());
        assertEquals(7, TimeUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(7, TimeUnit.YEAR)), t).getCount());


        assertEquals(7, TimeUtils.differenceInYears(t, t.advanceTime(new CompoundTimeUnit(-7, TimeUnit.YEAR))).getCount());
        assertEquals(7, TimeUtils.differenceInYears(t.advanceTime(new CompoundTimeUnit(-7, TimeUnit.YEAR)), t).getCount());

    }



}
