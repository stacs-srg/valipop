package utils.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CompoundTimeUnitTest {

    // test initialisation
    // of year/s
    @Test
    public void initStringForYears() {
        CompoundTimeUnit cTU = new CompoundTimeUnit("1y");
        assertEquals(1, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.YEAR);

        cTU = new CompoundTimeUnit("5y");
        assertEquals(5, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.YEAR);
    }

    @Test
    public void initValuesForYears() {
        CompoundTimeUnit cTU = new CompoundTimeUnit(1, TimeUnit.YEAR);
        assertEquals(1, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.YEAR);

        cTU = new CompoundTimeUnit(5, TimeUnit.YEAR);
        assertEquals(5, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.YEAR);
    }


    // of month/s
    @Test
    public void initStringForMonths() {
        CompoundTimeUnit cTU = new CompoundTimeUnit("1m");
        assertEquals(1, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.MONTH);

        cTU = new CompoundTimeUnit("15m");
        assertEquals(15, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.MONTH);
    }

    @Test
    public void initValuesForMonths() {
        CompoundTimeUnit cTU = new CompoundTimeUnit(1, TimeUnit.MONTH);
        assertEquals(1, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.MONTH);

        cTU = new CompoundTimeUnit(15, TimeUnit.MONTH);
        assertEquals(15, cTU.getCount());
        assertTrue(cTU.getUnit() == TimeUnit.MONTH);
    }

    // of invalid utils.time unit
    @Test(expected = InvalidTimeUnit.class)
    public void initStringWithInvalidTimeUnit() {
        CompoundTimeUnit cTU = new CompoundTimeUnit("1w");
    }

    // of invalid count
    @Test(expected = NumberFormatException.class)
    public void initStringWithInvalidCount() {
        CompoundTimeUnit cTU = new CompoundTimeUnit("1f4m");
    }

}
