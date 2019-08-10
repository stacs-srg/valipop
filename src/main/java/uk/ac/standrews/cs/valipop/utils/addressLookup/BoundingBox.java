package uk.ac.standrews.cs.valipop.utils.addressLookup;

import java.io.Serializable;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BoundingBox implements Serializable {

    private static final long serialVersionUID = 568989809832084920L;

    private Coords bottomLeft;
    private Coords topRight;

    public BoundingBox(String[] coords) throws InvalidCoordSet {

        if(coords.length != 4) {
            throw new InvalidCoordSet();
        }

        bottomLeft = new Coords(coords[0], coords[2]);
        topRight = new Coords(coords[1], coords[3]);

        assertCorrectOrientation();

    }

    private void assertCorrectOrientation() {

        if(bottomLeft.lon > topRight.lon) {
            Coords temp = bottomLeft;
            bottomLeft = topRight;
            topRight = temp;
        }

        if(bottomLeft.lat > topRight.lat) {

            // recording wrong corners - so swap to other corners
            double tempLat = bottomLeft.lat;
            bottomLeft = new Coords(topRight.lat, bottomLeft.lon);
            topRight = new Coords(tempLat, topRight.lon);

        }

    }

    public Coords getBottomLeft() {
        return bottomLeft;
    }

    public Coords getTopRight() {
        return topRight;
    }

    public boolean containsPoint(Coords coords) {
        return containsPoint(coords.lat, coords.lon);
    }

    public boolean containsPoint(double lat, double lon) {
        return (bottomLeft.lat <= lat && lat <= topRight.lat) && (bottomLeft.lon <= lon && lon <= topRight.lon);
    }
}
