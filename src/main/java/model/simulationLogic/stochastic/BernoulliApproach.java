package model.simulationLogic.stochastic;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BernoulliApproach {


    public static int chooseValue(int size, double rate, Random random) {

        int eventCount = 0;

        for(int i = 0; i < size; i++) {
            if(random.nextFloat() < rate) {
                eventCount++;
            }
        }

        return eventCount;
    }
}
