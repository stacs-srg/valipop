package utils;

import model.interfaces.populationModel.IPerson;

import java.util.Collection;
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

    public static int countPeopleInMap(Map<Integer, Collection<IPerson>> map) {

        int count = 0;

        for(Integer i : map.keySet()) {
            count += map.get(i).size();
        }

        return count;

    }
}
