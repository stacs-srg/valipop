package uk.ac.standrews.cs.valipop.utils.addressLookup;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Coords {

    final double lat;
    final double lon;

    public Coords(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Coords(String lat, String lon) {
        this(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    public String toString() {
        return String.valueOf(lat) + ", " + String.valueOf(lon);
    }

}
