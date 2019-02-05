package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Geography {

    private final Cache residentialGeography;

    private TreeMap<Double, TreeMap<Double, Area>> areaLookup = new TreeMap<>();

    private final int HISTORY_PRECISION = 4;
    private final double PRECISION_ADJUSTMENT = Math.pow(10, HISTORY_PRECISION);

    // TODO extract to config random
    private Random rand = new Random();


    public Geography(Cache residentialGeography) {
        this.residentialGeography = residentialGeography;

        for(Area area : this.residentialGeography.getAllAreas()) {
            addToLookup(area);
        }

    }

    public Address getRandomEmptyAddress() {

        List<Area> allAreas = residentialGeography.getAllAreas();

        Address area = null;

        do {
            area = allAreas.get(rand.nextInt(allAreas.size())).getFreeAddress();
        } while (area == null);

        return area;

    }

    public Address getEmptyAddress(Coords coords) {
        return getEmptyAddress(coords.lat, coords.lon);
    }

    public Address getEmptyAddress(double lat, double lon) {

        // check is coords in area lookup
        Map<Double, Area> index = areaLookup.get(round(lat));
        if(index != null) {
            Area area = index.get(round(lon));
            if(area != null)
                return area.getFreeAddress();
        }

        Area area = residentialGeography.checkCache(lat, lon);

        if(area == null) {
            return null;
        } else {
            addToLookup(area, lat, lon);
            return area.getFreeAddress();
        }
    }

    public Address getEmptyAddressAtDistance(Coords origin, double distance) {

        // convert distance in to lat/long degrees

        int stepBy = 6;

        int angle = rand.nextInt(360);
        int count = 0;

        Address address = null;

        do {
            Coords candidateLocation = GPSDistanceConverter.move(origin, distance, angle);

            address = getEmptyAddress(candidateLocation);

            count++;

            if((angle += stepBy) >= 360) {
               angle -= 360;
            }

        } while (address == null && count < 360 / stepBy);

        if(address == null) {
            address = getNearestEmptyAddressAtDistance(origin, distance);
        }

        return address;
    }

    public Address getNearestEmptyAddressAtDistance(Coords origin, double distance) {

        int angle = rand.nextInt(360);
        int count = 0;

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

            if(++angle >= 360) {
                angle -= 360;
            }

        } while (count < 360);

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
            if(area.containsPoint(lat, lon) && (address = area.getFreeAddress()) != null) {
                return address;
            }

            double distance = area.getDistanceTo(lat, lon);

            if(distance < smallestDistance && (address = area.getFreeAddress()) != null) {
                smallestDistance = distance;
                nearestAddress = address;
            }

        }

//        if(nearestAddress == null) {
//            System.out.println("Something seems broke - cannot find the 'nearest' address to below location: ");
//            System.out.println(lat + ", " + lon);
//        }

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


}
