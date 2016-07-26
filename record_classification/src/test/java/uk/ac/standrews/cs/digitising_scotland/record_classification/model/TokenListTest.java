/*
 * record_classification - Automatic record attribute classification.
 * Copyright © 2012-2016 Digitising Scotland project
 * (http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenListTest {

    @Test
    public void tokenisation() {

        checkTokenisation("the quick brown fox jumps", "the", "quick", "brown", "fox", "jumps");
        checkTokenisation(" the   quick brown fox jumps   ", "the", "quick", "brown", "fox", "jumps");
        checkTokenisation(" the. quick. brown. fox. jumps!   .", "the", "quick", "brown", "fox", "jumps");
        checkTokenisation(" the-quick-brown.- fox.-jumps!   .", "the", "quick", "brown", "fox", "jumps");
        checkTokenisation(" quick.  fox. jumps! brown. the.  .", "quick", "fox", "jumps", "brown", "the");
        checkTokenisation("string_283", "string_283");
        checkTokenisation("string-283", "string", "283");
    }

    private void checkTokenisation(String s, String... expected_tokens) {

        TokenList token_list = new TokenList(s);

        assertEquals(token_list.size(), expected_tokens.length);

        for (int i = 0; i < token_list.size(); i++) {
            assertEquals(token_list.get(i), expected_tokens[i]);
        }
    }
}
