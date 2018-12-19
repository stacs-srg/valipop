package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.Serializable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Coords implements Serializable {

    private static final long serialVersionUID = 1209867823409878923L;

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
