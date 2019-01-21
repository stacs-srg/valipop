package uk.ac.standrews.cs.valipop.utils.addressLookup;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Geography {

    private final Cache residentialGeography;

    public Geography(Cache residentialGeography) {
        this.residentialGeography = residentialGeography;
    }

    public Address getEmptyAddress(long lat, long lon) {
        Area area = residentialGeography.checkCache(lat, lon);
        return area.getFreeAddress();
    }

}
