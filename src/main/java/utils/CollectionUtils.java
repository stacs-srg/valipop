package utils;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CollectionUtils {


    public static int sumIntegerCollection(Collection<Integer> values) {

        int sum = 0;

        for(int v : values) {
            sum += v;
        }

        return sum;

    }
}
