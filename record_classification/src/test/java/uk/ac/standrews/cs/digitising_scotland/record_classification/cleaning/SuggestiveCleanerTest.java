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
package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import org.apache.lucene.search.spell.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class SuggestiveCleanerTest extends TextCleanerTest {

    private static final String DICTIONARY_WORDS = "chocolate\nstadium\nsuper market\ntasty\nunpaid\nunknown\nmelting\nunder\nhot\nsunshine\ninsensitive";

    public SuggestiveCleanerTest() throws IOException {

        super(new SuggestiveCleaner(new PlainTextDictionary(new StringReader(DICTIONARY_WORDS)), new JaroWinklerDistance(), 0.8f), new HashMap<String, String>() {{
            put("estadium made of shokolat", "stadium made of chocolate");
            put("like the ones in supermarket", "like the ones in super market");
            put("notknown nupaid but tasky", "unknown unpaid but tasty");
            put("mleting undre the hut sonshiney", "melting under the hot sunshine");
            put("desensitiv of case", "insensitive of case");
        }});
    }
}
