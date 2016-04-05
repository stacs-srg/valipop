package scale_testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This maps from year to YearBucket which is a map from number of children
 * to sets of people.
 *
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BucketComplex {

    HashMap<Integer, YearBucket> byYear = new HashMap<Integer, YearBucket>();

    public BucketComplex() {

        for(int i = 0; i < 10000; i++) {
            byYear.put(i, new YearBucket());
        }

    }


}
