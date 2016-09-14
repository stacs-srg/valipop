package model.simulationLogic;

import java.util.Comparator;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RemainderComparator implements Comparator<AgeRangeWithExactFatherValue> {


    @Override
    public int compare(AgeRangeWithExactFatherValue o1, AgeRangeWithExactFatherValue o2) {

        double r1 = o1.getRemainder();
        double r2 = o2.getRemainder();



        if(r1 < r2) {
            return -1;
        } else if(r1 > r2) {
            return 1;
        }

        return 0;
    }

}
