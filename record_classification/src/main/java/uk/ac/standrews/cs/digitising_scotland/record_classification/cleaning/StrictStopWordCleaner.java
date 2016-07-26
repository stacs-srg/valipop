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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import java.util.*;

/**
 * Returns an empty string if the entire data exactly matches any of the stop words.
 *
 * @author Masih Hajiarab Derkani
 */
public class StrictStopWordCleaner implements TextCleaner {

    private static final String EMPTY_STRING = "";
    private final Collection<String> stop_words;

    public StrictStopWordCleaner(Collection<String> stop_words) {

        this.stop_words = stop_words;
    }

    @Override
    public String cleanData(final String data) {

        return stop_words.contains(data) ? EMPTY_STRING : data;
    }
}
