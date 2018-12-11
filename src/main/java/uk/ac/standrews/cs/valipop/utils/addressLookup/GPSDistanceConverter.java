package uk.ac.standrews.cs.valipop.utils.addressLookup;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class GPSDistanceConverter {

    public static double distance(Coords a, Coords b, char unit) {

        double lat1 = a.lat;
        double lon1 = a.lon;

        double lat2 = b.lat;
        double lon2 = b.lon;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
