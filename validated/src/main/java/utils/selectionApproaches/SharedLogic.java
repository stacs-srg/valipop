package utils.selectionApproaches;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SharedLogic {

    private static Random randomNumberGenerator = new Random();

    public static int calculateNumberToHaveEvent(int people, Double eventRate) {

        double toHaveEvent = people * eventRate;
        int flooredToHaveEvent = (int) toHaveEvent;
        toHaveEvent -= flooredToHaveEvent;

        // this is a random dice roll to see if the fraction of a has the event or not

        if (randomNumberGenerator.nextInt(100) < toHaveEvent * 100) {
            flooredToHaveEvent++;
        }

//        if (toHaveEvent > 0.5) {
////            if (randomNumberGenerator.nextDouble() < toHaveEvent) {
////                flooredToHaveEvent++;
////            }
////        } else {
//            flooredToHaveEvent++;
//        }

        return flooredToHaveEvent;

    }

}
