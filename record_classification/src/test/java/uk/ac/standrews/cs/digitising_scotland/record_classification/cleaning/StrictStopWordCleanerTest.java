package uk.ac.standrews.cs.digitising_scotland.record_classification.cleaning;

import java.util.*;

/**
 * @author Masih Hajiarab Derkani
 */
public class StrictStopWordCleanerTest extends TextCleanerTest {

    public StrictStopWordCleanerTest() {

        super(new StrictStopWordCleaner(Arrays.asList("unpaid", "UNKNOWN", "Something peculiar", "")), new HashMap<String, String>() {

            {
                put("unpaid", "");
                put("unpaid fellow", "unpaid fellow");
                put("Something peculiar", "");
                put("Something Peculiar", "Something Peculiar");
                put("UNKNOWN Something peculiar", "UNKNOWN Something peculiar");
                put("", "");
                put("UNKNOWN", "");
                put("unknown", "unknown");

            }
        });
    }
}
