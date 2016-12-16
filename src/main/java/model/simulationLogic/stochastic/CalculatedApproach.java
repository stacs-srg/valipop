package model.simulationLogic.stochastic;

import model.simulationLogic.Simulation;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CalculatedApproach {

    public static int chooseValue(int size, double rate) {
        double events = size * rate;

        int roundedEvents = events % 1 < 0.5 ? (int) events : (int) events + 1;

//        Simulation.log.info("CA: " + roundedEvents + " (" + rate + " * " + size + " = " + events + ")");

        return roundedEvents;
    }

}
