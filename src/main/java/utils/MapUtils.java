package utils;

import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MapUtils {


    public static int getMax(Set<Integer> integers) {

        int max = Integer.MIN_VALUE;

        for(Integer i : integers) {
            if(i > max) {
                max = i;
            }
        }
        return max;

    }
}
