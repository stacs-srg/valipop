package uk.ac.standrews.cs.valipop.utils.addressLookup;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BoundingBoxTests {

    @Test
    public void checkAssertOrientation() throws InvalidCoordSet {

        String[] coords = {"56.2603902","56.4203902","-2.8755844","-2.7155844"};

        BoundingBox boundingBox = new BoundingBox(coords);
        Assert.assertEquals(Double.parseDouble(coords[0]), boundingBox.getBottomLeft().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[2]), boundingBox.getBottomLeft().lon, 1E-20);

        Assert.assertEquals(Double.parseDouble(coords[1]), boundingBox.getTopRight().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[3]), boundingBox.getTopRight().lon, 1E-20);

        String[] coordsFlipped = {coords[1],coords[0],coords[3],coords[2]};

        boundingBox = new BoundingBox(coordsFlipped);
        Assert.assertEquals(Double.parseDouble(coords[0]), boundingBox.getBottomLeft().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[2]), boundingBox.getBottomLeft().lon, 1E-20);

        Assert.assertEquals(Double.parseDouble(coords[1]), boundingBox.getTopRight().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[3]), boundingBox.getTopRight().lon, 1E-20);

        String[] coordsWrongCorners = {coords[0],coords[1],coords[3],coords[2]};

        boundingBox = new BoundingBox(coordsWrongCorners);
        Assert.assertEquals(Double.parseDouble(coords[0]), boundingBox.getBottomLeft().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[2]), boundingBox.getBottomLeft().lon, 1E-20);

        Assert.assertEquals(Double.parseDouble(coords[1]), boundingBox.getTopRight().lat, 1E-20);
        Assert.assertEquals(Double.parseDouble(coords[3]), boundingBox.getTopRight().lon, 1E-20);



    }


}
