package utils;

import model.Person;
import model.implementation.populationStatistics.IntegerRange;
import model.interfaces.populationModel.IPerson;

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

        for(Integer i : integers) {
            if(i > max) {
                max = i;
            }
        }
        return max;

    }

    public static int countPeopleInMap(Map<Integer, Collection<Person>> map) {

        int count = 0;

        for(Integer i : map.keySet()) {
            count += map.get(i).size();
        }

        return count;

    }

    public static int sumOfFlooredValues(Map<IntegerRange, Double> map) {

        int sum = 0;

        for(IntegerRange iR: map.keySet()) {
            double d = map.get(iR);
            sum += (int) d;
        }

        return sum;

    }

    public static Map<Integer,Integer> floorAllValuesInMap(Map<IntegerRange, Double> map) {

        Map<Integer, Integer> temp = new HashMap<Integer, Integer>();

        for(IntegerRange iR : map.keySet()) {

            temp.put(iR.getValue(), map.get(iR).intValue());

        }

        return temp;

    }
}
