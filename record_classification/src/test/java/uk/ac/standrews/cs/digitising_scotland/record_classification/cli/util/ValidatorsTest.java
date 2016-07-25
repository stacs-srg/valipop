/*
 * Copyright 2016 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.cli.util;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class ValidatorsTest {

    @Test
    public void testBetweenZeroToOneInclusive() throws Exception {

        assertTrue(Validators.isBetweenZeroToOneInclusive(0.2));
        assertTrue(Validators.isBetweenZeroToOneInclusive(0.0));
        assertTrue(Validators.isBetweenZeroToOneInclusive(1.0));
        assertTrue(Validators.isBetweenZeroToOneInclusive(0.0 - Validators.DELTA / 2));
        assertTrue(Validators.isBetweenZeroToOneInclusive(1.0 + Validators.DELTA / 2));
        assertFalse(Validators.isBetweenZeroToOneInclusive(0.0 - Validators.DELTA));
        assertFalse(Validators.isBetweenZeroToOneInclusive(1.0 + Validators.DELTA));
    }
}
