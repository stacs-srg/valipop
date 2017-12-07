package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.math3.random.RandomGenerator;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CollectionUtils {

    /*
    -------- Adapted shuffle code taken from Collections class ---------
     */

    private static final int SHUFFLE_THRESHOLD = 5;

    public static void shuffle(List<?> list, RandomGenerator rnd) {
        int size = list.size();
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i=size; i>1; i--)
                Collections.swap(list, i-1, rnd.nextInt(i));
        } else {
            Object arr[] = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, rnd.nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (int i=0; i<arr.length; i++) {
                it.next();
                it.set(arr[i]);
            }
        }
    }

    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
