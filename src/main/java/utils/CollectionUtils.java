package utils;

import model.IPerson;
import utils.time.Date;
import utils.time.DateClock;

import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CollectionUtils {


    public static int sumIntegerCollection(Collection<Integer> values) {

        int sum = 0;

        for (int v : values) {
            sum += v;
        }

        return sum;

    }

    public static int countPeopleInCollectionAliveOnDate(Collection<IPerson> people, Date date) {

        int count = 0;

        for(IPerson p : people) {
            if(p.aliveOnDate(date)) {
                count++;
            }
        }


        return count;
    }
}
