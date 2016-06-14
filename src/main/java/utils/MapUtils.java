package utils;

import datastructure.summativeStatistics.structure.OneDimensionDataDistribution;
import model.IPerson;
import model.Person;
import datastructure.summativeStatistics.structure.IntegerRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class MapUtils {


    public static int getMax(Set<Integer> integers) {

        int max = Integer.MIN_VALUE;

        for (Integer i : integers) {
            if (i > max) {
                max = i;
            }
        }
        return max;

    }

    public static <U> int countObjectsInCollectionsInMap(Map<Integer, Collection<U>> map) {

        int count = 0;

        for (Integer i : map.keySet()) {
            count += map.get(i).size();
        }

        return count;

    }

    public static int sumOfFlooredValues(Map<IntegerRange, Double> map) {

        int sum = 0;

        for (IntegerRange iR : map.keySet()) {
            double d = map.get(iR);
            sum += (int) d;
        }

        return sum;

    }

    public static Map<Integer, Integer> floorAllValuesInMap(Map<IntegerRange, Double> map) {

        Map<Integer, Integer> temp = new HashMap<Integer, Integer>();

        for (IntegerRange iR : map.keySet()) {

            temp.put(iR.getValue(), map.get(iR).intValue());

        }

        return temp;

    }

    public static void print(String label, Map<IntegerRange, ?> temp, int s, int interval, int e) {
        System.out.print(label + " | ");
        for (int i = s; i <= e; i += interval) {
            IntegerRange iR = null;
            for (IntegerRange r : temp.keySet()) {
                if (r.contains(i)) {
                    iR = r;
                    break;
                }
            }

            System.out.print(temp.get(iR) + " | ");
        }
        System.out.println();
    }

    public static Map<IntegerRange, OneDimensionDataDistribution> clone(Map<IntegerRange, OneDimensionDataDistribution> tableData) {

        Map<IntegerRange, OneDimensionDataDistribution> clone = new HashMap<IntegerRange, OneDimensionDataDistribution>();

        for (IntegerRange iR : tableData.keySet()) {
            clone.put(iR, tableData.get(iR).clone());
        }

        return clone;

    }
}
