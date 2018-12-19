package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ReverseGeocodeLookup {

    // config stuff
    public static final int ABODES_PER_KM = 500;


    Cache cache;

    public ReverseGeocodeLookup(Cache cache) {
        this.cache = cache;
    }


    public Area getArea(Coords coords) throws IOException, InvalidCoordSet, InterruptedException {
        return getArea(coords.lat, coords.lon);
    }

    public Area getArea(double lat, double lon) throws IOException, InvalidCoordSet, InterruptedException {

        // look up in cache
        Area area = cache.checkCache(lat, lon);

        // if not found the hit API
        if(area == null) {
            area = OpenStreetMapAPI.getAreaFromAPI(lat, lon, cache);

            if(!area.isErroneous() && area.isWay()) {
                cache.areaDB.add(area);
            }

            // Check if requested point falls in requested boundng box, if not then keep note of which BB the point relates to
            if(area.isErroneous() || !area.isWay() || !area.containsPoint(lat, lon)) {
                cache.addHistory(lat, lon, area);
            }
        }

        return area;
    }








}


