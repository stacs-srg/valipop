package model.implementation.populationStatistics;

import datastructure.summativeStatistics.structure.IntegerRange;
import datastructure.summativeStatistics.structure.InvalidRangeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class IntegerRangeTest {

    // test String in constructor
    // for int value
    @Test
    public void stringInitIntValue() {
        IntegerRange iR = new IntegerRange("10");
        assertEquals(10, iR.getValue());
    }

    // for range value
    @Test
    public void stringInitRangeValue() {
        IntegerRange iR = new IntegerRange("10-20");
        assertEquals(10, iR.getMin());
        assertEquals(20, iR.getMax());
    }

    // for int plus value
    @Test
    public void stringInitIntPlusValue() {
        IntegerRange iR = new IntegerRange("10+");
        assertEquals(10, iR.getMin());
    }


    // for range with plus - FAIL case
    @Test(expected = NumberFormatException.class)
    public void stringInitInvalidRangeValue() {
        IntegerRange iR = new IntegerRange("10-27+");
    }


    // for bad input - FAIL case
    @Test(expected = NumberFormatException.class)
    public void stringInitInvalidInput() {
        IntegerRange iR = new IntegerRange("F8L");
    }

    // test int range constructor
    // for int values
    @Test
    public void initRangeValue() {
        IntegerRange iR = new IntegerRange(10, 20);
        assertEquals(10, iR.getMin());
        assertEquals(20, iR.getMax());
    }

    @Test(expected = InvalidRangeException.class)
    public void initRangeSameIntsValue() {
        IntegerRange iR = new IntegerRange(10, 10);
    }

    @Test(expected = InvalidRangeException.class)
    public void initRangeReversedIntsValue() {
        IntegerRange iR = new IntegerRange(20, 10);
    }

    // test int plus constructor
    // for value not plus
    @Test
    public void initIntValue() {
        IntegerRange iR = new IntegerRange(10);
        assertEquals(10, iR.getValue());
    }

    // for value plus
    @Test
    public void initIntPlusValue() {
        IntegerRange iR = new IntegerRange(10, true);
        assertEquals(10, iR.getMin());
    }


    // test contains method
    // for range
    @Test
    public void containsOnRange() {

        IntegerRange iR = new IntegerRange(0, 20);

        // below
        assertFalse(iR.contains(-1));

        // bottom bound
        assertTrue(iR.contains(0));

        // in
        assertTrue(iR.contains(10));

        // top bound
        assertTrue(iR.contains(20));

        // above
        assertFalse(iR.contains(21));

    }

    // for non plus
    @Test
    public void containsOnInt() {

        IntegerRange iR = new IntegerRange(10);

        // below
        assertFalse(iR.contains(9));

        // on
        assertTrue(iR.contains(10));

        // above
        assertFalse(iR.contains(11));

    }


    // for plus
    @Test
    public void containsOnIntPlus() {

        IntegerRange iR = new IntegerRange(10, true);

        // below
        assertFalse(iR.contains(9));

        // on
        assertTrue(iR.contains(10));

        // in
        assertTrue(iR.contains(11));

    }

}
