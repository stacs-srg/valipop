package uk.ac.standrews.cs.digitising_scotland.util.MTree.experiments.euclidean;

import uk.ac.standrews.cs.digitising_scotland.util.MTree.Distance;

/**
 * Created by graham on 22/03/2017.
 */
public class EuclideanDistance implements Distance<Point> {

    public float distance(Point p1, Point p2) {
        float xdistance = p1.x - p2.x;
        float ydistance = p1.y - p2.y;

        return (float) Math.sqrt((xdistance * xdistance) + (ydistance * ydistance));
    }

}

