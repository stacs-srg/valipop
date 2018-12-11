package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ReverseGeocodeLookup {

    static final String[] SCOTLAND_COORDS = {"54.4","59.4","-7.9","-1.3"};
    static BoundingBox SCOTLAND;

    static {
        try {
            SCOTLAND = new BoundingBox(SCOTLAND_COORDS);
        } catch (InvalidCoordSet invalidCoordSet) {
            invalidCoordSet.printStackTrace();
        }
    }

    static long seed = 2222;
    static Random threadRandom = new Random(seed);

    static ArrayList<Area> areaDB = new ArrayList<>();

    final static int HISTORY_PRECISION = 4;
    final static double PRECISION_ADJUSTMENT = Math.pow(10, HISTORY_PRECISION);

    static Map<Double, Map<Double, Area>> lookupHistory = new HashMap<>();


    public static void main(String[] args) throws IOException, InvalidCoordSet {

//        System.out.println(getArea(56.3407026, -2.80017470441128).toString());
//        System.out.println(getArea(56.3407026, -2.800171).toString());
//
//        System.out.println(getArea(56.3445345326, -2.725643526).toString());
//        System.out.println(getArea(56.3445345326, -2.725643526).toString());
//
//        System.out.println(getArea(56.3176557, -3.0073146).toString());

//        for(int i = 0; i < 100; i++) {
//            Coords c = randomCoordsIn(SCOTLAND);
//
//            System.out.println(c.toString());
//            System.out.println(getArea(c));
//        }

//        System.out.println(GPSDistanceConverter.distance(SCOTLAND.getBottomLeft(), new Coords(SCOTLAND.getBottomLeft().lat, SCOTLAND.getBottomLeft().lon + 0.0001), 'K') * 1000);
//        System.out.println(GPSDistanceConverter.distance(SCOTLAND.getTopRight(), new Coords(SCOTLAND.getTopRight().lat, SCOTLAND.getTopRight().lon + 0.0001), 'K') * 1000);

        System.out.println(getOccupiableAddressIn("scotland", SCOTLAND).toString());

    }

    public static Address getOccupiableAddressIn(String state, BoundingBox boundingBox) throws IOException {

        boolean found = false;

        int count = 1;

        while(!found) {

            System.out.println("count: " + count++);

            Coords c = randomCoordsIn(boundingBox);
            try {
                Area area = getArea(c);

                if(area.isWay() && area.isResidential() && area.getState().toLowerCase().equals(state)) {
                    Address address = area.getFreeAddress();
                    if(address != null) {
                        return address;
                    }
                }

            } catch (InvalidCoordSet invalidCoordSet) {
                invalidCoordSet.printStackTrace();
            }


        }

        return null;

    }

    public static Coords randomCoordsIn(BoundingBox boundingBox) {

        double lon = randomBetween(boundingBox.getBottomLeft().lon, boundingBox.getTopRight().lon);
        double lat = randomBetween(boundingBox.getBottomLeft().lat, boundingBox.getTopRight().lat);

        return new Coords(lat, lon);

    }

    private static double randomBetween(double a, double b) {
        double delta  = b - a;

        int rand = threadRandom.nextInt(new Long(Math.round(delta * PRECISION_ADJUSTMENT)).intValue());

        return a + rand / PRECISION_ADJUSTMENT;
    }


    public static Area getArea(Coords coords) throws IOException, InvalidCoordSet {
        return getArea(coords.lat, coords.lon);
    }

    public static Area getArea(double lat, double lon) throws IOException, InvalidCoordSet {

        // look up in cache
        Area area = checkCache(lat, lon);

        // if not found the hit API
        if(area == null) {
            area = OpenStreetMapAPI.getAreaFromAPI(lat, lon);

            if(!area.isErroneous() && area.isWay()) {
                areaDB.add(area);
            }

            // Check if requested point falls in requested boundng box, if not then keep note of which BB the point relates to
            if(area.isErroneous() || !area.containsPoint(lat, lon)) {
                Map<Double, Area> index = lookupHistory.get(round(lat));

                if(index == null) {
                    index = new HashMap<>();
                    lookupHistory.put(round(lat), index);
                }

                index.put(round(lon), area);

            }
        }

        return area;
    }

    private static Double round(double d) {
        return Math.round(d * PRECISION_ADJUSTMENT) / PRECISION_ADJUSTMENT;
    }

    private static Area checkCache(double lat, double lon) {

        // Checks that given coords don't correspond to a bounding box already retrieved
        Map<Double, Area> index = lookupHistory.get(round(lat));
        if(index != null) {
            return index.get(round(lon));
        }

        for(Area area : areaDB) {
            if(area.containsPoint(lat, lon)) {
                // Here we are just taking the first one we find - may want to change this later
                // (Bounding boxes can overlap due to their definition)
                return area;
            }
        }

        return null;
    }




}


