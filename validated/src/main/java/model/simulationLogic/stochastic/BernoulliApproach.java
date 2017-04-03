package model.simulationLogic.stochastic;

import model.simulationLogic.Simulation;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BernoulliApproach {


    public static int chooseValue(int size, double rate, Random random) {

        int eventCount = 0;

        if(rate == 0) {
//            log(size, rate, eventCount);
            return eventCount;
        }

        for(int i = 0; i < size; i++) {
            if(random.nextFloat() < rate) {
                eventCount++;
            }
        }

//        log(size, rate, eventCount);
        return eventCount;
    }


    private static void log(int size, double rate, int eventCount) {
        double calc = rate * size;
        Simulation.log.info("BA: " + eventCount + " (" + rate + " * " + size + " = " + calc + ")");
    }
}
