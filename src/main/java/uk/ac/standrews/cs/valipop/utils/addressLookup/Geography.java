package uk.ac.standrews.cs.valipop.utils.addressLookup;

import org.apache.commons.math3.random.RandomGenerator;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Geography {

    private final Cache residentialGeography;

    private TreeMap<Double, TreeMap<Double, Area>> areaLookup = new TreeMap<>();

    private final int HISTORY_PRECISION = 4;
    private final double PRECISION_ADJUSTMENT = Math.pow(10, HISTORY_PRECISION);

    private RandomGenerator rand;

    static final String[] SCOTLAND_COORDS = {"54.4","59.4","-7.9","-1.3"};
    static BoundingBox geographicalLimits;

    static {
        try {
            geographicalLimits = new BoundingBox(SCOTLAND_COORDS);
        } catch (InvalidCoordSet invalidCoordSet) {
            invalidCoordSet.printStackTrace();
        }
    }


    public Geography(Cache residentialGeography, RandomGenerator random) {
        this.residentialGeography = residentialGeography;
        this.rand = random;

        ArrayList<Area> newAllAreasList = new ArrayList<>();

        for(Area area : this.residentialGeography.getAllAreas()) {

            if(geographicalLimits.containsPoint(area.getCentriod())) {
                if (!area.isFull()) {
                    addToLookup(area);
                    newAllAreasList.add(area);
                }

            }
        }

        residentialGeography.setAllAreas(newAllAreasList);

    }

    public void updated(Address address) {
        if(address.getArea().isFull()) {
            removeFromLookup(address.getArea());
        } else {
            addToLookup(address.getArea());
        }
    }

    public Address getRandomEmptyAddress() {

        List<Area> allAreas = residentialGeography.getAllAreas();

        Address area = null;

        do {
            area = allAreas.get(rand.nextInt(allAreas.size())).getFreeAddress(this);
        } while (area == null);

        return area;

    }

    public Address getNearestEmptyAddressAtDistance(Coords origin, double distance) {

        int angle = rand.nextInt(360);
        int count = 0;

        int step = 6;

        Address address = null;
        double distanceDelta = Double.MAX_VALUE;

        do {
            Coords candidateLocation = GPSDistanceConverter.move(origin, distance, angle);

            Address selectedAddress = getNearestEmptyAddress(candidateLocation);

            if(selectedAddress != null) {
                double selectedDistanceDelta = Math.abs(distance - selectedAddress.getArea().getDistanceTo(origin));

                if(selectedDistanceDelta < distanceDelta) {
                    address = selectedAddress;
                    distanceDelta = selectedDistanceDelta;
                }
            }

            count++;

            if((angle += step) >= 360) {
                angle -= 360;
            }

        } while (count < 360 / step);

        if(address == null) {
            System.out.println("Something seems broke - cannot find the 'nearest' address to below location: ");
            System.out.println(origin.toString() + " @ distance " + distance);
        }

        return address;

    }

    public Address getNearestEmptyAddress(Coords origin) {
        return getNearestEmptyAddress(origin.lat, origin.lon);
    }

    public Address getNearestEmptyAddress(double lat, double lon) {

        List<Map.Entry<Double, Area>> list = new ArrayList<>();

        double flooredLon = lon;

        // middle row
        Map.Entry<Double, TreeMap<Double, Area>> e = areaLookup.floorEntry(lat);
        if(e != null) addToList(list, e.getValue().higherEntry(lon));

        Map.Entry<Double, Area> floorEntry;
        e = areaLookup.floorEntry(lat);
        if(e != null) {
            floorEntry = e.getValue().floorEntry(lon);
            addToList(list, floorEntry);

            if(floorEntry != null)
                flooredLon = floorEntry.getKey();
        }


        e = areaLookup.floorEntry(lat);
        if(e != null) addToList(list, e.getValue().lowerEntry(flooredLon));

        flooredLon = lon;

        double flooredLat = lat;

        if(!list.isEmpty()) {
            flooredLat = list.get(0).getKey();
        }

        // lower row
        e = areaLookup.lowerEntry(flooredLat);
        if(e != null) addToList(list, e.getValue().higherEntry(lon));


        e = areaLookup.lowerEntry(flooredLat);
        if(e != null) {
            floorEntry = e.getValue().floorEntry(lon);
            addToList(list, floorEntry);

            if (floorEntry != null)
                flooredLon = floorEntry.getKey();
        }

        e = areaLookup.lowerEntry(flooredLat);
        if(e != null) addToList(list, e.getValue().lowerEntry(flooredLon));

        flooredLon = lon;

        // upper row
        e = areaLookup.higherEntry(lat);
        if(e != null) addToList(list, e.getValue().floorEntry(lon));

        e = areaLookup.higherEntry(lat);
        if(e != null) {
            floorEntry = e.getValue().higherEntry(lon);
            addToList(list, floorEntry);

            if (floorEntry != null)
                flooredLon = floorEntry.getKey();
        }

        e = areaLookup.higherEntry(lat);
        if(e != null) addToList(list, e.getValue().lowerEntry(flooredLon));

        // now we have a list of the nearest existant areas by topology - we now need to calculate which is the nearest

        double smallestDistance = Double.MAX_VALUE;
        Address nearestAddress = null;

        for(Map.Entry<Double, Area> areaEntry : list) {

            Area area = areaEntry.getValue();

            Address address;
            if(area.containsPoint(lat, lon) && (address = area.getFreeAddress(this)) != null) {
                return address;
            }

            double distance = area.getDistanceTo(lat, lon);

            if(distance < smallestDistance && (address = area.getFreeAddress(this)) != null) {
                smallestDistance = distance;
                nearestAddress = address;
            }

        }

        if(nearestAddress == null) {
            System.out.println("Something seems broke - cannot find the 'nearest' address to below location: ");
            System.out.println(lat + ", " + lon);
        }

        return nearestAddress;

    }

    private void addToList(List<Map.Entry<Double, Area>> list, Map.Entry<Double, Area> toAdd) {

        if(toAdd != null) {
            list.add(toAdd);
        }

    }


    private Double round(double d) {
        return Math.round(d * PRECISION_ADJUSTMENT) / PRECISION_ADJUSTMENT;
    }

    private void addToLookup(Area area) {
        Coords centroid = area.getCentriod();
        addToLookup(area, centroid.lat, centroid.lon);
    }

    private void addToLookup(Area area, double lat, double lon) {
        areaLookup.computeIfAbsent(round(lat), k -> new TreeMap<>())
                .put(round(lon), area);
    }

    private void removeFromLookup(Area area) {

        Map<Double, Area> index = areaLookup.get(round(area.getCentriod().lat));
        if(index != null) {
            index.remove(round(area.getCentriod().lon), area);

            if (index.size() == 0) {
                areaLookup.remove(round(area.getCentriod().lat), index);
            }
        }

    }

}
