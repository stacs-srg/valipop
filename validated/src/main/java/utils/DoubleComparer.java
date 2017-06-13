package utils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DoubleComparer {

    public static boolean equal(double d1, double d2, double delta) {
        double diff = Math.abs(d1 - d2);
        return diff < delta;
    }
}
