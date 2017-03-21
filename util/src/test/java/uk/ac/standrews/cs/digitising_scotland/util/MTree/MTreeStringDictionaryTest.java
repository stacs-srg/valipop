/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util.MTree;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.util.dataset.DataSet;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by al on 27/01/2017.
 */
@Ignore
public class MTreeStringDictionaryTest {

    private MTree<String> t;
    private int count;

    @Before
    public void setUp() throws Exception {

        t = new MTree<>(new EditDistance2());
        String dict_file = "/usr/share/dict/words";
        DataSet data = new DataSet(Paths.get(dict_file));
        count = 0;
        for (List<String> lines : data.getRecords()) {  // file has one word per line
            t.add(lines.get(0));
            count++;
        }
    }

    /**
     * test to ensure that the correct number of words are in MTree
     */
    @Test
    public void unix_dictionary_size_test() {
        assertEquals(count, t.size());
    }

    /**
     * test nearest neighbour in  a dictionary of words
     */
    @Test
    public void nearest_neighbour() {

        DataDistance<String> result = t.nearestNeighbour("absilute");
        assertEquals("absolute", result.value);
    }

    /**
     * test nearest N in a dictionary of words
     */
    @Test
    public void nearest_N() {

        List<DataDistance<String>> results = t.nearestN("accelerat", 5);
        List<String> values = t.mapValues(results);
        assertTrue(values.contains("accelerate"));
        assertTrue(values.contains("accelerant"));
        assertTrue(values.contains("accelerated"));
        assertTrue(values.contains("accelerator"));
        assertTrue(values.contains("scelerat")); // noun: a villain, or extremely wicked person;
    }

    /**
     * test range search in a dictionary of words
     */
    @Test
    public void range() {
        List<DataDistance<String>> results = t.rangeSearch("tomato", 2);
        List<String> values = t.mapValues(results);
        assertTrue(values.contains("tomato")); // distance 0
        assertTrue(values.contains("pomato")); // distance 1
        assertTrue(values.contains("pomate")); // distance 2
        assertTrue(values.contains("potato")); // distance 2
        assertTrue(values.contains("tomcat")); // distance 2

        // System.out.println( "all at 2: " + result3 );
    }

    public class EditDistance2 implements Distance<String> {

        Levenshtein levenshtein = new Levenshtein();

        @Override
        public float distance(String s1, String s2) {

            return levenshtein.distance(s1, s2);
        }
    }
}