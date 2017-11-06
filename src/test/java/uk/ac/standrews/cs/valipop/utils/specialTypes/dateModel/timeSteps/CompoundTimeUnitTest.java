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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps;

import org.junit.Test;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.exceptions.InvalidTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
