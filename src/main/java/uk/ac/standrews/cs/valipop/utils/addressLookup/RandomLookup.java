package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.IOException;
import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class RandomLookup {

    // TODO get me from config
    static long seed = 2222;
    static Random threadRandom = new Random(seed);

    static final String[] SCOTLAND_COORDS = {"54.4","59.4","-7.9","-1.3"};
    static BoundingBox SCOTLAND;



    static {
        try {
            SCOTLAND = new BoundingBox(SCOTLAND_COORDS);
        } catch (InvalidCoordSet invalidCoordSet) {
            invalidCoordSet.printStackTrace();
        }
    }

//    public static Address getOccupiableAddressIn(String state, BoundingBox boundingBox) throws IOException, InterruptedException {
//
//        boolean found = false;
//
//        int count = 1;
//
//        while(!found) {
//
//            System.out.println("count: " + count++);
//
//            Coords c = randomCoordsIn(boundingBox);
//            try {
//                Area area = getArea(c);
//
//                if(area.isWay() && area.isResidential() && area.getState().toLowerCase().equals(state)) {
//                    Address address = area.getFreeAddress();
//                    if(address != null) {
//                        return address;
//                    }
//                }
//
//            } catch (InvalidCoordSet invalidCoordSet) {
//                invalidCoordSet.printStackTrace();
//            }
//
//
//        }
//
//        return null;
//
//    }
//
//    public static Coords randomCoordsIn(BoundingBox boundingBox) {
//
//        double lon = randomBetween(boundingBox.getBottomLeft().lon, boundingBox.getTopRight().lon);
//        double lat = randomBetween(boundingBox.getBottomLeft().lat, boundingBox.getTopRight().lat);
//
//        return new Coords(lat, lon);
//
//    }
//
//    private static double randomBetween(double a, double b) {
//        double delta  = b - a;
//
//        int rand = threadRandom.nextInt(new Long(Math.round(delta * PRECISION_ADJUSTMENT)).intValue());
//
//        return a + rand / PRECISION_ADJUSTMENT;
//    }

}
