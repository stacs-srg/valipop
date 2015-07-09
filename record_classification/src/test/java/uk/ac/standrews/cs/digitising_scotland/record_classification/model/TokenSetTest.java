/*
 * Copyright 2015 Digitising Scotland project:
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
package uk.ac.standrews.cs.digitising_scotland.record_classification.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenSetTest {

    @Test
    public void tokenisation() {

        checkTokenisation("the quick brown fox jumps", new HashSet<>(Arrays.asList("the", "quick", "brown", "fox", "jumps")));
        checkTokenisation(" the   quick brown fox jumps   ", new HashSet<>(Arrays.asList("the", "quick", "brown", "fox", "jumps")));
        checkTokenisation(" the. quick. brown. fox. jumps!   .", new HashSet<>(Arrays.asList("the", "quick", "brown", "fox", "jumps")));
    }

    private void checkTokenisation(String s, Set<String> tokens) {

        TokenSet token_set = new TokenSet(s);

        assertEquals(token_set.size(), tokens.size());

        for (String token : token_set) {
            assertTrue(tokens.contains(token));
        }
    }
}
